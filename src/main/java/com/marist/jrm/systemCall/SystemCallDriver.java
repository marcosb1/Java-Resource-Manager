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




  public static void main(String[] args) {

    SystemInfo si = new oshi.SystemInfo();

    HardwareAbstractionLayer hal = si.getHardware();
    OperatingSystem os = si.getOperatingSystem();

    printBasicInfo(hal.getComputerSystem());
    getProcesses(os, hal.getMemory());

    calcMemoryUsagePerThread();


  }

  public static ArrayList<ProcessModel> getProcesses(OperatingSystem os, GlobalMemory memory) {
    System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
    // Sort by highest CPU
    List<OSProcess> OSprocs = Arrays.asList(os.getProcesses(0, OperatingSystem.ProcessSort.CPU));
    ArrayList<ProcessModel> procs = new ArrayList<>();

    System.out.println("   PID  %CPU %MEM       VSZ       RSS Name");
    for (int i = 0; i < OSprocs.size() && i < 5; i++) {
      OSProcess p = OSprocs.get(i);

      OSProcess.State state = p.getState();
      ProcessModel process = new ProcessModel(p.getName(),String.valueOf(100d * p.getResidentSetSize() / memory.getTotal()),String.valueOf(p.getThreadCount()),"desc",state,new ArrayList<Integer>());
      procs.add(process);
      System.out.format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
        100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
        100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
        FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
    }

    return procs;
  }


  public static void printBasicInfo(final ComputerSystem computerSystem) {
    System.out.println("manufacturer: " + computerSystem.getManufacturer());
    System.out.println("model: " + computerSystem.getModel());
    System.out.println("serialnumber: " + computerSystem.getSerialNumber());
  }


}
