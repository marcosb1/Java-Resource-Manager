package com.marist.jrm.systemCall;

import com.marist.jrm.model.ProcessModel;
import org.junit.Test;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;



public class TestThreads {

  // System Call initializer data
  private SystemInfo si = new SystemInfo();
  private HardwareAbstractionLayer hal = si.getHardware();
  private OperatingSystem os = si.getOperatingSystem();


  /** testLaunch
   * This will test the launching of a new process. The process will be named java.exe.
   * @throws InterruptedException
   */
  @Test
  public void testLaunch() throws InterruptedException, IOException {
    GenerateProcess gp = new GenerateProcess();
    GenerateProcess.main(new String[0]);
    ArrayList<ProcessModel> processes = SystemCallDriver.getProcesses(this.os, this.hal.getMemory());
    for (ProcessModel p : processes) {
      if (p.getProcessName().equals("java.exe")) {
        assertTrue(true);
        System.out.println("number of threads: " + p.getThreadCount());
      }
      if (p.getThreadCount().equals("2")) {
        assertTrue(true);
      }
    }

  }

}
