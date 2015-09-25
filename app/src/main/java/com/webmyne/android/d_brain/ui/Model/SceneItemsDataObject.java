package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 08-09-2015.
 */
public class SceneItemsDataObject {
    private String sceneItemId, sceneControlType, name, machineIP, machineID, defaultValue, sceneComponentId;

    public SceneItemsDataObject() {
    }

    public SceneItemsDataObject(String sceneControlType, String name) {
        this.sceneControlType = sceneControlType;
        this.name = name;
    }

    // id from component table
    public String getSceneItemId() {
        return sceneItemId;
    }
    public void setSceneItemId(String sceneItemId) {
        this.sceneItemId = sceneItemId;
    }

    //component_id from component table
    public String getSceneComponentId() {
        return sceneComponentId;
    }
    public void setSceneComponentId(String sceneComponentId) {
        this.sceneComponentId = sceneComponentId;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    @Override
    public boolean equals(Object o) {
        if(sceneItemId.equals(((SceneItemsDataObject)o).getSceneItemId())) {
            return  true;
        } else {
            return false;
        }
    }
}
