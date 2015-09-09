package com.webmyne.android.d_brain.ui.Widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.priyasindkar.dbraindemo.R;

import animationHelpers.AnimationHelper;

/**
 * Created by priyasindkar on 03-09-2015.
 */
public class SceneSwitchItem extends LinearLayout{
    private Context mContext;
    LayoutInflater inflater;
    View viewLine;
    private ImageView imgSwitch;
    private TextView txtSwitchName;

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

}