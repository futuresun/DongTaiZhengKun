package com.dongtaizhengkun.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.dongtaizhengkun.R;
import com.dongtaizhengkun.SearchBTActivity;
import com.dongtaizhengkun.utils.WorkService;
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
    @ViewInject(R.id.printForm)
    private Button printForm;

    SharedPreferences settings;
    JSONArray jsonArray;

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
                    authors.setText(jsonArray.getJSONObject(0).getString("shouli"));

                    settings = MainHome.this.getContext().getSharedPreferences("netsetting", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("netnum", jsonArray.getJSONObject(0).getString("netpoint"));
                    editor.putString("netname", jsonArray.getJSONObject(0).getString("netname"));
                    editor.putString("authors", jsonArray.getJSONObject(0).getString("shouli"));
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
        deletelistBtn.setOnClickListener(this);
        deletecontactBtn.setOnClickListener(this);
        printForm.setOnClickListener(this);

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this.getContext(), WorkService.class);
            this.getContext().startService(intent);
        }

        return view;
    }

    public void init() {
        settings = this.getContext().getSharedPreferences("netsetting", 0);
        if (System.currentTimeMillis() - settings.getLong("time", 0L) < 30 * 24 * 60 * 60 * 1000L) {
            netnum.setText(settings.getString("netnum", ""));
            netaddr.setText(settings.getString("netname", ""));
            authors.setText(settings.getString("authors", ""));
        } else {
            netnum.setText("");
            netaddr.setText("");
            authors.setText("");
        }

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
                                String urlEncoding = "http://192.168.1.11:8080/dongtai/servlet/LoginServlet?" + data;
                                URL url = new URL(urlEncoding);
                                //System.out.println("urlEncoding------------>" + urlEncoding);
                                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                httpUrlConnection.setRequestMethod("GET");
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


                    /*new Thread() {
                        @Override
                        public void run() {
                            try {
                                String data = "user=" + URLEncoder.encode(user.getText().toString(), "utf-8") + "&password=" + URLEncoder.encode(psd.getText().toString(), "utf-8");
                                String urlEncoding = "http://192.168.1.11:8080/dongtai/servlet/LoginServlet?" + data;
                                URL url = new URL(urlEncoding);
                                //System.out.println("urlEncoding------------>" + urlEncoding);
                                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                                httpUrlConnection.setRequestMethod("GET");
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
                        }
                    }.start();*/

                }

                break;
            case R.id.deletelist:
                break;
            case R.id.deletecontact:
                break;
            case R.id.BTconnect:
                startActivity(new Intent(this.getContext(), SearchBTActivity.class));
                break;
            case R.id.printForm:
                startActivity(new Intent(this.getContext(), FormActivity.class));
                break;
            default:
                break;

        }
    }

}