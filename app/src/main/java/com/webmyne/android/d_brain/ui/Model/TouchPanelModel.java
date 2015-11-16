package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 21-09-2015.
 */
public class TouchPanelModel {
    private int id;
    private String name;

    private int mid;
    private String mname;
    private String mip;

    public TouchPanelModel() {
    }

    public TouchPanelModel(String name) {
        this.name = name;
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

    public void setmMId(int mid) {
        this.mid = mid;
    }
    public void setMname(String mname) {
        this.mname = mname;
    }
    public void setMIp(String ip) {
        this.mip = ip;
    }

    public String getMname() {
        return mname;
    }
    public String getMIp() {
        return mip;
    }
    public int getMId() {
        return mid;
    }

}
