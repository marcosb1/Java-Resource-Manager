package com.marist.jrm.systemCall;

import java.lang.management.*;

import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import com.marist.jrm.model.ProcessModel;

import java.util.*;

public class SystemCallDriver {

  public static void main(String[] args) {

    SystemInfo si = new oshi.SystemInfo();

    HardwareAbstractionLayer hal = si.getHardware();
    OperatingSystem os = si.getOperatingSystem();

    printBasicInfo(hal.getComputerSystem());
    //getProcesses(os, hal.getMemory());

    calcMemoryUsagePerThread();
  }

  private static void calcMemoryUsagePerThread() {
    int sampleTime = 10000;
    ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
    RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
    OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
    Map<Long, Long> threadInitialCPU = new HashMap<Long, Long>();
    Map<Long, Float> threadCPUUsage = new HashMap<Long, Float>();
    long initialUptime = runtimeMxBean.getUptime();

    ThreadInfo[] threadInfos = threadMxBean.dumpAllThreads(false, false);
    for (ThreadInfo info : threadInfos) {
      threadInitialCPU.put(info.getThreadId(), threadMxBean.getThreadCpuTime(info.getThreadId()));
    }

    try {Thread.sleep(sampleTime);} catch (InterruptedException e) {}

    long upTime = runtimeMxBean.getUptime();

    Map<Long, Long> threadCurrentCPU = new HashMap<Long, Long>();
    ThreadInfo[] threadInfos2 = threadMxBean.dumpAllThreads(false, false);
    for (ThreadInfo info : threadInfos2) {
      threadCurrentCPU.put(info.getThreadId(), threadMxBean.getThreadCpuTime(info.getThreadId()));
    }

    // CPU over all processes
    //int nrCPUs = osMxBean.getAvailableProcessors();
    // total CPU: CPU % can be more than 100% (devided over multiple cpus)
    long nrCPUs = 1;
    // elapsedTime is in ms.
    long elapsedTime = (upTime - initialUptime);
    for (ThreadInfo info : threadInfos) {
      // elapsedCpu is in ns
      Long initialCPU = threadInitialCPU.get(info.getThreadId());
      if (initialCPU != null) {
        long elapsedCpu = threadCurrentCPU.get(info.getThreadId()) - initialCPU;
        float cpuUsage = elapsedCpu / (elapsedTime * 1000000F * nrCPUs);
        threadCPUUsage.put(info.getThreadId(), cpuUsage);
      }
    }

 // threadCPUUsage contains cpu % per thread
    System.out.println(threadCPUUsage);
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

  // TODO: create buildApplication functions which will take in a ArrayList<Process>


  public static void printBasicInfo(final ComputerSystem computerSystem) {
    System.out.println("manufacturer: " + computerSystem.getManufacturer());
    System.out.println("model: " + computerSystem.getModel());
    System.out.println("serialnumber: " + computerSystem.getSerialNumber());
  }

  public static long[] getCPUUsage(OperatingSystem os, GlobalMemory memory) {
    long time = System.currentTimeMillis();
    long cpuUsed = 0;
    List<OSProcess> OSprocs = Arrays.asList(os.getProcesses(0, OperatingSystem.ProcessSort.CPU));
    long previousTime = 0;
    for (int i = 0; i < OSprocs.size() && i < 50; i++) {
      OSProcess p = OSprocs.get(i);
      long currentTime = p.getKernelTime() + p.getUserTime();

      if (previousTime != -1) {
        // If we have both a previous and a current time
        // we can calculate the CPU usage
        long timeDifference = currentTime - previousTime;
        cpuUsed = (100d * (timeDifference / ((double) 1000))) / cpuNumber;
      }

      previousTime = currentTime;
      cpuUsed += 100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime();
    }
    return new long[] { time, cpuUsed };
  }

  public static long[] getMemoryUsage(OperatingSystem os, GlobalMemory memory) {
    long time = System.currentTimeMillis();
    long memUsed = (memory.getTotal() - memory.getAvailable()) / 1073741824;
    return new long[] { time, memUsed };
  }
}
