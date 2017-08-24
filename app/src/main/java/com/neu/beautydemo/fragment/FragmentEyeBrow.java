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

public class FragmentEyeBrow extends Fragment implements View.OnClickListener {


    private ImageView imgEyebrow1;
    private ImageView imgEyebrow2;
    String TAG = "***eyebrow";
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_eye_brow,container,false);
        imgEyebrow1 = (ImageView)view.findViewById(R.id.eyebrow1);
        imgEyebrow2 = (ImageView)view.findViewById(R.id.eyebrow2);
        imgEyebrow1.setOnClickListener(this);
        imgEyebrow2.setOnClickListener(this);
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
            case R.id.eyebrow1:
                Log.e(TAG, "onClick: eyebrow1");
                mainActivity.setEyeBrowImg(1);
                break;
            case R.id.eyebrow2:
                Log.e(TAG, "onClick: eyebrow2");
                mainActivity.setEyeBrowImg(2);
                break;
        }
    }
}
