package com.neu.beautydemo.adapter;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.neu.beautydemo.R;
import com.neu.beautydemo.activity.MainActivity;
import com.neu.beautydemo.entity.College;

import java.util.List;

/**
 *
 * 外层ExpandListView 适配器的实现
 * Created by MH on 2016/6/16.
 */
public class SimpleExpandableListViewAdapter extends BaseExpandableListAdapter {


    // 大学的集合
    private List<College> colleges;

    private MainActivity activity;



    public SimpleExpandableListViewAdapter(List<College> colleges, MainActivity activity) {
        this.colleges = colleges;
        this.activity = activity;
    }

    @Override
    public int getGroupCount() {
        return colleges.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // 很关键，，一定要返回  1
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return colleges.get(groupPosition);
    }//此时，colleges为list.get

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return colleges.get(groupPosition).classList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView view = getGenericView(colleges.get(groupPosition).name);
        switch (groupPosition){
            case 0:
                view.setBackgroundResource(R.mipmap.menu2);
                break;
            case 1:
                view.setBackgroundResource(R.mipmap.menu2);
                break;
            case 2:
                view.setBackgroundResource(R.mipmap.menu3);
                break;
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        // 返回子ExpandableListView 的对象  此时传入是该父条目，即大学的对象（有歧义。。）

        return getGenericExpandableListView(colleges.get(groupPosition));
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private TextView getGenericView(String string) {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = 300;
        TextView textView = new TextView(activity);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.TOP|Gravity.LEFT);
        textView.setPadding(20, 20, 0, 20);
        textView.setText(string);
        textView.setTextSize(25);
        textView.setTextColor(Color.WHITE);
        /**这里设置的是大学的视图*/
        return textView;
    }


