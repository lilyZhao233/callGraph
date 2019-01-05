package sorra.tracesonar.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLUtil {

    public static Connection getCon(String database) throws ClassNotFoundException, SQLException {
        Connection connection;
        String driver="com.mysql.jdbc.Driver";
        String url="jdbc:mysql://localhost:3306/"+database;
        String user = "root";
        String password = "123456";

        Class.forName(driver);
        connection= DriverManager.getConnection(url, user,password);
        return connection;
    }
}
