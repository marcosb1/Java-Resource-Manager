package com.marist.jrm.systemCall;

import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import com.marist.jrm.model.Process;

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

  public static ArrayList<Process> getProcesses(OperatingSystem os, GlobalMemory memory) {
    System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
    // Sort by highest CPU
    List<OSProcess> OSprocs = Arrays.asList(os.getProcesses(0, OperatingSystem.ProcessSort.CPU));
    ArrayList<Process> procs = new ArrayList<>();

    System.out.println("   PID  %CPU %MEM       VSZ       RSS Name");
    for (int i = 0; i < OSprocs.size() && i < 50; i++) {
      OSProcess p = OSprocs.get(i);
      OSProcess.State state = p.getState();
      Process process = new Process(p.getName(),String.valueOf(100d * p.getResidentSetSize() / memory.getTotal()),String.valueOf(p.getThreadCount()),"desc",state);
      procs.add(process);
      System.out.format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
        100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
        100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
        FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
    }

    return procs;
  }

  // TODO: create buildApplication functions which will take in a ArrayList<Process>


  public static void printBasicInfo(final ComputerSystem computerSystem) {
    System.out.println("manufacturer: " + computerSystem.getManufacturer());
    System.out.println("model: " + computerSystem.getModel());
    System.out.println("serialnumber: " + computerSystem.getSerialNumber());
  }


}
