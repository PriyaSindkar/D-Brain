package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 19-11-2015.
 */
public class TouchPanelSwitchModel {
    private int id;
    private String mid, componentName, componentType, payLoad, pos1, pos2, pos3, pos4, pos5, pos6;

    public TouchPanelSwitchModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public String getPos1() {
        return pos1;
    }

    public void setPos1(String pos1) {
        this.pos1 = pos1;
    }

    public String getPos2() {
        return pos2;
    }

    public void setPos2(String pos2) {
        this.pos2 = pos2;
    }

    public String getPos3() {
        return pos3;
    }

    public void setPos3(String pos3) {
        this.pos3 = pos3;
    }

    public String getPos4() {
        return pos4;
    }

    public void setPos4(String pos4) {
        this.pos4 = pos4;
    }

    public String getPos5() {
        return pos5;
    }

    public void setPos5(String pos5) {
        this.pos5 = pos5;
    }

    public String getPos6() {
        return pos6;
    }

    public void setPos6(String pos6) {
        this.pos6 = pos6;
    }

    @Override
    public String toString() {
        return "TouchPanelSwitchModel{" +
                "id=" + id +
                ", mid='" + mid + '\'' +
                ", componentName='" + componentName + '\'' +
                ", componentType='" + componentType + '\'' +
                ", payLoad='" + payLoad + '\'' +
                ", pos1='" + pos1 + '\'' +
                ", pos2='" + pos2 + '\'' +
                ", pos3='" + pos3 + '\'' +
                ", pos4='" + pos4 + '\'' +
                ", pos5='" + pos5 + '\'' +
                ", pos6='" + pos6 + '\'' +
                '}';
    }
}
