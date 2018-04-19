package com.marist.jrm.application;
import java.sql.*;


/**
 * Created by Dominic Rossillo on 3/25/2018.
 */
public class SQLiteDBInit {

    public static void initDB() {

        Connection c = null;
        DatabaseMetaData metaData = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");
           // System.out.println("Opened database successfully");

            stmt = c.createStatement();
            metaData= c.getMetaData();
            String sql = "DROP TABLE IF EXISTS SYSTEM";
            stmt.executeUpdate(sql);
            sql="CREATE TABLE SYSTEM("+
                            "sysID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            "sysTime INTEGER     NOT NULL,"+
                            "sysCPUUsage           INTEGER    NOT NULL,"+
                            "sysUptime    INTEGER     NOT NULL,"+
                            "sysPhysicalMemory INTEGER     NOT NULL,"+
                            "sysFreeMemory INTEGER     NOT NULL,"+
                            "sysTotalThreads INTEGER     NOT NULL,"+
                            "sysTotalProcesses INTEGER     NOT NULL)";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS APPLICATION";
            stmt.executeUpdate(sql);
//            sql= "PRAGMA foreign_keys = ON";
//            stmt.executeUpdate(sql);
            sql =
            "CREATE TABLE APPLICATION("+
                    "appID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "appName           TEXT     NOT NULL,"+
                    "appDescription    TEXT     NOT NULL,"+
                    "appSysID   INTEGER             NOT NULL,"+
                    "FOREIGN KEY(appSysID) REFERENCES SYSTEM(sysID))";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS PROCESS";
            stmt.executeUpdate(sql);

            sql =
            "CREATE TABLE PROCESS("+
                    "procID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "procAppID           INTEGER    NOT NULL,"+
                    "procMemory    INTEGER     NOT NULL,"+
                    "procThreadCount INTEGER     NOT NULL,"+
                    "FOREIGN KEY(procAppID) REFERENCES APPLICATION(appID))";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS THREAD";
            stmt.executeUpdate(sql);
            sql =
            "CREATE TABLE THREAD("+
                    "threadID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "threadProcID INTEGER NOT NULL,"+
                    "threadMemory INTEGER NOT NULL,"+
                    "FOREIGN KEY(threadProcID) REFERENCES PROCESS(procID))";
            stmt.executeUpdate(sql);



            //System.out.println("Tables Created:");
            ResultSet rs = metaData.getTables(null, null, "%", null);
            while (rs.next()) {
                //System.out.println(rs.getString(3));
            }
//            stmt.close();
//            c.close();
            //System.out.println("Tables created successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

    }
}
