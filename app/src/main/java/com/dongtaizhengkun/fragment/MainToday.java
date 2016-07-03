package com.dongtaizhengkun.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.style.SubscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongtaizhengkun.R;
import com.dongtaizhengkun.WelcomeActivity;
import com.dongtaizhengkun.utils.Constant;
import com.dongtaizhengkun.utils.DBHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/6/2.
 */
public class MainToday extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.todayid)
    private Button todayBtn;
    @ViewInject(R.id.goodid)
    private EditText goodid;
    @ViewInject(R.id.changeid)
    private Button changeid;
    @ViewInject(R.id.netid)
    private Button netBtn;
    @ViewInject(R.id.todaylist)
    private ListView todaylist;

    DBHelper dbHelper;
    Cursor cursor;//当前查询结果

    List<List<String>> data1;
    List<String> data2;
    List<String> titleText;

    boolean changeFlag = false;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                Toast.makeText(MainToday.this.getContext(), "上传成功", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(MainToday.this.getContext(), "上传失败", Toast.LENGTH_LONG).show();
            return true;
        }
    });

    Handler handler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1)
                Toast.makeText(MainToday.this.getContext(), "查找失败", Toast.LENGTH_LONG).show();
            else if (msg.arg1 == 2)
                Toast.makeText(MainToday.this.getContext(), "无该订单", Toast.LENGTH_LONG).show();
            else {
                changeFlag = true;
                todaylist.setAdapter(new ArrayAdapter<String>(MainToday.this.getContext(), R.layout.listitem, titleText));
                MyTextWatcher.setListViewHeightBasedOnChildren(todaylist);
            }
            return true;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        //先判断是否进行了网点的配置
        SharedPreferences settings = inflater.getContext().getSharedPreferences("netsetting", 0);
        if (settings.getString("netnum", "").equals("") || settings.getString("netname", "").equals("") || settings.getString("authors", "").equals("")) {
            new AlertDialog.Builder(MainToday.this.getContext()).setTitle("未配置网点信息,请先登录").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.support.v4.app.FragmentManager fragmentManager = MainToday.this.getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragmentId, new MainHome());
                    transaction.commit();
                    WelcomeActivity.mainBtn.setChecked(true);
                }
            }).show();
        }

        View view = inflater.inflate(R.layout.today, null);
        ViewUtils.inject(this, view);
        todayBtn.setOnClickListener(this);
        changeid.setOnClickListener(this);
        netBtn.setOnClickListener(this);
        dbHelper = DBHelper.getInstance(inflater.getContext());

        //goodid.setText("0-XX-160626-0-");

        todaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View myView = MainToday.this.getLayoutInflater(null).inflate(R.layout.dialog, null);

                TextView sdate = (TextView) myView.findViewById(R.id.stime);
                final EditText rname = (EditText) myView.findViewById(R.id.rname);
                final EditText rnum = (EditText) myView.findViewById(R.id.rnum);
                final EditText dest = (EditText) myView.findViewById(R.id.rdes);
                final EditText out = (EditText) myView.findViewById(R.id.out);
                final EditText sname = (EditText) myView.findViewById(R.id.sname);
                final EditText snum = (EditText) myView.findViewById(R.id.snum);
                final EditText sid = (EditText) myView.findViewById(R.id.sid);
                final EditText pname = (EditText) myView.findViewById(R.id.pname);
                final EditText packname = (EditText) myView.findViewById(R.id.packname);
                final EditText scount = (EditText) myView.findViewById(R.id.scount);
                final EditText sinsurance = (EditText) myView.findViewById(R.id.sinsurance);
                final EditText sprepay = (EditText) myView.findViewById(R.id.sprepay);
                final EditText safterpay = (EditText) myView.findViewById(R.id.safterpay);
                final EditText sunpay = (EditText) myView.findViewById(R.id.sunpay);

                final String listtime = data1.get(position).get(0);
                final String recvname = data1.get(position).get(1);
                final String recvnum = data1.get(position).get(2);
                final String des = data1.get(position).get(3);
                final String sout = data1.get(position).get(4);
                final String sdname = data1.get(position).get(5);
                final String sdnum = data1.get(position).get(6);
                final String sdid = data1.get(position).get(7);
                final String pinname = data1.get(position).get(8);
                final String packingname = data1.get(position).get(9);
                final String count = data1.get(position).get(10);
                final String insurance = data1.get(position).get(11);
                final String prepay = data1.get(position).get(12);
                final String afterpay = data1.get(position).get(13);
                final String unpay = data1.get(position).get(14);
                final String xiugaibeizhu = data1.get(position).get(16);

                sdate.setText(listtime);
                rname.setText(recvname);
                rnum.setText(recvnum);
                dest.setText(des);
                out.setText(sout);
                sname.setText(sdname);
                snum.setText(sdnum);
                sid.setText(sdid);
                pname.setText(pinname);
                packname.setText(packingname);
                scount.setText(count);
                sinsurance.setText(insurance);
                sprepay.setText(prepay);
                safterpay.setText(afterpay);
                sunpay.setText(unpay);


                //最开始是系统的键盘输入方式没设置正确，导致不弹出软键盘
                AlertDialog.Builder builder = new AlertDialog.Builder(MainToday.this.getContext());
                builder.setTitle("订单详情").setView(myView)
                        .setPositiveButton("修改订单", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean flag = false;
                                String xiugai = xiugaibeizhu;
                                final ContentValues contentValues = new ContentValues();
                                contentValues.put("recvname", rname.getText().toString());
                                if (!rname.getText().toString().equals(recvname)) {
                                    xiugai = xiugai + "[(收货人)" + recvname + "->" + rname.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("recvnum", rnum.getText().toString());
                                if (!rnum.getText().toString().equals(recvnum)) {
                                    xiugai = xiugai + "[(收货电话)" + recvnum + "->" + rnum.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("sendname", sname.getText().toString());
                                if (!sname.getText().toString().equals(sdname)) {
                                    xiugai = xiugai + "[(发货人)" + sdname + "->" + sname.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("sendnum", snum.getText().toString());
                                if (!snum.getText().toString().equals(sdnum)) {
                                    xiugai = xiugai + "[(发货人电话)" + sdnum + "->" + snum.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("sendid", sid.getText().toString());
                                if (!sid.getText().toString().equals(sdid)) {
                                    xiugai = xiugai + "[(发货人ID)" + sdid + "->" + sid.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("destination", dest.getText().toString());
                                if (!dest.getText().toString().equals(des)) {
                                    xiugai = xiugai + "[(到站)" + des + "->" + dest.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("out", out.getText().toString());
                                if (!out.getText().toString().equals(sout)) {
                                    xiugai = xiugai + "[(外转)" + sout + "->" + out.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("pinname", pname.getText().toString());
                                if (!pname.getText().toString().equals(pinname)) {
                                    xiugai = xiugai + "[(品名)" + pinname + "->" + pname.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("package", packname.getText().toString());
                                if (!packname.getText().toString().equals(packingname)) {
                                    xiugai = xiugai + "[(包装)" + packingname + "->" + packname.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("count", scount.getText().toString());
                                if (!scount.getText().toString().equals(count)) {
                                    xiugai = xiugai + "[(件数)" + count + "->" + scount.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("baojia", sinsurance.getText().toString());
                                if (!sinsurance.getText().toString().equals(insurance)) {
                                    xiugai = xiugai + "[(保价)" + insurance + "->" + sinsurance.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("xianfu", sprepay.getText().toString());
                                if (!sprepay.getText().toString().equals(prepay)) {
                                    xiugai = xiugai + "[(现付)" + prepay + "->" + sprepay.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("tifu", safterpay.getText().toString());
                                if (!safterpay.getText().toString().equals(afterpay)) {
                                    xiugai = xiugai + "[(提付)" + afterpay + "->" + safterpay.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("daishou", sunpay.getText().toString());
                                if (!sunpay.getText().toString().equals(unpay)) {
                                    xiugai = xiugai + "[(代收款)" + unpay + "->" + sunpay.getText().toString() + "] ";
                                    flag = true;
                                }
                                contentValues.put("net", "NO");
                                contentValues.put("xiugaibeizhu", xiugai);
                                if (!changeFlag) {
                                    SQLiteDatabase sqLiteDatabase = DBHelper.getInstance(MainToday.this.getContext()).getReadableDatabase();
                                    sqLiteDatabase.update("today_orders", contentValues, "senddate = ?", new String[]{listtime});
                                    MainToday.this.onClick(todayBtn);
                                } else {
                                    new AsyncTask<Void, Integer, Void>() {
                                        private ProgressDialog dialog = null;

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                String data = "senddate=" + URLEncoder.encode(data2.get(0), "utf-8") + "&recvname=" + URLEncoder.encode((String) contentValues.get("recvname"), "utf-8") + "&recvnum=" + URLEncoder.encode((String) contentValues.get("recvnum"), "utf-8")
                                                        + "&destination=" + URLEncoder.encode((String) contentValues.get("destination"), "utf-8") + "&out=" + URLEncoder.encode((String) contentValues.get("out"), "utf-8")
                                                        + "&sendname=" + URLEncoder.encode((String) contentValues.get("sendname"), "utf-8") + "&sendnum=" + URLEncoder.encode((String) contentValues.get("sendnum"), "utf-8")
                                                        + "&sendid=" + URLEncoder.encode((String) contentValues.get("sendid"), "utf-8") + "&pinname=" + URLEncoder.encode((String) contentValues.get("pinname"), "utf-8")
                                                        + "&package=" + URLEncoder.encode((String) contentValues.get("package"), "utf-8") + "&count=" + URLEncoder.encode((String) contentValues.get("count"), "utf-8")
                                                        + "&baojia=" + URLEncoder.encode((String) contentValues.get("baojia"), "utf-8") + "&xianfu=" + URLEncoder.encode((String) contentValues.get("xianfu"), "utf-8")
                                                        + "&tifu=" + URLEncoder.encode((String) contentValues.get("tifu"), "utf-8") + "&daishou=" + URLEncoder.encode((String) contentValues.get("daishou"), "utf-8")
                                                        + "&beizhu=" + URLEncoder.encode(data2.get(15), "utf-8") + "&xiugaibeizhu=" + URLEncoder.encode((String) contentValues.get("xiugaibeizhu"), "utf-8")
                                                        + "&deleteornot=" + URLEncoder.encode("NO", "utf-8") + "&goodNum=" + URLEncoder.encode(data2.get(17), "utf-8")
                                                        + "&netName=" + URLEncoder.encode(data2.get(18), "utf-8");
                                                String urlEncoding = Constant.URL_ROOT + "DongTaiServlet?" + data;
                                                URL url = new URL(urlEncoding);
                                                System.out.println("urlEncoding------------>" + urlEncoding);
                                                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                                httpUrlConnection.setRequestMethod("GET");
                                                httpUrlConnection.setConnectTimeout(10000);
                                                httpUrlConnection.setReadTimeout(5000);
                                                httpUrlConnection.connect();
                                                //注意这里要获取返回的数据，否则不能成功提交????????
                                                int code = httpUrlConnection.getResponseCode();
                                                if (code != 200) {
                                                    publishProgress(0);
                                                    Message message = handler.obtainMessage();
                                                    message.arg1 = 1;
                                                    handler.sendMessage(message);
                                                    return null;
                                                }

                                            } catch (Exception e) {
                                                Message message = handler.obtainMessage();
                                                message.arg1 = 1;
                                                handler.sendMessage(message);
                                                e.printStackTrace();
                                            }
                                            changeFlag = false;
                                            publishProgress(0);
                                            Message message = handler.obtainMessage();
                                            message.arg1 = 0;
                                            handler.sendMessage(message);
                                            return null;
                                        }

                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            dialog = ProgressDialog.show(MainToday.this.getContext(), "", "正在上传，请稍等...", false);
                                        }

                                        @Override
                                        protected void onProgressUpdate(Integer... values) {
                                            super.onProgressUpdate(values);
                                            dialog.dismiss();
                                        }
                                    }.execute();
                                }
                            }
                        });
                if (!changeFlag) {
                    builder.setNegativeButton("删除订单", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(MainToday.this.getContext()).setTitle("删除订单").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SQLiteDatabase sqLiteDatabase = DBHelper.getInstance(MainToday.this.getContext()).getReadableDatabase();
                                    sqLiteDatabase.execSQL("update today_orders set deleteornot='YES',net='NO' where senddate='" + listtime + "'");
                                    MainToday.this.onClick(todayBtn);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                        }
                    });
                } else {
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
                builder.show();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        SharedPreferences settings = MainToday.this.getContext().getSharedPreferences("netsetting", 0);
        String netnum = settings.getString("netnum", "");
        switch (v.getId()) {
            case R.id.todayid:
                changeFlag = false;
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                cursor = dbHelper.getReadableDatabase().query("today_orders", new String[]{"senddate", "recvname", "recvnum",
                        "destination", "out", "sendname", "sendnum", "sendid", "pinname", "package", "count", "baojia",
                        "xianfu", "tifu", "daishou", "beizhu", "xiugaibeizhu", "goodno", "sendname"}, "senddate like ? and deleteornot='NO'", new String[]{simpleDateFormat.format(date) + "%"}, null, null, null, null);
                List<String> data = new ArrayList<String>();
                data1 = new ArrayList<List<String>>();
                while (cursor.moveToNext()) {
                    data2 = new ArrayList<String>();
                    data.add(cursor.getString(0) + "  收货方: " + cursor.getString(1));
                    data2.add(cursor.getString(0));
                    data2.add(cursor.getString(1));
                    data2.add(cursor.getString(2));
                    data2.add(cursor.getString(3));
                    data2.add(cursor.getString(4));
                    data2.add(cursor.getString(5));
                    data2.add(cursor.getString(6));
                    data2.add(cursor.getString(7));
                    data2.add(cursor.getString(8));
                    data2.add(cursor.getString(9));
                    data2.add(cursor.getString(10));
                    data2.add(cursor.getString(11));
                    data2.add(cursor.getString(12));
                    data2.add(cursor.getString(13));
                    data2.add(cursor.getString(14));
                    data2.add(cursor.getString(15));
                    data2.add(cursor.getString(16));
                    data2.add(cursor.getString(17));
                    data2.add(cursor.getString(18));
                    data1.add(data2);
                }
                cursor.close();
                sqLiteDatabase.close();
                todaylist.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.listitem, data));
                MyTextWatcher.setListViewHeightBasedOnChildren(todaylist);
                break;

            case R.id.changeid:
                //判断订单号是否合法
                //Pattern pattern = Pattern.compile("([0-9]*)-([0-9]*)-([0-9]*)-([0-9]*)-([0-9]*)");
                Pattern pattern = Pattern.compile("([0-9]*)-(X*)-([0-9]*)-([0-9]*)-([0-9]*)");
                final String order = goodid.getText().toString();
                Matcher matcher = pattern.matcher(order);
                if (!matcher.find()) {
                    Toast.makeText(this.getContext(), "订单号格式不正确", Toast.LENGTH_LONG).show();
                } else if (!netnum.equals(matcher.group(1))) {
                    Toast.makeText(this.getContext(), "无访问该订单权限", Toast.LENGTH_LONG).show();
                } else {
                    //判断是否是今日的订单
                    String sdate = matcher.group(3);
                    //Toast.makeText(this.getContext(), sdate, Toast.LENGTH_LONG).show();
                    Date tdate = new Date();
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyMMdd");
                    if (sdate.equals(simpleDateFormat1.format(tdate))) {

                        cursor = dbHelper.getReadableDatabase().query("today_orders", new String[]{"senddate", "recvname", "recvnum",
                                "destination", "out", "sendname", "sendnum", "sendid", "pinname", "package", "count", "baojia",
                                "xianfu", "tifu", "daishou", "beizhu", "xiugaibeizhu", "goodno", "sendname"}, "goodno=? and deleteornot='NO'", new String[]{goodid.getText().toString()}, null, null, null, null);
                        List<String> sdata = new ArrayList<String>();
                        data1 = new ArrayList<List<String>>();
                        while (cursor.moveToNext()) {
                            data2 = new ArrayList<String>();
                            sdata.add(cursor.getString(0) + "  收货方: " + cursor.getString(1));
                            data2.add(cursor.getString(0));
                            data2.add(cursor.getString(1));
                            data2.add(cursor.getString(2));
                            data2.add(cursor.getString(3));
                            data2.add(cursor.getString(4));
                            data2.add(cursor.getString(5));
                            data2.add(cursor.getString(6));
                            data2.add(cursor.getString(7));
                            data2.add(cursor.getString(8));
                            data2.add(cursor.getString(9));
                            data2.add(cursor.getString(10));
                            data2.add(cursor.getString(11));
                            data2.add(cursor.getString(12));
                            data2.add(cursor.getString(13));
                            data2.add(cursor.getString(14));
                            data2.add(cursor.getString(15));
                            data2.add(cursor.getString(16));
                            data2.add(cursor.getString(17));
                            data2.add(cursor.getString(18));
                            data1.add(data2);
                        }

                        cursor.close();
                        sqLiteDatabase.close();
                        todaylist.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.listitem, sdata));
                        MyTextWatcher.setListViewHeightBasedOnChildren(todaylist);
                    } else {

                        new AsyncTask<Void, Integer, Void>() {
                            private ProgressDialog dialog = null;

                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    String data = "order=" + URLEncoder.encode(order, "utf-8");
                                    String urlEncoding = Constant.URL_ROOT + "ChangeServlet?" + data;
                                    URL url = new URL(urlEncoding);
                                    System.out.println("urlEncoding------------>" + urlEncoding);
                                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                    httpUrlConnection.setRequestMethod("GET");
                                    httpUrlConnection.setConnectTimeout(10000);
                                    httpUrlConnection.setReadTimeout(5000);
                                    httpUrlConnection.connect();
                                    //注意这里要获取返回的数据，否则不能成功提交????????
                                    int code = httpUrlConnection.getResponseCode();
                                    if (code != 200) {
                                        publishProgress(0);
                                        Message message = handler2.obtainMessage();
                                        message.arg1 = 1;
                                        handler2.sendMessage(message);
                                        System.out.println("----------------------------------back");
                                        return null;
                                    }
                                    InputStream inputStream = httpUrlConnection.getInputStream();

                                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                    StringBuilder responseStrBuilder = new StringBuilder();

                                    String inputStr;
                                    while ((inputStr = streamReader.readLine()) != null) {
                                        responseStrBuilder.append(inputStr);
                                    }
                                    JSONArray jsonArray = new JSONArray(responseStrBuilder.toString());
                                    if (jsonArray.length() == 0) {
                                        publishProgress(0);
                                        Message message = handler2.obtainMessage();
                                        message.arg1 = 2;
                                        handler2.sendMessage(message);
                                        System.out.println("----------------------------------null");
                                        return null;
                                    }

                                    data1 = new ArrayList<List<String>>();
                                    titleText = new ArrayList<String>();
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    data2 = new ArrayList<String>();
                                    titleText.add(jsonObject.getString("senddate") + "  收货方: " + jsonObject.getString("recvname"));
                                    data2.add(jsonObject.getString("senddate"));
                                    data2.add(jsonObject.getString("recvname"));
                                    data2.add(jsonObject.getString("recvnum"));
                                    data2.add(jsonObject.getString("destination"));
                                    data2.add(jsonObject.getString("out1"));
                                    data2.add(jsonObject.getString("sendname"));
                                    data2.add(jsonObject.getString("sendnum"));
                                    data2.add(jsonObject.getString("sendid"));
                                    data2.add(jsonObject.getString("pinname"));
                                    data2.add(jsonObject.getString("package"));
                                    data2.add(jsonObject.getString("count"));
                                    data2.add(jsonObject.getString("baojia"));
                                    data2.add(jsonObject.getString("xianfu"));
                                    data2.add(jsonObject.getString("tifu"));
                                    data2.add(jsonObject.getString("daishou"));
                                    data2.add(jsonObject.getString("beizhu"));
                                    data2.add(jsonObject.getString("xiugaibeizhu"));
                                    data2.add(jsonObject.getString("goodNum"));
                                    data2.add(jsonObject.getString("netName"));
                                    data1.add(data2);

                                } catch (Exception e) {
                                    Message message = handler2.obtainMessage();
                                    message.arg1 = 1;
                                    handler2.sendMessage(message);
                                    System.out.println("----------------------------------error");
                                }
                                publishProgress(0);
                                Message message = handler2.obtainMessage();
                                message.arg1 = 0;
                                handler2.sendMessage(message);
                                System.out.println("----------------------------------ok");
                                return null;
                            }

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                dialog = ProgressDialog.show(MainToday.this.getContext(), "", "正在查找，请稍等...", false);
                            }

                            @Override
                            protected void onProgressUpdate(Integer... values) {
                                super.onProgressUpdate(values);
                                dialog.dismiss();
                            }
                        }.execute();
                    }
                }

                break;
            case R.id.netid:

                new AsyncTask<Void, Integer, Void>() {
                    private ProgressDialog dialog = null;

                    @Override
                    protected Void doInBackground(Void... params) {
                        SharedPreferences settings = MainToday.this.getContext().getSharedPreferences("netsetting", 0);
                        String netname = settings.getString("netname", "");
                        try {

                            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                            Cursor netCursor = dbHelper.getReadableDatabase().query("today_orders", new String[]{"senddate", "recvname", "recvnum",
                                    "destination", "out", "sendname", "sendnum", "sendid", "pinname", "package", "count", "baojia",
                                    "xianfu", "tifu", "daishou", "beizhu", "xiugaibeizhu", "deleteornot", "goodno"}, "senddate like ? and net='NO'", new String[]{"%"}, null, null, null, null);

                            while (netCursor.moveToNext()) {

                                String data = "senddate=" + URLEncoder.encode(netCursor.getString(0), "utf-8") + "&recvname=" + URLEncoder.encode(netCursor.getString(1), "utf-8") + "&recvnum=" + URLEncoder.encode(netCursor.getString(2), "utf-8")
                                        + "&destination=" + URLEncoder.encode(netCursor.getString(3), "utf-8") + "&out=" + URLEncoder.encode(netCursor.getString(4), "utf-8")
                                        + "&sendname=" + URLEncoder.encode(netCursor.getString(5), "utf-8") + "&sendnum=" + URLEncoder.encode(netCursor.getString(6), "utf-8")
                                        + "&sendid=" + URLEncoder.encode(netCursor.getString(7), "utf-8") + "&pinname=" + URLEncoder.encode(netCursor.getString(8), "utf-8")
                                        + "&package=" + URLEncoder.encode(netCursor.getString(9), "utf-8") + "&count=" + URLEncoder.encode(netCursor.getString(10), "utf-8")
                                        + "&baojia=" + URLEncoder.encode(netCursor.getString(11), "utf-8") + "&xianfu=" + URLEncoder.encode(netCursor.getString(12), "utf-8")
                                        + "&tifu=" + URLEncoder.encode(netCursor.getString(13), "utf-8") + "&daishou=" + URLEncoder.encode(netCursor.getString(14), "utf-8")
                                        + "&beizhu=" + URLEncoder.encode(netCursor.getString(15), "utf-8") + "&xiugaibeizhu=" + URLEncoder.encode(netCursor.getString(16), "utf-8")
                                        + "&deleteornot=" + URLEncoder.encode(netCursor.getString(17), "utf-8") + "&goodNum=" + URLEncoder.encode(netCursor.getString(18), "utf-8")
                                        + "&netName=" + URLEncoder.encode(netname, "utf-8");
                                String urlEncoding = Constant.URL_ROOT + "DongTaiServlet?" + data;
                                URL url = new URL(urlEncoding);
                                System.out.println("urlEncoding------------>" + urlEncoding);
                                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                httpUrlConnection.setRequestMethod("GET");
                                httpUrlConnection.setConnectTimeout(10000);
                                httpUrlConnection.setReadTimeout(5000);
                                httpUrlConnection.connect();
                                //注意这里要获取返回的数据，否则不能成功提交????????
                                int code = httpUrlConnection.getResponseCode();
                                if (code != 200) {
                                    publishProgress(0);
                                    Message message = handler.obtainMessage();
                                    message.arg1 = 1;
                                    handler.sendMessage(message);
                                    return null;
                                }
                                //System.out.println("code------------>" + code);
                                sqLiteDatabase.execSQL("update today_orders set net='YES' where senddate='" + netCursor.getString(0) + "'");
                                Thread.sleep(200);
                            }
                        } catch (Exception e) {
                            Message message = handler.obtainMessage();
                            message.arg1 = 1;
                            handler.sendMessage(message);
                        }
                        publishProgress(0);
                        Message message = handler.obtainMessage();
                        message.arg1 = 0;
                        handler.sendMessage(message);
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = ProgressDialog.show(MainToday.this.getContext(), "", "正在上传，请稍等...", false);
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        dialog.dismiss();
                    }
                }.execute();


                /*new Thread() {
                    SharedPreferences settings = MainToday.this.getContext().getSharedPreferences("netsetting", 0);
                    String netname = settings.getString("netname", "");
                    @Override
                    public void run() {
                        try {

                            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                            Cursor netCursor = dbHelper.getReadableDatabase().query("today_orders", new String[]{"senddate", "recvname", "recvnum",
                                    "destination", "out", "sendname", "sendnum", "sendid", "pinname", "package", "count", "baojia",
                                    "xianfu", "tifu", "daishou","beizhu","xiugaibeizhu","deleteornot","goodno"}, "senddate like ? and net='NO'", new String[]{"%"}, null, null, null, null);

                            while (netCursor.moveToNext()) {
                                sqLiteDatabase.execSQL("update today_orders set net='YES' where senddate='" + netCursor.getString(0) + "'");
                                String data = "senddate=" + URLEncoder.encode(netCursor.getString(0), "utf-8") + "&recvname=" + URLEncoder.encode(netCursor.getString(1), "utf-8") + "&recvnum=" + URLEncoder.encode(netCursor.getString(2), "utf-8")
                                        + "&destination=" + URLEncoder.encode(netCursor.getString(3), "utf-8") + "&out=" + URLEncoder.encode(netCursor.getString(4), "utf-8")
                                        + "&sendname=" + URLEncoder.encode(netCursor.getString(5), "utf-8") + "&sendnum=" + URLEncoder.encode(netCursor.getString(6), "utf-8")
                                        + "&sendid=" + URLEncoder.encode(netCursor.getString(7), "utf-8") + "&pinname=" + URLEncoder.encode(netCursor.getString(8), "utf-8")
                                        + "&package=" + URLEncoder.encode(netCursor.getString(9), "utf-8") + "&count=" + URLEncoder.encode(netCursor.getString(10), "utf-8")
                                        + "&baojia=" + URLEncoder.encode(netCursor.getString(11), "utf-8") + "&xianfu=" + URLEncoder.encode(netCursor.getString(12), "utf-8")
                                        + "&tifu=" + URLEncoder.encode(netCursor.getString(13), "utf-8") + "&daishou=" + URLEncoder.encode(netCursor.getString(14), "utf-8")
                                        + "&beizhu=" + URLEncoder.encode(netCursor.getString(15), "utf-8") + "&xiugaibeizhu=" + URLEncoder.encode(netCursor.getString(16), "utf-8")
                                        + "&deleteornot=" + URLEncoder.encode(netCursor.getString(17), "utf-8") + "&goodNum=" + URLEncoder.encode(netCursor.getString(18), "utf-8")
                                        + "&netName=" + URLEncoder.encode(netname, "utf-8");
                                String urlEncoding = "http://192.168.1.11:8080/dongtai/servlet/DongTaiServlet?" + data;
                                //String urlEncoding = "http://23.105.215.22:8080/dongtai/servlet/DongTaiServlet?" + data;
                                URL url = new URL(urlEncoding);
                                System.out.println("urlEncoding------------>" + urlEncoding);
                                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                httpUrlConnection.setRequestMethod("GET");
                                //httpUrlConnection.setConnectTimeout(10000);
                                //httpUrlConnection.setReadTimeout(5000);
                                httpUrlConnection.connect();
                                //注意这里要获取返回的数据，否则不能成功提交????????
                                int code = httpUrlConnection.getResponseCode();
                                //System.out.println("code------------>" + code);
                                Thread.sleep(200);
                            }
                        } catch (Exception e) {
                            System.out.println("...................>" + e.getMessage().toString());
                        }

                    }
                }.start();*/
                break;
            default:
                break;
        }
    }
}
