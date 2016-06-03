package com.dongtaizhengkun;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dongtaizhengkun.fragment.MainHome;
import com.dongtaizhengkun.fragment.MainNEW;
import com.dongtaizhengkun.fragment.MainPai;
import com.dongtaizhengkun.fragment.MainToday;
import com.dongtaizhengkun.utils.DBHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/6/2.
 */
public class WelcomeActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.selectId)
    private RadioGroup radioGroup;
    @ViewInject(R.id.homeId)
    private RadioButton mainBtn;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        ViewUtils.inject(this);
        //初始化数据库
        DBHelper.getInstance(this);
        fragmentManager = getSupportFragmentManager();
        mainBtn.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);
        changeFragment(new MainHome(), false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.homeId:
                changeFragment(new MainHome(), true);
                break;
            case R.id.tuanId:
                changeFragment(new MainNEW(), true);
                break;
            case R.id.findId:
                changeFragment(new MainToday(), true);
                break;
            case R.id.myId:
                changeFragment(new MainPai(), true);
                break;
            default:
                break;
        }
    }

    public void changeFragment(Fragment fragment, boolean isInit) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentId, fragment);
        if(!isInit) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
