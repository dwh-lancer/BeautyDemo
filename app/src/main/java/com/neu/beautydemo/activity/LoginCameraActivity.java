package com.neu.beautydemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.neu.beautydemo.R;
import com.neu.beautydemo.util.Constants;
import com.neu.beautydemo.util.Utils;
import com.neu.beautydemo.view.Tutorial3View;
//import com.neu.mylibrary.NDKLoader;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


public class LoginCameraActivity extends Activity implements CvCameraViewListener2, View.OnTouchListener {

    private static final String TAG = "***";

    static {
        OpenCVLoader.initDebug();
    }

    /**
     * 1. async initialization 异步初始化
     * 2. static initialization 静态初始化
     */

    private Mat mRgba;
    private Mat mFlipRgba;
    Mat mGray;
    Mat mShow;

//    private NDKLoader ndkLoader;
    String mCascadeFileModel;
    String mCascadeFileModel2;

    private Tutorial3View mOpenCvCameraView;
    private CascadeClassifier cascadeClassifier;
    Mat imageMat;

    public LoginCameraActivity() {

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    imageMat = new Mat();

                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(LoginCameraActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.camera_surface_view);
        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.enableView();//
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
//        ndkLoader = new NDKLoader();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.showLongToast(this, "触碰屏幕后，开始采集照片!");
        mCascadeFileModel = Utils.getValue(LoginCameraActivity.this, "mCascadeFileModel");
        mCascadeFileModel2= Utils.getValue(LoginCameraActivity.this, "mCascadeFileModel2");
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
//		mRgba = new Mat();
//		mRgba  = new Mat(height, width, CvType.CV_8UC4);
//		mFlipRgba = new Mat();
        mShow = new Mat();
        mGray = new Mat();
    }

    public void onCameraViewStopped() {
        mShow.release();
    }

    int frameCount = 1;

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mGray = inputFrame.gray();
        mShow = inputFrame.rgba();
        Core.flip(mShow, mShow, 1);

        //检测并显示
        MatOfRect faces = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mGray.height() / 2, mGray.height() / 2), new Size());
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(mShow, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

//		ndkLoader1.sendFrame(mShow);
//		Imgcodecs.imwrite("/storage/emulated/0/Pictures/"+frameCount+".jpg",mShow);
        frameCount++;
        return mShow;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    public void trainFaceRecModle() {
        Log.e(TAG, "trainFaceRecModle: nativeFaceLogin");
//        int result = ndkLoader.nativeFaceLogin(mCascadeFileModel,mCascadeFileModel2,
//                Environment.getExternalStorageDirectory().getPath() + "/");
        int result = 0;
        Log.e(TAG, "trainFaceRecModle: " + result);
        if (result == 4) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.showShortToast(LoginCameraActivity.this,"登录成功");
                    Utils.putBooleanValue(LoginCameraActivity.this, Constants.LOGINSTATE,true);
                    Utils.start_Activity(LoginCameraActivity.this, MainActivity.class, 0, null);
                    LoginCameraActivity.this.finish();
                }
            }, 1000);
        } else {
            Toast.makeText(this, "登录失败！", Toast.LENGTH_SHORT).show();
            LoginCameraActivity.this.finish();
        }
    }

    private Handler handler = new Handler();

    public void startTakePictures() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 3; i++) {
                    String fileName = Environment.getExternalStorageDirectory().getPath() +
                            "/FaceTest" + i + ".jpg";
                    mOpenCvCameraView.takePicture(fileName);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 3) {
                        trainFaceRecModle();
//						handler.post(new Runnable() {
//							@Override
//							public void run() {
//								Toast.makeText(LoginCameraActivity.this, "正在识别", Toast.LENGTH_LONG).show();
//							}
//						});
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "onTouch event");
        Utils.showLongToast(LoginCameraActivity.this, "正在采集登录照片");
        startTakePictures();
        return false;
    }
}
