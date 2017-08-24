package com.neu.beautydemo.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.neu.beautydemo.R;
import com.neu.beautydemo.util.ImageUtils;

/**
 * @author:Jack Tony
 * 
 * 重要：注意要申请权限！！！！
 *  <!-- 悬浮窗的权限 -->
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 * 
 * @tips  :思路：
 * 1.获得一个windowManager类
 * 2.通过wmParams设置好windows的各种参数
 * 3.获得一个视图的容器，找到悬浮窗视图的父控件，比如linearLayout
 * 4.将父控件添加到WindowManager中去
 * 5.通过这个父控件找到要显示的悬浮窗图标，并进行拖动或点击事件的设置
 * @date  :2014-9-25
 */
public class FloatingService extends Service {
	/**
	 * 定义浮动窗口布局
	 */
	LinearLayout mlayout;
	LinearLayout mLinearLayout;
	/**
	 * 悬浮窗控件
	 */
	ImageView mfloatingIv;
	/**
	 * 悬浮窗的布局
	 */
	LayoutParams wmParams;
	LayoutInflater inflater;
	/**
	 * 创建浮动窗口设置布局参数的对象
	 */
	WindowManager mWindowManager;

	//触摸监听器
	GestureDetector mGestureDetector;

	String TAG = "******";
	private String picPath; //图片路径
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		initWindow();//设置窗口的参数
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		picPath = intent.getStringExtra("fillName");
		if (picPath == null) {
			Toast.makeText(getApplicationContext(), "照片路径为空", 0).show();
		}
		Log.d(TAG, "服务fillName: "+picPath);
		initFloating(picPath);//设置悬浮窗图标
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mlayout != null) {	
			// 移除悬浮窗口
			mWindowManager.removeView(mlayout);
		}
	}

	
	/**
	 * 初始化windowManager
	 */
	private void initWindow() {
		mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
		wmParams = new LayoutParams();
		//设置window type ,以Toast，不需要权限
		wmParams.type = LayoutParams.TYPE_TOAST;
		//设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		//设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		//设置悬浮窗口长宽数据
		wmParams.width = 200;
		wmParams.height = 200;
		// 悬浮窗默认显示以左上角为起始坐标
		wmParams.gravity = Gravity.LEFT| Gravity.TOP;
		//悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0		
		wmParams.x = 100;
		wmParams.y = 100;
		//得到容器，通过这个inflater来获得悬浮窗控件
		inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mlayout = (LinearLayout) inflater.inflate(R.layout.layout_float_window, null);
		// 添加悬浮窗的视图
		mWindowManager.addView(mlayout, wmParams);
	}
	
	/**
	 * 找到悬浮窗的图标，并且设置事件
	 * 设置悬浮窗的点击、滑动事件
	 */
	private void initFloating(String fillName) {
		/**  图片在这里*/
		Log.d(TAG, "服务initFloating: "+fillName);
		mfloatingIv = (ImageView) mlayout.findViewById(R.id.img_float_window);
		//获得fillName的图片，然后给悬浮窗设置图片
		mfloatingIv.setImageBitmap(ImageUtils.compressBitmap(fillName));
		if(ImageUtils.compressBitmap(fillName) == null){
			Log.d(TAG, "获取照片失败"+fillName);
		}else {
			Log.d(TAG, "获取照片成功"+fillName);
		}

		mfloatingIv.setOnTouchListener(new FloatingListener());
//		mfloatingIv.getBackground().setAlpha(150);
		mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
		//设置监听器
	}
	
	//开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
	private int mTouchStartX,mTouchStartY,mTouchCurrentX,mTouchCurrentY;
	//开始时的坐标和结束时的坐标（相对于自身控件的坐标）
	private int mStartX,mStartY,mStopX,mStopY;
	private int mMode = 0;
	/** 拖拉照片模式 */
	private static final int MODE_DRAG = 1;
	/** 放大缩小照片模式 */
	private static final int MODE_ZOOM = 2;
	/**  缩放开始时的手指间距 */
	private float mStartDis;
	/**   最大缩放级别*/
	float mMaxScale=6;
	
	/**
	 * @author:金凯
	 * @tips  :自己写的悬浮窗监听器
	 * @date  :2014-3-28
	 */
	private class FloatingListener implements OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent event) {

			int action = event.getAction();
			switch(action & event.getActionMasked()){
				case MotionEvent.ACTION_DOWN:
					mMode = MODE_DRAG;
					mTouchStartX = (int)event.getRawX();
					mTouchStartY = (int)event.getRawY();
					mStartX = (int)event.getX();
					mStartY = (int)event.getY();
					Log.d(TAG, "ACTION_DOWN:"
                                +"mTouchStartX:"+mTouchStartX
                                +"mTouchStartY:"+mTouchStartY
                                +"mStartX:"+mStartX
                                +"mStartY:"+mStartY  );
					break; 
				case MotionEvent.ACTION_MOVE:
					if (mMode == MODE_ZOOM) {
//						Log.d(TAG, "ACTION_MOVE_zoom: ");
						setZoom(event);

					}else if (mMode==MODE_DRAG) {
						mTouchCurrentX = (int) event.getRawX();
						mTouchCurrentY = (int) event.getRawY();
						wmParams.x += mTouchCurrentX - mTouchStartX;
						wmParams.y += mTouchCurrentY - mTouchStartY;
						mWindowManager.updateViewLayout(mlayout, wmParams);
						Log.d(TAG, "ACTION_MOVE_drag: "
								+"mTouchCurrentX"+mTouchCurrentX
								+"mTouchCurrentY"+mTouchCurrentY  );
						mTouchStartX = mTouchCurrentX;
						mTouchStartY = mTouchCurrentY;
					}
		            break;
				case MotionEvent.ACTION_POINTER_DOWN:
					mMode=MODE_ZOOM;
					mStartDis = distance(event);
					Log.d(TAG, "ACTION_POINTER_DOWN: "+"mStartDis"+mStartDis);
					break;
				case MotionEvent.ACTION_UP:
					mMode  = 0 ;
					mStopX = (int)event.getX();
					mStopY = (int)event.getY();
					Log.d(TAG, "ACTION_UP: "
					+"mStopX"+mStopX
					+"mStopY"+mStopY  );
					//System.out.println("|X| = "+ Math.abs(mStartX - mStopX));
					//System.out.println("|Y| = "+ Math.abs(mStartY - mStopY));
//					if(Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1){
//						mMode = MODE_DRAG;
//					}
		            break; 
			}
			return mGestureDetector.onTouchEvent(event);  //此处必须返回false，否则OnClickListener获取不到监听
		}

	}
	/**  设置缩放Matrix
	*  @param event
	*/
	private void setZoom(MotionEvent event) {
		Log.d(TAG, "setZoom: ");
		//只有同时触屏两个点的时候才执行
		if(event.getPointerCount()<2) return;
		Log.d(TAG, "setZoom: "+"手指数"+  event.getPointerCount());
		float endDis = distance(event);// 结束距离
		Log.d(TAG, "setZoom: "+"endDis"+endDis);
		if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
			float scale = endDis - mStartDis;// 得到缩放倍数
			Log.d(TAG, "setZoom: "+"scale"+scale);
			mStartDis=endDis;//重置距离
			if (scale > 100){scale = 100;}

			wmParams.width += scale ;
			wmParams.height += scale;
			mWindowManager.updateViewLayout(mlayout,wmParams);
		}
	}
	/**
	 *  计算两个手指间的距离
	 *  @param event
	 *  @return
	 */
	private float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		/** 使用勾股定理返回两点之间的距离 */
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * @author:金凯
	 * @tips  :自己定义的手势监听类
	 * @date  :2014-3-29
	 */
//	private class  GestureListener extends SimpleOnGestureListener{
//		private final MatrixTouchListener listener;
//		public GestureListener(MatrixTouchListener listener) {
//			this.listener=listener;
//		}
//		@Override
//		public boolean onDown(MotionEvent e) {
//			//捕获Down事件
//			return true;
//		}
//		@Override
//		public boolean onDoubleTap(MotionEvent e) {
//			//触发双击事件
//			listener.onDoubleClick();
//			return true;
//		}
	class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Toast.makeText(getApplicationContext(), "你双击了悬浮窗", 0).show();
//			if (wmParams.width == 200 && wmParams.height == 200){
//				wmParams.width = 400;
//				wmParams.height = 400;
//			}else {
//				wmParams.width = 200;
//				wmParams.height = 200;
//			}
//			mWindowManager.updateViewLayout(mlayout,wmParams);
			if (mlayout != null) {
				// 移除悬浮窗口
				mWindowManager.removeView(mlayout);
			}
			return super.onDoubleTap(e);
		}
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
//			if (!isMove) {
//				Toast.makeText(getApplicationContext(), "你点击了悬浮窗", 0).show();
//				System.out.println("onclick");
//			}
			return super.onSingleTapConfirmed(e);
		}
	}

	
	
}
