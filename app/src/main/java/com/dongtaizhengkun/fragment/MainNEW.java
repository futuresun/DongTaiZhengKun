package com.dongtaizhengkun.fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongtaizhengkun.R;
import com.dongtaizhengkun.WelcomeActivity;
import com.dongtaizhengkun.utils.DBHelper;
import com.dongtaizhengkun.utils.Global;
import com.dongtaizhengkun.utils.WorkService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lvrenyang.utils.DataUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/2.
 */
public class MainNEW extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.commit)
    private Button commit;

    @ViewInject(R.id.list_recv)
    private ListView list_recv;
    @ViewInject(R.id.list_recvnum)
    private ListView list_recvnum;
    @ViewInject(R.id.list_send)
    private ListView list_send;
    @ViewInject(R.id.list_sendnum)
    private ListView list_sendnum;
    @ViewInject(R.id.list_sendid)
    private ListView list_sendid;
    @ViewInject(R.id.list_destination)
    private ListView list_destination;
    @ViewInject(R.id.list_outward)
    private ListView list_outward;
    @ViewInject(R.id.list_pname)
    private ListView list_pname;
    @ViewInject(R.id.list_packing)
    private ListView list_packing;

    @ViewInject(R.id.recvname)
    private EditText recvname;
    @ViewInject(R.id.recvnum)
    private EditText recvnum;
    @ViewInject(R.id.sendname)
    private EditText sendname;
    @ViewInject(R.id.sendnum)
    private EditText sendnum;
    @ViewInject(R.id.sendid)
    private EditText sendid;
    @ViewInject(R.id.destination)
    private EditText destination;
    @ViewInject(R.id.outward)
    private EditText outward;
    @ViewInject(R.id.product_name)
    private EditText product_name;
    @ViewInject(R.id.count)
    private EditText count;
    @ViewInject(R.id.packing)
    private EditText packing;
    @ViewInject(R.id.insurance)
    private EditText insurance;
    @ViewInject(R.id.prepay)
    private EditText prepay;
    @ViewInject(R.id.afterpay)
    private EditText afterpay;
    @ViewInject(R.id.unrecev)
    private EditText unrecev;
    @ViewInject(R.id.remark)
    private EditText remark;

    View view;
    DBHelper dbHelper;
    boolean printFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //先判断是否进行了网点的配置
        SharedPreferences settings = inflater.getContext().getSharedPreferences("netsetting", 0);
        if (settings.getString("netnum", "").equals("") || settings.getString("netname", "").equals("") || settings.getString("authors", "").equals("")) {
            new AlertDialog.Builder(MainNEW.this.getContext()).setTitle("未配置网点信息").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.support.v4.app.FragmentManager fragmentManager = MainNEW.this.getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragmentId, new MainHome());
                    transaction.commit();
                    WelcomeActivity.mainBtn.setChecked(true);
                }
            }).show();
        }


        view = inflater.inflate(R.layout.main_layout, null);
        ViewUtils.inject(this, view);

        //数字类型的文本框
        recvnum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        sendnum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        sendid.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        count.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        insurance.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        prepay.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        afterpay.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        unrecev.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        dbHelper = DBHelper.getInstance(inflater.getContext());

        //具有自动匹配功能的文本框
        recvname.addTextChangedListener(new MyTextWatcher(list_recv, view.findViewById(R.id.list_recvout), dbHelper, recvname, "recv_contact", new String[]{"name", "hot"}));
        recvnum.addTextChangedListener(new MyTextWatcher(list_recvnum, view.findViewById(R.id.list_recvnumout), dbHelper, recvnum, "recv_contact", new String[]{"num", "hot"}));
        sendname.addTextChangedListener(new MyTextWatcher(list_send, view.findViewById(R.id.list_sendout), dbHelper, sendname, "send_contact", new String[]{"name", "hot"}));
        sendnum.addTextChangedListener(new MyTextWatcher(list_sendnum, view.findViewById(R.id.list_sendnumout), dbHelper, sendnum, "send_contact", new String[]{"name", "hot"}));
        sendid.addTextChangedListener(new MyTextWatcher(list_sendid, view.findViewById(R.id.list_sendidout), dbHelper, sendid, "send_contact", new String[]{"name", "hot"}));
        destination.addTextChangedListener(new MyTextWatcher(list_destination, view.findViewById(R.id.list_destinationout), dbHelper, destination, "destination", new String[]{"name", "hot"}));
        outward.addTextChangedListener(new MyTextWatcher(list_outward, view.findViewById(R.id.list_outwardout), dbHelper, outward, "out_station", new String[]{"name", "hot"}));
        product_name.addTextChangedListener(new MyTextWatcher(list_pname, view.findViewById(R.id.list_pnameout), dbHelper, product_name, "product_name", new String[]{"name", "hot"}));
        packing.addTextChangedListener(new MyTextWatcher(list_packing, view.findViewById(R.id.list_packingout), dbHelper, packing, "packing", new String[]{"name", "hot"}));


        init();
        commit.setOnClickListener(this);

        return view;
    }

    public void init() {
        recvname.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_recvout));
        recvnum.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_recvnumout));
        sendname.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_sendout));
        sendnum.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_sendnumout));
        sendid.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_sendidout));
        destination.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_destinationout));
        outward.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_outwardout));
        product_name.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_pnameout));
        packing.setOnFocusChangeListener(new MyOnFocusChangeListener(R.id.list_packingout));

        list_recv.setOnItemClickListener(new MyItemClickListener(recvname, view.findViewById(R.id.list_recvout)));
        list_recvnum.setOnItemClickListener(new MyItemClickListener(recvnum, view.findViewById(R.id.list_recvnumout)));
        list_send.setOnItemClickListener(new MyItemClickListener(sendname, view.findViewById(R.id.list_sendout)));
        list_sendnum.setOnItemClickListener(new MyItemClickListener(sendnum, view.findViewById(R.id.list_sendnumout)));
        list_sendid.setOnItemClickListener(new MyItemClickListener(sendid, view.findViewById(R.id.list_sendidout)));
        list_destination.setOnItemClickListener(new MyItemClickListener(destination, view.findViewById(R.id.list_destinationout)));
        list_outward.setOnItemClickListener(new MyItemClickListener(outward, view.findViewById(R.id.list_outwardout)));
        list_pname.setOnItemClickListener(new MyItemClickListener(product_name, view.findViewById(R.id.list_pnameout)));
        list_packing.setOnItemClickListener(new MyItemClickListener(packing, view.findViewById(R.id.list_packingout)));
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {
        int listid;

        public MyOnFocusChangeListener(int listid) {
            this.listid = listid;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            view.findViewById(listid).setVisibility(View.GONE);
        }
    }

    class MyItemClickListener implements AdapterView.OnItemClickListener {

        EditText editText;
        View deleteView;

        public MyItemClickListener(EditText editText, View deleteView) {
            this.editText = editText;
            this.deleteView = deleteView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            editText.setText(((TextView) view).getText().toString());
            deleteView.setVisibility(View.GONE);
            editText.setSelection(editText.getText().length());
        }
    }

    @Override
    public void onClick(View v) {

        //判断空的文本框
        if (recvname.getText().toString().equals("") || recvnum.getText().toString().equals("") ||
                sendname.getText().toString().equals("") || sendnum.getText().toString().equals("") ||
                sendid.getText().toString().equals("") || destination.getText().toString().equals("") ||
                outward.getText().toString().equals("") || product_name.getText().toString().equals("") ||
                packing.getText().toString().equals("") || count.getText().toString().equals("") ||
                insurance.getText().toString().equals("") || prepay.getText().toString().equals("") ||
                afterpay.getText().toString().equals("") || unrecev.getText().toString().equals("")) {
            new AlertDialog.Builder(this.getContext()).setTitle("请输入完整的订单信息").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();

        } else {
            switch (v.getId()) {
                case R.id.commit:

                    new AlertDialog.Builder(this.getContext()).setTitle("确认保存并打印订单").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                            //数据库保存
                            //收货方联系人
                            if (sqLiteDatabase.query("recv_contact", new String[]{"name"}, "name = ?",
                                    new String[]{recvname.getText().toString()}, null, null, null, null).moveToFirst()) {
                                sqLiteDatabase.execSQL("update recv_contact set num=" + recvnum.getText().toString() + ", hot=hot+1" +
                                        " where name='" + recvname.getText().toString() + "'");
                            } else {
                                sqLiteDatabase.execSQL("insert into recv_contact values('" + recvname.getText().toString()
                                        + "', " + recvnum.getText().toString() + ", 1)");
                            }
                            //发货方联系人
                            if (sqLiteDatabase.query("send_contact", new String[]{"name"}, "name = ?",
                                    new String[]{sendname.getText().toString()}, null, null, null, null).moveToFirst()) {
                                sqLiteDatabase.execSQL("update send_contact set num=" + sendnum.getText().toString() + ", hot=hot+1, id=" +
                                        sendid.getText().toString() + " where name='" + sendname.getText().toString() + "'");
                            } else {
                                sqLiteDatabase.execSQL("insert into send_contact values('" + sendname.getText().toString()
                                        + "', " + sendnum.getText().toString() + "," + sendid.getText().toString() + ", 1)");
                            }
                            //到站
                            if (sqLiteDatabase.query("destination", new String[]{"name"}, "name = ?",
                                    new String[]{destination.getText().toString()}, null, null, null, null).moveToFirst()) {
                                sqLiteDatabase.execSQL("update destination set hot=hot+1" +
                                        " where name='" + destination.getText().toString() + "'");
                            } else {
                                sqLiteDatabase.execSQL("insert into destination values('" + destination.getText().toString()
                                        + "', " + "1)");
                            }
                            //外转
                            if (sqLiteDatabase.query("out_station", new String[]{"name"}, "name = ?",
                                    new String[]{outward.getText().toString()}, null, null, null, null).moveToFirst()) {
                                sqLiteDatabase.execSQL("update out_station set hot=hot+1" +
                                        " where name='" + outward.getText().toString() + "'");
                            } else {
                                sqLiteDatabase.execSQL("insert into out_station values('" + outward.getText().toString()
                                        + "', " + "1)");
                            }
                            //品名
                            if (sqLiteDatabase.query("product_name", new String[]{"name"}, "name = ?",
                                    new String[]{product_name.getText().toString()}, null, null, null, null).moveToFirst()) {
                                sqLiteDatabase.execSQL("update product_name set hot=hot+1" +
                                        " where name='" + product_name.getText().toString() + "'");
                            } else {
                                sqLiteDatabase.execSQL("insert into product_name values('" + product_name.getText().toString()
                                        + "', " + "1)");
                            }
                            //包装
                            if (sqLiteDatabase.query("packing", new String[]{"name"}, "name = ?",
                                    new String[]{packing.getText().toString()}, null, null, null, null).moveToFirst()) {
                                sqLiteDatabase.execSQL("update packing set hot=hot+1" +
                                        " where name='" + packing.getText().toString() + "'");
                            } else {
                                sqLiteDatabase.execSQL("insert into packing values('" + packing.getText().toString()
                                        + "', " + "1)");
                            }

                            SharedPreferences settings = MainNEW.this.getContext().getSharedPreferences("netsetting", 0);

                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyMMdd");
                            SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd");

                            int todayConut = 0;

                            //当天的单号
                            Cursor cursor = dbHelper.getReadableDatabase().query("today_orders", new String[]{"senddate"}, "senddate like ?",
                                    new String[]{simpleDateFormat3.format(date) + "%"}, null, null, null, null);
                            while (cursor.moveToNext()) {
                                todayConut++;
                            }

                            String goodNum = settings.getString("netnum", "") + "-XX-" + simpleDateFormat2.format(date) + "-" + todayConut + "-" + count.getText().toString();
                            //订单详情
                            sqLiteDatabase.execSQL("insert into today_orders values(" + settings.getString("netnum", "")//netpoint
                                    + ",'" + settings.getString("netname", "")//netname
                                    + "','" + simpleDateFormat1.format(date)//senddate
                                    + "','" + goodNum//goodno
                                    + "','" + sendname.getText().toString()
                                    + "'," + sendnum.getText().toString()
                                    + "," + sendid.getText().toString()
                                    + ",'" + recvname.getText().toString()
                                    + "'," + recvnum.getText().toString()
                                    + ",'" + destination.getText().toString()
                                    + "','" + outward.getText().toString()
                                    + "','" + product_name.getText().toString()
                                    + "'," + count.getText().toString()
                                    + ",'" + packing.getText().toString()
                                    + "'," + insurance.getText().toString()
                                    + "," + prepay.getText().toString()
                                    + "," + afterpay.getText().toString()
                                    + "," + unrecev.getText().toString()
                                    + ",'" + settings.getString("authors", "")//shouli
                                    + "','" + remark.getText().toString() + "', \"\", 'NO', 'NO')");


                            //打印订单
                            byte[] setHT1 = {0x1b, 0x44, 0x18, 0x00};
                            byte[] setKB1 = {0x1b, 0x40, 0x1d, 0x21, 0x01, 0x1b, 0x33, (byte) 0x80, 0x1d, 0x4c, 0x60, 0x00};
                            byte[] setHT2 = {0x1b, 0x44, 0x15, 0x00};
                            byte[] setKB2 = {0x1b, 0x40, 0x1d, 0x21, 0x01, 0x1b, 0x33, (byte) 0x80, 0x1d, 0x4c, 0x08, 0x00};
                            byte[] setKB3 = {0x1b, 0x40, 0x1d, 0x21, 0x00, 0x1b, 0x33, (byte) 0x60, 0x1d, 0x4c, 0x08, 0x00};
                            byte[] setKB4 = {0x1b, 0x40, 0x1d, 0x21, 0x00, 0x1b, 0x33, (byte) 0x80, 0x1d, 0x4c, 0x08, 0x00};
                            byte[] setHT3 = {0x1b, 0x44, 0x06, 0x0e, 0x16, 0x00};
                            byte[] setHT4 = {0x1b, 0x44, 0x0a, 0x00};
                            byte[] HT = {0x09};
                            byte[] LF = {0x0d, 0x0a};
                            byte[][] allbuf;

                            try {
                                allbuf = new byte[][]{

                                        setKB1, "东泰正坤".getBytes("gbk"), "  ".getBytes("gbk"), "受理单".getBytes("gbk"), LF,
                                        setKB2, setHT2, "发货时期".getBytes("gbk"), HT, simpleDateFormat1.format(date).getBytes("gbk"), LF,
                                        setKB2, setHT2, "货号".getBytes("gbk"), HT, goodNum.getBytes("gbk"), LF,
                                        setKB2, setHT3, "网点".getBytes("gbk"), HT, settings.getString("netname", "").getBytes("gbk"), HT, "单号".getBytes("gbk"), HT, goodNum.getBytes("gbk"), LF,
                                        setKB2, setHT3, "起站".getBytes("gbk"), HT, settings.getString("netname", "").getBytes("gbk"), HT, "收货方".getBytes("gbk"), HT, recvname.getText().toString().getBytes("gbk"), LF,
                                        setKB2, setHT3, "到站".getBytes("gbk"), HT, destination.getText().toString().getBytes("gbk"), HT, "电话".getBytes("gbk"), HT, recvnum.getText().toString().getBytes("gbk"), LF,
                                        setKB2, setHT3, "外转".getBytes("gbk"), HT, outward.getText().toString().getBytes("gbk"), HT, "发货方".getBytes("gbk"), HT, sendname.getText().toString().getBytes("gbk"), LF,
                                        setKB2, setHT3, "品名".getBytes("gbk"), HT, product_name.getText().toString().getBytes("gbk"), HT, "件数".getBytes("gbk"), HT, count.getText().toString().getBytes("gbk"), LF,
                                        setKB2, setHT3, "保价".getBytes("gbk"), HT, insurance.getText().toString().getBytes("gbk"), HT, "现付".getBytes("gbk"), HT, prepay.getText().toString().getBytes("gbk"), LF,
                                        setKB2, setHT3, "包装".getBytes("gbk"), HT, packing.getText().toString().getBytes("gbk"), HT, "提付".getBytes("gbk"), HT, afterpay.getText().toString().getBytes("gbk"), LF,
                                        setKB2, setHT3, "受理".getBytes("gbk"), HT, settings.getString("authors", "").getBytes("gbk"), HT, "代收".getBytes("gbk"), HT, unrecev.getText().toString().getBytes("gbk"), LF,
                                        setKB2, setHT4, "代收".getBytes("gbk"), HT, "呵呵".getBytes("gbk"), LF,
                                        setKB2, setHT4, "备注".getBytes("gbk"), HT, remark.getText().toString().getBytes("gbk"), LF,
                                        setKB3, "———————————————".getBytes("gbk"), LF,
                                        setKB3, setHT4, "发货电话:".getBytes("gbk"), HT, "028-83421808".getBytes("gbk"), LF,
                                        setKB3, setHT4, "到站电话:".getBytes("gbk"), HT, "028-63924228".getBytes("gbk"), LF,
                                        setKB3, setHT4, "代收款打款查询:".getBytes("gbk"), HT, "136 6624 1257".getBytes("gbk"), LF,
                                        setKB3, setHT4, "代收款打款查询:".getBytes("gbk"), HT, "189 8055 9461".getBytes("gbk"), LF,
                                        setKB3, setHT4, "投诉电话:".getBytes("gbk"), HT, "150 0284 8419".getBytes("gbk"), LF,
                                        setKB3, "附记:".getBytes("gbk"), LF,
                                        setKB3, "取得本受理单，视为已认可东泰正坤公示的《委托运输合同条款》及确认以上内容准确无误，愿承担相应的责任。".getBytes("gbk"), LF,
                                        setKB3, "———————————————".getBytes("gbk"), LF,
                                        setKB2, "取货人签字:".getBytes("gbk"), LF,
                                        setKB2, "证件号码:".getBytes("gbk"), LF, LF
                                };
                                byte[] buf = DataUtils.byteArraysToBytes(allbuf);
                                if (WorkService.workThread.isConnected()) {
                                    Bundle data = new Bundle();
                                    data.putByteArray(Global.BYTESPARA1, buf);
                                    data.putInt(Global.INTPARA1, 0);
                                    data.putInt(Global.INTPARA2, buf.length);
                                    WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                                } else {
                                    Toast.makeText(MainNEW.this.getContext(), Global.toast_notconnect, Toast.LENGTH_SHORT).show();
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();


                    break;
                default:
                    break;
            }
        }
    }

}

class MyTextWatcher implements TextWatcher {

    EditText editText;
    String tableName;
    String[] searchItems;
    DBHelper dbHelper;
    ListView listView;
    View view;

    public MyTextWatcher(ListView listView, View view, DBHelper dbHelper, EditText editText, String tableName, String[] searchItems) {
        this.editText = editText;
        this.tableName = tableName;
        this.searchItems = searchItems;
        this.dbHelper = dbHelper;
        this.listView = listView;
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String key = editText.getText().toString();
        List<String> data = new ArrayList<String>();
        if (!key.equals("")) {
            data.add(key);
            Cursor cursor = dbHelper.getReadableDatabase().query(tableName, searchItems, searchItems[0] + " like ?",
                    new String[]{"%" + key + "%"}, null, null, "hot desc", "0,4");
            int count1 = 0;
            while (count1 < 4 && cursor.moveToNext()) {
                data.add(cursor.getString(0));
                count1++;
            }
            cursor.close();
            listView.setAdapter(new ArrayAdapter<String>(editText.getContext(), R.layout.listitem, data));
            setListViewHeightBasedOnChildren(listView);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
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
}
