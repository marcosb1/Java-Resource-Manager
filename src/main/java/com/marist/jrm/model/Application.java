package com.marist.jrm.model;

public class Application {

    private String name;
    private String status;
    private ArrayList<Process> processes;

    public Application(String name,
                   String status,
                   ArrayList<Process> processes) {
        this.setName(name);
        this.setStatus(status);
        this.setProcesses(processes);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Process> getProcesses() {
      return this.processes;
    }

    public void setProcesses(ArrayList<Process> processes) {
      this.processes = processes;
    }
}
