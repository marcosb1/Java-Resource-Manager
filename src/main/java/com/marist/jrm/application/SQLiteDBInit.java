package com.marist.jrm.application;

import com.marist.jrm.client.GUIDriver;
import java.util.logging.Level;

import java.sql.*;


/**
 * Created by Dominic Rossillo on 3/25/2018.
 */
//class used to  handle the creation of database tables
public class SQLiteDBInit {

    //function that performs the creation of the database tables given pre written queries

    public static void initDB() {
        String url = "jdbc:sqlite:src/resources/jrmDB.db";
        //initialize  variables used during querying of database
        //Connection variable used to connect to the database
        Connection c = null;
        Statement stmt = null;

        try {
            //dynamically load sqlite JDBC driver
            Class.forName("org.sqlite.JDBC");
            //setting connection variable to the java-resource-manage db file in src/resources
            c = DriverManager.getConnection(url);
            // System.out.println("Opened database successfully");

            //initialize statement using the JDBC Driver connection
            stmt = c.createStatement();
            //create sql string used through out this function to feed to the statement
            //Dropping the table for system to ensure a empty table
            String sql = "DROP TABLE IF EXISTS SYSTEM";
            //pass the sql string into the database connection statement and execute
            stmt.executeUpdate(sql);
            //System table creation sql
            sql = "CREATE TABLE SYSTEM(" +
                    "sysID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "sysTime INTEGER     NOT NULL," +
                    "sysCPUUsage           INTEGER    NOT NULL," +
                    "sysUptime    INTEGER     NOT NULL," +
                    "sysPhysicalMemory INTEGER     NOT NULL," +
                    "sysFreeMemory INTEGER     NOT NULL," +
                    "sysTotalThreads INTEGER     NOT NULL," +
                    "sysTotalProcesses INTEGER     NOT NULL)";
            //execute system table creation
            stmt.executeUpdate(sql);
            //Drop application table to ensure it is empty
            sql = "DROP TABLE IF EXISTS APPLICATION";
            //execute drop
            stmt.executeUpdate(sql);
            //Application table creation sql
            sql =
                    "CREATE TABLE APPLICATION(" +
                            "appID INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "appName           TEXT     NOT NULL," +
                            "appStatus    TEXT     NOT NULL," +
                            "appSysID   INTEGER             NOT NULL," +
                            "FOREIGN KEY(appSysID) REFERENCES SYSTEM(sysID))";
            //execute application table creation
            stmt.executeUpdate(sql);
            //Drop PROCESS table to ensure it is empty
            sql = "DROP TABLE IF EXISTS PROCESS";
            //execute drop
            stmt.executeUpdate(sql);
            //Process table creation sql
            sql =
                    "CREATE TABLE PROCESS(" +
                            "procID INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "procAppID           INTEGER    NOT NULL," +
                            "procMemory    REAL      NOT NULL," +
                            "procThreadCount INTEGER     NOT NULL," +
                            "procDesc    TEXT     NOT NULL," +
                            "procState    TEXT     NOT NULL," +
                            "FOREIGN KEY(procAppID) REFERENCES APPLICATION(appID))";
            //execute Process table creation
            stmt.executeUpdate(sql);
            //Drop THREAD table to ensure it is empty
            sql = "DROP TABLE IF EXISTS THREAD";
            //execute drop
            stmt.executeUpdate(sql);
            //THREAD table creation sql
            sql =
                    "CREATE TABLE THREAD(" +
                            "threadID INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "threadProcID INTEGER NOT NULL," +
                            "threadMemory REAL  NOT NULL," +
                            "FOREIGN KEY(threadProcID) REFERENCES PROCESS(procID))";
            //execute Thread table creation
            stmt.executeUpdate(sql);

            //close connections
            stmt.close();
            c.close();
        } catch (Exception e) {
            GUIDriver.LOGGER.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }
}
