package com.marist.jrm.systemCall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.util.Arrays;
import java.util.List;

public class SystemCallDriver {

  public static void main(String[] args) {

    SystemInfo si = new oshi.SystemInfo();

    HardwareAbstractionLayer hal = si.getHardware();
    OperatingSystem os = si.getOperatingSystem();

    printBasicInfo(hal.getComputerSystem());
    printProcesses(os, hal.getMemory());

  }

  private static void printProcesses(OperatingSystem os, GlobalMemory memory) {
    System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
    // Sort by highest CPU
    List<OSProcess> procs = Arrays.asList(os.getProcesses(5, OperatingSystem.ProcessSort.CPU));

    System.out.println("   PID  %CPU %MEM       VSZ       RSS Name");
    for (int i = 0; i < procs.size() && i < 5; i++) {
      OSProcess p = procs.get(i);
      System.out.format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
        100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
        100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
        FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
    }
  }


  public static void printBasicInfo(final ComputerSystem computerSystem) {
    System.out.println("manufacturer: " + computerSystem.getManufacturer());
    System.out.println("model: " + computerSystem.getModel());
    System.out.println("serialnumber: " + computerSystem.getSerialNumber());
  }


}
