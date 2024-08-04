package connectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;
import java.util.logging.Logger;
import connectionPool.ConnectionPool;

public class ConnectionPoolExample {
    private static final int POOL_SIZE = 10;
    private static final int NUM_THREADS = 1000;
    private static final Logger logger = Logger.getLogger(ConnectionPoolExample.class.getName());

    public static void benchmarkConnPool() throws InterruptedException, SQLException {
        long startTime = System.currentTimeMillis();
        ConnectionPool pool = new ConnectionPool(POOL_SIZE);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUM_THREADS,
                NUM_THREADS,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(NUM_THREADS)
        );

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.execute(() -> {
                try {
                    Connection conn = pool.get();
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("SELECT SLEEP(1.0)");
                        logger.info(" executing query: ");
                    } catch (SQLException e) {
                        logger.warning("Error executing query: " + e.getMessage());
                    } finally {
                        // Add the connection after the execution
                        pool.put(conn);
                    }
                } catch (InterruptedException e) {
                    logger.warning("Error getting connection from pool: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
        pool.close();

        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info("Benchmark pool completed in " + elapsedTime + " ms");
    }

    public static void benchmarkDirectConn() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUM_THREADS,
                NUM_THREADS,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(NUM_THREADS)
        );

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.execute(() -> {
                try (Connection conn = DriverManager.getConnection(ConnectionPool.DB_URL, ConnectionPool.USER, ConnectionPool.PASS);
                     Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT SLEEP(1.0)");
                } catch (SQLException e) {
                    logger.warning("Error executing query: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info("Benchmark direct completed in " + elapsedTime + " ms");
    }


}