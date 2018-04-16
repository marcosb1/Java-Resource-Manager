package com.marist.jrm.application;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dominic Rossillo on 4/14/2018.
 */
public class SQLiteDBUtil {


    public static void insertThread(int threadProcID, int threadMemory) {
        int threadID=-1;


        String sql = "INSERT INTO THREAD(threadID,threadProcID,threadMemory) VALUES(?,?,?)";
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db",config.toProperties());


            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,threadID);
            sqlStatement.setInt(2,threadProcID);
            sqlStatement.setInt(3,threadMemory);
            sqlStatement.executeUpdate();
//            sqlStatement.close();
//
//
//            conn.close();
        }catch(SQLException e){
            if(e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                System.out.println("Attempt to add null process reference stopped!");
            }
            else{
                System.out.println(e.getMessage());
            }

        }


    }
    public static void insertApplication(String appName, String appDescription) {
        int appID=-1;
        String sql = "INSERT INTO APPLICATION(appID,appName,appDescription) VALUES(?,?,?)";
        try {

            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");


            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,appID);
            sqlStatement.setString(2,appName);
            sqlStatement.setString(3,appDescription);
            sqlStatement.executeUpdate();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
    public static void insertProcess(int procAppId, int procMemory,int procThreadCount) {

        int procID=-1;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db",config.toProperties());
            String sql = "INSERT INTO PROCESS(procID,procAppId,procMemory,procThreadCount) VALUES(?,?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,procID);
            sqlStatement.setInt(2,procAppId);
            sqlStatement.setInt(3,procMemory);
            sqlStatement.setInt(4,procThreadCount);
            sqlStatement.executeUpdate();
//            sqlStatement.close();
//            conn.close();

        }catch(SQLException e){

            if(e.getMessage().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) {
                System.out.println("Attempt to add null Application reference stopped!");
            }
            else{
                System.out.println(e.getMessage());
            }
        }

    }
    public static void insertSystem(int sysTime , int sysCPUUsage , int sysUpTime,int sysPhysicalMemory,int sysFreeMemory,int sysTotalThreads,int sysTotalProcesses) {

        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db",config.toProperties());
            String sql = "INSERT INTO SYSTEM(sysTime,sysCPUUsage,sysUpTime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses) VALUES(?,?,?,?,?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,sysTime);
            sqlStatement.setInt(2,sysCPUUsage);
            sqlStatement.setInt(3,sysUpTime);
            sqlStatement.setInt(4,sysPhysicalMemory);
            sqlStatement.setInt(5,sysFreeMemory);
            sqlStatement.setInt(6,sysTotalThreads);
            sqlStatement.setInt(7,sysTotalProcesses);
            sqlStatement.executeUpdate();
//            sqlStatement.close();
//            conn.close();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
