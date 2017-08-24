package com.neu.beautydemo.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mylibrary.BeautyLoader;
import com.neu.beautydemo.R;
import com.neu.beautydemo.activity.MainActivity;
import com.neu.beautydemo.entity.XmlModel;
import com.neu.beautydemo.util.ImageUtils;
import com.neu.beautydemo.util.Utils;
import com.neu.beautydemo.view.CameraFrameView;
import com.neu.beautydemo.view.Tutorial3View;



import java.io.File;

import static com.neu.beautydemo.activity.MainActivity.currentState;

/**
 * Created by dlancer on 2016/12/23.
 */

public class FragmentWrinkle extends Fragment implements View.OnClickListener{

    String TAG = "***wrinkle";
    private CameraFrameView customRect;
    private ImageView mScanHorizontalLineImageView;
    public ImageView imgFace;
    public TextView imgTakePicWrinkle;
    public TextView imgClearPicWrinkle;
    public ImageView imgWrinkleHead;//抬头纹
    public ImageView imgWrinkleFish;//鱼尾纹
    public ImageView imgWrinklePouch;//眼袋
    public ImageView imgWrinkle4;
    public ImageView imgWrinkle5;
    public ImageView imgWrinkle6;
    public TextView txtWrinkleHead;
    public TextView txtWrinkleLeftEye;
    public TextView txtWrinkleLeftPouch;
    public TextView txtWrinkleLeftFace;

    public TextView txtWrinkleRightEye;
    public TextView txtWrinkleRightPouch;
    public TextView txtWrinkleRightFace;

    String   foreHeadModelPath ;
    String       fishModelPath ;
    String      pouchModelPath ;
    String expressionModelPath ;
    String       faceModelPath ;
    String        eyeModelPath ;

    File   foreHeadModelFile;
    File       fishModelFile;
    File      pouchModelFile;
    File expressionModelFile;

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

    private Handler handler = new Handler();

    public String imgPath = Environment.getExternalStorageDirectory().getPath() +
            "/Wrinkle.png";
    public String rootPath= Environment.getExternalStorageDirectory().getPath() +
            "/";
    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foreHeadModelPath   = Utils.getValue(getActivity(),XmlModel.WRINKLE_FOREHEAD);
        fishModelPath       = Utils.getValue(getActivity(),XmlModel.WRINKLE_FISH_TAIL);
        pouchModelPath      = Utils.getValue(getActivity(),XmlModel.WRINKLE_POUCH);
        expressionModelPath = Utils.getValue(getActivity(),XmlModel.WRINKLE_EXPRESSION);
        faceModelPath       = Utils.getValue(getActivity(),XmlModel.HAA_FRONTAL_FACE);
        eyeModelPath        = Utils.getValue(getActivity(),XmlModel.EYE_TREE);
        Log.e(TAG, "onCreate: "+foreHeadModelPath+fishModelPath+
                pouchModelPath+expressionModelPath+ faceModelPath+eyeModelPath);
//        WrinkleLoader.wrinkleXmlPath(rootPath,foreHeadModelPath, fishModelPath,
//                pouchModelPath, expressionModelPath,faceModelPath,eyeModelPath);
        mainActivity =(MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_wrinkle,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
//        Utils.showShortToast(getActivity(),"点击屏幕后开始皱纹检测！");
        mainActivity.faceRec();
    }

