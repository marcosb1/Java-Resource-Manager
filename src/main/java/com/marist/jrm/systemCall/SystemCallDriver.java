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

  /**
   * Method to calculate the memory usage per individual thread.
   * This method uses the java.lang.management.* thread classes
   * and calculates the CPU usage per thread by getting the CPU usage
   * time per thread and dividing it by cpu uptime.
   *
   * This method will output data that will eventually be put on a graph
   * that and will be easy to read.
   */
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

  /**
   * Method to obtain all processes currently running on the machine.
   * For each process running, a ProcessModel object is constructed and added
   * to the return ArrayList. Also, metrics regarding the top 5 cpu-usage-heavy
   * processes are printed out.
   * @param os OperatingSystem
   * @param memory GlobalMemory
   * @return ArrayList of ProcessModel objects
   */
  public static ArrayList<ProcessModel> getProcesses(OperatingSystem os, GlobalMemory memory) {
    System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
    // Sort by highest CPU
    // To only gather x amount of processes, change
    // the first parameter of os.getProcesses from 0 to x.
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

  // TODO: create buildApplication functions which will take in a ArrayList<Process>


  /**
   * Simple method to print some basic info regarding the system.
   * @param computerSystem
   */
  public static void printBasicInfo(final ComputerSystem computerSystem) {
    System.out.println("manufacturer: " + computerSystem.getManufacturer());
    System.out.println("model: " + computerSystem.getModel());
    System.out.println("serialnumber: " + computerSystem.getSerialNumber());
  }

  /**
   * Method to obtain the total memory, memory available, and memory used.
   * Values (in gigabytes) are returned in an array of doubles in the following order:
   * [totalMemory,memoryUsed,memoryAvailable]
   * @param memory
   * @return
   */
  public static Double[] getMemoryMetrics(GlobalMemory memory) {
    Double[] memoryUsageVals = new Double[3];

    String memAvailString = FormatUtil.formatBytes(memory.getAvailable());
    memAvailString = memAvailString.substring(0,memAvailString.length()-3);

    String memTotalString = FormatUtil.formatBytes(memory.getTotal());
    memTotalString = memTotalString.substring(0,memTotalString.length()-3);



    Double memAvail = Double.parseDouble(memAvailString);
    Double memTotal = Double.parseDouble(memTotalString);
    Double memUsed = memTotal - memAvail;

    System.out.println("Memory: " + FormatUtil.formatBytes(memory.getAvailable()) + "/"
      + FormatUtil.formatBytes(memory.getTotal()));
    System.out.println("Available bytes: " + FormatUtil.formatBytes(memory.getSwapUsed()) + "/"
      + FormatUtil.formatBytes(memory.getSwapTotal()));
    System.out.println("Mem used: " + memUsed);

    memoryUsageVals[0] = memTotal;
    memoryUsageVals[1] = memUsed;
    memoryUsageVals[2] = memAvail;

    return memoryUsageVals;
  }

  public static long[] getMemoryUsage(OperatingSystem os, GlobalMemory memory) {
    long time = System.currentTimeMillis();
    long memUsed = (memory.getTotal() - memory.getAvailable()) / 1073741824;
    return new long[] { time, memUsed };
  }

}
