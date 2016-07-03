package com.dongtaizhengkun.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dongtaizhengkun.FormActivity;
import com.dongtaizhengkun.PrinterConnectDialog;
import com.dongtaizhengkun.R;
import com.dongtaizhengkun.SearchBQActivity;
import com.dongtaizhengkun.SearchBTActivity;
import com.dongtaizhengkun.utils.Constant;
import com.dongtaizhengkun.utils.WorkService;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/6/2.
 */
public class MainHome extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.user)
    private EditText user;
    @ViewInject(R.id.psd)
    private EditText psd;
    @ViewInject(R.id.netnum)
    private TextView netnum;
    @ViewInject(R.id.netaddr)
    private TextView netaddr;
    @ViewInject(R.id.startaddr)
    private TextView startaddr;
    @ViewInject(R.id.authors)
    private TextView authors;
    @ViewInject(R.id.login)
    private Button loginBtn;
    @ViewInject(R.id.deletelist)
    private Button deletelistBtn;
    @ViewInject(R.id.deletecontact)
    private Button deletecontactBtn;
    @ViewInject(R.id.BTconnect)
    private Button BTconnect;
    @ViewInject(R.id.BQconnect)
    private Button BQconnect;
    @ViewInject(R.id.printForm)
    private Button printForm;

    SharedPreferences settings;
    JSONArray jsonArray;

    public static GpService mGpService = null;
    public static final String CONNECT_STATUS = "connect.status";
    private static final String DEBUG_TAG = "MainActivity";
    private int mPrinterIndex = 0;
    private int mTotalCopies = 0;
    private PrinterServiceConnection conn = null;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                if (jsonArray.getJSONObject(0).getString("ok").equals("NO")) {
                    Toast.makeText(MainHome.this.getContext(), "用户名或密码错误", Toast.LENGTH_LONG).show();
                } else if (jsonArray.getJSONObject(0).getString("ok").equals("YES")) {
                    Toast.makeText(MainHome.this.getContext(), "登录成功", Toast.LENGTH_LONG).show();
                    netnum.setText(jsonArray.getJSONObject(0).getString("netpoint"));
                    netaddr.setText(jsonArray.getJSONObject(0).getString("netname"));
                    startaddr.setText(jsonArray.getJSONObject(0).getString("startaddr"));
                    authors.setText(jsonArray.getJSONObject(0).getString("shouli"));

                    settings = MainHome.this.getContext().getSharedPreferences("netsetting", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("netnum", jsonArray.getJSONObject(0).getString("netpoint"));
                    editor.putString("netname", jsonArray.getJSONObject(0).getString("netname"));
                    editor.putString("startaddr", jsonArray.getJSONObject(0).getString("startaddr"));
                    editor.putString("authors", jsonArray.getJSONObject(0).getString("shouli"));
                    editor.putString("change", jsonArray.getJSONObject(0).getString("change"));
                    editor.putString("send", jsonArray.getJSONObject(0).getString("send"));
                    editor.putLong("time", System.currentTimeMillis());
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        ViewUtils.inject(this, view);
        init();
        loginBtn.setOnClickListener(this);
        BTconnect.setOnClickListener(this);
        BQconnect.setOnClickListener(this);
        deletelistBtn.setOnClickListener(this);
        deletecontactBtn.setOnClickListener(this);
        printForm.setOnClickListener(this);

        startService();
        connection();

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this.getContext(), WorkService.class);
            this.getContext().startService(intent);
        }


        return view;
    }

    private void startService() {
        Intent i = new Intent(MainHome.this.getContext(), GpPrintService.class);
        this.getActivity().startService(i);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent();
        intent.setAction("com.gprinter.aidl.GpPrintService");
        intent.setPackage(this.getActivity().getPackageName());
        this.getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    public void init() {
        //保存登录信息30天
        settings = this.getContext().getSharedPreferences("netsetting", 0);
        if (System.currentTimeMillis() - settings.getLong("time", 0L) < 30 * 24 * 60 * 60 * 1000L) {
            set();
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("netnum", "");
            editor.putString("netname", "");
            editor.putString("startaddr", "");
            editor.putString("authors", "");
            editor.putString("change", "否");
            editor.putString("send", "否");
            editor.putLong("time", System.currentTimeMillis());
            editor.commit();
            set();
        }

    }

    public void set() {
        netnum.setText(settings.getString("netnum", ""));
        netaddr.setText(settings.getString("netname", ""));
        startaddr.setText(settings.getString("startaddr", ""));
        authors.setText(settings.getString("authors", ""));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                final String username = user.getText().toString();
                final String userpad = psd.getText().toString();
                if (user.getText().toString().equals("") || psd.getText().toString().equals("")) {
                    Toast.makeText(this.getContext(), "请输入用户名和密码", Toast.LENGTH_LONG).show();
                } else {

                    new AsyncTask<Void, Integer, Void>() {
                        private ProgressDialog dialog = null;

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                String data = "user=" + URLEncoder.encode(username, "utf-8") + "&password=" + URLEncoder.encode(userpad, "utf-8");
                                String urlEncoding = Constant.URL_ROOT + "LoginServlet?" + data;
                                URL url = new URL(urlEncoding);
                                //System.out.println("urlEncoding------------>" + urlEncoding);
                                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                httpUrlConnection.setRequestMethod("GET");
                                httpUrlConnection.setConnectTimeout(10000);
                                httpUrlConnection.setReadTimeout(5000);
                                httpUrlConnection.connect();
                                int code = httpUrlConnection.getResponseCode();

                                InputStream inputStream = httpUrlConnection.getInputStream();
                                BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                StringBuilder responseStrBuilder = new StringBuilder();
                                String inputStr;
                                while ((inputStr = streamReader.readLine()) != null) {
                                    responseStrBuilder.append(inputStr);
                                }
                                jsonArray = new JSONArray(responseStrBuilder.toString());
                                System.out.println(jsonArray.toString());
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
                            dialog = ProgressDialog.show(MainHome.this.getContext(), "登录提示", "正在登录，请稍等...", false);
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                            dialog.dismiss();
                        }
                    }.execute();
                }
                break;
            case R.id.deletelist:
                break;
            case R.id.deletecontact:
                break;
            case R.id.BTconnect:
                startActivity(new Intent(this.getContext(), SearchBTActivity.class));
                break;
            case R.id.BQconnect:
                Intent intent = new Intent(this.getContext(), PrinterConnectDialog.class);
                boolean[] state = getConnectState();
                intent.putExtra(CONNECT_STATUS, state);
                this.startActivity(intent);
                //startActivity(new Intent(this.getContext(), SearchBQActivity.class));
                break;
            case R.id.printForm:
                startActivity(new Intent(this.getContext(), FormActivity.class));
                break;
            default:
                break;

        }
    }

    public boolean[] getConnectState() {
        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            state[i] = false;
        }
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            try {
                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
                    state[i] = true;
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return state;
    }

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

}