package com.github.losevskiyfz.repository.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {

    private final BlockingQueue<Connection> pool;
    private final String jdbcUrl;
    private final int maxPoolSize;

    public ConnectionPool(String jdbcUrl, int maxPoolSize) throws SQLException {
        this.jdbcUrl = jdbcUrl;
        this.maxPoolSize = maxPoolSize;
        this.pool = new LinkedBlockingQueue<>(maxPoolSize);
        initializePool();
    }

    private void initializePool() throws SQLException {
        for (int i = 0; i < maxPoolSize; i++) {
            pool.offer(createNewConnection());
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    public Connection getConnection() throws InterruptedException {
        Connection realConn = pool.take();
        return PooledConnectionFactory.create(realConn, this);
    }

    void returnConnection(Connection connection) {
        if (connection != null) {
            pool.offer(connection);
        }
    }

    public void shutdown() throws SQLException {
        for (Connection conn : pool) {
            conn.close();
        }
    }
}
