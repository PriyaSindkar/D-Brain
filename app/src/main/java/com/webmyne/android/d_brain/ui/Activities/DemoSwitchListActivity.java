package com.webmyne.android.d_brain.ui.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Adapters.DemoSwitchListAdapter;

public class DemoSwitchListActivity extends AppCompatActivity {
    GridView gridView;
    Button btn;
    boolean isList = true;
    DemoSwitchListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_switch_activity);
        gridView = (GridView) findViewById(R.id.list_view);
        btn = (Button) findViewById(R.id.btn);


        // Instance of ImageAdapter Class
        adapter = new DemoSwitchListAdapter(this);
        gridView.setNumColumns(1);
        adapter.setViewType(0);
        gridView.setAdapter(adapter);
       // gridView.setLayoutAnimation(new GridLayoutAnimationController(new AlphaAnimation(1f,0.5f)));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapter.notifyDataSetChanged();

                if(isList) {
                    adapter.setViewType(1);
                    gridView.setNumColumns(2);
                    /*gridView.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);

                    gridView.setLayoutAnimation(new GridLayoutAnimationController(new AlphaAnimation(1f, 0.5f)));*/
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.setViewType(0);
                    gridView.setNumColumns(1);
                  /*  gridView.setVisibility(View.GONE);


                    gridView.setLayoutAnimation(new GridLayoutAnimationController(new AlphaAnimation(1f, 0.5f)));
                    gridView.setVisibility(View.VISIBLE);*/
                    adapter.notifyDataSetChanged();
                }
                isList = !isList;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
