package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.animation.SlideEnter.SlideRightEnter;
import com.flyco.animation.SlideEnter.SlideTopEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Listeners.onAddSchedulerClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onAddToSceneClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onFavoriteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.Model.ComponentModel;
import com.webmyne.android.d_brain.ui.Model.TouchPanelModel;
import com.webmyne.android.d_brain.ui.dbHelpers.AppConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;
import com.webmyne.android.d_brain.ui.xmlHelpers.MainXmlPullParser;
import com.webmyne.android.d_brain.ui.xmlHelpers.XMLValues;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class LongPressOptionsDialog extends BaseDialog {
    private TextView txtRename, txtAddToScene, txtAddToFavorite, txtAddToScheduler;
    private ImageView imgCancel;
    public onFavoriteClickListener _favoriteClick;
    public onAddToSceneClickListener _addToSceneClick;
    public onAddSchedulerClickListener _addSchedulerClick;
    public onRenameClickListener _renameClick;
    int pos;

    public LongPressOptionsDialog(Context context, int pos) {
        super(context);
        this.pos = pos;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new FadeEnter());

        View inflate = View.inflate(context, R.layout.dialog_long_press_options, null);
        txtRename = (TextView) inflate.findViewById(R.id.txtRename);
        txtAddToScene = (TextView) inflate.findViewById(R.id.txtAddToScene);
        txtAddToFavorite = (TextView) inflate.findViewById(R.id.txtAddToFavorite);
        txtAddToScheduler = (TextView) inflate.findViewById(R.id.txtAddToScheduler);

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
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        txtRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _renameClick.onRenameOptionClick(pos, "");
                dismiss();
            }
        });

        txtAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _favoriteClick.onFavoriteOptionClick(pos);
                dismiss();
            }
        });

        txtAddToScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _addToSceneClick.onAddToSceneOptionClick(pos);
                dismiss();
            }
        });

        txtAddToScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _addSchedulerClick.onAddSchedulerOptionClick(pos);
                dismiss();
            }
        });

        return true;
    }

    public void setFavoriteClickListener(onFavoriteClickListener obj){
        this._favoriteClick = obj;
    }

    public void setAddToSceneClickListener(onAddToSceneClickListener obj){
        this._addToSceneClick = obj;
    }

    public void setAddSchedulerClickListener(onAddSchedulerClickListener obj){
        this._addSchedulerClick = obj;
    }

    public void setRenameClickListener(onRenameClickListener obj){
        this._renameClick = obj;
    }

}
