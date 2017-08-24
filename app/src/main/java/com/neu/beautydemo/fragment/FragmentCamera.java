package com.neu.beautydemo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mylibrary.BeautyLoader;
import com.example.mylibrary.FaceCallBack;
import com.neu.beautydemo.R;

import com.neu.beautydemo.activity.MainActivity;
import com.neu.beautydemo.entity.XmlModel;
import com.neu.beautydemo.util.Constants;
import com.neu.beautydemo.util.Utils;
import com.neu.beautydemo.view.Tutorial3View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.imgproc.Imgproc.INTER_AREA;


/**
 * Created by dlancer on 2016/12/23.
 */

public class FragmentCamera extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2,FaceCallBack {


    private static final String TAG = "***";

    static {
        OpenCVLoader.initDebug();
    }

    /**
     * 1. async initialization 异步初始化
     * 2. static initialization 静态初始化
     */
    private MainActivity mainActivity;
    private TextView txtResult;
    private Tutorial3View mOpenCvCameraView;
    private CascadeClassifier cascadeClassifier;
    Mat imageMat;
    Mat mGray;
    Mat mShow;
    Mat mShowWhite;
    Mat mShowWrinkle;
    Context context;
    public BeautyLoader beautyLoader;
//    public static final int DEFAULT = 0;
//    public static final int GLASSES = 1;
//    public static final int EYE_BROW = 2;
//    public static final int SKIN = 3;
//    public static final int WRINKLE = 4;
//    public static final int FACE_REC = 5;
    public static int mFrameType = 0;
    public static int mSkinWhiteType = 0;
    public static int mEyebrowType = 0;
    public static int mGlassesType = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    imageMat = new Mat();

                    //加载人脸检测xml
                    try {
                        // Copy the resource into a temp file so OpenCV can load it
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getActivity().getDir("cascade", Context.MODE_PRIVATE);
                        File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        // Load the cascade classifier
                        Log.d(TAG, "onManagerConnected: " + mCascadeFile.getAbsolutePath());
                        cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                    } catch (Exception e) {
                        //Error loading cascade待解决
                        Log.e("OpenCVActivity", "Error loading cascade", e);
                    }

                    //enable camera
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_surface_view, container, false);
        mOpenCvCameraView = (Tutorial3View) view.findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.enableView();//
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setCameraOrientation(90);
//        mOpenCvCameraView.setMaxFrameSize(1280,720);
//        mOpenCvCameraView.setRotation(90);
//        mOpenCvCameraView.setCameraIndex();
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        txtResult = (TextView) view.findViewById(R.id.txt_result);
        mainActivity =(MainActivity) getActivity();
//        LogcatHelper.getInstance(getActivity()).start();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        beautyLoader = new BeautyLoader();
//        beautyLoader.setGestureCallBack(this);
        beautyLoader.setFaceCallBack(this);
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, context, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mShow = new Mat();
        mShowWhite = new Mat();
        mShowWrinkle = new Mat();
        mGray = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mShow.release();
    }

    public void faceRec() {
        //检测并显示
        MatOfRect faces = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(mGray, faces, 1.1, 2, 2,
                    new Size(mGray.height() / 2, mGray.height() / 2), new Size());
        } else {
            Log.e(TAG, "onCameraFrame: "+"分类器空" );
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(mShow, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

    }

    public void sendFrame2Galsses(){
        beautyLoader.nativeSendFrame2Glasses(mShow.getNativeObjAddr(), mGlassesType);
    }
    public void sendFrame2Eyebrow(){beautyLoader.nativeSendFrame2Eyeborw(mShow.getNativeObjAddr(),mEyebrowType);}
    public void sendFrame2FaceRec(){beautyLoader.nativeSendFrame2FaceRec(mShow.getNativeObjAddr());}
    public void sendFrame2Skin() { beautyLoader.nativeSendFrame2Skin(mShow.getNativeObjAddr(),mSkinWhiteType);}


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mGray = inputFrame.gray();
        mShow = inputFrame.rgba();
        Core.flip(mShow, mShow, 1);
        Mat optArray = new Mat();
//        Core.rotate(mShow,mShow,90);
        //0 means flipping around the x-axis and positive value
        // 1 means flipping around y-axis. Negative value
        // -1 means flipping around both axes (see the discussion below for the formulas).
//        beautyLoader.sendFrame2Gestures(mShow);

        if (mFrameType == Constants.DEFAULT) {
//            faceRec();

            Log.d(TAG, "onCameraFrame: "+mShow.cols()+"*"+mShow.rows());
        } else if (mFrameType == Constants.GLASSES) {
            sendFrame2Galsses();
//            beautyLoader.nativeSendFrame2Glasses2(mShow.getNativeObjAddr(),optArray.getNativeObjAddr(),mGlassesType);
        }else if (mFrameType == Constants.EYE_BROW) {
            sendFrame2Eyebrow();
        }else if (mFrameType == Constants.SKIN) {
            sendFrame2Skin();
        }else if (mFrameType == Constants.FACE_REC){
            sendFrame2FaceRec();
        }
        return mShow;
    }


    private Handler handler = new Handler();

    //此函数是当初为了截取一张视频帧。
    public void startTakePictures1(final String picName) {
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/" + picName + ".jpg";
        Imgproc.cvtColor(mShow,mShowWrinkle,Imgproc.COLOR_RGBA2BGRA);
        Imgcodecs.imwrite(fileName,mShowWrinkle);
        mFrameType = Constants.DEFAULT;
//        mOpenCvCameraView.takePicture(fileName);
    }
    //此函数是使用相机的截屏功能保存一张图片
    public void startTakePictures(final String picName) {
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/" + picName + ".png";
        mOpenCvCameraView.takePicture(fileName);
    }

    @Override
    public void faceInMid() {
        Log.e(TAG, "faceInMid: " );
        mFrameType = Constants.DEFAULT;
        mainActivity.takePicWrinkle();
    }

//    private Handler handler = new Handler();
//    @Override
//    public void setText(final String result) {
//        Log.e(TAG, "setText: "+result);
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                txtResult.setText(result);
//                if (result.equals("皱纹识别")){
//                    mainActivity.switchFragmentWrinkle();
//                }else if (result.equals("粉刺痘痘")){
//                    mainActivity.switchFragmentPimple();
//                }else if (result.equals("虚拟眼镜")){
//                    mainActivity.switchFragmentGlasses();
//                }
//            }
//        });
//    }
}
