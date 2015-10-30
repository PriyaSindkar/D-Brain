package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.animation.SlideEnter.SlideTopEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class SaveAlertDialog extends BaseDialog {
    private TextView txtYes, txtNo, txttitle;
    private ImageView imgCancel;
    private onSaveClickListener _onSaveClick;
    private String message = "";

    public SaveAlertDialog(Context context) {
        super(context);
    }

    public SaveAlertDialog(Context context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideBottomEnter());

        View inflate = View.inflate(context, R.layout.dialog_save_alert, null);
        txtYes = (TextView) inflate.findViewById(R.id.txtYes);
        txtNo = (TextView) inflate.findViewById(R.id.txtNo);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);
        txttitle = (TextView) inflate.findViewById(R.id.txttitle);

        if(!this.message.equals("")) {
            txttitle.setText(this.message);
        }

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
                _onSaveClick.onSaveClick(true);
                dismiss();
            }
        });

        txtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onSaveClick.onSaveClick(false);
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

    public void setSaveListener(onSaveClickListener obj){
        this._onSaveClick = obj;
    }
}
