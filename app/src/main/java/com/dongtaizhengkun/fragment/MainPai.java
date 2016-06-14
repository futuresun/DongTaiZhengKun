package com.dongtaizhengkun.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongtaizhengkun.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
public class MainPai extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.recv_name)
    private EditText recv_name;
    @ViewInject(R.id.recv_num)
    private EditText recv_num;
    @ViewInject(R.id.search_pai)
    private Button search_pai;
    @ViewInject(R.id.pailist)
    private ListView pailist;

    List<List<String>> data1;
    List<String> data2;
    List<String> datalist = new ArrayList<String>();

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pailist.setAdapter(new ArrayAdapter<String>(MainPai.this.getContext(), R.layout.listitem, datalist));
            MyTextWatcher.setListViewHeightBasedOnChildren(pailist);
            return true;
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pai, null);
        ViewUtils.inject(this, view);

        pailist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View myView = MainPai.this.getLayoutInflater(null).inflate(R.layout.dialogpai, null);
                TextView sdate = (TextView) myView.findViewById(R.id.stime);
                TextView rname = (TextView) myView.findViewById(R.id.rname);
                TextView rnum = (TextView) myView.findViewById(R.id.rnum);
                TextView dest = (TextView) myView.findViewById(R.id.rdes);
                TextView out = (TextView) myView.findViewById(R.id.out);
                TextView sname = (TextView) myView.findViewById(R.id.sname);
                TextView snum = (TextView) myView.findViewById(R.id.snum);
                TextView sid = (TextView) myView.findViewById(R.id.sid);
                TextView pname = (TextView) myView.findViewById(R.id.pname);
                TextView packname = (TextView) myView.findViewById(R.id.packname);
                TextView scount = (TextView) myView.findViewById(R.id.scount);
                TextView sinsurance = (TextView) myView.findViewById(R.id.sinsurance);
                TextView sprepay = (TextView) myView.findViewById(R.id.sprepay);
                TextView safterpay = (TextView) myView.findViewById(R.id.safterpay);
                TextView sunpay = (TextView) myView.findViewById(R.id.sunpay);

                sdate.setText(data2.get(0));
                rname.setText(data2.get(1));
                rnum.setText(data2.get(2));
                dest.setText(data2.get(3));
                out.setText(data2.get(4));
                sname.setText(data2.get(5));
                snum.setText(data2.get(6));
                sid.setText(data2.get(7));
                pname.setText(data2.get(8));
                packname.setText(data2.get(9));
                scount.setText(data2.get(10));
                sinsurance.setText(data2.get(11));
                sprepay.setText(data2.get(12));
                safterpay.setText(data2.get(13));
                sunpay.setText(data2.get(14));


                AlertDialog.Builder builder = new AlertDialog.Builder(MainPai.this.getContext());
                builder.setTitle("订单详情").setView(myView).setPositiveButton("签收", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EditText editText = new EditText(MainPai.this.getContext());
                        new AlertDialog.Builder(MainPai.this.getContext()).setTitle("收款金额")
                                .setView(editText).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Date date = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                                try {
                                    String data = "sendtime=" + URLEncoder.encode(data2.get(0), "utf-8") + "&recvtime=" + URLEncoder.encode(simpleDateFormat.format(date), "utf-8") + "&recvmoney=" + URLEncoder.encode(editText.getText().toString(), "utf-8");
                                    //String urlEncoding = "http://192.168.1.13:8080/dongtai/servlet/CheckOutServlet?" + data;
                                    String urlEncoding = "http://23.105.215.22:8080/dongtai/servlet/CheckOutServlet?" + data;
                                    URL url = null;
                                    url = new URL(urlEncoding);
                                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                    httpUrlConnection.setRequestMethod("GET");
                                    //建立TCP连接的
                                    httpUrlConnection.connect();
                                    //这里才是真正进行发送
                                    int code = httpUrlConnection.getResponseCode();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        search_pai.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        final String rname = recv_name.getText().toString();
        final String rnum = recv_num.getText().toString();
        SharedPreferences settings = this.getContext().getSharedPreferences("netsetting", 0);
        final String netname = settings.getString("netname", "");

        new Thread() {
            @Override
            public void run() {
                try {
                    String data = "rname=" + URLEncoder.encode(rname, "utf-8") + "&rnum=" + URLEncoder.encode(rnum, "utf-8") + "&netname=" + URLEncoder.encode(netname, "utf-8");
                    //String urlEncoding = "http://192.168.1.13:8080/dongtai/servlet/PaiDanServlet?" + data;
                    String urlEncoding = "http://23.105.215.22:8080/dongtai/servlet/PaiDanServlet?" + data;
                    URL url = new URL(urlEncoding);
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    //建立TCP连接的
                    httpUrlConnection.connect();
                    //这里才是真正进行发送
                    int code = httpUrlConnection.getResponseCode();
                    InputStream inputStream = httpUrlConnection.getInputStream();

                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null) {
                        responseStrBuilder.append(inputStr);
                    }
                    JSONArray jsonArray = new JSONArray(responseStrBuilder.toString());


                    data1 = new ArrayList<List<String>>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        data2 = new ArrayList<String>();
                        datalist.add(jsonObject.getString("senddate") + "  收货方: " + jsonObject.getString("recvname"));
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
                        data1.add(data2);
                    }
                    handler.sendMessage(handler.obtainMessage());
                    //System.out.println(jsonArray.length());
                    //System.out.println(jsonArray.toString());
                    //System.out.println("code------------>" + code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
