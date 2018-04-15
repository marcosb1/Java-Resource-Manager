package com.marist.jrm.application;

import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.AssertJUnit.assertEquals;


/**
 * Created by Dominic Rossillo on 4/14/2018.
 */
public class SQLiteDBUtilTest {
    boolean debug= true;
    @Test
    public void testInsertThread() {
        SQLiteDBUtil util=new SQLiteDBUtil();


        SQLiteDBInit.initDB();

        int threadProcID=-1;
        int threadMemory=10;
        util.insertApplication("DEBUGapp","app for debuging");
        util.insertProcess(-1,1000,2);
        util.insertThread(threadProcID,threadMemory);

        String sql = "SELECT threadID,threadProcID,threadMemory FROM THREAD";

        try {
            Connection conn= DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");

            ResultSet rs= conn.createStatement().executeQuery(sql);


            assertEquals(-1,rs.getInt("threadID") );
            assertEquals(threadProcID,rs.getInt("threadProcID") );
            assertEquals(threadMemory,rs.getInt("threadMemory") );


            while (rs.next()) {
                if(debug) {
                    System.out.println(rs.getInt("threadID") + "\t" +
                            rs.getString("threadProcID") + "\t" +
                            rs.getString("threadMemory") + "\t");
                }
            }
            rs.close();

            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }


    }

    @Test
    public void testThreadProcessIntegrity() {
        SQLiteDBUtil util=new SQLiteDBUtil();


        SQLiteDBInit.initDB();

        int threadProcID=0;
        int threadMemory=10;
        util.insertApplication("DEBUGapp", "app for debuging");
        util.insertThread(threadProcID, threadMemory);
        //util.insertProcess(0,1000,2);


    }

    @Test
    public void testInsertApplication() {

        SQLiteDBInit.initDB();
        SQLiteDBUtil util=new SQLiteDBUtil();

        String testAppName="DEBUGapp";
        String testAppDesc="app for debuging";
        System.out.println("inserting app");
        util.insertApplication(testAppName,testAppDesc);

        String sql = "SELECT appID,appName,appDescription FROM APPLICATION";

        System.out.println("enter try");
        try {
            Connection conn= DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");

            ResultSet rs= conn.createStatement().executeQuery(sql);

            assertEquals(testAppName,rs.getString("appName") );
            assertEquals(testAppDesc,rs.getString("appDescription") );
            while(rs.next()){
                if(debug) {
                    System.out.println(rs.getInt("appID") + "\t" +
                            rs.getString("appName") + "\t" +
                            rs.getString("appDescription") + "\t");
                }
            }

        } catch (SQLException e) {
           // e.printStackTrace();
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    @Test
    public void testInsertProcess() {
        SQLiteDBUtil util=new SQLiteDBUtil();
        SQLiteDBInit.initDB();

        util.insertApplication("DEBUGapp","app for debuging");
        int procAppID=-1;
        int procMemory=1000;
        int procThreadCount=2;
        util.insertProcess(procAppID,procMemory,procThreadCount);

        try {
            String sql = "SELECT procID,procAppID,procMemory,procThreadCount FROM PROCESS";
            Connection conn= DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");
            //Statement stmt = conn.createStatement();
            ResultSet rs= conn.createStatement().executeQuery(sql);

            assertEquals(procAppID,rs.getInt("procAppID") );
            assertEquals(procMemory,rs.getInt("procMemory") );
            assertEquals(procThreadCount,rs.getInt("procThreadCount") );
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
    @Test
    public void testProcessApplicationIntegrity() {
        SQLiteDBUtil util=new SQLiteDBUtil();


        SQLiteDBInit.initDB();

        int procAppID=0;
        int procMemory=1000;
        int procThreadCount=2;
        util.insertProcess(procAppID,procMemory,procThreadCount);
        //util.insertProcess(0,1000,2);


    }

    @Test
    public void testInsertSystem() {
        SQLiteDBUtil util=new SQLiteDBUtil();
        SQLiteDBInit.initDB();
        int sysTime = 00000000;
        int sysCPUUsage= 1;
        int sysUptime= 0;
        int sysPhysicalMemory= 6400000;
        int sysFreeMemory= 6399999;
        int sysTotalThreads=0;
        int sysTotalProcesses= 0;
        util.insertSystem(sysTime,sysCPUUsage,sysUptime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses);

        String sql = "SELECT sysTime,sysCPUUsage,sysUptime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses FROM SYSTEM";

        try {
            Connection conn= DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");

            ResultSet rs= conn.createStatement().executeQuery(sql);
            assertEquals(sysTime,rs.getInt("sysTime") );
            assertEquals(sysCPUUsage,rs.getInt("sysCPUUsage") );
            assertEquals(sysUptime,rs.getInt("sysUptime") );
            assertEquals(sysPhysicalMemory,rs.getInt("sysPhysicalMemory") );
            assertEquals(sysFreeMemory,rs.getInt("sysFreeMemory") );
            assertEquals(sysTotalThreads,rs.getInt("sysTotalThreads") );
            assertEquals(sysTotalProcesses,rs.getInt("sysTotalProcesses") );
            while(rs.next()){
                System.out.println(rs.getInt("sysTime")+"\t"+
                        rs.getInt("sysCPUUsage")+"\t"+
                        rs.getInt("sysUptime")+"\t"+
                        rs.getInt("sysPhysicalMemory")+"\t"+
                        rs.getInt("sysFreeMemory")+"\t"+
                        rs.getInt("sysTotalThreads")+"\t"+
                        rs.getInt("sysTotalProcesses")+"\t");
            }
        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
