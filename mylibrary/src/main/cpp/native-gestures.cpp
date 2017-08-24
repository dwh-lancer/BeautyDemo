#include <opencv2/core/mat.hpp>/************************************************************************/
/*
Description:	手势检测
				先滤波去噪
				-->转换到HSV空间
				-->根据皮肤在HSV空间的分布做出阈值判断，这里用到了inRange函数，
				然后进行一下形态学的操作，去除噪声干扰，是手的边界更加清晰平滑
				-->得到的2值图像后用findContours找出手的轮廓，去除伪轮廓后，再用convexHull函数得到凸包络
Author:			Yang Xian
Email:			yang_xian521@163.com
Version:		2011-11-2
History:
*/

#include <opencv2/opencv.hpp>
#include <jni.h>
#include <android/log.h>


#define LOG_TAG "***"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
jobject obj;
jclass jcls;
jmethodID callback;
using namespace cv;
using namespace std;

string str = "/storage/emulated/0/letv/";
//IplImage* frame1;
char c;
int frameNum = -1;			// Frame counter
bool lastImgHasHand=false;
int previousX=0;
int previousY=0;
Mat frame;
Mat frame2;
Mat frameHSV;
Mat mask(frame.rows, frame.cols, CV_8UC1);	// 2值掩膜
Mat dst(frame);	// 输出图像
Mat frameRGB;

bool bHandFlag = false;

vector< vector<Point> > contours;	// 轮廓
vector< vector<Point> > filterContours;	// 筛选后的轮廓
vector< Vec4i > hierarchy;	// 轮廓的结构信息
vector< Point > hull;	// 凸包络的点集

bool movement=false;
int icount=0;
int presentX=0;
int presentY=0;
string Int_to_String(int n) {
    ostringstream stream;
    stream << n;  //n为int类型
    return stream.str();
}
int imgCount =35;
int imgCount1=1;
int dst1Count=1;
int dst2Count=1;
 int rgbimgCount=1;
