package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 08-09-2015.
 */
public class SceneItemsDataObject {
    private String sceneItemId, sceneControlType, name, machineIP, machineID;

    public SceneItemsDataObject() {
    }

    public SceneItemsDataObject(String sceneControlType, String name) {
        this.sceneControlType = sceneControlType;
        this.name = name;
    }

    public String getSceneItemId() {
        return sceneItemId;
    }

    public void setSceneItemId(String sceneItemId) {
        this.sceneItemId = sceneItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSceneControlType() {
        return sceneControlType;
    }

    public void setSceneControlType(String sceneControlType) {
        this.sceneControlType = sceneControlType;
    }

    public String getMachineIP() {
        return machineIP;
    }

    public void setMachineIP(String machineIP) {
        this.machineIP = machineIP;
    }

    public String getMachineID() {
        return machineID;
    }

    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }
}
