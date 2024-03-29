package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 21-09-2015.
 */
public class ComponentModel {
    private int id;
    private String componentId;
    private String name;
    private String type;
    private String mid;
    private String mip;
    private String machineName;
    private String details;
    private String defaultValue;

    public ComponentModel() {
    }

    public ComponentModel(String componentId, String name, String type, String mid, String mip) {
        this.componentId = componentId;
        this.name = name;
        this.type = type;
        this.mid = mid;
        this.mip = mip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMip() {
        return mip;
    }

    public void setMip(String mip) {
        this.mip = mip;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "ComponentModel{" +
                "id=" + id +
                ", componentId='" + componentId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", mid='" + mid + '\'' +
                ", mip='" + mip + '\'' +
                ", machineName='" + machineName + '\'' +
                ", details='" + details + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
