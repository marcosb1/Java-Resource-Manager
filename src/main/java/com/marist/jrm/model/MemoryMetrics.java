package com.marist.jrm.model;

public class MemoryMetrics {

  Double totalMemory;
  Double memoryUsed;
  Double memoryAvailable;

  public MemoryMetrics(Double totalMemory, Double memoryUsed, Double memoryAvailble) {
    this.totalMemory = totalMemory;
    this.memoryUsed = memoryUsed;
    this.memoryAvailable = memoryAvailble;
  }

  public Double getTotalMemory() {
    return totalMemory;
  }

  public void setTotalMemory(Double totalMemory) {
    this.totalMemory = totalMemory;
  }

  public Double getMemoryUsed() {
    return memoryUsed;
  }

  public void setMemoryUsed(Double memoryUsed) {
    this.memoryUsed = memoryUsed;
  }
  public Double getMemoryAvailable() {
    return memoryAvailable;
  }

  public void setMemoryAvailable(Double memoryAvailable) {
    this.memoryAvailable = memoryAvailable;
  }
}
