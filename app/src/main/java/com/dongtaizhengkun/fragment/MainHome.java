package com.dongtaizhengkun.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dongtaizhengkun.R;
import com.dongtaizhengkun.SearchBTActivity;
import com.dongtaizhengkun.utils.WorkService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/6/2.
 */
public class MainHome extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.netnum)
    private EditText netnum;
    @ViewInject(R.id.netname)
    private EditText netname;
    @ViewInject(R.id.authors)
    private EditText authors;
    @ViewInject(R.id.save)
    private Button saveBtn;
    @ViewInject(R.id.deletelist)
    private Button deletelistBtn;
    @ViewInject(R.id.deletecontact)
    private Button deletecontactBtn;
    @ViewInject(R.id.BTconnect)
    private Button BTconnect;

    SharedPreferences settings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        ViewUtils.inject(this, view);
        netnum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        init(inflater);
        saveBtn.setOnClickListener(this);
        BTconnect.setOnClickListener(this);

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this.getContext(), WorkService.class);
            this.getContext().startService(intent);
        }

        return view;
    }

    public void init(LayoutInflater inflater) {
        settings = inflater.getContext().getSharedPreferences("netsetting", 0);
        netnum.setText(settings.getString("netnum",""));
        netname.setText(settings.getString("netname",""));
        authors.setText(settings.getString("authors",""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("netnum",netnum.getText().toString());
                editor.putString("netname",netname.getText().toString());
                editor.putString("authors", authors.getText().toString());
                editor.commit();
                break;
            case R.id.deletelist:
                break;
            case R.id.deletecontact:
                break;
            case R.id.BTconnect:
                startActivity(new Intent(this.getContext(), SearchBTActivity.class));
                break;
            default:
                break;

        }
    }

}