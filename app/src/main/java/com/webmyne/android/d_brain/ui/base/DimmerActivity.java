package com.webmyne.android.d_brain.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.webmyne.android.d_brain.R;

public class DimmerActivity extends AppCompatActivity {

    private Toolbar tooolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimmer);

        init();
    }

    private void init() {
        tooolbar = (Toolbar) findViewById(R.id.toolbar);
        tooolbar.setTitle("Dimmers");
    }

}
