package com.webmyne.android.d_brain.ui.Widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;


/**
 * Created by priyasindkar on 03-09-2015.
 */
public class SceneSwitchItem extends LinearLayout{
    private Context mContext;
    LayoutInflater inflater;
    View viewLine;
    private ImageView imgSwitch;
    private TextView txtSwitchName, txtMachineName;
    private LinearLayout linearParent;
    private String switchId, machineIP, componentPrimaryId, machineId, isActive;

    public SceneSwitchItem(Context _context) {
        super(_context);
        this.mContext = _context;
        init(mContext);
    }

    public SceneSwitchItem(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = context;
        init(mContext);
    }

    public SceneSwitchItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(mContext);
    }


    public void init(Context context) {
       /* inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.motor_switch_item, this);*/
        View.inflate(context, R.layout.scene_switch_item, this);

        setOrientation(VERTICAL);
        imgSwitch = (ImageView) findViewById(R.id.imgMotorLeftArrow);

        txtSwitchName = (TextView) findViewById(R.id.txtSwitchName);
        txtMachineName = (TextView) findViewById(R.id.txtMachineName);
        linearParent = (LinearLayout) findViewById(R.id.linearParent);
        viewLine = findViewById(R.id.viewLine);

        setFocusable(true);
    }
    public void setText(String text) {
        txtSwitchName.setText(text);
    }

    public String getText() {
        return txtSwitchName.getText().toString();
    }

    public String getSwitchId() {
        return switchId;
    }

    public void setSwitchId(String switchId) {
        this.switchId = switchId;
    }

    public String getComponentPrimaryId() {
        return componentPrimaryId;
    }

    public void setComponentPrimaryId(String componentPrimaryId) {
        this.componentPrimaryId = componentPrimaryId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineIP() {
        return machineIP;
    }

    public void setMachineIP(String machineIP) {
        this.machineIP = machineIP;
    }

    public String getMachineName() {
        return txtMachineName.getText().toString().trim();
    }

    public void setMachineName(String machineName) {
        txtMachineName.setText(machineName);
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public void enableDisableComponent(String isActive) {
        if(isActive.equals("false")) {
            setFocusable(false);
            linearParent.setAlpha(0.5f);
        }
        else {
            setFocusable(true);
            linearParent.setAlpha(1.0f);
        }
    }

    @Override
    public String toString() {
        return "SceneSwitchItem{" +
                "switchId='" + switchId + '\'' +
                '}';
    }
}
