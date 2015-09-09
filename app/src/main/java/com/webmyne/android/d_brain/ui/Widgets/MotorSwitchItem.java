package com.webmyne.android.d_brain.ui.Widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebIconDatabase;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;


/**
 * Created by priyasindkar on 03-09-2015.
 */
public class MotorSwitchItem extends LinearLayout{
    private Context mContext;
    LayoutInflater inflater;
    View view;
    private ImageView leftArrow, rightArrow, stop, imgRotateSwitches;
    private TextView txtMotorName;
    private AnimationHelper animationHelper;
    private boolean isLeftRight = true, isLeftUpPressed = false, isRightDownPressed = false;
    private int stateToSave = 0;

    public MotorSwitchItem(Context _context) {
        super(_context);
        this.mContext = _context;
        init(mContext);
    }

    public MotorSwitchItem(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = context;
        init(mContext);
    }

    public MotorSwitchItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(mContext);
    }

    public void init(Context context) {
       /* inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.motor_switch_item, this);*/

       // mContext =context;
        View.inflate(context, R.layout.motor_list_item, this);

        //setSaveEnabled(true);

        setOrientation(HORIZONTAL);
        leftArrow = (ImageView) findViewById(R.id.imgMotorLeftArrow);
        stop = (ImageView) findViewById(R.id.imgMotorStop);
        rightArrow = (ImageView) findViewById(R.id.imgMotorRightArrow);
        imgRotateSwitches = (ImageView) findViewById(R.id.imgRotateSwitches);
        txtMotorName = (TextView) findViewById(R.id.txtMotorName);

        animationHelper = new AnimationHelper();


        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Motor Stopped", Toast.LENGTH_SHORT).show();
            }
        });


        leftArrow.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TOUCH DOWN", "TOUCH DOWN");
                        isLeftUpPressed = true;
                        callLeftUpEvent();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.e("TOUCH UP", "TOUCH UP");
                        isLeftUpPressed = false;
                        return true;
                }
                return false;
            }
        });


        rightArrow.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isRightDownPressed = true;
                        callRightDownEvent();
                        return true;
                    case MotionEvent.ACTION_UP:
                        isRightDownPressed = false;
                        return true;
                }
                return false;
            }
        });

        imgRotateSwitches.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICK", "CLICK");
                rotateMotorButtons();
            }
        });


    }

    /*private void call() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLeftUpPressed) {
                    Log.e("TAG", "PRESSED");
                } else {
                    Log.e("TAG", "RELEASED");
                    handler.removeCallbacks(this);
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }*/


    private void callLeftUpEvent() {
        final Handler handler = new Handler();
        final Runnable runnable= new Runnable() {
            public void run() {
                try {
                    if( !isLeftUpPressed){
                        Toast.makeText(mContext, "Left Button Released", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "RELEASED");
                        handler.removeCallbacks(this);
                    } else {
                        Toast.makeText(mContext, "Left Button Pressed", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "PRESSED");
                        handler.postDelayed(this, 1000);
                    }
                } catch (Exception e) {
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void callRightDownEvent() {
        final Handler handler = new Handler();
        final Runnable runnable= new Runnable() {
            public void run() {
                try {
                    if( !isRightDownPressed){
                        Toast.makeText(mContext, "Right Button Released", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Right RELEASED");
                        handler.removeCallbacks(this);
                    } else {
                        Toast.makeText(mContext, "Right Button Pressed", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Right PRESSED");
                        handler.postDelayed(this, 1000);
                    }
                } catch (Exception e) {
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    public void rotateMotorButtons(){

       if(isLeftRight) {
            animationHelper.rotateViewClockwiseLeftToUp(leftArrow);
            animationHelper.rotateViewClockwiseLeftToUp(rightArrow);
        } else {
            animationHelper.rotateViewAntiClockwiseLeftToUp(leftArrow);
            animationHelper.rotateViewAntiClockwiseLeftToUp(rightArrow);
        }
        isLeftRight = !isLeftRight;
    }


    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.stateToSave = this.stateToSave;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end

        this.stateToSave = ss.stateToSave;
    }

    static class SavedState extends BaseSavedState {
        int stateToSave;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.stateToSave = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.stateToSave);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
