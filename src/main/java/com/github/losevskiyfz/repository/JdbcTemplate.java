package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.repository.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ConnectionPool connectionPool = context.resolve(ConnectionPool.class);
    private static final Logger LOG = LogManager.getLogger(JdbcTemplate.class);

    public <T> List<T> queryForList(String sql, Class<T> type) {
        LOG.info("Executing query: {}", sql);
        List<T> resultList = new ArrayList<>();
        try (var connection = connectionPool.getConnection();
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                T row = type.getDeclaredConstructor().newInstance();
                for (var field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(row, resultSet.getObject(field.getName()));
                }
                resultList.add(row);
            }
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
        return resultList;
    }

}
