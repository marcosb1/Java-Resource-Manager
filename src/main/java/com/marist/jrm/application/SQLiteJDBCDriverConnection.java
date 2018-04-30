package com.marist.jrm.application;

import com.marist.jrm.client.GUIDriver;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * this class is a generic JDBC driver connection class
 * @author Dominic Rossillo
 */
public class SQLiteJDBCDriverConnection {
    /**
     * Connect to database
     */
    Connection conn = null;
    public Connection connect() {

        try {
            // db parameters
            //set the url connectino to the jrmDB file
            String url = "jdbc:sqlite:src/resources/jrmDB.db";
            SQLiteConfig config = new SQLiteConfig();
            //set enforce foreign keys to true for the config
            config.enforceForeignKeys(true);
            // create a connection to the database
            conn = DriverManager.getConnection(url,config.toProperties());
            //if (conn == null){
            //System.out.println("conn failed");
            //}
            //System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                GUIDriver.LOGGER.log(Level.SEVERE, ex.getMessage());
            }
        }
        return conn;
    }

}