    public void getWrinkleResult(){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Bitmap bitmap = ImageUtils.decodeBitmapFromFSDCard(imgPath);
                Bitmap bitmap = Tutorial3View.bm;
                if (bitmap != null){
                    imgFace.setImageBitmap(bitmap);
                }
//                String wrinkleResult = WrinkleLoader.wrinkleRec(imgPath);
                String wrinkleResult ="";
                if (wrinkleResult.isEmpty()){
                    Utils.showLongToast(getActivity(), "无结果！");
                }else if (wrinkleResult.equals("error")) {
                    Utils.showLongToast(getActivity(), "请正对屏幕，在光线充足处重新拍摄！");
                } else {
                    String[] devices = wrinkleResult.split("\\/");
                    Log.e(TAG, "run: " + devices[0] + "\n"
                            + devices[1] + "\n"
                            + devices[2] + "\n"
                            + devices[3] + "\n"
                            + devices[4] + "\n"
                            + devices[5] + "\n"
                            + devices[6]);
                    String[] foreheadResult = devices[0].split("\\*");
                    String[] leftEyeResult = devices[1].split("\\*");
                    String[] rightEyeResult = devices[2].split("\\*");
                    String[] leftPouchResult = devices[3].split("\\*");
                    String[] rightPouchResult = devices[4].split("\\*");
                    String[] leftFaceResult = devices[5].split("\\*");
                    String[] rightFaceResult = devices[6].split("\\*");
                    if (foreheadResult[2].equals("1")) {
                        foreheadResult_x = Integer.parseInt(foreheadResult[0]);
                        foreheadResult_y = Integer.parseInt(foreheadResult[1]);
                        Log.e(TAG, "额头有皱纹: "+foreheadResult_x+"*"+foreheadResult_y );
                        txtWrinkleHead.setText(R.string.option2);
                    } else {
                        Log.e(TAG, "额头无皱纹: ");
                        txtWrinkleHead.setText("");
                    }
                    if (leftEyeResult[2].equals("1")) {
                        leftEyeResult_x = Integer.parseInt(leftEyeResult[0]);
                        leftEyeResult_y = Integer.parseInt(leftEyeResult[1]);
                        Log.e(TAG, "左眼角有皱纹: "+leftEyeResult_x+"*"+leftEyeResult_y );
                        txtWrinkleLeftEye.setText("左眼角有鱼尾纹");
                    } else {
                        Log.e(TAG, "左眼角无皱纹: ");
                        txtWrinkleLeftEye.setText("");
                    }
                    if (rightEyeResult[2].equals("1")) {
                        rightEyeResult_x = Integer.parseInt(rightEyeResult[0]);
                        rightEyeResult_y = Integer.parseInt(rightEyeResult[1]);
                        Log.e(TAG, "右眼角有皱纹: "+rightEyeResult_x+"*"+rightEyeResult_y );
                        txtWrinkleRightEye.setText("右眼角有鱼尾纹");
                    } else {
                        Log.e(TAG, "右眼角无皱纹: ");
                        txtWrinkleRightEye.setText("");
                    }
                    if (leftPouchResult[2].equals("1")) {
                        leftPouchResult_x = Integer.parseInt(leftPouchResult[0]);
                        leftPouchResult_y = Integer.parseInt(leftPouchResult[1]);
                        Log.e(TAG, "左眼袋有皱纹: "+leftPouchResult_x+"*"+leftPouchResult_y );
                        txtWrinkleLeftPouch.setText("左眼袋有皱纹");
                    } else {
                        Log.e(TAG, "左眼袋无皱纹: ");
                        txtWrinkleLeftPouch.setText("");
                    }
                    if (rightPouchResult[2].equals("1")) {
                        rightPouchResult_x = Integer.parseInt(rightPouchResult[0]);
                        rightPouchResult_y = Integer.parseInt(rightPouchResult[1]);
                        Log.e(TAG, "右眼袋有皱纹: "+rightPouchResult_x+"*"+rightPouchResult_y );
                        txtWrinkleRightPouch.setText("右眼袋有皱纹");
                    } else {
                        Log.e(TAG, "右眼袋无皱纹: ");
                        txtWrinkleRightPouch.setText("");
                    }
                    if (leftFaceResult[2].equals("1")) {
                        leftFaceResult_x = Integer.parseInt(leftFaceResult[0]);
                        leftFaceResult_y = Integer.parseInt(leftFaceResult[1]);
                        Log.e(TAG, "左脸有表情纹: "+leftFaceResult_x+"*"+leftFaceResult_y );
                        txtWrinkleLeftFace.setText("左脸有表情纹");
                    } else {
                        Log.e(TAG, "左脸无表情纹: ");
                        txtWrinkleLeftFace.setText("");
                    }
                    if (rightFaceResult[2].equals("1")) {
                        rightFaceResult_x = Integer.parseInt(rightFaceResult[0]);
                        rightFaceResult_y = Integer.parseInt(rightFaceResult[1]);
                        Log.e(TAG, "右脸有表情纹: "+rightFaceResult_x+"*"+rightFaceResult_y );
                        txtWrinkleRightFace.setText("右脸有表情纹");

                    } else {
                        Log.e(TAG, "右脸无表情纹: ");
                        txtWrinkleRightFace.setText("");
                    }
                }
            }
        }, 1000);
        currentState = 0;
        verticalAnimation.cancel();
    }


    public void initView(View view) {

        imgFace = (ImageView)view.findViewById(R.id.img_face_wrinkle);
        customRect =(CameraFrameView)view.findViewById(R.id.customRect);
        mScanHorizontalLineImageView = (ImageView) view.findViewById(R.id.scanHorizontalLineImageView);
        imgTakePicWrinkle = (TextView)view.findViewById(R.id.img_takePicWrinkle);
        imgClearPicWrinkle = (TextView)view.findViewById(R.id.img_clearPicWrinkle);
        imgTakePicWrinkle.setOnClickListener(this);
        imgClearPicWrinkle.setOnClickListener(this);

        //抬头纹
        txtWrinkleHead = (TextView)view.findViewById(R.id.txt_wrinkle_head);
        //鱼尾纹
        txtWrinkleLeftEye  = (TextView)view.findViewById(R.id.txt_wrinkle_left_eye);
        txtWrinkleRightEye = (TextView)view.findViewById(R.id.txt_right_eye);
        //眼袋纹
        txtWrinkleLeftPouch = (TextView)view.findViewById(R.id.txt_wrinkle_left_pouch);
        txtWrinkleRightPouch = (TextView)view.findViewById(R.id.txt_right_pouch);
        //表情纹
        txtWrinkleLeftFace = (TextView)view.findViewById(R.id.txt_wrinkle_left_face);
        txtWrinkleRightFace = (TextView)view.findViewById(R.id.txt_right_face);
    }
    Animation verticalAnimation;
    public void startScanAnimation(){
        // 从上到下的平移动画

        verticalAnimation = new TranslateAnimation(0, 0, -30, mainActivity.screenHeight);
        verticalAnimation.setDuration(1000); // 动画持续时间
        verticalAnimation.setRepeatCount(Animation.INFINITE); // 无限循环

        // 播放动画
        customRect.setVisibility(View.VISIBLE);//限定框框显示出来
        mScanHorizontalLineImageView.setVisibility(View.VISIBLE);
        mScanHorizontalLineImageView.setAnimation(verticalAnimation);
        verticalAnimation.startNow();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_takePicWrinkle:
                Log.e(TAG, "onClick: img_takePicWrinkle");
                break;
            case R.id.img_clearPicWrinkle:
                Log.e(TAG, "onClick: img_clearPicWrinkle");
                imgFace.setImageBitmap(null);
                txtWrinkleHead.setText("");
                txtWrinkleLeftEye.setText("");
                txtWrinkleLeftPouch.setText("");
                txtWrinkleLeftFace.setText("");
                txtWrinkleRightEye.setText("");
                txtWrinkleRightPouch.setText("");
                txtWrinkleRightFace.setText("");
//                mainActivity.faceRec();
                break;
        }
    }
}
