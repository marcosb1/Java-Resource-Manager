package com.marist.jrm.systemCall;

import java.lang.management.*;

import com.marist.jrm.model.ApplicationModel;
import com.marist.jrm.model.MemoryMetrics;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import com.marist.jrm.model.ProcessModel;

import java.util.*;

/**
 * Class to perform all system call utility functions using the OSHI library
 * @author Rob Catalano
 * @copyright 2018
 */
public class SystemCallDriver {

  public static final long BYTES_PER_GIG = 1073741824;

  /**
   * Method to calculate the memory usage per individual thread.
   * This method uses the java.lang.management.* thread classes
   * and calculates the CPU usage per thread by getting the CPU usage
   * time per thread and dividing it by cpu uptime.
   *
   * This method will output data that will eventually be put on a graph
   * that and will be easy to read.
   */
  private static void calcCPUUsagePerThread() {
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

  /**
   * method to return the percentage of CPU usage
   * @param hal
   * @return long[systemTime,percentageCPUUsage]
   */
  public static long[] getCPUUsage(HardwareAbstractionLayer hal) {
    long time = System.currentTimeMillis();
    double cpuUsage = hal.getProcessor().getSystemCpuLoadBetweenTicks() * 100;
    return new long[]{time,(long)cpuUsage};
  }

  /**
   * Method to obtain all processes currently running on the machine.
   * For each process running, a ProcessModel object is constructed and added
   * to the return List. Also, metrics regarding the top 5 cpu-usage-heavy
   * processes are printed out.
   * @param os OperatingSystem
   * @param memory GlobalMemory
   * @return ArrayList of ProcessModel objects
   */
  public static List<ProcessModel> getProcesses(OperatingSystem os, GlobalMemory memory) {
    // Sort by highest CPU
    // To only gather x amount of processes, change
    // the first parameter of os.getProcesses from 0 to x.
    List<OSProcess> OSprocs = Arrays.asList(os.getProcesses(0, OperatingSystem.ProcessSort.CPU));
    List<ProcessModel> procs = new ArrayList<>();

    for (int i = 0; i < OSprocs.size() && i < 50; i++) {
      OSProcess p = OSprocs.get(i);

      OSProcess.State state = p.getState();
      ProcessModel process = new ProcessModel(p.getName(),String.valueOf(100d * p.getResidentSetSize() / memory.getTotal()),String.valueOf(p.getThreadCount()),"desc",state,new ArrayList<Integer>());
      procs.add(process);
    }

    return procs;
  }

  /**
   * Method that creates a list of ApplicationModel objects from all of the current processes
   *
   * @param allProcesses
   * @return
   */
  public static List<ApplicationModel> getApplications(List<ProcessModel> allProcesses) {
    List<String> uniqueProcesses = new ArrayList<>();
    List<ApplicationModel> allApplications = new ArrayList<>();
    for (ProcessModel p: allProcesses) {
      String appName = p.getProcessName().replaceAll(".exe","");
      if (!uniqueProcesses.contains(appName)) {
        uniqueProcesses.add(appName);
      }
    }
    for (String appName: uniqueProcesses) {
      List<ProcessModel> appProcesses = new ArrayList<>();
      for (ProcessModel p: allProcesses) {
        if (p.getProcessName().contains(appName)) {
          appProcesses.add(p);
        }
      }
      ApplicationModel app = new ApplicationModel(appName,"Running",appProcesses);
      allApplications.add(app);
    }
    return allApplications;
  }

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
  public static MemoryMetrics getMemoryMetrics(GlobalMemory memory) {

    long memAvail = memory.getAvailable() / BYTES_PER_GIG;
    long memTotal = memory.getTotal() / BYTES_PER_GIG;
    long memUsed = memTotal - memAvail;

    MemoryMetrics memoryUsageMetrics = new MemoryMetrics(memTotal,memUsed,memAvail);

    return memoryUsageMetrics;
  }

  /**
   * Abstract method to retrieve the memory used at a certain time
   * @param memory
   * @return tuple containing the time and the memory used measurement
   */
  public static long[] getMemoryUsage(GlobalMemory memory) {
    long time = System.currentTimeMillis();
    long memUsed = (memory.getTotal() - memory.getAvailable()) / BYTES_PER_GIG;
    return new long[] { time, memUsed };
  }

}
