package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.animation.Attention.Flash;
import com.flyco.animation.Attention.Swing;
import com.flyco.animation.Attention.Tada;
import com.flyco.animation.BounceEnter.BounceLeftEnter;
import com.flyco.animation.FallEnter.FallEnter;
import com.flyco.animation.FlipEnter.FlipTopEnter;
import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.animation.SlideEnter.SlideLeftEnter;
import com.flyco.animation.SlideEnter.SlideTopEnter;
import com.flyco.animation.ZoomEnter.ZoomInBottomEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class MachineUnAvailableAlertDialog extends BaseDialog {
    private TextView txtYes, txtNo, txttitle;
    private ImageView imgCancel;

    public MachineUnAvailableAlertDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new Flash());

        View inflate = View.inflate(context, R.layout.dialog_save_alert, null);
        txtYes = (TextView) inflate.findViewById(R.id.txtYes);
        txtNo = (TextView) inflate.findViewById(R.id.txtNo);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);
        txttitle = (TextView) inflate.findViewById(R.id.txttitle);

        txttitle.setText(context.getString(R.string.machine_unavailable));
        txtNo.setVisibility(View.GONE);
        txtYes.setText("Ok");

        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return inflate;
    }



    @Override
    public boolean setUiBeforShow() {
        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return true;
    }
}
