package com.dongtaizhengkun;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dongtaizhengkun.utils.DBAdapter;
import com.dongtaizhengkun.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button commit;
    private ListView list_recv;
    private EditText recvname;
    private EditText recvnum;
    private EditText sendname;
    private EditText sendnum;
    private EditText sendid;
    private EditText outward;
    private EditText product_name;
    private EditText count;
    private EditText packing;
    private EditText insurance;
    private EditText prepay;
    private EditText afterpay;
    private EditText unrecev;
    private EditText remark;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        commit = (Button) findViewById(R.id.commit);

        recvname = (EditText) findViewById(R.id.recvname);
        recvnum = (EditText) findViewById(R.id.recvnum);
        sendname = (EditText) findViewById(R.id.sendname);
        sendnum = (EditText) findViewById(R.id.sendnum);
        sendid = (EditText) findViewById(R.id.sendid);
        outward = (EditText) findViewById(R.id.outward);
        product_name = (EditText) findViewById(R.id.product_name);
        count = (EditText) findViewById(R.id.count);
        packing = (EditText) findViewById(R.id.packing);
        insurance = (EditText) findViewById(R.id.insurance);
        prepay = (EditText) findViewById(R.id.prepay);
        afterpay = (EditText) findViewById(R.id.afterpay);
        unrecev = (EditText) findViewById(R.id.unrecev);
        remark = (EditText) findViewById(R.id.remark);

        list_recv = (ListView) findViewById(R.id.list_recv);
        list_recv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recvname.setText(((TextView) view).getText().toString());
                MainActivity.this.findViewById(R.id.list_recvout).setVisibility(View.GONE);
                recvname.setSelection(recvname.getText().length());
            }
        });

        commit.setOnClickListener(this);

        dbHelper = DBHelper.getInstance(this);

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into recv_contact values('冯啊', 18328346188, 1)," +
                "('冯的', 18318396610, 2),('梁就', 18745638712, 8)," +
                "('冯飞', 18328396610, 3),('冯跑', 18765638712, 3)," +
                "('冯发', 18338396610, 4),('冯我', 18775638712, 4)," +
                "('冯个', 18348396610, 5),('冯人', 18785638712, 2)," +
                "('冯就', 18358396610, 6),('冯要', 18795638712, 2)," +
                "('李阳', 17234987542, 6)");


        recvname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = recvname.getText().toString();
                List<String> data = new ArrayList<String>();
                if (!key.equals("")) {
                    data.add(key);
                    Cursor cursor = dbHelper.getReadableDatabase().query("recv_contact", new String[]{"name", "hot"}, "name like ?",
                            new String[]{"%" + key + "%"}, null, null, "hot desc", "0,4");
                    int count1 = 0;
                    while (count1<4 && cursor.moveToNext()) {
                        data.add(cursor.getString(0));
                        count1++;
                    }
                    cursor.close();
                    list_recv.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.listitem, data));
                    setListViewHeightBasedOnChildren(list_recv);
                    MainActivity.this.findViewById(R.id.list_recvout).setVisibility(View.VISIBLE);
                } else {
                    MainActivity.this.findViewById(R.id.list_recvout).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recvname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MainActivity.this.findViewById(R.id.list_recvout).setVisibility(View.GONE);
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {

    }
}
