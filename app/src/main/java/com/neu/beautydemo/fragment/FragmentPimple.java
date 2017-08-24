package com.neu.beautydemo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mylibrary.BeautyLoader;
import com.neu.beautydemo.R;
import com.neu.beautydemo.activity.MainActivity;
import com.neu.beautydemo.entity.XmlModel;
import com.neu.beautydemo.util.ImageUtils;
import com.neu.beautydemo.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.neu.beautydemo.activity.MainActivity.currentState;

/**
 * Created by dlancer on 2016/12/23.
 */

public class FragmentPimple extends Fragment{

    public FragmentCamera fragmentCamera;
    public ImageView imgFace;
    public String TAG = "***pimple";
    File NoseModel;
    File MouseModel;
    public String  faceModelPath;
    public String   eyeModelPath;
    public String  noseModelPath;
    public String mouseModelPath;
    public ImageView imgPimpleHead;
    public ImageView imgPimpleLeftFace;
    public ImageView imgPimpleRightFace;
    public ImageView imgPimpleDown;

    public String imgPath= Environment.getExternalStorageDirectory().getPath() +
            "/Pimple.jpg";
    public String rootPath= Environment.getExternalStorageDirectory().getPath() +
            "/";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          faceModelPath= Utils.getValue(getActivity(),XmlModel.HAA_FRONTAL_FACE);
           eyeModelPath= Utils.getValue(getActivity(),XmlModel.EYE_TREE);
          noseModelPath= Utils.getValue(getActivity(),XmlModel.NOSE);
         mouseModelPath= Utils.getValue(getActivity(),XmlModel.MOUTH);
        Log.e(TAG, "onCreate: "+faceModelPath+eyeModelPath+noseModelPath+mouseModelPath );
        BeautyLoader.pimpleXmlPath(faceModelPath,eyeModelPath,
                noseModelPath,mouseModelPath);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_pimple,container,false);
        //设置下touch事件
//        LinearLayout layoutPimple = (LinearLayout)view.findViewById(R.id.layout_pimple);
//        layoutPimple.setOnTouchListener(this);

        imgFace = (ImageView)view.findViewById(R.id.img_pimple_face);
        imgPimpleHead = (ImageView)view.findViewById(R.id.img_pimple_head);
        imgPimpleLeftFace = (ImageView)view.findViewById(R.id.img_pimple_left_face);
        imgPimpleRightFace = (ImageView)view.findViewById(R.id.img_pimple_right_face);
        imgPimpleDown = (ImageView)view.findViewById(R.id.img_pimple_down);
//        newThread();
        fragmentCamera = new FragmentCamera();
        return view;
    }
//先调用痘痘识别算法，在将图片显示出来
    private Handler handler = new Handler();
    @Override
    public void onResume() {
        super.onResume();
//       pimpleRec();
//        Utils.showLongToast(getActivity(),"请摆正脸型！点击屏幕检测粉刺痘痘！");
    }

    public void getPimpleResult(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BeautyLoader.pimplePicPath(imgPath);
            }
        }, 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageUtils.decodeBitmapFromFSDCard(imgPath);
                Bitmap bitmap1 = ImageUtils.decodeBitmapFromFSDCard(rootPath+"roi1.jpg");
                Bitmap bitmap2 = ImageUtils.decodeBitmapFromFSDCard(rootPath+"roi2.jpg");
                Bitmap bitmap3 = ImageUtils.decodeBitmapFromFSDCard(rootPath+"roi3.jpg");
                Bitmap bitmap4 = ImageUtils.decodeBitmapFromFSDCard(rootPath+"roi4.jpg");
                if (bitmap != null){
                    imgFace.setImageBitmap(bitmap);
                }
                if (bitmap4 != null){
                    imgPimpleHead.setImageBitmap(bitmap4);
                    Log.e(TAG, "run: "+"imgPimpleHead");
                }
                if (bitmap1 != null){
                    imgPimpleLeftFace.setImageBitmap(bitmap1);
                    Log.e(TAG, "run: "+"imgPimpleLeftFace");
                }else {
                    Log.e(TAG, "run: "+"imgPimpleLeftFace空");
                }
                if (bitmap2 != null){
                    imgPimpleRightFace.setImageBitmap(bitmap2);
                    Log.e(TAG, "run: "+"imgPimpleRightFace");
                }else {
                    Log.e(TAG, "run: "+"imgPimpleRightFace空");
                }
                if (bitmap3 != null){
                    imgPimpleDown.setImageBitmap(bitmap3);
                    Log.e(TAG, "run: "+"imgPimpleDown");
                }else {
                    Log.e(TAG, "run: "+"imgPimpleDown空");
                }
            }
        }, 5000);
        currentState = 0;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


}
