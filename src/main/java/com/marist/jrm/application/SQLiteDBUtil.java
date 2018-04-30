package com.marist.jrm.application;

import com.marist.jrm.client.GUIDriver;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.logging.Level;

/**
 * class consisting of utility functions to allow for the insertion of records into the database
 * @author Dominic Rossillo
 * @since 4/14/2018
 */
public class SQLiteDBUtil {
    private static String url = "jdbc:sqlite:src/resources/jrmDB.db";
    //function used to insert a Thread into the thread table taking in the Process ID of the parent process and the memory being utilized by the thread
    //returns an Integer representing the id of the thread created
    public static int insertThread(int threadProcID, int threadMemory)throws SQLException {
        //Create sql string to insert into the table
        String sql = "INSERT INTO THREAD(threadProcID,threadMemory) VALUES(?,?)";
        try {
            //Create Sql Config object
            SQLiteConfig config = new SQLiteConfig();

            //set enforce foreign keys to true for the config
            config.enforceForeignKeys(true);
            SQLiteJDBCDriverConnection driver =  new SQLiteJDBCDriverConnection();

            //create connection to database using the path the the jrmDB.db file and the config object set to  properties object
            Connection conn = DriverManager.getConnection(url, config.toProperties());

            //create a prepared statement using the sql insert string
            PreparedStatement sqlStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //set the values of the ? values in the sql statement
            sqlStatement.setInt(1, threadProcID);
            sqlStatement.setInt(2, threadMemory);

            //execute insert
            sqlStatement.executeUpdate();

            //create sql string to find the id of thread inserted
            sql = "SELECT last_insert_rowid()";

            //execute query
            ResultSet rs = conn.createStatement().executeQuery(sql);
            rs.next();

            //return the id of the thread created
            return rs.getInt(1);

        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                GUIDriver.LOGGER.log(Level.SEVERE, "Attempt to add null process reference stopped!");
            } else {
                GUIDriver.LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;

        }

    }

    //function used to insert a Application into the Application table taking in the Applications name, the applications description , and the  System ID of the parent System
    //returns an Integer representing the id of the Application created
    public static int insertApplication(String appName, String appStatus, int appSysID) throws SQLException {

        //Create sql string to insert into the table
        String sql = "INSERT INTO APPLICATION(appName,appStatus,appSysID) VALUES(?,?,?)";

        try {
            //Create Sql Config object
            SQLiteConfig config = new SQLiteConfig();

            //set enforce foreign keys to true for the config
            config.enforceForeignKeys(true);

            //create connection to database using the path the the jrmDB.db file and the config object set to  properties object
            Connection conn = DriverManager.getConnection(url, config.toProperties());

            //create a prepared statement using the sql insert string
            PreparedStatement sqlStatement = conn.prepareStatement(sql);

            //set the values of the ? values in the sql statement
            sqlStatement.setString(1, appName);
            sqlStatement.setString(2, appStatus);
            sqlStatement.setInt(3, appSysID);

            //execute insert
            sqlStatement.executeUpdate();

            //create sql string to find the id of Application inserted
            sql = "SELECT last_insert_rowid()";

            //execute query
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();

            //return the id of the Application created
            return rs.getInt(1);

        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                GUIDriver.LOGGER.log(Level.SEVERE, "Attempt to add null System reference stopped!");
            } else {
                GUIDriver.LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        }

    }

    //function used to insert a Process into the Process table taking in the Application ID of the parent Application, the memory the process is utilizing, and the number of threads the process contains
    //returns an Integer representing the id of the Process created
    public static int insertProcess(int procAppId, double procMemory, int procThreadCount, String procDesc, String procState) throws SQLException {


        try {
            //Create Sql Config object
            SQLiteConfig config = new SQLiteConfig();

            //set enforce foreign keys to true for the config
            config.enforceForeignKeys(true);

            //create connection to database using the path the the jrmDB.db file and the config object set to  properties object
            Connection conn = DriverManager.getConnection(url, config.toProperties());

            //Create sql string to insert into the table
            String sql = "INSERT INTO PROCESS(procAppId,procMemory,procThreadCount,procDesc,procState) VALUES(?,?,?,?,?)";

            //create a prepared statement using the sql insert string
            PreparedStatement sqlStatement = conn.prepareStatement(sql);

            //set the values of the ? values in the sql statement
            sqlStatement.setInt(1, procAppId);
            sqlStatement.setDouble(2, procMemory);
            sqlStatement.setInt(3, procThreadCount);
            sqlStatement.setString(4, procDesc);
            sqlStatement.setString(5, procState);

            //execute insert
            sqlStatement.executeUpdate();

            //create sql string to find the id of Application inserted
            sql = "SELECT last_insert_rowid()";

            //execute query
            ResultSet rs = conn.createStatement().executeQuery(sql);
            rs.next();

            //return the id of the Process created
            return rs.getInt(1);
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                GUIDriver.LOGGER.log(Level.SEVERE, "Attempt to add null Application reference stopped!");
            } else {
                GUIDriver.LOGGER.log(Level.SEVERE, e.getMessage());
            }

            throw e;
        }


    }

    //function used to insert a System into the System table taking in the current system time, syscpuusage, the system uptime, total physical memory, free memory, total number of threads and processes
    //returns an Integer representing the id of the System state created
    public static int insertSystem(double sysTime, double sysCPUUsage, double sysUpTime, double sysPhysicalMemory, double sysFreeMemory, int sysTotalThreads, int sysTotalProcesses) throws SQLException {

        try {
            //Create Sql Config object
            SQLiteConfig config = new SQLiteConfig();

            //set enforce foreign keys to true for the config
            config.enforceForeignKeys(true);

            //create connection to database using the path the the jrmDB.db file and the config object set to  properties object
            Connection conn = DriverManager.getConnection(url, config.toProperties());

            //Create sql string to insert into the table
            String sql = "INSERT INTO SYSTEM(sysTime,sysCPUUsage,sysUpTime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses) VALUES(?,?,?,?,?,?,?)";

            //create a prepared statement using the sql insert string
            PreparedStatement sqlStatement = conn.prepareStatement(sql);

            //set the values of the ? values in the sql statement

            sqlStatement.setDouble(1, sysTime);
            sqlStatement.setDouble(2, sysCPUUsage);
            sqlStatement.setDouble(3, sysUpTime);
            sqlStatement.setDouble(4, sysPhysicalMemory);
            sqlStatement.setDouble(5, sysFreeMemory);
            sqlStatement.setInt(6, sysTotalThreads);
            sqlStatement.setInt(7, sysTotalProcesses);

            //execute insert
            sqlStatement.executeUpdate();

            //create sql string to find the id of System state inserted
            sql = "SELECT last_insert_rowid()";

            //execute query
            ResultSet rs = conn.createStatement().executeQuery(sql);
            rs.next();

            //return the id of the System state created
            return rs.getInt(1);


        } catch (SQLException e) {
            GUIDriver.LOGGER.log(Level.SEVERE, e.getMessage());
            throw e;
        }

    }


}