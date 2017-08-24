package com.neu.beautydemo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.neu.beautydemo.R;
import com.neu.beautydemo.activity.MainActivity;

/**
 * Created by dlancer on 2016/12/23.
 */

public class FragmentSkin extends Fragment implements View.OnClickListener {

    private ImageView imgSkinWhite;
    private ImageView imgSkinSunshine2;
    private ImageView imgSkinBubble1;
    String TAG = "***Skin";
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_skin,container,false);
        imgSkinWhite = (ImageView)view.findViewById(R.id.skin_white);
        imgSkinSunshine2 = (ImageView)view.findViewById(R.id.sunshine2);
        imgSkinBubble1 = (ImageView)view.findViewById(R.id.bubble1);
        imgSkinWhite.setOnClickListener(this);
        imgSkinSunshine2.setOnClickListener(this);
        imgSkinBubble1.setOnClickListener(this);
        mainActivity =(MainActivity) getActivity();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.skin_white:
                Log.e(TAG, "onClick: skin_white");
                mainActivity.setImgSkin(1);
                break;
            case R.id.sunshine2:
                Log.e(TAG, "onClick: sunshine2");
                mainActivity.setImgSkin(2);
                break;
            case R.id.bubble1:
                Log.e(TAG, "onClick: bubble1");
                mainActivity.setImgSkin(3);
                break;
        }
    }
}
