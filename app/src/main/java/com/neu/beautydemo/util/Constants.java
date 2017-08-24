package com.neu.beautydemo.util;

/**
 * Created by lancer on 16/7/23.
 */
public interface Constants {
    int DEFAULT = 0;
    int GLASSES = 1;
    int EYE_BROW = 2;
    int SKIN = 3;
    int WRINKLE = 4;
    int FACE_REC = 5;

    String FIRST_OPEN = "first_open";
    String url = "http://www.myclound.cn/app.php";
    String IMG_FACE_URI = "imgFaceUri";

    String USER_PHONE = "userPhone";
    String USER_PWD = "userPwd";

    String regJudgeUrl = "http://www.myclound.cn/MagicMirrorUserSign.php";
    String regUrl      = "http://www.myclound.cn/MagicMirrorUserSignup.php";
    String resetPwdUrl= "http://www.myclound.cn/MagicMirrorUserResetPwd.php";

    String loginUrl = "http://123.207.136.49/MagicMirrorUserLogin.php";




    //登录结果
    int LOGIN_SUCCEED = 1;
    int LOGIN_FAILED = 2;
    int LOGIN_NONE_REG = 3;
    int LOGIN_ERROR = 4;
    int LOGIN_PHONE_PWD_ERROR = 5;
    String LOGINSTATE = "loginState";

    //判断注册结果
    int REG_JUDGE_REGISTERED = 11;
    int REG_JUDGE_NONE_REGISTERED = 12;
    int REG_JUDGE_ERROR = 13;

    //注册结果
    int REG_SUCCEED = 21;
    int REG_FAILED = 22;
    int REG_REGISTERED = 23;
    int REG_ERROR = 24;

    //找回密码结果
    int RESETPWD_SUCCEED = 31;
    int RESETPWD_FAILED = 32;
    int RESETPWD_REGISTERED = 33;
    int RESETPWD_ERROR = 34;
}
