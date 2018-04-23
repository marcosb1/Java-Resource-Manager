package com.marist.jrm.model;

import oshi.software.os.OSProcess;


public class Process {

    private String processName;
    private String memory;
    private String threadCount;
    private String description;
    private OSProcess.State state;

    public Process(String processName,
                   String memory,
                   String threadCount,
                   String description,
                   OSProcess.State state) {
        this.processName = processName;
        this.memory = memory;
        this.threadCount = threadCount;
        this.description = description;
        this.state = state;
    }

    public OSProcess.State getState() {
        return state;
    }

    public void setState(OSProcess.State state) {
        this.state = state;
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
}

