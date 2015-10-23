package com.webmyne.android.d_brain.ui.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Activities.CreateSceneActivity;
import com.webmyne.android.d_brain.ui.Activities.SceneActivity;
import com.webmyne.android.d_brain.ui.Adapters.SceneListAdapter;
import com.webmyne.android.d_brain.ui.Adapters.SceneListCursorAdapter;
import com.webmyne.android.d_brain.ui.Helpers.AdvancedSpannableString;
import com.webmyne.android.d_brain.ui.Helpers.Utils;
import com.webmyne.android.d_brain.ui.Helpers.VerticalSpaceItemDecoration;
import com.webmyne.android.d_brain.ui.Listeners.onDeleteClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onLongClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onRenameClickListener;
import com.webmyne.android.d_brain.ui.Listeners.onSingleClickListener;
import com.webmyne.android.d_brain.ui.base.HomeDrawerActivity;
import com.webmyne.android.d_brain.ui.dbHelpers.DBConstants;
import com.webmyne.android.d_brain.ui.dbHelpers.DatabaseHelper;

import java.sql.SQLException;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class SceneFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SceneListCursorAdapter adapter;
    private int totalNoOfScenes = 0;
    private TextView txtEmptyView, txtEmptyView1;
    private LinearLayout emptyView;
    private Cursor sceneListCursor;

    public static SceneFragment newInstance() {
        SceneFragment fragment = new SceneFragment();

        return fragment;
    }

    public SceneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scene_list, container, false);
        init(view);


        /*adapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                Intent intent = new Intent(getActivity(), SceneActivity.class);
                startActivity(intent);
                //Toast.makeText(getActivity(), "Single Click Item Pos: " + pos, Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setLongClickListener(new onLongClickListener() {

            @Override
            public void onLongClick(int pos) {
                Toast.makeText(getActivity(), "Options Will Open Here", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setDeleteClickListener(new onDeleteClickListener() {
            @Override
            public void onDeleteOptionClick(int pos) {
                Toast.makeText(getActivity(), "Deleted Sccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setRenameClickListener(new onRenameClickListener() {

            @Override
            public void onRenameOptionClick(int pos, String _oldName) {
                Toast.makeText(getActivity(), "Rename Sccessful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRenameOptionClick(int pos, String oldName, String oldDetails) {

            }
        });*/
        
        return view;
    }

    private void init(View view) {
        ((HomeDrawerActivity) getActivity()).setTitle("Scenes");
        ((HomeDrawerActivity) getActivity()).hideAppBarButton();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);
        txtEmptyView1 = (TextView) view.findViewById(R.id.txtEmptyView1);
        txtEmptyView = (TextView) view.findViewById(R.id.txtEmptyView);

        updateSceneList();

        if(sceneListCursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            AdvancedSpannableString sp = new AdvancedSpannableString("Click Here");
            sp.setUnderLine("Click Here");
            sp.setColor(getResources().getColor(R.color.yellow), "Click Here");
            txtEmptyView1.setText(sp);
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        txtEmptyView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateSceneActivity.class);
                getActivity().startActivity(intent);
            }
        });


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());


        mRecyclerView.setLayoutManager(layoutManager);
        int margin = Utils.pxToDp(getResources().getDimension(R.dimen.STD_MARGIN), getActivity());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(margin));
        mRecyclerView.setItemViewCacheSize(0);

        adapter = new SceneListCursorAdapter(getActivity(), sceneListCursor);
        adapter.setType(1);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setItemAnimator(new LandingAnimator());

        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mRecyclerView.getItemAnimator().setMoveDuration(500);
        mRecyclerView.getItemAnimator().setChangeDuration(500);

        adapter.setSingleClickListener(new onSingleClickListener() {
            @Override
            public void onSingleClick(int pos) {
                sceneListCursor.moveToPosition(pos);

                String sceneId = sceneListCursor.getString(sceneListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_ID));
                String sceneName = sceneListCursor.getString(sceneListCursor.getColumnIndexOrThrow(DBConstants.KEY_S_NAME));

                // Redirect To the respective saved scene
                Intent intent = new Intent(getActivity(), SceneActivity.class);
                intent.putExtra("scene_id", sceneId);
                intent.putExtra("scene_name", sceneName);
                getActivity().startActivity(intent);
            }
        });

    }

    private void updateSceneList() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.openDataBase();

            sceneListCursor = dbHelper.getAllScenes("");
            dbHelper.close();
        } catch (SQLException e) {
            Log.e("SQLEXP", e.toString());
        }

    }

}
