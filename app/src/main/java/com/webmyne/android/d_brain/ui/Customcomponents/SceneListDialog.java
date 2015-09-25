package com.webmyne.android.d_brain.ui.Customcomponents;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.SceneActivity;
import com.webmyne.android.d_brain.ui.Adapters.SceneListAdapter;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * Created by priyasindkar on 16-09-2015.
 */
public class SceneListDialog extends BaseDialog {
    private RecyclerView mRecyclerView;
    private SceneListAdapter adapter;
    private ImageView imgCancel;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private int totalNoOfScenes = 5;

    public SceneListDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideBottomEnter());

        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(context, R.layout.dialog_scene_list, null);
        mRecyclerView = (RecyclerView) inflate.findViewById(R.id.recycler_view);
        imgCancel = (ImageView) inflate.findViewById(R.id.imgCancel);
        emptyView = (LinearLayout) inflate.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) inflate.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) inflate.findViewById(R.id.txtEmptyView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(context.getResources().getDimension(R.dimen.STD_MARGIN), context);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        adapter = new SceneListAdapter(context, totalNoOfScenes);
        adapter.setType(0);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        if(totalNoOfScenes == 0) {
            AdvancedSpannableString sp = new AdvancedSpannableString("Click Here");
            sp.setUnderLine("Click Here");
            sp.setColor(context.getResources().getColor(R.color.yellow), "Click Here");
            txtEmptyView1.setText(sp);

            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        txtEmptyView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SceneActivity.class);
                context.startActivity(intent);
                dismiss();
            }
        });

        adapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                // Redirect To the respective saved scene
                Intent intent = new Intent(context, SceneActivity.class);
                context.startActivity(intent);
                dismiss();
            }
        });

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

        return true;
    }
}
