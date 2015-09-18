package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.Attention.Flash;
import com.flyco.animation.Attention.Swing;
import com.flyco.animation.Attention.Tada;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.animation.FlipEnter.FlipTopEnter;
import com.flyco.animation.Jelly;
import com.flyco.animation.NewsPaperEnter;
import com.flyco.animation.SlideEnter.SlideRightEnter;
import com.flyco.animation.SlideEnter.SlideTopEnter;
import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class AddMachineDialog extends BaseDialog {
    private TextView txtAddMachine;
    private ImageView imgCancel;

    public AddMachineDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideTopEnter());


        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(context, R.layout.dialog_add_machine, null);
        //txtAddMachine = ViewFindUtils.find(inflate, R.id.txtAddMachine);
        txtAddMachine = (TextView) inflate.findViewById(R.id.txtAddMachine);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);
       /* tv_exit = ViewFindUtils.find(inflate, R.id.tv_exit);*/
       /* inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));*/



        return inflate;
    }



    @Override
    public boolean setUiBeforShow() {
        txtAddMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //dismiss();
                Toast.makeText(context, "Machine Added", Toast.LENGTH_SHORT).show();
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
