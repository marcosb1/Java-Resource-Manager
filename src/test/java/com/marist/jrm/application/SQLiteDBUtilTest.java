package com.marist.jrm.application;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;


/**
 * Created by Dominic Rossillo on 4/14/2018.
 */
public class SQLiteDBUtilTest {
    boolean debug= true;
    int sysID = 1;
    int sysTime = 00000000;
    int sysCPUUsage= 1;
    int sysUptime= 0;
    int sysPhysicalMemory= 6400000;
    int sysFreeMemory= 6399999;
    int sysTotalThreads=0;
    int sysTotalProcesses= 0;
    int threadProcID=1;
    int threadMemory=10;
    int procAppID=1;
    int procMemory=1000;
    int procThreadCount=2;
    private static String url = "jdbc:sqlite:src/resources/jrmDB.db";
    String testAppName="DEBUGapp";
    String testAppDesc="app for debuging";

    @Test
    public void testInsertThread() {
        System.out.println("testInsertThread :");
        SQLiteDBUtil util=new SQLiteDBUtil();


        SQLiteDBInit.initDB();

        //System.out.println("inserting app");



        try {
            util.insertSystem(sysTime,sysCPUUsage,sysUptime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses);
            util.insertApplication(testAppName,testAppDesc,sysID);
            util.insertProcess(1,1000,2);
            util.insertThread(threadProcID,threadMemory);

            String sql = "SELECT threadID,threadProcID,threadMemory FROM THREAD";
            Connection conn= DriverManager.getConnection(url);

            ResultSet rs= conn.createStatement().executeQuery(sql);



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
    public void testInsertApplication() {
        System.out.println("testInsertApplication :");
        SQLiteDBInit.initDB();
        SQLiteDBUtil util=new SQLiteDBUtil();






        //System.out.println("enter try");
        try {
            //System.out.println("inserting app");
            util.insertSystem(sysTime,sysCPUUsage,sysUptime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses);


            util.insertApplication(testAppName,testAppDesc,sysID);

            String sql = "SELECT appID,appName,appDescription FROM APPLICATION";
            Connection conn= DriverManager.getConnection(url);

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
        System.out.println("testInsertProcess :");
        SQLiteDBUtil util=new SQLiteDBUtil();
        SQLiteDBInit.initDB();


        try {
            util.insertSystem(sysTime,sysCPUUsage,sysUptime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses);
            util.insertApplication("DEBUGapp","app for debuging",sysID);

            util.insertProcess(procAppID,procMemory,procThreadCount);
            String sql = "SELECT procID,procAppID,procMemory,procThreadCount FROM PROCESS";
            Connection conn= DriverManager.getConnection(url);
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
    public void testInsertSystem() {
        SQLiteDBUtil util=new SQLiteDBUtil();
        SQLiteDBInit.initDB();
        System.out.println("testInsertSystem :");


        try {
            for(int i=0;i<2;i++) {
                util.insertSystem(sysTime, sysCPUUsage, sysUptime, sysPhysicalMemory, sysFreeMemory, sysTotalThreads, sysTotalProcesses);

            }

            String sql = "SELECT sysID,sysTime,sysCPUUsage,sysUptime,sysPhysicalMemory,sysFreeMemory,sysTotalThreads,sysTotalProcesses FROM SYSTEM";
            Connection conn= DriverManager.getConnection(url);

            ResultSet rs= conn.createStatement().executeQuery(sql);

            assertEquals(sysTime,rs.getInt("sysTime") );
            assertEquals(sysCPUUsage,rs.getInt("sysCPUUsage") );
            assertEquals(sysUptime,rs.getInt("sysUptime") );
            assertEquals(sysPhysicalMemory,rs.getInt("sysPhysicalMemory") );
            assertEquals(sysFreeMemory,rs.getInt("sysFreeMemory") );
            assertEquals(sysTotalThreads,rs.getInt("sysTotalThreads") );
            assertEquals(sysTotalProcesses,rs.getInt("sysTotalProcesses") );
            while(rs.next()){
                System.out.println(rs.getInt("sysID")+"\t"+
                        rs.getInt("sysTime")+"\t"+
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




    @Test
    public void testThreadProcessIntegrity() throws SQLException {
        System.out.println("testThreadProcessIntegrity :");
        SQLiteDBUtil util=new SQLiteDBUtil();



        SQLiteDBInit.initDB();



        //System.out.println("inserting app");
        try {
            util.insertSystem(sysTime, sysCPUUsage, sysUptime, sysPhysicalMemory, sysFreeMemory, sysTotalThreads, sysTotalProcesses);

            util.insertApplication("DEBUGapp", "app for debuging", sysID);
            util.insertThread(threadProcID, threadMemory);
        }
        catch (SQLException e){
            //System.err.println( e.getClass().getName() + ": " + e.getMessage() );

        }



        //util.insertProcess(0,1000,2);


    }

    @Test
    public void testProcessApplicationIntegrity() throws SQLException {
        System.out.println("Test testProcessApplicationIntegrity :");
        SQLiteDBUtil util=new SQLiteDBUtil();



        SQLiteDBInit.initDB();
        try {
            util.insertSystem(sysTime, sysCPUUsage, sysUptime, sysPhysicalMemory, sysFreeMemory, sysTotalThreads, sysTotalProcesses);
            util.insertProcess(procAppID, procMemory, procThreadCount);
        }
        catch (SQLException e){
            //System.err.println( e.getClass().getName() + ": " + e.getMessage() );

        }
        //fail(String.format("Expected %s, but no exception was thrown.", throwableClass.getSimpleName()));

        //util.insertProcess(0,1000,2);



    }


}
