package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 21-09-2015.
 */
public class SchedulerModel {
    private int id;
    private String schedulerName;
    private String componentId;
    private String componentName;
    private String componentType;
    private String mip;
    private String machineName;
    private boolean isScene;
    private String defaultValue;
    private String dateTime;

    public SchedulerModel() {
    }

    public SchedulerModel(String schedulerName, String componentId, String name, String componentType, String mip, String machineName, boolean isScene, String defaultValue) {
        this.schedulerName = schedulerName;
        this.componentId = componentId;
        this.componentName = name;
        this.componentType = componentType;
        this.mip = mip;
        this.machineName = machineName;
        this.isScene = isScene;
        this.defaultValue = defaultValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
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

    public boolean isScene() {
        return isScene;
    }

    public void setIsScene(boolean isScene) {
        this.isScene = isScene;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}