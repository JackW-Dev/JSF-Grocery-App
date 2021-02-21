package dbase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager
{
    private static final String DBURL = "jdbc:derby://localhost:1527/groceryDB";
    
    public static Connection getConnection() throws SQLException
    {
        DriverManager.registerDriver(
                new org.apache.derby.jdbc.ClientDriver());
        return DriverManager.getConnection(DBURL, "grocery", "pass");
    }

    
}
