package com.dongtaizhengkun;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.TscCommand;
import com.gprinter.command.TscCommand.DIRECTION;
import com.gprinter.command.TscCommand.FONTMUL;
import com.gprinter.command.TscCommand.FONTTYPE;
import com.gprinter.command.TscCommand.MIRROR;
import com.gprinter.command.TscCommand.ROTATION;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;

import org.apache.commons.lang.ArrayUtils;

import java.util.Vector;

/**
 * Created by Administrator on 2016/7/2.
 *
 */
public class SearchBQActivity extends Activity {

    public static GpService mGpService = null;
    public static final String CONNECT_STATUS = "connect.status";
    private static final String DEBUG_TAG = "MainActivity";
    private int mPrinterIndex = 0;
    private int mTotalCopies = 0;
    private PrinterServiceConnection conn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.btconfig);

        startService();
        connection();
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

    public void openPortDialogueClicked(View view) {
        Log.d(DEBUG_TAG, "openPortConfigurationDialog ");
        Intent intent = new Intent(this, PrinterConnectDialog.class);
        boolean[] state = getConnectState();
        intent.putExtra(CONNECT_STATUS, state);
        this.startActivity(intent);
    }

    public void printTestPageClicked(View view) {
        try {
            // 尝试打印
            TscCommand tsc = new TscCommand();
            tsc.addSize(50, 40);
            tsc.addGap(2);
            tsc.addDirection(DIRECTION.BACKWARD, MIRROR.NORMAL);
            tsc.addReference(0, 0);
            tsc.addTear(ENABLE.OFF);
            tsc.addCls();
			/*tsc.addText(0, 0, FONTTYPE.SIMPLIFIED_CHINESE,
					ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1,
					"Welcome to use Gprinter!");*/
            tsc.addBox(10, 10, 380, 300);
            //tsc.addErase(18, 18, 364, 284);
            tsc.addErase(18, 18, 364, 69);

            tsc.addErase(18, 89, 364, 69);

            tsc.addErase(18, 161, 117, 69);
            tsc.addErase(141, 161, 117, 69);
            tsc.addErase(264, 161, 117, 69);

            tsc.addErase(18, 233, 117, 69);
            tsc.addErase(141, 233, 240, 69);

            tsc.addText(50, 30, FONTTYPE.SIMPLIFIED_CHINESE,
                    ROTATION.ROTATION_0, FONTMUL.MUL_2, FONTMUL.MUL_2,
                    "东泰正坤物流");
            tsc.addText(55, 99, FONTTYPE.SIMPLIFIED_CHINESE,
                    ROTATION.ROTATION_0, FONTMUL.MUL_2, FONTMUL.MUL_2,
                    "成都-->重庆");
            tsc.addText(30, 172, FONTTYPE.SIMPLIFIED_CHINESE,
                    ROTATION.ROTATION_0, FONTMUL.MUL_2, FONTMUL.MUL_2,
                    "件数");
            tsc.addText(30, 243, FONTTYPE.SIMPLIFIED_CHINESE,
                    ROTATION.ROTATION_0, FONTMUL.MUL_2, FONTMUL.MUL_2,
                    "单号");

            tsc.addPrint(1,1);
            tsc.addSound(2, 100);
            Vector<Byte> datas = tsc.getCommand(); // 发送数据
            Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
            byte[] bytes = ArrayUtils.toPrimitive(Bytes);
            String str = Base64.encodeToString(bytes, Base64.DEFAULT);
            int rel = mGpService.sendTscCommand(mPrinterIndex, str);
            //int rel = mGpService.printeTestPage(mPrinterIndex);

            Toast.makeText(getApplicationContext(), rel+"",
                    Toast.LENGTH_SHORT).show();
            // int rel = mGpService.printeTestPage(mPrinterIndex); //
            Log.i("ServiceConnection", "rel " + rel);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void getPrinterCommandTypeClicked(View view) {
        try {
            int type = mGpService.getPrinterCommandType(mPrinterIndex);
            if (type == GpCom.ESC_COMMAND) {
                Toast.makeText(getApplicationContext(), "打印机使用ESC命令",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "打印机使用TSC命令",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void getPrinterStatusClicked(View view) {
        try {
            mTotalCopies = 0;
            int status = mGpService.queryPrinterStatus(mPrinterIndex, 500);
            String str = new String();
            if (status == GpCom.STATE_NO_ERR) {
                str = "打印机正常";
            } else {
                str = "打印机 ";
                if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                    str += "脱机";
                }
                if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                    str += "缺纸";
                }
                if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                    str += "打印机开盖";
                }
                if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                    str += "打印机出错";
                }
                if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                    str += "查询超时";
                }
            }
            Toast.makeText(getApplicationContext(),
                    "打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
                    .show();
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void startService() {
        Intent i = new Intent(this, GpPrintService.class);
        startService(i);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //写下你希望按下返回键达到的效果代码，不写则不会有反应
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
