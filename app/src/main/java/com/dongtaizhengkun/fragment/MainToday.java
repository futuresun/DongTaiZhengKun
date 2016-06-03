package com.dongtaizhengkun.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dongtaizhengkun.R;
import com.dongtaizhengkun.utils.DBHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/2.
 */
public class MainToday extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.todayid)
    private Button todayBtn;
    @ViewInject(R.id.netid)
    private Button netBtn;
    @ViewInject(R.id.todaylist)
    private ListView todaylist;

    DBHelper dbHelper;
    Cursor cursor;//当前查询结果

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today, null);
        ViewUtils.inject(this, view);
        todayBtn.setOnClickListener(this);
        netBtn.setOnClickListener(this);
        dbHelper = DBHelper.getInstance(inflater.getContext());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.todayid:
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                cursor = dbHelper.getReadableDatabase().query("today_orders", new String[]{"senddate", "recvname"}, "senddate like ?",
                        new String[]{simpleDateFormat.format(date) + "%"}, null, null, null, null);
                List<String> data = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    data.add(cursor.getString(0) + "  收货方: " + cursor.getString(1));
                }
                cursor.close();
                sqLiteDatabase.close();
                todaylist.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.listitem, data));
                MyTextWatcher.setListViewHeightBasedOnChildren(todaylist);
                break;
            case R.id.netid:
                break;
            default:
                break;
        }
    }
}
