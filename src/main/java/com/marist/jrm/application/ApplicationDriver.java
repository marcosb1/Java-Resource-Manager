package com.marist.jrm.application;

import java.sql.Connection;

public class ApplicationDriver {
    int heartBeatInteval=1;
    public static void main(String Args[]){
        Connection conn= SQLiteJDBCDriverConnection.connect();
        SQLiteDBInit.initDB();

    }
  
}
