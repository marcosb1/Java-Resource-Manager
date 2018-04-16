package com.marist.jrm.systemCall;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.lang.Thread;

public class GenerateProcess {

  ProcessBuilder pb = new ProcessBuilder("echo", "hellothere");
  Process testProcess;

  public static void main(String[] args) {
    System.out.println("Main Thread");
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        System.out.println("Inner Thread");
      }
    });
    thread.start();
    try {
      thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates the process and its file directory
   * @throws IOException
   */
  public GenerateProcess() throws IOException {

    Map<String, String> env = pb.environment();
    pb.directory(new File("seperateProcessDir"));
    File log = new File("log");
    pb.redirectErrorStream(true);
    pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
    testProcess = pb.start();
    assert pb.redirectOutput().file() == log;
  }
}
