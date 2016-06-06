package com.dongtaizhengkun;
/**
 * Created by Administrator on 2016/6/6.
 */
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dongtaizhengkun.R;
import com.dongtaizhengkun.utils.Global;
import com.dongtaizhengkun.utils.WorkService;
import com.lvrenyang.utils.DataUtils;

public class FormActivity extends Activity implements OnClickListener {

    private static Handler mHandler = null;
    private static String TAG = "FormActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        findViewById(R.id.buttonPrintForm).setOnClickListener(this);

        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.buttonPrintForm: {
                //0x1b,0x33  行间距
                //0x1d,0x4c 左边空白局域大小
                //0x1d,0x21 字体大小
                //0x1b,0x44 水平制表
                byte[] setHT1 = {0x1b,0x44,0x18,0x00};
                byte[] setKB1 = {0x1b,0x40,0x1d,0x21,0x01,0x1b,0x33,(byte) 0x80,0x1d,0x4c,0x60,0x00};
                byte[] setHT2 = {0x1b,0x44,0x15,0x00};
                byte[] setKB2 = {0x1b,0x40,0x1d,0x21,0x01,0x1b,0x33,(byte) 0x80,0x1d,0x4c,0x08,0x00};
                byte[] setKB3 = {0x1b,0x40,0x1d,0x21,0x00,0x1b,0x33,(byte) 0x60,0x1d,0x4c,0x08,0x00};
                byte[] setKB4 = {0x1b,0x40,0x1d,0x21,0x00,0x1b,0x33,(byte) 0x80,0x1d,0x4c,0x08,0x00};
                byte[] setHT3 = {0x1b,0x44,0x06,0x0e,0x16,0x00};
                byte[] setHT4 = {0x1b,0x44,0x0a,0x00};
                byte[] HT = {0x09};
                byte[] LF = {0x0d,0x0a};
                byte[][] allbuf;
                try {
				/*allbuf = new byte[][]{
						setHT,"FOOD".getBytes(),HT,"PRICE".getBytes(),LF,LF,
						setHT,"DECAF16".getBytes(),HT,"30".getBytes(),LF,
						setHT,"ISLAND BLEND".getBytes(),HT,"180".getBytes(),LF,
						setHT,"FLAVOR SMALL".getBytes(),HT,"30".getBytes(),LF,
						setHT,"Kenya AA".getBytes(),HT,"90".getBytes(),LF,
						setHT,"CHAI".getBytes(),HT,"15.5".getBytes(),LF,
						setHT,"MOCHA".getBytes(),HT,"20".getBytes(),LF,
						setHT,"BREVE".getBytes(),HT,"1000".getBytes(),LF,LF,LF
						};*/
                    allbuf = new byte[][]{

                            setKB1,"东泰正坤".getBytes("gbk"),"  ".getBytes("gbk"),"受理单".getBytes("gbk"),LF,
                            setKB2,setHT2,"发货时期".getBytes("gbk"),HT,"2016/04/04".getBytes("gbk"),LF,
                            setKB2,setHT2,"货号".getBytes("gbk"),HT,"18502".getBytes("gbk"),LF,
                            setKB2,setHT3,"网点".getBytes("gbk"),HT,"大丰".getBytes("gbk"),HT,"单号".getBytes("gbk"),HT,"18502".getBytes("gbk"),LF,
                            setKB2,setHT3,"起站".getBytes("gbk"),HT,"成都".getBytes("gbk"),HT,"收货方".getBytes("gbk"),HT,"柯南".getBytes("gbk"),LF,
                            setKB2,setHT3,"到站".getBytes("gbk"),HT,"重庆".getBytes("gbk"),HT,"电话".getBytes("gbk"),HT,"666666".getBytes("gbk"),LF,
                            setKB2,setHT3,"外转".getBytes("gbk"),HT,"南充".getBytes("gbk"),HT,"发货方".getBytes("gbk"),HT,"电子科大".getBytes("gbk"),LF,
                            setKB2,setHT3,"品名".getBytes("gbk"),HT,"日化".getBytes("gbk"),HT,"件数".getBytes("gbk"),HT,"100".getBytes("gbk"),LF,
                            setKB2,setHT3,"保价".getBytes("gbk"),HT,"100".getBytes("gbk"),HT,"现付".getBytes("gbk"),HT,"5000".getBytes("gbk"),LF,
                            setKB2,setHT3,"包装".getBytes("gbk"),HT,"纸".getBytes("gbk"),HT,"提付".getBytes("gbk"),HT,"1000".getBytes("gbk"),LF,
                            setKB2,setHT3,"受理".getBytes("gbk"),HT,"贵".getBytes("gbk"),HT,"代收".getBytes("gbk"),HT,"20000".getBytes("gbk"),LF,
                            setKB2,setHT4,"代收".getBytes("gbk"),HT,"呵呵".getBytes("gbk"),LF,
                            setKB2,setHT4,"备注".getBytes("gbk"),HT,"严实打包".getBytes("gbk"),LF,
                            setKB3,"———————————————".getBytes("gbk"),LF,
                            setKB3,setHT4,"发货电话:".getBytes("gbk"),HT,"028-83421808".getBytes("gbk"),LF,
                            setKB3,setHT4,"到站电话:".getBytes("gbk"),HT,"028-63924228".getBytes("gbk"),LF,
                            setKB3,setHT4,"代收款打款查询:".getBytes("gbk"),HT,"136 6624 1257".getBytes("gbk"),LF,
                            setKB3,setHT4,"代收款打款查询:".getBytes("gbk"),HT,"189 8055 9461".getBytes("gbk"),LF,
                            setKB3,setHT4,"投诉电话:".getBytes("gbk"),HT,"150 0284 8419".getBytes("gbk"),LF,
                            setKB3,"附记:".getBytes("gbk"),LF,
                            setKB3,"取得本受理单，视为已认可东泰正坤公示的《委托运输合同条款》及确认以上内容准确无误，愿承担相应的责任。".getBytes("gbk"),LF,
                            setKB3,"———————————————".getBytes("gbk"),LF,
                            setKB2,"取货人签字:".getBytes("gbk"),LF,
                            setKB2,"证件号码:".getBytes("gbk"),LF,LF
						/*,
						setHT,"FOOD".getBytes(),HT,"PRICE".getBytes(),LF,LF,
						setHT,"DECAF16".getBytes(),HT,"30".getBytes(),LF,
						setHT,"ISLAND BLEND".getBytes(),HT,"180".getBytes(),LF,
						setHT,"FLAVOR SMALL".getBytes(),HT,"30".getBytes(),LF,
						setHT,"Kenya AA".getBytes(),HT,"90".getBytes(),LF,
						setHT,"CHAI".getBytes(),HT,"15.5".getBytes(),LF,
						setHT,"MOCHA".getBytes(),HT,"20".getBytes(),LF,
						setHT,"BREVE".getBytes(),HT,"1000".getBytes(),LF,LF,LF*/
                    };
                    byte[] buf = DataUtils.byteArraysToBytes(allbuf);
                    if (WorkService.workThread.isConnected()) {
                        Bundle data = new Bundle();
                        data.putByteArray(Global.BYTESPARA1, buf);
                        data.putInt(Global.INTPARA1, 0);
                        data.putInt(Global.INTPARA2, buf.length);
                        WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                    } else {
                        Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    static class MHandler extends Handler {

        WeakReference<FormActivity> mActivity;

        MHandler(FormActivity activity) {
            mActivity = new WeakReference<FormActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FormActivity theActivity = mActivity.get();
            switch (msg.what) {

                case Global.CMD_POS_WRITERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Result: " + result);
                    break;
                }

            }
        }
    }

}

