package com.marist.jrm.application;

public class ApplicationDriver {
  
    public static void main(String Args[]){
        SQLiteJDBCDriverConnection.connect();
        SQLiteDBInit.initDB();
    }
  
}
