package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.animation.SlideEnter.SlideLeftEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSaveClickListener;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class RenameDialog extends BaseDialog {
    private EditText edtInputName, edtInputDetails;
    private TextInputLayout txtInputDetails;
    private TextView txtRename;
    private ImageView imgCancel;
    private onRenameClickListener _onRenameClick;
    private String oldName="", oldDetails = null;
    private Context mContext;

    public RenameDialog(Context context) {
        super(context);
    }

    public RenameDialog(Context context, String _oldName) {
        super(context);
        this.oldName = _oldName;
    }

    public RenameDialog(Context context, String _oldName, String _oldDetails) {
        super(context);
        this.oldName = _oldName;
        this.oldDetails = _oldDetails;
    }

   /* public RenameDialog(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = context;
    }

    public RenameDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }
*/
    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideLeftEnter());

        View inflate = View.inflate(context, R.layout.dialog_rename, null);
        txtRename = (TextView) inflate.findViewById(R.id.txtRename);
        txtInputDetails = (TextInputLayout) inflate.findViewById(R.id.txtInputDetails);
        edtInputDetails = (EditText) inflate.findViewById(R.id.edtInputDetails);
        edtInputName = (EditText) inflate.findViewById(R.id.edtInputName);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);

        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return inflate;
    }


    @Override
    public boolean setUiBeforShow() {
         edtInputName.setText(oldName);

        if(oldDetails != null) {
            txtInputDetails.setVisibility(View.VISIBLE);
            edtInputDetails.setText(oldDetails);
        }

        txtRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtInputName.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                } else {
                    if(oldDetails != null) {
                        _onRenameClick.onRenameOptionClick(0, edtInputName.getText().toString().trim(), edtInputDetails.getText().toString().trim());
                    } else {
                        _onRenameClick.onRenameOptionClick(0, edtInputName.getText().toString().trim());
                    }
                }
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

    public void setRenameListener(onRenameClickListener obj){
        this._onRenameClick = obj;
    }
}
