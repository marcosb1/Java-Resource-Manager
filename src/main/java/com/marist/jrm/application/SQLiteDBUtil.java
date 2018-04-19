package com.marist.jrm.application;

import org.sqlite.SQLiteConfig;

import java.sql.*;

/**
 * Created by Dominic Rossillo on 4/14/2018.
 */
public class SQLiteDBUtil {


    public static int insertThread(int threadProcID, int threadMemory) {



        String sql = "INSERT INTO THREAD(threadProcID,threadMemory) VALUES(?,?)";
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db", config.toProperties());


            PreparedStatement sqlStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Generated Key = "+sqlStatement.getGeneratedKeys().getLong(1));
            //sqlStatement.setInt(1,threadID);
            sqlStatement.setInt(1, threadProcID);
            sqlStatement.setInt(2, threadMemory);
            sqlStatement.executeUpdate();

            sql = "SELECT last_insert_rowid()";
            ResultSet rs= conn.createStatement().executeQuery(sql);
            rs.next();

            //System.out.println("ID ="+rs.getInt(1));
            return rs.getInt(1);
//            sqlStatement.close();
//
//
//            conn.close();
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                System.out.println("Attempt to add null process reference stopped!");
            } else {
                System.out.println(e.getMessage());
            }

        }

        return -1;
    }

    public static int insertApplication(String appName, String appDescription, int appSysID) {


        String sql = "INSERT INTO APPLICATION(appName,appDescription,appSysID) VALUES(?,?,?)";
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db", config.toProperties());


            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            //sqlStatement.setInt(1,appID);
            sqlStatement.setString(1, appName);
            sqlStatement.setString(2, appDescription);
            sqlStatement.setInt(3, appSysID);
            sqlStatement.executeUpdate();

            sql = "SELECT last_insert_rowid()";
            ResultSet rs= conn.createStatement().executeQuery(sql);
            rs.next();

            System.out.println("ID ="+rs.getInt(1));
            return rs.getInt(1);
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                System.out.println("Attempt to add null System reference stopped!");
            } else {
                System.out.println(e.getMessage());
            }
        }
        return -1;
    }

    public static int insertProcess(int procAppId, int procMemory, int procThreadCount) {


        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db", config.toProperties());
            String sql = "INSERT INTO PROCESS(procAppId,procMemory,procThreadCount) VALUES(?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            //sqlStatement.setInt(1,procID);
            sqlStatement.setInt(1, procAppId);
            sqlStatement.setInt(2, procMemory);
            sqlStatement.setInt(3, procThreadCount);
            sqlStatement.executeUpdate();

            sql = "SELECT last_insert_rowid()";
            ResultSet rs= conn.createStatement().executeQuery(sql);
            rs.next();

            System.out.println("ID ="+rs.getInt(1));
            return rs.getInt(1);
//            sqlStatement.close();
//            conn.close();



        } catch (SQLException e) {

            if (e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                System.out.println("Attempt to add null Application reference stopped!");
            } else {
                System.out.println(e.getMessage());
            }
        }
        return -1;

    }

    public static int insertSystem(int sysTime, int sysCPUUsage, int sysUpTime, int sysPhysicalMemory, int sysFreeMemory, int sysTotalThreads, int sysTotalProcesses) {

        try {

            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db", config.toProperties());
            String sql = "INSERT INTO SYSTEM(sysTime,sysCPUUsage,sysUpTime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses) VALUES(?,?,?,?,?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);



            sqlStatement.setInt(1, sysTime);
            sqlStatement.setInt(2, sysCPUUsage);
            sqlStatement.setInt(3, sysUpTime);
            sqlStatement.setInt(4, sysPhysicalMemory);
            sqlStatement.setInt(5, sysFreeMemory);
            sqlStatement.setInt(1, sysTime);
            sqlStatement.setInt(2, sysCPUUsage);
            sqlStatement.setInt(3, sysUpTime);
            sqlStatement.setInt(4, sysPhysicalMemory);
            sqlStatement.setInt(5, sysFreeMemory);
            sqlStatement.setInt(6, sysTotalThreads);
            sqlStatement.setInt(7, sysTotalProcesses);
            sqlStatement.executeUpdate();

            sql = "SELECT last_insert_rowid()";
            ResultSet rs= conn.createStatement().executeQuery(sql);
            rs.next();

            System.out.println("ID ="+rs.getInt(1));
            return rs.getInt(1);
//            sqlStatement.close();
//            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }


}