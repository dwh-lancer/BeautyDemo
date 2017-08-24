package com.neu.beautydemo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.neu.beautydemo.R;
import com.neu.beautydemo.activity.MainActivity;
import com.neu.beautydemo.adapter.SimpleExpandableListViewAdapter;
import com.neu.beautydemo.entity.Classes;
import com.neu.beautydemo.entity.College;

import java.util.ArrayList;
import java.util.List;

public class FragmentGuide extends Fragment{
    MainActivity activity;
    private static final String TAG = "FragmentGuide";
    private ExpandableListView listview;
    private List<College> colleges; 

    public static FragmentGuide newInstance() {
        FragmentGuide fragment = new FragmentGuide();
        return fragment;
    }
    public FragmentGuide() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();//获取activity的属性和方法
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        initData();
        // 查找控件
        listview = (ExpandableListView) view.findViewById(R.id.expandablelistview);
        SimpleExpandableListViewAdapter adapter = new SimpleExpandableListViewAdapter(colleges,activity);
        // 设置适配器
        listview.setAdapter(adapter);
        listview.setGroupIndicator(null);//箭头
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void initData() {
        String[] classType1 = {"眼影","腮红","眼线","唇彩","粉底/隔离","眼镜","整体美妆"};
        String[] studentType = {"启动镜像", "美妆记录"};
        String[] classType2 = {"面部健康","局部放大","方案定制","效果测评"};
        String[] classType3 = {"零距离"};

        College[] college = new College[3];
        college[0] = new College();
        college[1] = new College();
        college[2] = new College();


        List<Classes> classesList1 = new ArrayList<>();
        //美妆大学有七个班级，每个班级有两个同学
        for(int i = 0 ;i<7;i++) {
            Classes classes = new Classes();
            classes.name = classType1[i];
            List<String> list = new ArrayList<>();
            list.add(studentType[0]);
            list.add(studentType[1]);
            classes.students = list;
            classesList1.add(classes);
        }
        college[0].name = "美妆";
        college[0].classList = classesList1;

        List<Classes> classesList2 = new ArrayList<>();
        //护肤大学有4个班级，每个班级有不同的同学
        //1班添加学生
        Classes class1 = new Classes();
        class1.name = classType2[0];
        List<String> list1 = new ArrayList<>();
        list1.add("皱纹分布");
        list1.add("粉刺痘痘");
        list1.add("虚拟眼镜");
        list1.add("虚拟眉毛");
        list1.add("换肤");
        class1.students = list1;
        //2班添加学生
        Classes class2 = new Classes();
        class2.name = classType2[1];
        List<String> list2 = new ArrayList<>();
        list2.add( "祛痘局部" );
        list2.add( "剃须放大");
        list2.add( "隐形眼镜");
        list2.add( "粉刺放大");

        class2.students = list2;
        //3班添加学生
        Classes class3 = new Classes();
        class3.name = classType2[2];
        List<String> list3 = new ArrayList<>();
        list3.add("当季护肤定制");
        list3.add("控油祛痘整体方案");
        list3.add("抗衰老护理整体方案");
        class3.students = list3;
        //4班添加学生
        Classes class4 = new Classes();
        class4.name = classType2[3];
        List<String> list4 = new ArrayList<>();
        list4.add("记录");
        list4.add("查看记录");
        class4.students = list4;
        classesList2.add(class1);
        classesList2.add(class2);
        classesList2.add(class3);
        classesList2.add(class4);

        college[1].name = "护肤";
        college[1].classList = classesList2;

        List<Classes> classesList3 = new ArrayList<>();
        Classes class5 = new Classes();
        class5.name = "零距离";
        List<String> list5 = new ArrayList<>();
        list5.add("零距离");
        class5.students = list5;
        classesList3.add(class5);
        college[2].name = "零距离";
        college[2].classList = classesList3;


        colleges = new ArrayList<>();
        for (int i = 0;i<3;i++){
            colleges.add(college[i]);
        }

    }

}
