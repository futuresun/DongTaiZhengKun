package com.dongtaizhengkun.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dongtaizhengkun.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/6/29.
 *
 */
public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> data;

    private static HashMap<Integer, Boolean> isSelected;



    public ListViewAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < data.size(); i++) {
            getIsSelected().put(i, false);
        }
    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.datalist, null);
            viewHolder = new ViewHolder();
            viewHolder.cb = (CheckBox)convertView.findViewById(R.id.itemid);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.goodnumid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvName.setText(data.get(position));

        viewHolder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);
                } else {
                    isSelected.put(position, true);
                    setIsSelected(isSelected);
                }
            }
        });

        viewHolder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ListViewAdapter.isSelected = isSelected;
    }
}

