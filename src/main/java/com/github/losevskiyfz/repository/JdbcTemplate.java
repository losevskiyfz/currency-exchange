package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.exception.SqlObjectNotFoundException;
import com.github.losevskiyfz.exception.UniqueConstraintViolationException;
import com.github.losevskiyfz.repository.pool.ConnectionPool;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JdbcTemplate {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ConnectionPool connectionPool = context.resolve(ConnectionPool.class);
    private static final Logger LOG = Logger.getLogger(JdbcTemplate.class.getName());

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) {
        LOG.info(String.format("Executing query: %s", sql));
        List<T> resultList = new ArrayList<>();
        try (var connection = connectionPool.getConnection();
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                T row = rowMapper.mapRow(resultSet);
                resultList.add(row);
            }
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return resultList;
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws SqlObjectNotFoundException{
        LOG.info(String.format("Executing query: %s with parameters: %s", sql, List.of(args)));
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return rowMapper.mapRow(resultSet);
                } else {
                    throw new SqlObjectNotFoundException("No result found");
                }
            }
        } catch (SqlObjectNotFoundException e){
            throw e;
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Integer update(String sql, Object... args) {
        LOG.info(String.format("Executing update: %s with parameters: %s", sql, List.of(args)));
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            if (e.getMessage().contains("CONSTRAINT_UNIQUE")) {
                throw new UniqueConstraintViolationException(e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }
}
