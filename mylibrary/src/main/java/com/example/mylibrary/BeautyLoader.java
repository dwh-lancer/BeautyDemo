package com.example.mylibrary;

import android.util.Log;

import org.opencv.core.Mat;

/**
 * Created by dlancer on 2016/12/27.
 */

public class BeautyLoader {
    String TAG = "***";
//    private GestureCallBack gestureCallBack = null;
//
//    public void setGestureCallBack(GestureCallBack gestureCallBack){
//        this.gestureCallBack = gestureCallBack;
//    }
private FaceCallBack faceCallBack = null;

    public void setFaceCallBack(FaceCallBack faceCallBack){
        this.faceCallBack = faceCallBack;
    }
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-eyebrow");
        System.loadLibrary("native-faceRec");
        System.loadLibrary("native-gestures");
        System.loadLibrary("native-glasses");
        System.loadLibrary("native-skin");
        System.loadLibrary("native-wrinkle");
        System.loadLibrary("native-pimple");
    }

    /**
     * 皱纹检测，模型文件地址
     * @param xml_1  foreHeadModelFilePath = xml_1;
     * @param xml_2  eyesModelFilePath = xml_2;
     * @param xml_3  pouchModelFilePath = xml_3;
     * @param xml_4  expressionModelFilePath = xml_4;
     * @param xml_5  face_cascade_name = xml_5;
     * @param xml_6  eyes_cascade_name = xml_6;
     */
    public static native void  wrinkleXmlPath(String rootPath,String xml_1,String xml_2,String xml_3,
                                              String xml_4,String xml_5,String xml_6);

    public static native String  wrinkleRec(String svmpath);

    /**
     * 粉刺痘痘、模型文件地址
     * @param xml_1 face_cascade_name
     * @param xml_2 eyes_cascade_name
     * @param xml_3 nose_cascade_name
     * @param xml_4 mouth_cascade_name
     */
    public static native void pimpleXmlPath(String xml_1,String xml_2,String xml_3,String xml_4);

    /**
     * 粉刺痘痘、测试图片地址
     */
    public static native void  pimplePicPath(String pimplePicPath);

    /**
     * 手势识别
     * @param frame
     */
    public void sendFrame2Gestures(Mat frame){
        nativeSendFrame2Gestures(frame.getNativeObjAddr());
    }
    public native void nativeSendFrame2Gestures(long inputFrame);
    public static int currentNum = 11;
//    public void callback(int orientation){
//        if (orientation == 1){
//            Log.e(TAG, "Java_callback: "+"向上-1" );
////            --currentNum;
//            if (currentNum ==11){
//                currentNum = 13;
//                gestureCallBack.setText("虚拟眼镜");
//            }else if (currentNum ==12){
//                currentNum = 11;
//                gestureCallBack.setText("皱纹识别");
//            }else if (currentNum ==13){
//                currentNum = 12;
//                gestureCallBack.setText("粉刺痘痘");
//            }
//            Log.e(TAG, "Java_callback: "+"currentNum" +currentNum);
//        }else if (orientation == 2){
//            Log.d(TAG, "Java_callback: "+"向下+1" );
////            ++currentNum;
//            if (currentNum ==11){
//                currentNum = 12;
//                gestureCallBack.setText("粉刺痘痘");
//            }else if (currentNum ==12){
//                currentNum = 13;
//                gestureCallBack.setText("虚拟眼镜");
//            }else if (currentNum ==13){
//                currentNum = 11;
//                gestureCallBack.setText("皱纹识别");
//            }
//            Log.d(TAG, "Java_callback: "+"currentNum" +currentNum);
//    }}
    /**
     * 检测人脸是否在中央。
     */
    public native void nativeSendFrame2FaceRec(long inputFrame);
    public void faceCallBack(int i){faceCallBack.faceInMid();}

    /**
     * 虚拟眼镜，发送每一帧图片。
     */
    public native void nativeSendFrame2Glasses(long inputFrame,int type);
    public native void nativeSendFrame2Glasses2(long inputFrame,long outputFrame,int type);

    /**
     * 眉毛，发送每一帧图片。
     */
    public native void nativeSendFrame2Eyeborw(long inputFrame,int type);
    /**
     * 肤色替换
     */
    public native void nativeSendFrame2Skin(long inputFrame,int type);

}
