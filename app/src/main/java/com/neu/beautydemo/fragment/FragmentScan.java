package com.neu.beautydemo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.neu.beautydemo.R;

/**
 * Created by dlancer on 2016/12/23.
 */

public class FragmentScan extends Fragment {

    private String TAG = "***Scan";
    private ImageView mScanHorizontalLineImageView;
    private RelativeLayout layoutScan;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_scan,container,false);
        mScanHorizontalLineImageView = (ImageView) view.findViewById(R.id.scanHorizontalLineImageView);
        layoutScan = (RelativeLayout)view.findViewById(R.id.layout_scan);
        return view;
    }
    Animation verticalAnimation;
    public void startAnimation(){
        // 从上到下的平移动画
        verticalAnimation = new TranslateAnimation(0, 0, 0, 400);
        verticalAnimation.setDuration(3000); // 动画持续时间
        verticalAnimation.setRepeatCount(Animation.INFINITE); // 无限循环

        // 播放动画
        mScanHorizontalLineImageView.setAnimation(verticalAnimation);
        verticalAnimation.startNow();
    }

    @Override
    public void onResume() {
        super.onResume();
        startAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        verticalAnimation.cancel();
    }

}
