package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 08-09-2015.
 */
public class SceneItemsDataObject {
    private int sceneControlType;

    public SceneItemsDataObject() {
    }

    public SceneItemsDataObject(int sceneControlType) {
        this.sceneControlType = sceneControlType;
    }

    public int getSceneControlType() {
        return sceneControlType;
    }

    public void setSceneControlType(int sceneControlType) {
        this.sceneControlType = sceneControlType;
    }
}