    /**
     *  返回子ExpandableListView 的对象  此时传入的是该大学下所有班级的集合。
     * @param college
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public ExpandableListView getGenericExpandableListView(final College college){
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        CustomExpandableListView view = new CustomExpandableListView(activity);

        // 加载班级的适配器
        final ClassesExpandableListViewAdapter adapter = new ClassesExpandableListViewAdapter
                (college.classList,activity);
        view.setDivider(null);
        view.setAdapter(adapter);
        view.setPadding(20,0,0,0);
        view.setGroupIndicator(null);
        view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,
                                        View view, int i, int i1, long l) {
                switch (college.name){
    /**美妆 */
                    case "美妆":
                        if (i == 0 && i1 == 0){
                            //眼影 启动镜像
//                            activity.setMeizVisible();
                            Toast.makeText(activity, "眼影 启动镜像", Toast.LENGTH_SHORT).show();
                        }else if (i == 0 && i1 == 1){
                            //眼影 美妆记录
                            Toast.makeText(activity, "眼影 美妆记录", Toast.LENGTH_SHORT).show();
                        }else if (i == 1 && i1 == 0){
                            //腮红 启动镜像
                            Toast.makeText(activity, "腮红 启动镜像", Toast.LENGTH_SHORT).show();
                        }else if (i == 1 && i1 == 1){
                            //腮红 美妆记录
                            Toast.makeText(activity, "腮红 美妆记录", Toast.LENGTH_SHORT).show();
                        }else if (i == 2 && i1 == 0){
                            //眼线 启动镜像
                            Toast.makeText(activity, "眼线 启动镜像", Toast.LENGTH_SHORT).show();
                        }else if (i == 2 && i1 == 1){
                            //眼线 美妆记录
                            Toast.makeText(activity, "眼线 美妆记录", Toast.LENGTH_SHORT).show();
                        }else if (i == 3 && i1 == 0){
                            //唇彩 启动镜像
                            Toast.makeText(activity, "唇彩 启动镜像", Toast.LENGTH_SHORT).show();
                        }else if (i == 3 && i1 == 1){
                            //唇彩 美妆记录
                            Toast.makeText(activity, "唇彩 美妆记录", Toast.LENGTH_SHORT).show();
                        }else if (i == 4 && i1 == 0){
                            //粉底/隔离 启动镜像
                            Toast.makeText(activity, "粉底/隔离 启动镜像", Toast.LENGTH_SHORT).show();
                        }else if (i == 4 && i1 == 1){
                            //粉底/隔离 美妆记录
                            Toast.makeText(activity, "粉底/隔离 美妆记录", Toast.LENGTH_SHORT).show();
                        }else if (i == 5 && i1 == 0){
                            //眼镜 启动镜像
                            Toast.makeText(activity, "眼镜 启动镜像", Toast.LENGTH_SHORT).show();
                        }else if (i == 5 && i1 == 1){
                            //眼镜 美妆记录
                            Toast.makeText(activity, "眼镜 美妆记录", Toast.LENGTH_SHORT).show();
                        }else if (i == 6 && i1 == 0){
                            //整体美妆 启动镜像
                            Toast.makeText(activity, "整体美妆 启动镜像", Toast.LENGTH_SHORT).show();
                        }else if (i == 6 && i1 == 1){
                            //整体美妆 美妆记录
                            Toast.makeText(activity, "整体美妆 美妆记录", Toast.LENGTH_SHORT).show();
                        }else {

                        }
                        break;
    /**护肤 */
                    case "护肤":
                        if (i == 0 && i1 == 0){
                            //面部健康 皱纹分布
//                            activity.setImgWrinkleVisible();
                            activity.switchFragmentWrinkle();
                            Toast.makeText(activity, "面部健康 皱纹分布", Toast.LENGTH_SHORT).show();
                        }else if (i == 0 && i1 == 1){
                            //面部健康 粉刺痘痘
                            activity.switchFragmentPimple();
                            Toast.makeText(activity, "面部健康 粉刺痘痘", Toast.LENGTH_SHORT).show();
                        }else if (i == 0 && i1 == 2){
                            //面部健康 敷面膜
                            activity.switchFragmentGlasses();
                            Toast.makeText(activity, "面部健康 虚拟眼镜", Toast.LENGTH_SHORT).show();
                        }else if (i == 0 && i1 == 3){
                            //面部健康 敷面膜
                            activity.switchFragmentEyeBrow();
                            Toast.makeText(activity, "面部健康 虚拟眉毛", Toast.LENGTH_SHORT).show();
                        }else if (i == 0 && i1 == 4){
                            //面部健康 敷面膜
                            activity.switchFragmentSkin();
                            Toast.makeText(activity, "面部健康 换肤", Toast.LENGTH_SHORT).show();
                        }else if (i == 1 && i1 == 0){
                            //局部放大 祛痘局部
                            Toast.makeText(activity, "局部放大 祛痘局部", Toast.LENGTH_SHORT).show();
                        }else if (i == 1 && i1 == 1){
                            //局部放大 剃须放大
                            Toast.makeText(activity, "局部放大 剃须放大", Toast.LENGTH_SHORT).show();
                        }else if (i == 1 && i1 == 2){
                            //局部放大 隐形眼镜
                            Toast.makeText(activity, "局部放大 隐形眼镜", Toast.LENGTH_SHORT).show();
                        }else if (i == 1 && i1 == 3){
                            //局部放大 粉刺放大
                            Toast.makeText(activity, "局部放大 粉刺放大", Toast.LENGTH_SHORT).show();
                        }else if (i == 2 && i1 == 0){
                            //方案定制 当季护肤定制
                            Toast.makeText(activity, "方案定制 当季护肤定制", Toast.LENGTH_SHORT).show();
                        }else if (i == 2 && i1 == 1){
                            //方案定制 控油祛痘 整体方案
                            Toast.makeText(activity, "方案定制 控油祛痘 整体方案", Toast.LENGTH_SHORT).show();
                        }else if (i == 2 && i1 == 2){
                            //方案定制 抗衰老护理整体方案
                            Toast.makeText(activity, "方案定制 抗衰老护理整体方案", Toast.LENGTH_SHORT).show();
                        }
                        else if (i == 3 && i1 == 0){
                            //效果测评 记录
                            Toast.makeText(activity, "效果测评 记录", Toast.LENGTH_SHORT).show();
                        }else if (i == 3 && i1 == 1){
                            //效果测评 查看记录
                            Toast.makeText(activity, "效果测评 查看记录", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "零距离":
                        Toast.makeText(activity, "零距离", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        return view;
    }
}
