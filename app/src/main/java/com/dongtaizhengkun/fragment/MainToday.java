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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.dongtaizhengkun.utils.DBHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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

    List<List<String>> data1;
    List<String> data2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today, null);
        ViewUtils.inject(this, view);
        todayBtn.setOnClickListener(this);
        netBtn.setOnClickListener(this);
        dbHelper = DBHelper.getInstance(inflater.getContext());

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
                final String xiugaibeizhu = data1.get(position).get(15);

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
                                ContentValues contentValues = new ContentValues();
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
                                SQLiteDatabase sqLiteDatabase = DBHelper.getInstance(MainToday.this.getContext()).getReadableDatabase();
                                sqLiteDatabase.update("today_orders", contentValues, "senddate = ?", new String[]{listtime});
                                MainToday.this.onClick(todayBtn);
                            }
                        }).setNegativeButton("删除订单", new DialogInterface.OnClickListener() {
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
                }).show();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.todayid:
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                cursor = dbHelper.getReadableDatabase().query("today_orders", new String[]{"senddate", "recvname", "recvnum",
                        "destination", "out", "sendname", "sendnum", "sendid", "pinname", "package", "count", "baojia",
                        "xianfu", "tifu", "daishou", "xiugaibeizhu"}, "senddate like ? and deleteornot='NO'", new String[]{simpleDateFormat.format(date) + "%"}, null, null, null, null);
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
                    data1.add(data2);
                }
                cursor.close();
                sqLiteDatabase.close();
                todaylist.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.listitem, data));
                MyTextWatcher.setListViewHeightBasedOnChildren(todaylist);
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
                        publishProgress(0);
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = ProgressDialog.show(MainToday.this.getContext(), "登录提示", "正在上传，请稍等...", false);
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
