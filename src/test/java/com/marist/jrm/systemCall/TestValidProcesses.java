package com.marist.jrm.systemCall;

import com.marist.jrm.model.ProcessModel;
import org.junit.Test;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class TestValidProcesses {

  // System Call initializer data
  private SystemInfo si = new SystemInfo();
  private HardwareAbstractionLayer hal = si.getHardware();
  private OperatingSystem os = si.getOperatingSystem();
  ArrayList<ProcessModel> processes = SystemCallDriver.getProcesses(os, hal.getMemory());

  /**
   * Tests to make sure no invalid processes are being entered
   * All valid processes have at least one thread, a name,
   * and use some ram.
   */
  @Test
  public void testNonEmptyProcesses() {

    for (ProcessModel p : processes) {
      assertTrue(Integer.parseInt(p.getThreadCount()) > 0);
      assertTrue(!p.getProcessName().isEmpty());
      assertTrue(Double.parseDouble(p.getMemory().substring(0,6)) > 0.0);
    }

  }

  /**
   * Tests for zombie processes. If a zombie process is found, the enum of that process
   * is set to STOPPED, where it will then be discarded.
   * @throws AssertionError
   */
  @Test
  public void testForZombies() throws AssertionError {
    ProcessModel mostRecentProc = null;
    try {
      for (ProcessModel p : processes) {
        mostRecentProc = p;
        assertFalse(p.getState() == OSProcess.State.ZOMBIE);
      }
    } catch (AssertionError e) {
      assertTrue(false);
      mostRecentProc.setState(OSProcess.State.STOPPED);
    }


  } 

}