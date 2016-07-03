package com.dongtaizhengkun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dongtaizhengkun.utils.Constant;
import com.dongtaizhengkun.utils.ListViewAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/6/26.
 *
 */
public class DaiShouActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.backid)
    private Button backBtn;
    @ViewInject(R.id.piid)
    private Button piBtn;
    @ViewInject(R.id.allid)
    private Button allBtn;
    @ViewInject(R.id.reallid)
    private Button reallBtn;
    @ViewInject(R.id.list_data)
    private ListView list_data;

    ListViewAdapter adapter;
    ArrayList<String> data;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Toast.makeText(DaiShouActivity.this, "发放代收款失败", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DaiShouActivity.this, "发放代收款成功", Toast.LENGTH_LONG).show();
            }
            finish();
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daishou);

        ViewUtils.inject(this);
        backBtn.setOnClickListener(this);
        piBtn.setOnClickListener(this);
        allBtn.setOnClickListener(this);
        reallBtn.setOnClickListener(this);

        data = getIntent().getStringArrayListExtra("data");
        adapter = new ListViewAdapter(getApplicationContext(), data);
        list_data.setAdapter(adapter);
        list_data.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backid:
                finish();
                break;
            case R.id.piid:

                final HashMap<Integer, Boolean> tag = ListViewAdapter.getIsSelected();
                if(tag.size() == 0) return;


                new AsyncTask<Void, Integer, Void>() {
                    private ProgressDialog dialog = null;
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            StringBuilder sb = new StringBuilder();
                            for(int i=0; i<tag.size(); i++) {
                                if(tag.get(i)) {
                                    sb.append(data.get(i) + "##");
                                }
                            }

                            String data = "sendchoose=" + URLEncoder.encode(sb.toString(), "utf-8");
                            String urlEncoding = Constant.URL_ROOT + "ChooseServlet?" + data;
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
                                Message message = handler.obtainMessage();
                                message.arg1 = 1;
                                handler.sendMessage(message);
                                return null;
                            }

                        } catch (Exception e) {
                            Message message = handler.obtainMessage();
                            message.arg1 = 1;
                            handler.sendMessage(message);
                        } finally {
                            Message message = handler.obtainMessage();
                            message.arg1 = 0;
                            handler.sendMessage(message);
                            publishProgress(0);
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = ProgressDialog.show(DaiShouActivity.this, "", "正在加载，请稍等...", false);
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        dialog.dismiss();

                    }
                }.execute();
                break;
            case R.id.allid:
                for (int i = 0; i < list_data.getCount(); i++) {
                    ListViewAdapter.getIsSelected().put(i, true);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.reallid:
                HashMap<Integer, Boolean> tag1 = ListViewAdapter.getIsSelected();
                for (int i = 0; i < list_data.getCount(); i++) {
                    tag1.put(i, !tag1.get(i));
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
