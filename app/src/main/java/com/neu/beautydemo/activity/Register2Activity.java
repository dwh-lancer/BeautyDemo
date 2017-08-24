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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neu.beautydemo.R;
import com.neu.beautydemo.server.CallBackInterface;
import com.neu.beautydemo.server.ConnectServer;
import com.neu.beautydemo.util.Constants;
import com.neu.beautydemo.util.Utils;


public class Register2Activity extends AppCompatActivity implements CallBackInterface {
    private EditText edtRegCode;
    private EditText edtRegPwd;
    private ImageView imgDelPwd;

    private String regPhone;
    private String regPwd;
    private ConnectServer connectServer;

    public String TAG = "***";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        initView();

    }

    private void initView() {
        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("完成注册");
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Toast.makeText(Register2Activity.this, "asdf ", Toast.LENGTH_SHORT).show();
            }
        });
        //
        edtRegCode = (EditText) findViewById(R.id.edt_register_code);
        edtRegCode.setText("489454");
        edtRegCode.setFocusable(false);
        edtRegPwd = (EditText) findViewById(R.id.edt_register_pwd);
        edtRegPwd.addTextChangedListener(new TextChangeListener());
        imgDelPwd = (ImageView)findViewById(R.id.img_deleteRegPwd);

        connectServer = new ConnectServer();

        regPhone = getIntent().getExtras().getString("phone");
        Log.d(TAG, "onCreate: regPhone" + regPhone);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                executeRegister();
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
            regPwd = edtRegPwd.getText().toString();
            if (regPwd.length()>0){
                imgDelPwd.setVisibility(View.VISIBLE);
            }else {
                imgDelPwd.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private void executeRegister() {
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
        connectServer.executeRegister(regPhone, regPwd);
    }

    @Override
    public void execute(int regResult, Object b) {
        switch (regResult) {
            case Constants.REG_SUCCEED:
                Utils.showShortToast(Register2Activity.this, "注册成功");

                Utils.start_Activity(Register2Activity.this,LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP,null);
                Utils.finish(Register2Activity.this);
                break;
            case Constants.REG_FAILED:
                Utils.showShortToast(Register2Activity.this, "注册失败");
                break;
            case Constants.REG_REGISTERED:
                Utils.showShortToast(Register2Activity.this, "此手机号已经注册");
                break;
            case Constants.REG_ERROR:
                Utils.showShortToast(Register2Activity.this, "注册错误");
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
