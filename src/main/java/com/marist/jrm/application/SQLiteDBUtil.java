package com.marist.jrm.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dominic Rossillo on 4/14/2018.
 */
public class SQLiteDBUitl {


    public void insertThread(int threadProcID, int threadMemory) {
        int threadID=-1;
        try {
            Connection conn = SQLiteJDBCDriverConnection.connect() ;
            String sql = "INSERT INTO THREAD(threadID,threadProcID,threadMemory) VALUES(?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,threadID);
            sqlStatement.setInt(2,threadProcID);
            sqlStatement.setInt(3,threadMemory);
            sqlStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }


    }
    public void insertApplication(String appName, String appDescription) {
        int appID=-1;
        try {
            Connection conn = SQLiteJDBCDriverConnection.connect() ;
            String sql = "INSERT INTO APPLICATION(appID,appName,appDescription) VALUES(?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,appID);
            sqlStatement.setString(2,appName);
            sqlStatement.setString(3,appDescription);
            sqlStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
    public void insertProcess(int procAppId, int procMemory,int procThreadCount) {

        int procID=-1;
        try {
            Connection conn = SQLiteJDBCDriverConnection.connect() ;
            String sql = "INSERT INTO PROCESS(procID,procAppId,procMemory,procThreadCount) VALUES(?,?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,procID);
            sqlStatement.setInt(2,procAppId);
            sqlStatement.setInt(3,procMemory);
            sqlStatement.setInt(4,procThreadCount);
            sqlStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
    public void insertSystem(int sysTime , int sysCPUUsage , int sysUpTime,int sysPhysicalMemory,int sysFreeMemory,int sysTotalThreads,int sysTotalProcesses) {

        try {
            Connection conn = SQLiteJDBCDriverConnection.connect() ;
            String sql = "INSERT INTO PROCESS(procID,procAppId,procMemory,procThreadCount) VALUES(?,?,?,?,?,?,?)";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            sqlStatement.setInt(1,sysTime);
            sqlStatement.setInt(2,sysCPUUsage);
            sqlStatement.setInt(3,sysUpTime);
            sqlStatement.setInt(4,sysPhysicalMemory);
            sqlStatement.setInt(5,sysFreeMemory);
            sqlStatement.setInt(6,sysTotalThreads);
            sqlStatement.setInt(7,sysTotalProcesses);
            sqlStatement.executeUpdate();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
