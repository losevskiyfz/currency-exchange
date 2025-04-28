package com.github.losevskiyfz.repository.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.logging.Logger;

public class PooledConnectionFactory {

    private static final Logger LOG = Logger.getLogger(PooledConnectionFactory.class.getName());

    public static Connection create(Connection realConnection, ConnectionPool pool) {
        LOG.info("Creating a pooled connection.");
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionInvocationHandler(realConnection, pool)
        );
    }

    private static class ConnectionInvocationHandler implements InvocationHandler {
        private final Connection realConnection;
        private final ConnectionPool pool;
        private boolean isClosed = false;

        ConnectionInvocationHandler(Connection realConnection, ConnectionPool pool) {
            this.realConnection = realConnection;
            this.pool = pool;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("close".equals(method.getName())) {
                if (!isClosed) {
                    isClosed = true;
                    pool.returnConnection(realConnection);
                }
                return null;
            } else if ("isClosed".equals(method.getName())) {
                return isClosed;
            }

            if (isClosed) {
                throw new IllegalStateException("Connection is already closed.");
            }

            return method.invoke(realConnection, args);
        }
    }
}