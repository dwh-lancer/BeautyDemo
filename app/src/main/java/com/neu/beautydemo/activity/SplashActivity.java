package com.neu.beautydemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.neu.beautydemo.R;
import com.neu.beautydemo.util.Constants;
import com.neu.beautydemo.util.Utils;
//import com.lancer.mylibrary.common.Constants;
//import com.lancer.mylibrary.common.Utils;

/**
 * @desc 启动屏
 * Created by devilwwj on 16/1/23.
 */
public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){

            if (msg.what == 1){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }else if (msg.what == 0){
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用
        boolean isFirstOpen = Utils.getBooleanValue(this, Constants.FIRST_OPEN);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 如果不是第一次启动app，则正常显示启动屏
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.guide_activity_splash);
        // 判断是否已经登录！
        boolean isLogin = Utils.getBooleanValue(SplashActivity.this, Constants.LOGINSTATE);
        if (isLogin){
            Message msg = handler.obtainMessage();
            msg.what = 1;
            handler.sendMessageDelayed(msg,2000);
        }else{
            Message msg = handler.obtainMessage();
            msg.what = 0;
            handler.sendMessageDelayed(msg,2000);
        }
    }
}
