package com.neu.beautydemo.fragment;

import android.app.Fragment;
import android.content.Context;
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

public class FragmentGlasses extends Fragment implements View.OnClickListener {

    private ImageView imgGlas;
    private ImageView imgGlas2;
    String TAG = "***Glasses";
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_glasses,container,false);
        imgGlas  = (ImageView)view.findViewById(R.id.glasses);
        imgGlas2 = (ImageView)view.findViewById(R.id.glasses2);
        imgGlas.setOnClickListener(this);
        imgGlas2.setOnClickListener(this);
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
            case R.id.glasses:
                Log.e(TAG, "onClick: glasses");
                mainActivity.setGlassesImg(1);
                break;
            case R.id.glasses2:
                Log.e(TAG, "onClick: glasses2");
                mainActivity.setGlassesImg(2);
                break;
        }
    }
}
