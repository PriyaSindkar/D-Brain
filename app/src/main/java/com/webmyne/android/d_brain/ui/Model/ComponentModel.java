package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 21-09-2015.
 */
public class ComponentModel {
    private int id;
    private String name;
    private String type;
    private String mid;
    private String mip;

    public ComponentModel() {
    }

    public ComponentModel(String name, String type, String mid, String mip) {
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


    @Override
    public String toString() {
        return "ComponentModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", mid='" + mid + '\'' +
                ", mip='" + mip + '\'' +
                '}';
    }
}
