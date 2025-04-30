package com.github.losevskiyfz.repository;

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(java.sql.ResultSet resultSet) throws java.sql.SQLException;
}