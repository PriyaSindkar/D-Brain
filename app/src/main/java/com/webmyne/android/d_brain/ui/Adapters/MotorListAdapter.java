package com.webmyne.android.d_brain.ui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.webmyne.android.d_brain.R;
import com.webmyne.android.d_brain.ui.Helpers.AnimationHelper;
import com.webmyne.android.d_brain.ui.Widgets.MotorSwitchItem;

import java.util.ArrayList;


public class MotorListAdapter extends RecyclerView.Adapter<MotorListAdapter.ViewHolder> {
    static int VIEW_TYPE;
    public Context _ctx;
    //public onRotateSwitchesListener _rotateClick;
    ArrayList<MotorSwitchItem> list;


    // 0 - for List Type
    // 1 - for Gri Type

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class ListViewHolder extends ViewHolder{
        public  TextView txtMotorName;
        public  ImageView imgRotateSwitches, leftArrow, rightArrow;
        public boolean isLeftRight = true;
        public MotorSwitchItem motorSwitchItem;

        public ListViewHolder ( View view ) {
            super ( view );
            this.motorSwitchItem = (MotorSwitchItem) view.findViewById(R.id.motorSwitchItem);
            this.txtMotorName = (TextView) view.findViewById(R.id.txtMotorName);
            this.imgRotateSwitches = (ImageView) view.findViewById(R.id.imgRotateSwitches);
            this.rightArrow = (ImageView) view.findViewById(R.id.imgMotorRightArrow);
            this.leftArrow = (ImageView) view.findViewById(R.id.imgMotorLeftArrow);

        }
    }

    public class GridViewHolder extends ViewHolder{
        public  TextView textView;
        //public MotorSwitchItem motorSwitchItem;
        public  ImageView imgRotateSwitches;


        public GridViewHolder ( View view ) {
            super ( view );
            this.textView = (TextView) view.findViewById(R.id.txtMotorName);
            //this.motorSwitchItem = (MotorSwitchItem) view.findViewById(R.id.motorSwitchItem);
            this.imgRotateSwitches = (ImageView) view.findViewById(R.id.imgRotateSwitches);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(VIEW_TYPE==0)
            return 0;
        else
            return 1;
    }

  /*  public void add(int position, String item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }*/



    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setType(int type){
        VIEW_TYPE = type;
    }

    public MotorListAdapter(Context ctx) {
        this._ctx = ctx;
    }

    public MotorListAdapter(Context ctx, ArrayList<MotorSwitchItem> _list) {
        this._ctx = ctx;
        this.list = _list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        ViewHolder vh;

        LayoutInflater mInflater = LayoutInflater.from (parent.getContext());
        switch (viewType) {

            case 0:
                ViewGroup viewgroup1 = ( ViewGroup ) mInflater.inflate ( R.layout.motor_list_item, parent, false );
                ListViewHolder listHolder = new ListViewHolder (viewgroup1);
                return listHolder;
            case 1:
                ViewGroup viewgroup2 = ( ViewGroup ) mInflater.inflate(R.layout.motor_list_grid_item, parent, false);
                GridViewHolder gridHolder = new GridViewHolder (viewgroup2);
                return gridHolder;
            default:
                ViewGroup viewgroup3 = ( ViewGroup ) mInflater.inflate ( R.layout.motor_list_grid_item, parent, false );
                GridViewHolder gridHolder1 = new GridViewHolder (viewgroup3);
                return gridHolder1;
        }

    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        switch (viewHolder.getItemViewType () ) {
            case 0:
               final ListViewHolder listHolder = ( ListViewHolder ) viewHolder;
                listHolder.txtMotorName.setText("Motor "+ position);

                /*ImageView imgRotateSwitches = listHolder.imgRotateSwitches;
                final ImageView leftArrow =  listHolder.leftArrow;
                final ImageView rightArrow = listHolder.rightArrow;*/

              // ((TextView)listHolder.motorSwitchItem.findViewById(R.id.txtMotorName)).setText("Motor " + position);


                /*((ImageView)listHolder.motorSwitchItem.findViewById(R.id.imgRotateSwitches)).setTag(listHolder);
                ((ImageView)listHolder.motorSwitchItem.findViewById(R.id.imgRotateSwitches)).setOnClickListener(clickListener);*/


                    /*ImageView imgRotateSwitches = (ImageView) listHolder.motorSwitchItem.findViewById(R.id.imgRotateSwitches);
                    final ImageView leftArrow = (ImageView) listHolder.motorSwitchItem.findViewById(R.id.imgMotorLeftArrow);
                    final ImageView rightArrow = (ImageView) listHolder.motorSwitchItem.findViewById(R.id.imgMotorRightArrow);


                    imgRotateSwitches.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("TAG", "item clicked " + position);

                            AnimationHelper animationHelper = new AnimationHelper();

                            if (listHolder.isLeftRight) {
                                animationHelper.rotateViewClockwiseLeftToUp(leftArrow);
                                animationHelper.rotateViewClockwiseLeftToUp(rightArrow);
                            } else {
                                animationHelper.rotateViewAntiClockwiseLeftToUp(leftArrow);
                                animationHelper.rotateViewAntiClockwiseLeftToUp(rightArrow);
                            }
                            listHolder.isLeftRight = !listHolder.isLeftRight;
                            notifyDataSetChanged();
                           // _rotateClick.onRotateSwitchesClick(position);
                        }

                    });*/



                listHolder.imgRotateSwitches.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TAG", "item clicked " + position);

                        AnimationHelper animationHelper = new AnimationHelper();

                        if(listHolder.isLeftRight) {
                            animationHelper.rotateViewClockwiseLeftToUp(listHolder.leftArrow);
                            animationHelper.rotateViewClockwiseLeftToUp(listHolder.rightArrow);
                        } else {
                            animationHelper.rotateViewAntiClockwiseLeftToUp(listHolder.leftArrow);
                            animationHelper.rotateViewAntiClockwiseLeftToUp(listHolder.rightArrow);
                        }
                        listHolder.isLeftRight = !listHolder.isLeftRight;
                    }
                });



                break;
            case 1:
                final GridViewHolder groupViewHolder = ( GridViewHolder ) viewHolder;

               /* groupViewHolder.motorSwitchItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupViewHolder.motorSwitchItem.rotateMotorButtons();
                        //_rotateClick.onRotateSwitchesClick(position, groupViewHolder.motorSwitchItem);
                    }
                });*/
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 33;
    }

    /*public void setRotateClickListener(onRotateSwitchesListener obj){
        this._rotateClick = obj;
    }*/





   /* View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        if(VIEW_TYPE == 0) {
            ListViewHolder listViewHolder = (ListViewHolder) view.getTag();
            Log.e("POS", String.valueOf(listViewHolder.getPosition()));
            Log.e("CLC","CKIC");

            ImageView left =  (ImageView)listViewHolder.motorSwitchItem.findViewById(R.id.imgMotorLeftArrow);
            ImageView right =  (ImageView)listViewHolder.motorSwitchItem.findViewById(R.id.imgMotorRightArrow);

            AnimationHelper animationHelper = new AnimationHelper();

           // if(listViewHolder.isLeftRight) {
                animationHelper.rotateViewClockwiseLeftToUp(left);
                animationHelper.rotateViewClockwiseLeftToUp(right);
           *//* } else {
                animationHelper.rotateViewAntiClockwiseLeftToUp(left);
                animationHelper.rotateViewAntiClockwiseLeftToUp(right);
            }
            listViewHolder.isLeftRight = !listViewHolder.isLeftRight;*//*
        }

        }
    };*/

}
