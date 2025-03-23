package com.github.losevskiyfz.repository.pool;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.config.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    private final BlockingQueue<Connection> connectionPool;
    private final String url;
    private final int poolSize;

    public ConnectionPool() throws SQLException {
        Properties properties = ApplicationContext.getInstance().resolve(Properties.class);
        this.url = properties.getProperty("db.url");
        this.poolSize = Integer.parseInt(properties.getProperty("db.poolsize"));
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);
        initializeConnections();
    }

    private void initializeConnections() throws SQLException {
        for (int i = 0; i < poolSize; i++) {
            connectionPool.offer(createConnection());
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public Connection borrowConnection() throws InterruptedException {
        return connectionPool.take();
    }

    public void returnConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(true);
            connectionPool.offer(connection);
        }
    }
}

