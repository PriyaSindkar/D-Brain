package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 08-09-2015.
 */
public class SceneItemsDataObject {
    private int sceneControlType;
    private String name;

    public SceneItemsDataObject() {
    }

    public SceneItemsDataObject(int sceneControlType, String name) {
        this.sceneControlType = sceneControlType;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSceneControlType() {
        return sceneControlType;
    }

    public void setSceneControlType(int sceneControlType) {
        this.sceneControlType = sceneControlType;
    }
}
