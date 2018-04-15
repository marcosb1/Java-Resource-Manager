package com.marist.jrm.application;

public class ApplicationDriver {
    int heartBeatInteval=1;
    public static void main(String Args[]){
        //Connection conn= SQLiteJDBCDriverConnection.connect();
        SQLiteDBInit.initDB();
        SQLiteDBUtil util=new SQLiteDBUtil();
        //util.insertApplication("Dom test", "Dom desc");


    }
  
}
