package com.neu.beautydemo.server;

import android.util.Log;

import com.neu.beautydemo.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import okhttp3.Call;


/**
 * Created by dlancer on 2016/12/6.
 */

public class ConnectServer {
    public static String TAG = "ConnectServer";
    private CallBackInterface callBack = null;
    public void setConnectCallBack(CallBackInterface callBack){
        this.callBack = callBack;
    }
    public static int loginResult;
    public  void executeLogin(final String userPhone, final String userPwd){
        OkHttpUtils
                .post()
                .url(Constants.loginUrl)
                .addParams("userPhone",userPhone)
                .addParams("userPwd",userPwd)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loginResult = Constants.LOGIN_ERROR;
                        callBack.execute(loginResult,null);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        if (response.contains("登录成功")){
                            loginResult = Constants.LOGIN_SUCCEED;
                        }else if (response.contains("未注册")){
                            loginResult = Constants.LOGIN_NONE_REG;
                        }else if (response.contains("账户或密码错误")){
                            loginResult = Constants.LOGIN_PHONE_PWD_ERROR;
                        }
                        callBack.execute(loginResult,null);
                        Log.d("***", "登录：response: "+response);
                    }
                });
    }
    public static int regJudgeResult;
    public void executeRegJudge(final String userPhone){
        OkHttpUtils
                .post()
                .url(Constants.regJudgeUrl)
                .addParams("userPhone",userPhone)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        regJudgeResult  = Constants.REG_JUDGE_ERROR;
                        callBack.execute(regJudgeResult,null);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: "+response);
                        if (response.contains("已注册")){
                            regJudgeResult  = Constants.REG_JUDGE_REGISTERED;
                        }else if (response.contains("未注册")){
                            regJudgeResult  = Constants.REG_JUDGE_NONE_REGISTERED;
                        }
                        callBack.execute(regJudgeResult,null);
                    }
                });
    }
    public static int regResult;
    public void executeRegister(final String userPhone, final String userPwd){
        OkHttpUtils
                .post()
                .url(Constants.regUrl)
                .addParams("userPhone",userPhone)
                .addParams("userPwd",userPwd)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        regResult = Constants.REG_ERROR;
                        callBack.execute(regResult,null);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: "+response);
                        if (response.contains("注册成功")){
                            regResult = Constants.REG_SUCCEED;
                        }else if (response.contains("注册失败")){
                            regResult = Constants.REG_FAILED;
                        }
                        callBack.execute(regResult,null);
                        Log.d("***", "response: "+response);
                    }
                });
    }
    //找回密码
    public static int resetPwdResult;
    public void resetPwd(final String userPhone, final String userPwd){
        OkHttpUtils
                .post()
                .url(Constants.resetPwdUrl)
                .addParams("userPhone",userPhone)
                .addParams("userPwd",userPwd)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: "+response);
                        if (response.contains("密码重置成功")){
                            resetPwdResult = Constants.RESETPWD_SUCCEED;
                        }else if (response.contains("密码重置失败")){
                            resetPwdResult = Constants.RESETPWD_FAILED;
                        }
                        callBack.execute(resetPwdResult,null);
                        Log.d("***", "response: "+response);
                    }
                });
    }

}
