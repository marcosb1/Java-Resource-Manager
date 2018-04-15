package com.marist.jrm.application;
import java.sql.*;


/**
 * Created by Dominic Rossillo on 3/25/2018.
 */
public class SQLiteDBInit {

    public static void initDB() {
        boolean debug=true;
        Connection c = null;
        DatabaseMetaData metaData = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:src/resources/jrmDB.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            metaData= c.getMetaData();
            String sql = "DROP TABLE IF EXISTS APPLICATION";
            stmt.executeUpdate(sql);

            sql =
            "CREATE TABLE APPLICATION("+
                   " appID INT PRIMARY KEY     NOT NULL,"+
                   " appName           TEXT    NOT NULL,"+
                   " appDescription    TEXT     NOT NULL)";
            stmt.executeUpdate(sql);


            if(debug){
                sql = "INSERT INTO APPLICATION(appID,appName,appDescription) VALUES(?,?,?)";
                PreparedStatement debugSqlStatement = c.prepareStatement(sql);
                debugSqlStatement.setInt(1,0);
                debugSqlStatement.setString(2,"DEBUGapp");
                debugSqlStatement.setString(3,"app for debuging");
                debugSqlStatement.executeUpdate();


                sql = "SELECT appID,appName,appDescription FROM APPLICATION";
                stmt  = c.createStatement();
                ResultSet rs= stmt.executeQuery(sql);
                while(rs.next()){
                    System.out.println(rs.getInt("appID")+"\t"+
                                       rs.getString("appName")+"\t"+
                                       rs.getString("appDescription")+"\t");
                }
            }





            sql = "DROP TABLE IF EXISTS PROCESS";
            stmt.executeUpdate(sql);

            sql =
            "CREATE TABLE PROCESS("+
                    "procID INT PRIMARY KEY     NOT NULL,"+
                    "procAppID           INT    NOT NULL,"+
                    "procMemory    INT     NOT NULL,"+
                    "procThreadCount INT     NOT NULL,"+
                    "FOREIGN KEY(procAppID) REFERENCES APPLICATION(appID))";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS THREAD";
            stmt.executeUpdate(sql);
            sql =
            "CREATE TABLE THREAD("+
                    "threadID INT PRIMARY KEY     NOT NULL,"+
                    "threadProcID           INT    NOT NULL,"+
                    "threadMemory    INT     NOT NULL,"+
                    "FOREIGN KEY(threadProcID) REFERENCES PROCESS(procID))";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS SYSTEM";
            stmt.executeUpdate(sql);
            sql=
            "CREATE TABLE SYSTEM("+
                    "sysTime INT PRIMARY KEY     NOT NULL,"+
                    "sysCPUUsage           INT    NOT NULL,"+
                    "sysUptime    INT     NOT NULL,"+
                    "sysPhyicalMemory INT     NOT NULL,"+
                    "sysFreeMemory INT     NOT NULL,"+
                    "sysTotalThreads INT     NOT NULL,"+
                    "sysTotalProcesses INT     NOT NULL)";
            stmt.executeUpdate(sql);


            System.out.println("Tables Created:");
            ResultSet rs = metaData.getTables(null, null, "%", null);
            while (rs.next()) {
                System.out.println(rs.getString(3));
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Tables created successfully");
    }
}
