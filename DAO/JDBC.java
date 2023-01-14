package DAO;
import model.Globals;
import model.Tools;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *  This class used for opening and closing connection to database.
 */
public abstract class JDBC {

    // Setup all data needed for the jdbcUrl
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?serverTimezone=UTC";
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = Globals.databaseUsername; // Username
    private static final String password = Globals.databasePassword; // Password
    public static Connection connection;  // Connection Interface


    /**
     *  Attempts to open connection to database.
     */
    public static void openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            Tools.consoleMessage(Tools.MsgType.INFO, "Connected to DB", "JDBC.openConnection()");
        }
        catch(Exception e)
        {

            Tools.consoleMessage(Tools.MsgType.ERROR, e.getMessage(), "JDBC.openConnection()" );
            Tools.consoleMessage(Tools.MsgType.ERROR, "Make sure mySQL service is running", "JDBC.openConnection()");

        }
    }


    /**
     *  Attempts to close connection to database.
     */
    public static void closeConnection() {
        try {
            connection.close();
            Tools.consoleMessage(Tools.MsgType.INFO, "Connection closed", "JDBC.closeConnection()" );
        }
        catch(Exception e)
        {
            Tools.consoleMessage(Tools.MsgType.ERROR, e.getMessage(), "JDBC.closeConnection()" );
        }
    }
}