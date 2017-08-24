package com.neu.beautydemo.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.mylibrary.BeautyLoader;
import com.neu.beautydemo.R;
import com.neu.beautydemo.entity.XmlModel;
import com.neu.beautydemo.fragment.FragmentCamera;
import com.neu.beautydemo.fragment.FragmentEyeBrow;
import com.neu.beautydemo.fragment.FragmentGlasses;
import com.neu.beautydemo.fragment.FragmentGuide;
import com.neu.beautydemo.fragment.FragmentRect;
import com.neu.beautydemo.fragment.FragmentPimple;
import com.neu.beautydemo.fragment.FragmentScan;
import com.neu.beautydemo.fragment.FragmentSkin;
import com.neu.beautydemo.fragment.FragmentWrinkle;
import com.neu.beautydemo.util.Constants;
import com.neu.beautydemo.util.LogcatHelper;
import com.neu.beautydemo.util.Utils;
import com.neu.beautydemo.view.MainView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements MainView {


    private FragmentCamera fragmentCamera;
    private FragmentRect fragmentRect;
    private FragmentPimple fragmentPimple;
    private FragmentWrinkle fragmentWrinkle;
    private FragmentGuide fragmentGuide;
    private FragmentGlasses fragmentGlasses;
    private FragmentEyeBrow fragmentEyeBrow;
    private FragmentSkin fragmentSkin;
    private FragmentScan fragmentScan;
    private Fragment currentFragment;

    //模型文件
    File mEyeModelFile;
    File mHaFaceModelFile;
    File NoseModel;
    File MouseModel;
    File foreHeadModelFile;
    File fishModelFile;
    File pouchModelFile;
    File expressionModelFile;

    public String glassesPath;
    public String glassesPath2;
    public String ImgPathBubble1;
    public String ImgPathSunshine2;
    public String ImgPathEyebrow1;
    public String ImgPathEyebrow2;
    public String faceModelPath;
    public String eyeModelPath;

    int foreheadResult_x;
    int foreheadResult_y;
    int leftEyeResult_x;
    int leftEyeResult_y;
    int rightEyeResult_x;
    int rightEyeResult_y;
    int leftPouchResult_x;
    int leftPouchResult_y;
    int rightPouchResult_x;
    int rightPouchResult_y;
    int leftFaceResult_x;
    int leftFaceResult_y;
    int rightFaceResult_x;
    int rightFaceResult_y;


    //获取屏幕的宽高
    public static int screenWidth;
    public static int screenHeight;

    //选择人脸图片时用到的变量、常量。
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    public Uri imgUri;
    public static String wrinklePic = "/storage/emulated/0/Wrinkle.jpg";//人脸 照片路径
    public String pimpleImgPath;
    public String wrinkleImgPath;
    public String TAG = "***";
    public static int currentState = 0;


    public ImageView imgFace;

    public BeautyLoader beautyLoader;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main1);
        LogcatHelper.getInstance(this).start();
        judgeXml();
        initView();
        initFragment(savedInstanceState);
    }

    @Override
    public void initView() {
        //获取屏幕信息,screenWidth,screenHeigh
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_out:
                Utils.putBooleanValue(MainActivity.this, Constants.LOGINSTATE, false);
                Utils.start_Activity(MainActivity.this, LoginActivity.class, 0, null);
                Utils.finish(MainActivity.this);
                break;
            case R.id.btn_takePic:

                break;
        }
    }

    /**
     * 添加主页默认fragment(相机、矩形框、导航)
     */
    private void initFragment(Bundle savedInstanceState) {
        //判断activity是否重建，如果不是，则不需要重新建立fragment.
        if (savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (fragmentCamera == null) {
                fragmentCamera = new FragmentCamera();
            }
            if (fragmentRect == null) {
                fragmentRect = new FragmentRect();
            }
            if (fragmentGuide == null) {
                fragmentGuide = new FragmentGuide();
            }
            currentFragment = fragmentRect;//将当前fragment设为矩形框
            ft.add(R.id.layout_main, fragmentCamera, "FragmentCamera");
            //矩形框框效果不好，等有时间了再修改
            ft.add(R.id.layout_main, fragmentRect, "FragmentRect");
            ft.add(R.id.layout_guide, fragmentGuide, "FragmentGuide");
            ft.commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        beautyLoader = new BeautyLoader();
//        beautyLoader.setGestureCallBack(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogcatHelper.getInstance(this).stop();
    }

    @Override
    public void switchFragmentWrinkle() {
        FragmentCamera.mFrameType = 0;//停止实时眼镜
        if (fragmentWrinkle == null) {
            fragmentWrinkle = new FragmentWrinkle();
        }
        if (fragmentCamera == null) {
            fragmentCamera = new FragmentCamera();
        }
        switchContent(currentFragment, fragmentWrinkle);
    }

    //此函数作用是先保存一张截屏，然后在识别皱纹
    public void takePicWrinkle() {
        fragmentCamera.startTakePictures("Wrinkle");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentWrinkle.getWrinkleResult();
            }
        }, 1000);
    }
    //此函数作用是检测人脸是否在中央，若是则算法回调takePicWrinkle来识别皱纹
    public void faceRec() {
        if (currentState == 0) {
            currentState = 1;
            fragmentCamera.mFrameType = Constants.FACE_REC;
            fragmentWrinkle.startScanAnimation();
        }
    }


    @Override
    public void switchFragmentPimple() {
//        if (currentState == 0) {
//            currentState = 1;
//            FragmentCamera.mFrameType = 0;//停止实时眼镜
//            if (fragmentPimple == null) {
//                fragmentPimple = new FragmentPimple();
//            }
//            if (fragmentCamera == null) {
//                fragmentCamera = new FragmentCamera();
//            }
//            switchContent(currentFragment, fragmentPimple);
//            //等几秒再发送图片地址
//            fragmentCamera.startTakePictures("Pimple");
//            fragmentPimple.getPimpleResult();
//        }
        Utils.showShortToast(this, "算法存在bug，咱不提供演示，节后更新！");
    }

    @Override
    public void switchFragmentGlasses() {
        if (fragmentGlasses == null) {
            fragmentGlasses = new FragmentGlasses();
        }
        switchContent(currentFragment, fragmentGlasses);
    }
    public void setGlassesImg(int type) {
//        FragmentCamera.mFrameType = 0;
        FragmentCamera.mGlassesType = type;
        FragmentCamera.mFrameType = 1;
    }

    @Override
    public void switchFragmentEyeBrow() {
        if (fragmentEyeBrow == null) {
            fragmentEyeBrow = new FragmentEyeBrow();
        }
        switchContent(currentFragment, fragmentEyeBrow);
    }
    public void setEyeBrowImg(int type) {
//        FragmentCamera.mFrameType = 0;
        FragmentCamera.mEyebrowType = type;
        FragmentCamera.mFrameType = 2;
    }

    @Override
    public void switchFragmentSkin() {
        if (fragmentSkin == null) {
            fragmentSkin = new FragmentSkin();
        }
        switchContent(currentFragment, fragmentSkin);
    }
    public void setImgSkin(int type) {
        FragmentCamera.mSkinWhiteType = type;
        FragmentCamera.mFrameType = 3;

    }


    /**
     * 当fragment进行切换时，采用隐藏与显示的方法加载fragment以防止数据的重复加载
     *
     * @param from
     * @param to
     */
    public void switchContent(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentManager fm = getFragmentManager();
            //添加渐隐渐现的动画
            FragmentTransaction ft = fm.beginTransaction();//.setCustomAnimations(
            // android.R.animator.fade_in, android.R.animator.fade_out);
            //FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(R.anim.in_from_right_fragment,R.anim.out_to_left_fragment);
            if (!to.isAdded()) {    // 先判断是否被add过
                ft.hide(from).add(R.id.layout_main, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    public long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Utils.showShortToast(MainActivity.this, "请再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //判断xml模型文件是否已经加载
    public void judgeXml(){
        boolean isXmlLoaded = Utils.getBooleanValue(MainActivity.this,XmlModel.XMLSTATE);
        if (!isXmlLoaded){
            loadXML();
        }
    }

    private void loadXML() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadEyeModel();
                loadHaFaceModel();
                loadImgGlasses();
                loadImgGlasses2();
                loadNoseModel();
                loadMouceModel();
                loadForeHeadModel();
                loadFishModel();
                loadPouchModel();
                loadExpressionModel();
                loadImgBubble1();
                loadImgSunshine2();
                loadImgEyebrow1();
                loadImgEyebrow2();
                Utils.putBooleanValue(MainActivity.this,XmlModel.XMLSTATE,true);
                faceModelPath = Utils.getValue(MainActivity.this, XmlModel.HAA_FRONTAL_FACE);
                eyeModelPath = Utils.getValue(MainActivity.this, XmlModel.EYE_TREE);
                pimpleImgPath = Environment.getExternalStorageDirectory().getPath() +
                        "/Pimple.jpg";
                wrinkleImgPath = Environment.getExternalStorageDirectory().getPath() +
                        "/Wrinkle.jpg";
                Log.e(TAG, "run: " + faceModelPath + eyeModelPath);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showShortToast(MainActivity.this, "模型文件加载完成！");
                    }
                });
            }
        }).start();
    }

    //haarcascade_eye_tree_eyeglasses
    public void loadEyeModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.haarcascade_eye_tree_eyeglasses);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            mEyeModelFile = new File(cascadeDir, "haarcascade_eye_tree_eyeglasses.xml");
            FileOutputStream os = new FileOutputStream(mEyeModelFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "loadEyeModel: " + mEyeModelFile.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.EYE_TREE, mEyeModelFile.getAbsolutePath());
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load loadeyeModelPath. Exception thrown: " + e);
        }
    }

    //haarcascade_frontalface_alt
    public void loadHaFaceModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            mHaFaceModelFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(mHaFaceModelFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "mHaFaceModelFile: " + mHaFaceModelFile.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.HAA_FRONTAL_FACE, mHaFaceModelFile.getAbsolutePath());
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load loadfaceModelPath. Exception thrown: " + e);
        }
    }

    public void loadImgGlasses() {
        Resources res = this.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.glasses);
        Bitmap img = d.getBitmap();

        String fn = "glasses1.png";
        glassesPath = this.getFilesDir() + File.separator + fn;
        Log.d(TAG, "loadImgGlasses1: " + glassesPath);
        try {
            OutputStream os = new FileOutputStream(glassesPath);
            img.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    public void loadImgGlasses2() {
        Resources res = this.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.glasses2);
        Bitmap img = d.getBitmap();

        String fn = "glasses2.png";
        glassesPath2 = this.getFilesDir() + File.separator + fn;
        Log.d(TAG, "loadImgGlasses2: " + glassesPath2);
        try {
            OutputStream os = new FileOutputStream(glassesPath2);
            img.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    public void loadImgBubble1() {
        Resources res = this.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.bubble1);
        Bitmap img = d.getBitmap();

        String fn = "bubble1.png";
        ImgPathBubble1 = this.getFilesDir() + File.separator + fn;
        Log.d(TAG, "loadImgBubble1: " + ImgPathBubble1);
        try {
            OutputStream os = new FileOutputStream(ImgPathBubble1);
            img.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    public void loadImgSunshine2() {
        Resources res = this.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.sunshine2);
        Bitmap img = d.getBitmap();

        String fn = "sunshine2.png";
        ImgPathSunshine2 = this.getFilesDir() + File.separator + fn;
        Log.d(TAG, "loadImgSunshine2: " + ImgPathSunshine2);
        try {
            OutputStream os = new FileOutputStream(ImgPathSunshine2);
            img.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    public void loadImgEyebrow1() {
        Resources res = this.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.eyebrow1);
        Bitmap img = d.getBitmap();

        String fn = "eyebrow1.png";
        ImgPathEyebrow1 = this.getFilesDir() + File.separator + fn;
        Log.d(TAG, "loadImgEyebrow1: " + ImgPathEyebrow1);
        try {
            OutputStream os = new FileOutputStream(ImgPathEyebrow1);
            img.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }
    public void loadImgEyebrow2() {
        Resources res = this.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.eyebrow2);
        Bitmap img = d.getBitmap();

        String fn = "eyebrow2.png";
        ImgPathEyebrow2 = this.getFilesDir() + File.separator + fn;
        Log.d(TAG, "loadImgPathEyebrow2: " + ImgPathEyebrow2);
        try {
            OutputStream os = new FileOutputStream(ImgPathEyebrow2);
            img.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    //haarcascade_mcs_nose
    public void loadNoseModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.haarcascade_mcs_nose);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            NoseModel = new File(cascadeDir, "haarcascade_mcs_nose.xml");
            FileOutputStream os = new FileOutputStream(NoseModel);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "loadNoseModel: " + NoseModel.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.NOSE, NoseModel.getAbsolutePath());
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load Pimple-haarcascade_mcs_nose. Exception thrown: " + e);
        }
    }

    //haarcascade_mcs_mouth
    public void loadMouceModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.haarcascade_mcs_mouth);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            MouseModel = new File(cascadeDir, "haarcascade_mcs_mouth.xml");
            FileOutputStream os = new FileOutputStream(MouseModel);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "loadMouceModel: " + MouseModel.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.MOUTH, MouseModel.getAbsolutePath());

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load load Pimple-haarcascade_mcs_mouth. Exception thrown: " + e);
        }
    }

    //wrinkle_forehead
    public void loadForeHeadModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.wrinkle_forehead);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            foreHeadModelFile = new File(cascadeDir, "wrinkle_forehead.xml");
            FileOutputStream os = new FileOutputStream(foreHeadModelFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "loadForeHeadModel: " + foreHeadModelFile.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.WRINKLE_FOREHEAD, foreHeadModelFile.getAbsolutePath());
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load load ForeHeadModel. Exception thrown: " + e);
        }
    }

    //wrinkle_fishtail
    public void loadFishModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.wrinkle_fishtail);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            fishModelFile = new File(cascadeDir, "wrinkle_fishtail.xml");
            FileOutputStream os = new FileOutputStream(fishModelFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "loadFishModel: " + fishModelFile.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.WRINKLE_FISH_TAIL, fishModelFile.getAbsolutePath());

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load loadFishModel. Exception thrown: " + e);
        }
    }

    //wrinkle_pouch
    public void loadPouchModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.wrinkle_pouch);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            pouchModelFile = new File(cascadeDir, "wrinkle_pouch.xml");
            FileOutputStream os = new FileOutputStream(pouchModelFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "loadPouchModel: " + pouchModelFile.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.WRINKLE_POUCH, pouchModelFile.getAbsolutePath());

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load wrinkle_pouch. Exception thrown: " + e);
        }
    }

    //wrinkle_expression
    public void loadExpressionModel() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.wrinkle_expression);
            File cascadeDir = getDir("neu", Context.MODE_PRIVATE);
            expressionModelFile = new File(cascadeDir, "wrinkle_expression.xml");
            FileOutputStream os = new FileOutputStream(expressionModelFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.e(TAG, "loadExpressionModel: " + expressionModelFile.getAbsolutePath());
            Utils.putValue(MainActivity.this, XmlModel.WRINKLE_EXPRESSION, expressionModelFile.getAbsolutePath());
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load loadExpressionModel. Exception thrown: " + e);
        }
    }

}
