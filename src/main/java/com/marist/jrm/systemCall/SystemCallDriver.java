package com.marist.jrm.systemCall;

import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import com.marist.jrm.model.ProcessModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemCallDriver {

  public static void main(String[] args) {

    SystemInfo si = new oshi.SystemInfo();

    HardwareAbstractionLayer hal = si.getHardware();
    OperatingSystem os = si.getOperatingSystem();

    printBasicInfo(hal.getComputerSystem());
    getProcesses(os, hal.getMemory());
  }

  public static ArrayList<ProcessModel> getProcesses(OperatingSystem os, GlobalMemory memory) {
    // Sort by highest CPU
    List<OSProcess> OSprocs = Arrays.asList(os.getProcesses(0, OperatingSystem.ProcessSort.CPU));
    ArrayList<ProcessModel> procs = new ArrayList<>();

    for (int i = 0; i < OSprocs.size() && i < 50; i++) {
      OSProcess p = OSprocs.get(i);
      OSProcess.State state = p.getState();
      ProcessModel process = new ProcessModel(p.getName(),
              String.valueOf(100d * p.getResidentSetSize() / memory.getTotal()),
              String.valueOf(p.getThreadCount()),
              "desc",
              state,
              new ArrayList<Integer>());
      procs.add(process);
    }

    return procs;
  }


  public static void printBasicInfo(final ComputerSystem computerSystem) {
    System.out.println("manufacturer: " + computerSystem.getManufacturer());
    System.out.println("model: " + computerSystem.getModel());
    System.out.println("serialnumber: " + computerSystem.getSerialNumber());
  }

  public static int[] getCPUUsage(OperatingSystem os, GlobalMemory memory) {
    // TODO:
    return null;
  }

  public static long[] getMemoryUsage(OperatingSystem os, GlobalMemory memory) {
    long time = System.currentTimeMillis();
    long memUsed = (memory.getTotal() - memory.getAvailable()) / 1073741824;
    return new long[] { time, memUsed };
  }
}
