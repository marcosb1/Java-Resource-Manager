package com.marist.jrm.model;


public class Process {

    private String processName;
    private String memory;
    private String threadCount;
    private String description;
    private ArrayList<Int> threadUsages;

    public Process(String processName,
                  String memory,
                  String threadCount,
                  String description,
                  ArrayList<Int> threadUsages) {
        this.setProcessName(processName);
        this.setMemory(memory);
        this.setThreadCount(threadCount);
        this.setDescription(description);
        this.setThreadUsages(threadUsages);
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String Memory) {
        this.memory = memory;
    }

    public String getThreadCount() {
        return this.threadCount;
    }

    public void setThreadCount(String threadCount) {
        this.threadCount = threadCount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Int> getThreadUsages() {
      return this.threadUsages;
    }

    public void setThreadUsages(ArrayList<Int> threadUsages) {
      this.threadUsages = threadUsages;
    }
}
