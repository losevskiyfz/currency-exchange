package com.github.losevskiyfz.repository.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ConnectionPool {

    private static final Logger LOG = Logger.getLogger(ConnectionPool.class.getName());
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
        LOG.info(String.format("Initializing connection pool with size: %s", maxPoolSize));
        for (int i = 0; i < maxPoolSize; i++) {
            pool.offer(createNewConnection());
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    public Connection getConnection() throws InterruptedException {
        LOG.info("Acquiring a connection from the pool");
        Connection realConn = pool.take();
        return PooledConnectionFactory.create(realConn, this);
    }

    void returnConnection(Connection connection) {
        if (connection != null) {
            LOG.info("Returning connection to the pool");
            pool.offer(connection);
        }
    }

    public void shutdown() throws SQLException {
        for (Connection conn : pool) {
            conn.close();
        }
    }
}
