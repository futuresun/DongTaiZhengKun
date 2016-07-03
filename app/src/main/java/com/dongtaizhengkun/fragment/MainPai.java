package com.dongtaizhengkun.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongtaizhengkun.DaiShouActivity;
import com.dongtaizhengkun.R;
import com.dongtaizhengkun.WelcomeActivity;
import com.dongtaizhengkun.utils.Constant;
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
 *
 */
public class MainPai extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.recv_name)
    private EditText recv_name;
    @ViewInject(R.id.recv_num)
    private EditText recv_num;
    @ViewInject(R.id.ordernum)
    private EditText ordernum;
    @ViewInject(R.id.wangdian)
    private EditText wangdian;
    @ViewInject(R.id.search_pai)
    private Button search_pai;
    @ViewInject(R.id.senddai)
    private Button senddai;
    @ViewInject(R.id.pailist)
    private ListView pailist;

    List<List<String>> data1;
    List<String> data2;
    List<String> datalist = new ArrayList<String>();

    ArrayList<String> datalistnew;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pailist.setAdapter(new ArrayAdapter<String>(MainPai.this.getContext(), R.layout.listitem, datalist));
            MyTextWatcher.setListViewHeightBasedOnChildren(pailist);
            return true;
        }
    });

    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                Toast.makeText(MainPai.this.getContext(), "签收成功", Toast.LENGTH_LONG).show();
            } else if (msg.arg1 == 1)
                Toast.makeText(MainPai.this.getContext(), "签收失败", Toast.LENGTH_LONG).show();
            return true;
        }
    });

    Handler handler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Toast.makeText(MainPai.this.getContext(), "网络异常", Toast.LENGTH_LONG).show();
            } else if (msg.arg1 == 2) {
                Toast.makeText(MainPai.this.getContext(), "无符合条件的订单", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MainPai.this.getContext(), DaiShouActivity.class);
                intent.putStringArrayListExtra("data", datalistnew);
                MainPai.this.startActivity(intent);
            }
            return true;
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //先判断是否进行了网点的配置
        SharedPreferences settings = inflater.getContext().getSharedPreferences("netsetting", 0);
        if (settings.getString("netnum", "").equals("") || settings.getString("netname", "").equals("") || settings.getString("authors", "").equals("")) {
            new AlertDialog.Builder(MainPai.this.getContext()).setTitle("未配置网点信息,请先登录").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.support.v4.app.FragmentManager fragmentManager = MainPai.this.getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragmentId, new MainHome());
                    transaction.commit();
                    WelcomeActivity.mainBtn.setChecked(true);
                }
            }).show();
        }


        View view = inflater.inflate(R.layout.pai, null);
        ViewUtils.inject(this, view);
        ordernum.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        //有相应权限的用户才能发放代收款
        /*if(!settings.getString("send", "").equals("是")) {
            senddai.setEnabled(false);
        }*/

        pailist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final List<String> data2 = data1.get(position);
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
                final TextView safterpay = (TextView) myView.findViewById(R.id.safterpay);
                final TextView sunpay = (TextView) myView.findViewById(R.id.sunpay);

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
                        TextView textView = new TextView(MainPai.this.getContext());
                        textView.setText(" 提付: " + safterpay.getText().toString() + " 元\n\n" + " 代收款: " + sunpay.getText().toString() + " 元");
                        new AlertDialog.Builder(MainPai.this.getContext()).setTitle("确认收款项")
                                .setView(textView).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new AsyncTask<Void, Integer, Void>() {
                                    private ProgressDialog dialog = null;

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        Date date = new Date();
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                                        try {
                                            String data = "sendtime=" + URLEncoder.encode(data2.get(0), "utf-8") + "&recvtime=" + URLEncoder.encode(simpleDateFormat.format(date), "utf-8");
                                            String urlEncoding = Constant.URL_ROOT + "CheckOutServlet?" + data;
                                            URL url = new URL(urlEncoding);
                                            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                            httpUrlConnection.setRequestMethod("GET");
                                            httpUrlConnection.setConnectTimeout(10000);
                                            httpUrlConnection.setReadTimeout(5000);
                                            //建立TCP连接的
                                            httpUrlConnection.connect();
                                            //这里才是真正进行发送
                                            int code = httpUrlConnection.getResponseCode();
                                            if (code != 200) {
                                                publishProgress(0);
                                                Message message = handler1.obtainMessage();
                                                message.arg1 = 1;
                                                handler1.sendMessage(message);
                                                return null;
                                            }
                                        } catch (Exception e) {
                                            publishProgress(0);
                                            Message message = handler1.obtainMessage();
                                            message.arg1 = 1;
                                            handler1.sendMessage(message);
                                        }
                                        publishProgress(0);
                                        Message message = handler1.obtainMessage();
                                        message.arg1 = 0;
                                        handler1.sendMessage(message);
                                        return null;
                                    }

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        dialog = ProgressDialog.show(MainPai.this.getContext(), "", "正在签收，请稍等...", false);
                                    }

                                    @Override
                                    protected void onProgressUpdate(Integer... values) {
                                        super.onProgressUpdate(values);
                                        dialog.dismiss();
                                        MainPai.this.onClick(search_pai);
                                    }
                                }.execute();


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
        senddai.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_pai:
                datalist.clear();
                final String rname = recv_name.getText().toString();
                final String rnum = recv_num.getText().toString();
                final String orderNum = ordernum.getText().toString();
                SharedPreferences settings = this.getContext().getSharedPreferences("netsetting", 0);
                final String netname = settings.getString("netname", "");

                new AsyncTask<Void, Integer, Void>() {
                    private ProgressDialog dialog = null;

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            String data = "rname=" + URLEncoder.encode(rname, "utf-8") + "&rnum=" + URLEncoder.encode(rnum, "utf-8") + "&orderNum=" + URLEncoder.encode(orderNum, "utf-8") + "&netname=" + URLEncoder.encode(netname, "utf-8");
                            String urlEncoding = Constant.URL_ROOT + "PaiDanServlet?" + data;
                            URL url = new URL(urlEncoding);
                            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                            httpUrlConnection.setRequestMethod("GET");
                            httpUrlConnection.setConnectTimeout(10000);
                            httpUrlConnection.setReadTimeout(5000);
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        publishProgress(0);
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = ProgressDialog.show(MainPai.this.getContext(), "", "正在加载，请稍等...", false);
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        dialog.dismiss();
                    }
                }.execute();
                break;
            case R.id.senddai:
                //获取待发放代收款的订单
                final String netNum = wangdian.getText().toString();
                if (netNum.equals("")) {
                    Toast.makeText(this.getContext(), "请输入网点编号", Toast.LENGTH_LONG).show();
                } else {
                    new AsyncTask<Void, Integer, Void>() {
                        private ProgressDialog dialog = null;

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                datalistnew = new ArrayList<String>();
                                String data = "netNum=" + URLEncoder.encode(netNum, "utf-8");
                                String urlEncoding = Constant.URL_ROOT + "PaiServlet?" + data;
                                URL url = new URL(urlEncoding);
                                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                httpUrlConnection.setRequestMethod("GET");
                                httpUrlConnection.setConnectTimeout(10000);
                                httpUrlConnection.setReadTimeout(5000);
                                //建立TCP连接的
                                httpUrlConnection.connect();
                                //这里才是真正进行发送
                                int code = httpUrlConnection.getResponseCode();
                                if (code != 200) {
                                    publishProgress(0);
                                    Message message = handler2.obtainMessage();
                                    message.arg1 = 1;
                                    handler2.sendMessage(message);
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

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    datalistnew.add(jsonObject.getString("goodNum"));
                                }
                            } catch (Exception e) {
                                Message message = handler2.obtainMessage();
                                message.arg1 = 1;
                                handler2.sendMessage(message);
                            } finally {
                                publishProgress(0);
                                return null;
                            }
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            dialog = ProgressDialog.show(MainPai.this.getContext(), "", "正在加载，请稍等...", false);
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                            dialog.dismiss();
                            if(datalistnew.size() == 0) {
                                Message message = handler2.obtainMessage();
                                message.arg1 = 2;
                                handler2.sendMessage(message);
                            } else {
                                Message message = handler2.obtainMessage();
                                message.arg1 = 0;
                                handler2.sendMessage(message);
                            }
                        }
                    }.execute();
                }
                break;
            default:
                break;
        }
    }
}
