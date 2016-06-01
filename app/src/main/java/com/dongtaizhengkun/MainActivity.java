package com.dongtaizhengkun;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

        list_recv = (ListView)findViewById(R.id.list_recv);

        commit.setOnClickListener(this);

        DBHelper dbHelper = new DBHelper(this,"ok",null,1);

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into recv_contact values('冯麒', 18328346188)," +
                "('冯敬', 18328396610),('梁贵', 18745638712)," +
                "('李阳', 17234987542)");


        recvname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = recvname.getText().toString();
                List<String> data = new ArrayList<String>();
                data.add("冯麒");
                data.add("冯敬");
                data.add("冯敬");

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.listitem, null);
                TextView textView = (TextView)view.findViewById(R.id.hehe);



                list_recv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1,data));
                list_recv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
