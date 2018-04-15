package com.marist.jrm.application;

import java.sql.*;

/**
 * Created by Dominic Rossillo on 4/14/2018.
 */
public class SQLiteDBUtilTest {
    SQLiteDBUtil util=new SQLiteDBUtil();

    public void testInsertThread() {
        Connection conn= SQLiteJDBCDriverConnection.connect();
        SQLiteDBInit.initDB();
        util.insertApplication("DEBUGapp","app for debuging");
        util.insertProcess(0,1000,2);
        util.insertThread(0,10);
        String sql = "SELECT threadID,threadProcID,threadMemory FROM APPLICATION";
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getInt("threadID")+"\t"+
                        rs.getString("threadProcID")+"\t"+
                        rs.getString("threadMemory")+"\t");
            }
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void testInsertApplication() {
        Connection conn= SQLiteJDBCDriverConnection.connect();
        SQLiteDBInit.initDB();
        util.insertApplication("DEBUGapp","app for debuging");

        String sql = "SELECT appID,appName,appDescription FROM APPLICATION";
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getInt("appID")+"\t"+
                        rs.getString("appName")+"\t"+
                        rs.getString("appDescription")+"\t");
            }
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void testInsertProcess() {
        Connection conn= SQLiteJDBCDriverConnection.connect();
        SQLiteDBInit.initDB();

        util.insertApplication("DEBUGapp","app for debuging");

        util.insertProcess(0,1000,2);

        try {
            String sql = "SELECT procID,procAppID,procMemory,procThreadCount FROM PROCESS";
            Statement stmt  = conn.createStatement();
            ResultSet rs= null;
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getInt("procID")+"\t"+
                        rs.getInt("procAppID")+"\t"+
                        rs.getInt("procMemory")+"\t"+
                        rs.getInt("procThreadCount")+"\t");
            }
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

    }

    public void testInsertSystem() {
        Connection conn= SQLiteJDBCDriverConnection.connect();
        SQLiteDBInit.initDB();
        util.insertSystem(00000000,1,0,6400000,6399999,0,0);

        String sql = "SELECT sysTime,sysCPUUsage,sysUptime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcess FROM SYSTEM";
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getInt("sysTime")+"\t"+
                        rs.getInt("sysCPUUsage")+"\t"+
                        rs.getInt("sysUptime")+"\t"+
                        rs.getInt("sysPhysicalMemory")+"\t"+
                        rs.getInt("sysFreeMemory")+"\t"+
                        rs.getInt("sysTotalThreads")+"\t"+
                        rs.getInt("sysTotalProcess")+"\t");
            }
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
