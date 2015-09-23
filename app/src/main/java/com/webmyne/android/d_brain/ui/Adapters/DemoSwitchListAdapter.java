package com.webmyne.android.d_brain.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.service.voice.VoiceInteractionService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.webmyne.android.d_brain.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DemoSwitchListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<String> movieItems = new ArrayList<>();
    private Map<String, String> itemStatus = new HashMap<>();
    private int lastPosition = -1;
    private static int VIEW_TYPE;

    public DemoSwitchListAdapter(Activity activity) {
        this.activity = activity;
        //this.movieItems = movieItems;

        for(int i=0; i<33; i++) {
            movieItems.add("Switch_"+i);
        }

        for(int i=0; i<33; i++) {
            /*if(i % 5==0)
                itemStatus.add("01");
            else*/
                itemStatus.put(movieItems.get(i), "00");
        }
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setViewType(int type) {
        VIEW_TYPE = type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if(VIEW_TYPE == 0) {
                convertView = inflater.inflate(R.layout.demo_list_item, null);
            } else {
                convertView = inflater.inflate(R.layout.demo_grid_item, null);
            }

            viewHolder = new ViewHolderItem();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.txtSwitchName);
            viewHolder.imgSwitch = (SwitchButton) convertView.findViewById(R.id.imgSwitch);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        viewHolder.textViewItem.setText(movieItems.get(position));

        if(itemStatus.get(movieItems.get(position)).equals("00"))
            viewHolder.imgSwitch.setChecked(false);
        else {
            viewHolder.imgSwitch.setChecked(true);
        }

        viewHolder.imgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    setSwitchState("01", position);
                } else {
                    setSwitchState("00", position);
                }
                viewHolder.imgSwitch.setTag(isChecked);
            }
        });


        Animation animation = AnimationUtils.loadAnimation(activity, (position > lastPosition) ? R.anim.up_from_botttom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private void setSwitchState(String state, int position){
        this.itemStatus.put(movieItems.get(position), state);
        notifyDataSetChanged();
    }

    static class ViewHolderItem {
        TextView textViewItem;
        SwitchButton imgSwitch;
    }

}




/*
public class DemoSwitchListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private int lastPosition = -1;
    private static int VIEW_TYPE;

    public DemoSwitchListAdapter(Activity context) {
        super(context, R.layout.demo_list_item);

        this.context=context;
    }

    public void setViewType(int type) {
        VIEW_TYPE = type;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView;
        if(VIEW_TYPE == 0) {
            rowView=inflater.inflate(R.layout.demo_list_item, null,true);
        } else {
            rowView=inflater.inflate(R.layout.demo_grid_item, null,true);
        }


       // View rowView=inflater.inflate(R.layout.demo_list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtSwitchName);
       // ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_botttom : R.anim.down_from_top);
        rowView.startAnimation(animation);
        lastPosition = position;
        txtTitle.setText("Switch_"+position);
        return rowView;

    };
}*/
