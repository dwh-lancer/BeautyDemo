#include <iostream>
#include <vector>
#include <string>
#include <android/log.h>
#include<opencv2/opencv.hpp>
#include <jni.h>

#define SKIN_WHITEN 1
#define SUN_SHINE 2
#define BUBBLE 3

#define LOG_TAG "***Skin"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
using namespace std;
using namespace cv;

string imgPathSunshine2 = "/data/data/com.neu.beautydemo/files/sunshine2.png";
string imgPathBubble1  = "/data/data/com.neu.beautydemo/files/bubble1.png";
string xmlFacePath= "/data/data/com.neu.beautydemo/app_neu/haarcascade_frontalface_alt.xml";

int detectFaceAndProcess(Mat srcImage,int type);
void SkinWhiten(Mat faceROI);
void SunShineProcess(Mat faceROI);
void BubbleProcess(Mat faceROI);


/*检测人脸区域*/
int detectFaceAndProcess(Mat srcImage,int type)
{
	/*人脸检测模型文件以及分类器的创建*/
	CascadeClassifier face_casade;
	Mat srcImageGray;
	vector<Rect> faces;
	if (!face_casade.load(xmlFacePath))
	{
		LOGD("can not find the face modelFile!!!");
	}
	/*转换为灰度图像--直方图均衡化--人脸检测*/
	cvtColor(srcImage, srcImageGray, CV_RGB2GRAY);
	equalizeHist(srcImageGray, srcImageGray);
	face_casade.detectMultiScale(srcImageGray, faces, 1.1, 3, 0, Size(60, 60));
	__android_log_print(ANDROID_LOG_DEBUG, "***Skin", "faces.size():%d",faces.size());
	if (faces.size() != 0)
	{
		for (int i = 0; i < faces.size(); i++)
		{
			Mat	faceROI = srcImage(faces[i]);
			if(type == 2){
				SunShineProcess(faceROI);
			} else if(type == 3){
				BubbleProcess(faceROI);
			}
		}
		return 0;
	}
	else
	{
		LOGD("can not find faces!!!");
		return -1;
	}
}

void SkinWhiten(Mat faceROI)
{
	LOGD("美白");
	int value = 2;
	//两次高斯模糊
	GaussianBlur(faceROI, faceROI, Size(2 * value - 1, 2 * value - 1), 0, 0);
	GaussianBlur(faceROI, faceROI, Size(2 * value - 1, 2 * value - 1), 0, 0);

	//改变图像亮度和对比度
	double alpha = 1.0, beta = 50;
	for (int y = 0; y < faceROI.rows; y++)
	{
		for (int x = 0; x < faceROI.cols; x++)
		{
			for (int c = 0; c < 3; c++)
			{
				faceROI.at<Vec3b>(y, x)[c] = saturate_cast<uchar>(alpha*(faceROI.at<Vec3b>(y, x)[c]) + beta);
			}
		}
	}
}

void  SunShineProcess(Mat faceROI)
{
	Mat SunShineImage = imread(imgPathSunshine2);
	cvtColor(SunShineImage, SunShineImage,CV_BGR2RGBA);
	Mat SunShineImageResize;
	resize(SunShineImage, SunShineImageResize, Size(faceROI.rows, faceROI.cols));
	addWeighted(faceROI, 0.5, SunShineImageResize, 0.5, 0.0, faceROI);
}

void BubbleProcess(Mat faceROI)
{
	Mat BubbleImage = imread(imgPathBubble1);
	cvtColor(BubbleImage, BubbleImage,CV_BGR2RGBA);
	Mat BubbleImageResize;
	resize(BubbleImage, BubbleImageResize, Size(faceROI.rows, faceROI.cols));
	addWeighted(faceROI, 0.8, BubbleImageResize, 0.2, 0.0, faceROI);
}



extern "C"{
JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_nativeSendFrame2Skin(JNIEnv *env, jobject instance,
															 jlong inputFrame,jint type) {

	Mat &mRgb = *(Mat*)inputFrame;
//	main1(mRgb);
	if (type == 1) {
		LOGD("美白");
		SkinWhiten(mRgb);
	} else if (type == 2) {
		LOGD("阳光");
		detectFaceAndProcess(mRgb,2);
	} else if (type == 3) {
		LOGD("气泡");
		detectFaceAndProcess(mRgb,3);
	}
}
}
