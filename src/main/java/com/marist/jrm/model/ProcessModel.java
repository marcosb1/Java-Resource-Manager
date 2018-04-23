package com.marist.jrm.model;


import oshi.software.os.OSProcess;

import java.util.ArrayList;

public class ProcessModel {

    private String processName;
    private String memory;
    private String threadCount;
    private String description;
    private OSProcess.State state;
    private ArrayList<Integer> threadUsages;

    public ProcessModel(String processName,
                        String memory,
                        String threadCount,
                        String description,
                        OSProcess.State state,
                        ArrayList<Integer> threadUsages) {
        this.setProcessName(processName);
        this.setMemory(memory);
        this.setThreadCount(threadCount);
        this.setDescription(description);
        this.setState(state);
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

    public void setMemory(String memory) {
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

    public OSProcess.State getState() {
        return this.state;
    }

    public void setState(OSProcess.State state) {
        this.state = state;
    }

    public ArrayList<Integer> getThreadUsages() {
      return this.threadUsages;
    }

    public void setThreadUsages(ArrayList<Integer> threadUsages) {
      this.threadUsages = threadUsages;
    }
}
