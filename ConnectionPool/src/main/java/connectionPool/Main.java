package connectionPool;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {

        new ConnectionPool(10);
        //ConnectionPoolExample.benchmarkDirectConn();
        ConnectionPoolExample.benchmarkConnPool();


    }
}