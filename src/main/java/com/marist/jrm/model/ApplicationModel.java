package com.marist.jrm.model;

import java.util.ArrayList;

public class ApplicationModel {

    private String name;
    private String status;
    private ArrayList<ProcessModel> processes;

    public ApplicationModel(String name,
                            String status,
<<<<<<< HEAD
                            ArrayList<Process> processes) {
=======
                            ArrayList<ProcessModel> processes) {
>>>>>>> origin/build-dev
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

<<<<<<< HEAD
    public ArrayList<Process> getProcesses() {
        return this.processes;
    }

    public void setProcesses(ArrayList<Process> processes) {
        this.processes = processes;
=======
    public ArrayList<ProcessModel> getProcesses() {
      return this.processes;
    }

    public void setProcesses(ArrayList<ProcessModel> processes) {
      this.processes = processes;
>>>>>>> origin/build-dev
    }
}