//Mat frame1;
void main1(JNIEnv* env,Mat frame1)
{

//    while (imgCount<83)
//cvSaveImage("frame0.jpg",frame1);
//  imwrite(str+Int_to_String(imgCount)+".jpg", frame);
//    {
        int minX=320;//屏幕的一半
        int maxX=240;
        int minY=320;
        int maxY=240;
//       frame1 = imread("/storage/emulated/0/qt/"+Int_to_String(imgCount) + ".jpg");

//        Mat frame3;
//        Mat frame4;
//        Mat frame5;
//        Mat frame7;
//        frame1 = imread("/storage/emulated/0/qt/1.jpg");
        cvtColor(frame1, frameRGB,CV_RGBA2BGRA);
//        cvtColor(frameRGB, frame7,CV_BGRA2BGR);
        cvtColor(frameRGB, frame,CV_RGBA2BGR);//可以用的
//        frame5 = imread("/storage/emulated/0/rgb42.jpg");
//        cvtColor(frame5, frame4,CV_RGBA2BGR);
//        cvtColor(frame2, frame3,CV_BGR2RGB);
//        imwrite("/storage/emulated/0/BGRA.jpg", frameRGB);
//        imwrite("/storage/emulated/0/BGR.jpg", frame2);
//        imwrite("/storage/emulated/0/RGB.jpg", frame3);
//        imwrite("/storage/emulated/0/BGR1.jpg", frame4);
//        imwrite("/storage/emulated/0/BGR2.jpg", frame7);
//        frame = imread("/storage/emulated/0/BGR.jpg");
//        rgbimgCount++;
        if(frame.empty())
        {
            LOGD("frame视频帧图像空");
        } else{
            LOGD("frame视频帧图像非空~");
        }
       //  Mat frame32 = cvCreateImage(cvGetSize(frame),32,frame->nChannels);

//        imgCount++;
        // 中值滤波，去除椒盐噪声
        medianBlur(frame, frame, 5);
        cvtColor( frame, frameHSV, CV_RGB2HSV );
        if(frameHSV.empty())
        {
            LOGD("frameHSV视频帧图像空1");
        } else{
            LOGD("frameHSV视频帧图像非空1");
        }
//        imwrite("/storage/emulated/0/frameHSV.jpg", frameHSV);
//        imgCount1++;
        Mat dstTemp1(frame.rows, frame.cols, CV_8UC1);
        Mat dstTemp2(frame.rows, frame.cols, CV_8UC1);
        // 对HSV空间进行量化，得到2值图像，亮的部分为手的形状
        inRange(frameHSV, Scalar(0,30,30), Scalar(40,170,256), dstTemp1);
        inRange(frameHSV, Scalar(156,30,30), Scalar(180,170,256), dstTemp2);
        bitwise_or(dstTemp1, dstTemp2, mask);

   //    	inRange(frameHSV, Scalar(0,30,30), Scalar(180,170,256), dst);
        // 形态学操作，去除噪声，并使手的边界更加清晰
        Mat element = getStructuringElement(MORPH_RECT, Size(3, 3));
        erode(mask, mask, element);
        morphologyEx(mask, mask, MORPH_OPEN, element);
        dilate(mask, mask, element);
        morphologyEx(mask, mask, MORPH_CLOSE, element);
        frame.copyTo(dst, mask);
//       imwrite("/storage/emulated/0/LIANG!----BGR2.jpg", dst);
//        dst1Count++ ;
        contours.clear();
        hierarchy.clear();
        filterContours.clear();
//        // 得到手的轮廓
        findContours(mask, contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        if(!contours.empty()) {
            LOGD("轮廓非空");
        }
//      // 去除伪轮廓ede
        for (size_t i = 0; i < contours.size(); i++) {
            //	approxPolyDP(Mat(contours[i]), Mat(approxContours[i]), arcLength(Mat(contours[i]), true)*0.02, true);
            if (fabs(contourArea(Mat(contours[i]))) > 30000)    //判断手进入区域的阈值
            {
                filterContours.push_back(contours[i]);
            }
        }
        // 画轮廓
        if (filterContours.size() > 0) {
            icount++;
            lastImgHasHand = true;
            drawContours(dst, filterContours, -1, Scalar(255, 0, 255), 3/*, 8, hierarchy*/);
//            imwrite(str+"dst1"+Int_to_String(dst1Count)+".jpg", dst);
//            dst1Count++ ;
            for (size_t j = 0; j < filterContours.size(); j++) {
                convexHull(Mat(filterContours[j]), hull, true);
                int hullcount = (int) hull.size();
                for (int i = 0; i < hullcount - 1; i++) {
                    line(dst, hull[i + 1], hull[i], Scalar(255, 255, 255), 2, CV_AA);//白色

                    //printf("num%d:x=%d\ty=%d\t\n",i,hull[i].x,hull[i].y);
                    if (hull[i].x > maxX)
                        maxX = hull[i].x;
                    if (hull[i].x < minX)
                        minX = hull[i].x;
                    if (hull[i].y > maxY)
                        maxY = hull[i].y;
                    if (hull[i].y < minY)
                        minY = hull[i].y;
                    //	printf("miniX=%d\tminiY=%d\tmaxX=%d\tmaxY=%d\t\n",minX,minY,maxX,maxY);
//                    __android_log_print(ANDROID_LOG_DEBUG, "JNI-先前的X.Y", "%d-%d-%d-%d", minX, minY,
//                                        maxX, maxY);
                }

                line(dst, hull[hullcount - 1], hull[0], Scalar(0, 255, 0), 2, CV_AA);//绿色，最后一条

//                imwrite("/storage/emulated/0/qt/data/"+Int_to_String(dst2Count)+".jpg", dst);
                dst2Count++;
                if (icount == 1)//第一个轮廓的中心位置存在全局变量中，到最后一个再跟它比。
                {
                    previousX = (minX + maxX) / 2;

                    //printf("previousX=%d\n",previousX);
                    previousY = (minY + maxY) / 2;
                    //printf("previousY=%d\n",previousY);
                __android_log_print(ANDROID_LOG_DEBUG, "JNI-先前的X.Y","%d-%d",previousX,previousY);
                }
                else {
                    presentX = (minX + maxY) / 2;
                    presentY = (minY + maxY) / 2;
                     __android_log_print(ANDROID_LOG_DEBUG, "JNI-当前的X.Y","%d-%d",presentX,presentY);
                }
            }
        } else {
            if (lastImgHasHand == true) {
                if ((previousY - presentY) < 0)//中文的大括号和英文的大括号用肉眼看不出来，坑啊
                {
                    // printf("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<left\n");//镜像，没有flip过来，所以这里注意点。
                    __android_log_print(ANDROID_LOG_DEBUG, "***NDKLoaderJNI", "下*******");
                    env->CallVoidMethod(obj,callback,2);
//                LOGD("123");
                } else if ((previousY - presentY) > 0) {
                    // printf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>right\n");
                    __android_log_print(ANDROID_LOG_ERROR, "***NDKLoaderJNI", "********上");
                    env->CallVoidMethod(obj,callback,1);
                }

                icount = 0;
                lastImgHasHand = false;
            }
        }

        dst.release();
	}

char* jstring2str(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes","(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}




extern "C" {

JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_nativeSendFrame2Gestures(JNIEnv *env, jobject instance,
                                                     jlong inputFrame) {

    Mat frame = *(Mat*)inputFrame;
    if(!frame.empty()){
        LOGD("视频帧图像非空");
        __android_log_print(ANDROID_LOG_ERROR,"GestureRec","frame像素%d*%d",frame.cols,frame.rows);
    }
    main1(env,frame);
    obj  = instance;
    jcls = env->GetObjectClass(obj);
    callback = env->GetMethodID(jcls,"callback","(I)V");
//    env->CallVoidMethod(obj,callback,2);
}
}
