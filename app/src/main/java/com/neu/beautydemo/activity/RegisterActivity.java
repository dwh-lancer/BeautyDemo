package com.neu.beautydemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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

public class RegisterActivity extends AppCompatActivity implements CallBackInterface {
    private EditText edtRegPhone;
    private ImageView imgDelete;
    private ConnectServer connectServer;
    String regPhone;

    public String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initToolBar();
        initView();
    }
    private void initView(){
        edtRegPhone = (EditText)findViewById(R.id.edt_register_phone);
        imgDelete = (ImageView)findViewById(R.id.img_deleteRegPhone1);
        edtRegPhone.addTextChangedListener(new TextChangeListener());
        Intent intent = getIntent();
        edtRegPhone.setText(intent.getStringExtra("phone"));
        connectServer = new ConnectServer();

    }

    private void initToolBar(){
        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("手机注册");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Toast.makeText(RegisterActivity.this, "asdf ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_register_next:
                executeRegister();
                break;
            case R.id.btn_register_face:
                executeFaceRegister();
                break;
            case R.id.img_deleteRegPhone1:
                edtRegPhone.setText("");
                break;
        }
    }
    private void executeFaceRegister(){
        Utils.start_Activity(RegisterActivity.this,OpenCameraActivity.class,0,null);
    }
    private void executeRegister(){
        if (!Utils.isNetworkAvailable(this)){
            Utils.showShortToast(this,"无网络");
            return;
        }
        regPhone = edtRegPhone.getText().toString();
        if (!Utils.isMobileNO(regPhone)){
            Utils.showShortToast(this,"请输入正确的11位手机号");
            return;
        }
        connectServer.setConnectCallBack(RegisterActivity.this);

        connectServer.executeRegJudge(regPhone);
    }

    @Override
    public void execute(int regJudgeResult, Object b) {
        if (!Utils.isNetworkAvailable(this)){
            Utils.showShortToast(this,"无网络");
            return;
        }
        if (regJudgeResult == Constants.REG_JUDGE_REGISTERED){
            Utils.showShortToast(this,"账户已注册，请登录");
            return;
        }
        new AlertDialog.Builder(this).setTitle("提示").setMessage("我们将发送验证码短信到这个号码："+regPhone)
                .setPositiveButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bundle bundle = new Bundle();
                        bundle.putString("phone",regPhone);
                        Utils.start_Activity(RegisterActivity.this,Register2Activity.class,0,bundle);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
    }

    private class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            Log.d(TAG, "onTextChanged: charSequence"+charSequence
                    +"\n"+"start"+start+"\n"+"before"+before+"\n"+"count"+count);
            regPhone = edtRegPhone.getText().toString();
            if (regPhone.length()>0){
                imgDelete.setVisibility(View.VISIBLE);
            }else {
                imgDelete.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
