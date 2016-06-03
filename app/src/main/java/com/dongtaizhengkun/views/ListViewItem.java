package com.dongtaizhengkun.views;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/1.
 */
public class ListViewItem extends TextView {

    public ListViewItem(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
