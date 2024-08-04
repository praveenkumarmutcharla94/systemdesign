package connectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ConnectionPool {
    public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/testdb";
    public static final String USER = "root";
    public static final String PASS = "";
    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

    private final BlockingQueue<Connection> pool;

    public ConnectionPool(int size) throws SQLException {
        pool = new LinkedBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            pool.add(DriverManager.getConnection(DB_URL, USER, PASS));
        }
    }

    public Connection get() throws InterruptedException {
        logger.fine("Get attempt");
        Connection conn = pool.take();
        logger.fine("Got connection");
        return conn;
    }

    public void put(Connection conn) {
        logger.fine("Put attempt");
        try {
            pool.put(conn);
            logger.fine("Connection returned to pool");
        } catch (InterruptedException e) {
            logger.warning("Failed to return connection to pool: " + e.getMessage());
            try {
                conn.close();
                logger.fine("Connection discarded");
            } catch (SQLException ex) {
                logger.warning("Failed to close connection: " + ex.getMessage());
            }
        }
    }

    public void close() throws SQLException, InterruptedException {
        while (!pool.isEmpty()) {
            pool.take().close();
        }
    }
}