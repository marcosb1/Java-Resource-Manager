package com.marist.jrm.model;

import java.util.ArrayList;

public class ApplicationModel {

    private String name;
    private String status;
    private ArrayList<ProcessModel> processes;

    public ApplicationModel(String name,
                            String status,
                            ArrayList<ProcessModel> processes) {
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

    public ArrayList<ProcessModel> getProcesses() {
      return this.processes;
    }

    public void setProcesses(ArrayList<ProcessModel> processes) {
      this.processes = processes;
    }
}
