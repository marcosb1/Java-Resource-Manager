package com.marist.jrm.model;

/**
 * Simple class to hold memory usage metrics
 */
public class MemoryMetrics {

  long totalMemory;
  long memoryUsed;
  long memoryAvailable;

  public MemoryMetrics(long totalMemory, long memoryUsed, long memoryAvailble) {
    this.totalMemory = totalMemory;
    this.memoryUsed = memoryUsed;
    this.memoryAvailable = memoryAvailble;
  }

  public long getTotalMemory() {
    return totalMemory;
  }

  public void setTotalMemory(long totalMemory) {
    this.totalMemory = totalMemory;
  }

  public long getMemoryUsed() {
    return memoryUsed;
  }

  public void setMemoryUsed(long memoryUsed) {
    this.memoryUsed = memoryUsed;
  }
  public long getMemoryAvailable() {
    return memoryAvailable;
  }

  public void setMemoryAvailable(long memoryAvailable) {
    this.memoryAvailable = memoryAvailable;
  }
}
