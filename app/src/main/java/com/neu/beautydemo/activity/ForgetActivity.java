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


public class ForgetActivity extends AppCompatActivity implements CallBackInterface {
    private EditText edtRegPhone;
    private ImageView imgForgetPhone;
    private ConnectServer connectServer;
    String regPhone;

    public String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        initView();
        initToolBar();
    }
    private void initView(){
        edtRegPhone = (EditText)findViewById(R.id.edt_register_phone);
        edtRegPhone.addTextChangedListener(new TextChangeListener());
        imgForgetPhone = (ImageView)findViewById(R.id.img_deleteRegPhone1);
        connectServer = new ConnectServer();
    }

    private void initToolBar(){
        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("忘记密码");
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

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_register_next:
                executeRegister();
                break;
            case R.id.img_deleteRegPhone1:
                edtRegPhone.setText("");
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
            regPhone = edtRegPhone.getText().toString();
            if (regPhone.length()>0){
                imgForgetPhone.setVisibility(View.VISIBLE);
            }else {
                imgForgetPhone.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
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
        connectServer.setConnectCallBack(ForgetActivity.this);

        connectServer.executeRegJudge(regPhone);
    }

    @Override
    public void execute(int regJudgeResult, Object b) {
        if (!Utils.isNetworkAvailable(this)){
            Utils.showShortToast(this,"无网络");
            return;
        }
        if (regJudgeResult == Constants.REG_JUDGE_REGISTERED){
            //已经注册，跳转到下一步
            Bundle bundle = new Bundle();
            bundle.putString("phone",regPhone);
            Utils.start_Activity(ForgetActivity.this,Forget2Activity.class,0,bundle);

            return;
        }
        new AlertDialog.Builder(this).setTitle("提示").setMessage("未注册的手机号，请先去注册？")
                .setPositiveButton("注册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Bundle bundle = new Bundle();
                        bundle.putString("phone",regPhone);
                        Utils.start_Activity(ForgetActivity.this,RegisterActivity.class,
                                Intent.FLAG_ACTIVITY_CLEAR_TOP,bundle);
                        Utils.finish(ForgetActivity.this);

                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
