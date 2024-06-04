package com.tsafrir.sagi.schoolproject;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    List<SideListItem> rowItem;

    public CustomAdapter(Context context, List<SideListItem> rowItem){
        this.context = context;
        this.rowItem = rowItem;
    }

    private class ViewHolder{
        ImageView icon;
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);

            SideListItem row_pos = rowItem.get(position);

            holder.icon.setImageResource(row_pos.getIcon());
            holder.title.setText(row_pos.getTitle());
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public int getCount(){
        return rowItem.size();
    }

    @Override
    public Object getItem(int position){
        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position){
        return rowItem.indexOf(getItem(position));
    }

}
