package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by krishnakumar on 16-11-2015.
 */
public class Machine {

    public int machineId;
    public String machineIp;
    public String machineName;
    public String machineProductCode;

    public Machine() {
    }

    public Machine(int machineId, String machineIp, String machineName) {
        this.machineId = machineId;
        this.machineIp = machineIp;
        this.machineName = machineName;
    }
    public String getMachineProductCode() {
        return machineProductCode;
    }

    public void setMachineProductCode(String machineProductCode) {
        this.machineProductCode = machineProductCode;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
