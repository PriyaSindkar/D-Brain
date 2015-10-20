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
    private TextView txtSwitchName;
    private String switchId, machineIP, machineName;

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

    public String getMachineIP() {
        return machineIP;
    }

    public void setMachineIP(String machineIP) {
        this.machineIP = machineIP;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    @Override
    public String toString() {
        return "SceneSwitchItem{" +
                "switchId='" + switchId + '\'' +
                '}';
    }
}
