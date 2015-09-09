package com.webmyne.android.d_brain.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.webmyne.android.d_brain.R;

public class DimmerActivity extends AppCompatActivity {

    private Toolbar tooolbar;
    private TextView txtTitle;
    private ImageView imgBack;
    private ListView dimmerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimmer);

        init();
    }

    private void init() {
        dimmerListView = (ListView) findViewById(R.id.dimmerListView);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtTitle = (TextView) findViewById(R.id.toolbarTitle);
        txtTitle.setText("Dimmers");
    }

}
