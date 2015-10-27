package com.webmyne.android.d_brain.ui.Customcomponents;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flyco.animation.SlideEnter.SlideLeftEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Customcomponents.CustomProgressBar.ExternalCirclePainter;
import com.webmyne.android.d_brain.ui.Model.SchedulerModel;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class AddToSchedulerDialog extends BaseDialog {
    private ImageView imgCancel;
    private Context mContext;
    private EditText edtComponentName, edtDate, edtTime, edtSchedulerName;
    private TextView txtSaveScheduler, txtValue;
    private SwitchButton imgSwitch;
    private SeekBar seekBar;
    private LinearLayout linearDimmerSeekBar;
    private SchedulerModel schedulerModel;

    public AddToSchedulerDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public AddToSchedulerDialog(Context context, SchedulerModel _schedulerModel) {
        super(context);
        this.mContext = context;
        this.schedulerModel = _schedulerModel;
    }


    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideLeftEnter());

        View inflate = View.inflate(context, R.layout.dialog_add_to_scheduler, null);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);

        edtSchedulerName = (EditText) inflate.findViewById(R.id.edtSchedulerName);
        edtComponentName = (EditText) inflate.findViewById(R.id.edtComponentName);
        edtDate = (EditText) inflate.findViewById(R.id.edtDate);
        edtTime = (EditText) inflate.findViewById(R.id.edtTime);

        txtSaveScheduler = (TextView) inflate.findViewById(R.id.txtSaveScheduler);
        txtValue = (TextView) inflate.findViewById(R.id.txtValue);

        linearDimmerSeekBar = (LinearLayout) inflate.findViewById(R.id.linearDimmerSeekBar);
        imgSwitch = (SwitchButton) inflate.findViewById(R.id.imgSwitch);
        seekBar = (SeekBar) inflate.findViewById(R.id.seekBar);

        edtComponentName.setText(schedulerModel.getComponentName());

        if(schedulerModel.getComponentType().equals(AppConstants.SWITCH_TYPE )|| schedulerModel.getComponentType().equals(AppConstants.SCENE_TYPE)) {
            linearDimmerSeekBar.setVisibility(View.GONE);
        } else if (schedulerModel.getComponentType().equals(AppConstants.DIMMER_TYPE)) {
            linearDimmerSeekBar.setVisibility(View.VISIBLE);
            // by default set to 0
            txtValue.setText("00");
            seekBar.setProgress(0);
        }
        return inflate;
    }


    @Override
    public boolean setUiBeforShow() {
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar newCalendar = Calendar.getInstance();
                int year, month, day;

                year = newCalendar.get(Calendar.YEAR);
                month  = newCalendar.get(Calendar.MONTH);
                day  = newCalendar.get(Calendar.DAY_OF_MONTH);

                if(edtDate.getText().toString().trim().length() != 0) {
                    String[] date = edtDate.getText().toString().trim().split("-");
                    year = Integer.parseInt(date[0]);
                    month = Integer.parseInt(date[1])-1;
                    day = Integer.parseInt(date[2]);
                }

                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        edtDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                }, year, month, day);
                fromDatePickerDialog.show();
            }
        });

        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                if(edtTime.getText().toString().trim().length() != 0) {
                    String[] time = edtTime.getText().toString().trim().split(":");
                    mHour = Integer.parseInt(time[0]);
                    mMinute = Integer.parseInt(time[1]);
                }

                TimePickerDialog tpd = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                edtTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtValue.setText(""+progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        txtSaveScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtSchedulerName.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, "Please Enter Scheduler Name.", Toast.LENGTH_SHORT).show();
                } else if(edtDate.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, "Please Select Scheduler Date.", Toast.LENGTH_SHORT).show();
                } else if(edtTime.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, "Please Select Scheduler Time.", Toast.LENGTH_SHORT).show();
                } else {
                    String dimmerDefaultProgress = "";
                    if (schedulerModel.getComponentType().equals(AppConstants.DIMMER_TYPE)) {
                        if(seekBar.getProgress() == 0) {
                            dimmerDefaultProgress = "00";
                        } else {
                            dimmerDefaultProgress = String.valueOf(seekBar.getProgress() - 1);
                        }
                        if (imgSwitch.isChecked()) {
                            schedulerModel.setDefaultValue("01" + dimmerDefaultProgress);
                        } else {
                            schedulerModel.setDefaultValue("00" + dimmerDefaultProgress);
                        }
                    } else {
                        if (imgSwitch.isChecked()) {
                            schedulerModel.setDefaultValue("01");
                        } else {
                            schedulerModel.setDefaultValue("00");
                        }
                    }

                    schedulerModel.setSchedulerName(edtSchedulerName.getText().toString().trim());
                    schedulerModel.setDateTime(edtDate.getText().toString().trim() + " " + edtTime.getText().toString().trim());

                    try {
                        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
                        dbHelper.openDataBase();
                        dbHelper.insertIntoScheduler(schedulerModel);
                        dbHelper.close();

                        Toast.makeText(mContext, "Scheduler Saved.", Toast.LENGTH_SHORT).show();
                        dismiss();

                    } catch(Exception e) {

                    }
                }

            }
        });

        return true;
    }

}