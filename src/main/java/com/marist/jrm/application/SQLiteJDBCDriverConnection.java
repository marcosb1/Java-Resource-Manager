package com.marist.jrm.application;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author sqlitetutorial.net
 */
public class SQLiteJDBCDriverConnection {
    /**
     * Connect to database
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // db parameters

            String url = "jdbc:sqlite:src/resources/jrmDB.db";
            // create a connection to the database
            conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");
            if (conn == null){
                System.out.println("conn failed");
            }
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return conn;
    }
    /**
     * @param args the command line arguments
     */

}