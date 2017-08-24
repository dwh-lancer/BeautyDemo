package com.neu.beautydemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.neu.beautydemo.R;
import com.neu.beautydemo.server.CallBackInterface;
import com.neu.beautydemo.server.ConnectServer;
import com.neu.beautydemo.util.Constants;
import com.neu.beautydemo.util.Utils;

public class Forget2Activity extends AppCompatActivity implements CallBackInterface {
    private TextView edtPhone;
    private EditText edtRegCode;
    private EditText edtRegPwd;
    private ImageView imgDeletePhone;
    private ImageView imgDeletePwd;

    private Button btn;

    private String regPhone;
    private String regPwd;
    private ConnectServer connectServer;

    public String TAG = "***";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget2);
        initView();
        initToolBar();
    }

    private void initView() {

        //验证码、密码、删除
        edtRegCode = (EditText) findViewById(R.id.edt_register_code);
        edtRegCode.setText("564564");
        edtRegCode.setFocusable(false);
        edtRegPwd = (EditText) findViewById(R.id.edt_register_pwd);
        edtRegPwd.addTextChangedListener(new TextChangeListener());
        imgDeletePwd = (ImageView) findViewById(R.id.img_deleteRegPwd);

        connectServer = new ConnectServer();

        //先设置手机号输入框可见
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout_forget_phone);
        relativeLayout.setVisibility(View.VISIBLE);
        //将intent传过来的手机号填到输入框
        edtPhone = (TextView) findViewById(R.id.edt_phone);
        regPhone = getIntent().getExtras().getString("phone");
        edtPhone.setText(regPhone);
        Log.d(TAG, "onCreate: regPhone" + regPhone);

        //btn的text改为 确定
        btn = (Button)findViewById(R.id.btn_register);
        btn.setText("确定");
    }

    private void initToolBar(){
        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("账户验证");
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                resetPwd();
                break;
            case R.id.img_deleteRegPwd:
                edtRegPwd.setText("");
                break;
        }
    }

    private class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            Log.d(TAG, "onTextChanged: charSequence"+charSequence
                    +"\n"+"start"+start+"\n"+"before"+before+"\n"+"count"+count);

            regPwd =  edtRegPwd.getText().toString();
            if (regPwd.length()>0){
                imgDeletePwd.setVisibility(View.VISIBLE);
            }else {
                imgDeletePwd .setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void resetPwd() {
        if (!Utils.isNetworkAvailable(this)){
            Utils.showShortToast(this,"无网络");
            return;
        }
        String regCode = edtRegCode.getText().toString();
        regPwd = edtRegPwd.getText().toString();
        if (TextUtils.isEmpty(regCode)) {
            Utils.showShortToast(this, "请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(regPwd)) {
            Utils.showShortToast(this, "请输入密码");
            return;
        }
        connectServer.setConnectCallBack(this);
        connectServer.resetPwd(regPhone, regPwd);
    }




    @Override
    public void execute(int regResult, Object b) {
        switch (regResult) {
            case Constants.RESETPWD_SUCCEED:
                Utils.showShortToast(Forget2Activity.this, "修改密码成功");

                Utils.start_Activity(Forget2Activity.this,LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP,null);
                Utils.finish(Forget2Activity.this);
                break;
            case Constants.RESETPWD_FAILED:
                Utils.showShortToast(Forget2Activity.this, "修改密码失败");
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
