package com.neu.beautydemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.neu.beautydemo.R;
import com.neu.beautydemo.server.CallBackInterface;
import com.neu.beautydemo.server.ConnectServer;
import com.neu.beautydemo.util.Constants;
import com.neu.beautydemo.util.Utils;



public class LoginActivity extends AppCompatActivity implements CallBackInterface {
     private EditText edtLoginPhone;
    private EditText edtLoginPwd;
    private ImageView imgDelPhone;
    private ImageView imgDelPwd;

    private ConnectServer connectServer;
    String TAG = "LoginActivity";

    //用户名和密码
    String loginPhone;
    String loginPwd;
    public Boolean isFocusPhone = false;
    public Boolean isFocusPwd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolBar();
        initView();
    }

    private void initToolBar(){
        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("登录");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }
    private void initView(){

       edtLoginPhone = (EditText) findViewById(R.id.edt_login_phone);
        edtLoginPhone.setOnFocusChangeListener(new CheckOnFocusChangeListener());
        edtLoginPhone.addTextChangedListener(new TextChangeListener());

        edtLoginPwd = (EditText) findViewById(R.id.edt_login_pwd);
        edtLoginPwd.setOnFocusChangeListener(new CheckOnFocusChangeListener());
        edtLoginPwd.addTextChangedListener(new TextChangeListener());

        imgDelPhone = (ImageView)findViewById(R.id.img_deletePhone);
        imgDelPwd   = (ImageView)findViewById(R.id.img_deletePwd);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_login:
                excuteLogin();
                break;
            case R.id.btn_login_face:
                excuteFaceLogin();
                break;
            case R.id.tv_reg_in_login:
                Utils.start_Activity(LoginActivity.this,RegisterActivity.class,0,null);
                LoginActivity.this.finish();
                break;
            case R.id.tv_forget_in_login:
                Utils.start_Activity(LoginActivity.this,ForgetActivity.class,0,null);
                break;
            case R.id.img_deletePhone:
                edtLoginPhone.setText("");
                break;
            case R.id.img_deletePwd:
                edtLoginPwd.setText("");
                break;
        }
    }
    private void excuteFaceLogin(){
        Utils.start_Activity(LoginActivity.this,LoginCameraActivity.class,0,null);
//        LoginActivity.this.finish();
    }
    private void excuteLogin(){
        if (!Utils.isNetworkAvailable(this)){
            Utils.showShortToast(this,"无网络");
            return;
        }
        loginPhone = edtLoginPhone.getText().toString();
        loginPwd = edtLoginPwd.getText().toString();
        if (TextUtils.isEmpty(loginPhone)){
            Utils.showShortToast(this,"请输入手机号");
            return;
        }
        if (!Utils.isMobileNO(loginPhone)){
            Utils.showShortToast(this,"请输入正确的11位手机号");
            return;
        }
        if (TextUtils.isEmpty(loginPwd)){
            Utils.showShortToast(this,"请输入密码");
            return;
        }
        connectServer = new ConnectServer();
        connectServer.setConnectCallBack(this);
        connectServer.executeLogin(loginPhone, loginPwd);
    }

    @Override
    public void execute(int a,Object b) {
        switch (a){
            case Constants.LOGIN_SUCCEED:
                Utils.showShortToast(LoginActivity.this,"登录成功");
                Utils.putBooleanValue(LoginActivity.this,Constants.LOGINSTATE,true);
                if(Utils.hasValue(LoginActivity.this,Constants.USER_PHONE)){
                    Log.d(TAG, "登录后，查找本地是否由手机号"+Utils.getValue(LoginActivity.this,Constants.USER_PHONE));
                    Utils.RemoveValue(LoginActivity.this,Constants.USER_PHONE);
                    Utils.RemoveValue(LoginActivity.this,Constants.USER_PWD);
                }
                Utils.putValue(LoginActivity.this,Constants.USER_PHONE,loginPhone);
                Utils.putValue(LoginActivity.this,Constants.USER_PWD,loginPwd);
                Log.d(TAG, "登录成功后写入的手机号"+Utils.getValue(LoginActivity.this,Constants.USER_PHONE));
                Utils.start_Activity(LoginActivity.this,MainActivity.class,0,null);
                LoginActivity.this.finish();
                break;
            case Constants.LOGIN_FAILED:
                Utils.showShortToast(LoginActivity.this,"登录失败");
                break;
            case Constants.LOGIN_NONE_REG:
                Utils.showShortToast(LoginActivity.this,"账号不存在,请先注册！");
                break;
            case Constants.LOGIN_ERROR:
                Utils.showShortToast(LoginActivity.this,"登录错误");
                break;
            case Constants.LOGIN_PHONE_PWD_ERROR:
                Utils.showShortToast(LoginActivity.this,"账户或密码错误");
                break;
        }
    }

    private class CheckOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            loginPhone = edtLoginPhone.getText().toString();
            loginPwd = edtLoginPwd.getText().toString();
            switch (view.getId()){
                case R.id.edt_login_phone:
                    isFocusPhone = true;
                    isFocusPwd = false;
                    if (hasFocus){
                        if (!TextUtils.isEmpty(loginPhone)){
                            imgDelPhone.setVisibility(View.VISIBLE);
                        }else {
                            imgDelPhone.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        imgDelPhone.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.id.edt_login_pwd:
                    isFocusPhone = false;
                    isFocusPwd = true;
                    if (hasFocus){
                        if (!TextUtils.isEmpty(loginPwd)){
                            imgDelPwd.setVisibility(View.VISIBLE);
                        }else {
                            imgDelPwd.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        imgDelPwd.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
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
            loginPhone = edtLoginPhone.getText().toString();
            loginPwd = edtLoginPwd.getText().toString();
            if (isFocusPhone && isFocusPwd == false){
                if (loginPhone.length()>0){
                    imgDelPhone.setVisibility(View.VISIBLE);
                }else {
                    imgDelPhone.setVisibility(View.INVISIBLE);
                }
            }else if (isFocusPhone == false && isFocusPwd){
                if (loginPwd.length()>0){
                    imgDelPwd.setVisibility(View.VISIBLE);
                }else {
                    imgDelPwd.setVisibility(View.INVISIBLE);
                }
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis()-exitTime)>2000){
                Utils.showShortToast(LoginActivity.this,"请再按一次退出程序");
                exitTime = System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
