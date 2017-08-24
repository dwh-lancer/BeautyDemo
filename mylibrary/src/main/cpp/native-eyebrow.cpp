#include <jni.h>
#include <string>
#include<opencv2/opencv.hpp>
#include <android/log.h>
#include <iostream>
#include <stdio.h>
#define LOG_TAG "***eyebrow"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace cv;
using namespace std;

void detectAndDisplay(Mat frame);

CascadeClassifier face_cascade;
CascadeClassifier eyes_cascade;

int leftLineX, leftLineY,rightLine,EYEBROWwidth, EYEBROWsize,startPoint;
std::vector<Rect> eyes;
Mat logo;
Mat logonew;
Mat logoBGR;
// 定义一个Mat类型，用于存放，图像的ROI
Mat imageROI;
int RightEYEBROW_X = 0;//右眉毛坐标
int RightEYEBROW_Y = 0;
int RightEYEBROW_Width = 0;
int RightEYEBROW_Height = 0;

int  LeftEYEBROW_X = 0;//左眉毛坐标
int  LeftEYEBROW_Y = 0;
int  LeftEYEBROW_Width = 0;
int  LeftEYEBROW_Height = 0;

bool xmlState = false;
int currentType = 0;
string imgEyebrowPath;
string xmlFacePath    = "/data/data/com.neu.beautydemo/app_neu/haarcascade_frontalface_alt.xml";
string xmlEyePath     = "/data/data/com.neu.beautydemo/app_neu/haarcascade_eye_tree_eyeglasses.xml";

string Int_to_String(int n)
{
    ostringstream stream;
    stream<<n;  //n为int类型
    return stream.str();
}

void main1(Mat frame){

    detectAndDisplay(frame);
    if (eyes.size() == 2) {
        imageROI = frame(Rect(startPoint, leftLineY, logonew.cols, logonew.rows));
        addWeighted(imageROI, 1, logonew, 1, 0., imageROI);
    }
    else {
        __android_log_print(ANDROID_LOG_DEBUG, "***", "loss,eyes.size()%d", eyes.size());
    }
}

void detectAndDisplay(Mat frame){
    int eyeBROWwidth, eyeBROWhight, rightLineX;
    vector<Rect> faces;
    Mat  frameGray;
    cvtColor(frame,frameGray,CV_RGB2GRAY);
    equalizeHist(frameGray,frameGray);
    face_cascade.detectMultiScale(frameGray, faces, 1.1, 2, 0);//检测人脸
    if (faces.size() == 0) {
        LOGD("can not find faces!!!");
        return;
    } else {
        LOGD("find face,%d",faces.size());
        for (size_t i = 0; i < faces.size(); i++) {
            //在原图像中找到人脸区域，灰度化、直方图均衡化
            Mat faceROI = frame(faces[i]);
//            eyes_cascade.detectMultiScale( faceROI, eyes, 1.1,2,0);
            eyes_cascade.detectMultiScale(faceROI, eyes, 1.1, 3, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));
            int facesX = faces[i].x;
            int facesY = faces[i].y;
            int facesWidth = faces[i].width;
            if (eyes.size() == 2) {
                //第一个眼睛的相应坐标【左上角以及相应的宽和高】
                int eyesZeroX = eyes[0].x;
                int eyesZeroY = eyes[0].y;
                int eyesZeroWidth = eyes[0].width;
                int eyesZeroHeight = eyes[0].height;


                //第二个眼睛的相应坐标【左上角以及相应的宽和高】
                int eyesOneX = eyes[1].x;
                int eyesOneY = eyes[1].y;
                int eyesOneWidth = eyes[1].width;
                int eyesOneHeight = eyes[1].height;
                if (eyes[0].x >= eyes[1].x)//0是右眼，1是左眼
                {
                    //画出右眉毛区域
                    //rectangle(frame,cvPoint(eyesZeroX + facesX, eyesZeroY - eyesZeroHeight * 2/ 5 + facesY),cvPoint(eyesZeroX + facesX+eyesZeroWidth*1.3,eyesZeroY - eyesZeroHeight * 2/ 5 + facesY+eyesZeroHeight/2),Scalar( 255, 0,0 ),1,8, 0 );
                    //imshow("righteyesbrow_Rect",frame);
                    // waitKey(10000);
                    //右眉毛坐标
                    RightEYEBROW_X = eyesZeroX + facesX;
                    RightEYEBROW_Y = eyesZeroY - eyesZeroHeight * 2 / 5 + facesY;
                    RightEYEBROW_Width = eyesZeroWidth*1.3;
                    RightEYEBROW_Height = eyesZeroHeight / 2;
                    // string img = "/storage/emulated/0/headwrinkle/leftEyeCorner" + Int_to_String(imgCount) + ".jpg";

                    //画出左眉毛区域
                    //rectangle(frame,cvPoint(eyesOneX + facesX- eyesOneWidth*1/5, eyesOneY - eyesOneHeight * 2/ 5 + facesY),cvPoint(eyesOneX + facesX+eyesOneWidth,eyesOneY - eyesOneHeight * 2/ 5 + facesY+eyesOneHeight/2),Scalar( 255, 0,0 ),1,8, 0 );
                    //imshow("lefteyesbrow_Rect",frame);
                    // waitKey(10000);
                    // rectangle(frame,cvPoint(eyesOneX + facesX, eyesOneY - eyesOneHeight * 2/ 5 + facesY),cvPoint(eyesOneX + facesX+eyesOneWidth,eyesOneY - eyesOneHeight * 2/ 5 + facesY+eyesOneHeight/2),Scalar( 255, 0,0 ),1,8, 0 );
                    //imshow("lefteyesbrow_Rect1",frame);
                    //waitKey(10000);

                    //左眉毛坐标
                    LeftEYEBROW_X = eyesOneX + facesX- eyesOneWidth*1/5;
                    LeftEYEBROW_Y = eyesOneY - eyesOneHeight * 2 / 5 + facesY;
                    LeftEYEBROW_Width = eyesOneWidth;
                    LeftEYEBROW_Height = eyesOneHeight / 2;
                    //计算眉毛素材尺寸
                    leftLineX =eyesOneX- eyesOneWidth*1/5 + facesX;
                    leftLineY = eyesOneY - eyesOneHeight * 2 / 5 + facesY;
                    //EYEBROWsize=eyesZeroWidth*1.3;
                    rightLine=eyesZeroX+ eyesZeroWidth*1.3 +facesX;
                    eyeBROWhight=eyesOneHeight / 2;//眉毛素材宽度
                    eyeBROWwidth=rightLine - leftLineX;
                    EYEBROWwidth=eyeBROWwidth;    //眉毛素材长度也是=实际眉毛长度
                    startPoint=leftLineX;
                    resize(logoBGR, logonew, Size(EYEBROWwidth, eyeBROWhight), 0, 0, INTER_AREA);

                }
                else
                {
                    //画出右眉毛区域
                    // rectangle(frame,cvPoint(eyesOneX + facesX, eyesOneY - eyesOneHeight * 2/ 5 + facesY),cvPoint(eyesOneX + facesX+eyesOneWidth*1.3,eyesOneY - eyesOneHeight * 2/ 5 + facesY+eyesOneHeight/2),Scalar( 255, 0,0 ),1,8, 0 );
                    // imshow("Righteyesbrow_Rect",frame);
                    // waitKey(10000);
                    RightEYEBROW_X = eyesOneX + facesX;
                    RightEYEBROW_Y = eyesOneY - eyesOneHeight * 2 / 5 + facesY;
                    RightEYEBROW_Width = eyesOneWidth*1.3;
                    RightEYEBROW_Height = eyesOneHeight / 2;
                    //画出左眉毛区域
                    // rectangle(frame,cvPoint(eyesZeroX + facesX-eyesOneWidth*1/5, eyesZeroY - eyesZeroHeight * 2/ 5 + facesY),cvPoint(eyesZeroX + facesX+eyesZeroWidth,eyesZeroY - eyesZeroHeight * 2/ 5 + facesY+eyesZeroHeight/2),Scalar( 255, 0,0 ),1,8, 0 );
                    // imshow("lefteyesbrow_Rect",frame);
                    // waitKey(10000);
                    //左眉毛坐标
                    LeftEYEBROW_X = eyesZeroX + facesX-eyesOneWidth*1/5;
                    LeftEYEBROW_Y = eyesZeroY - eyesZeroHeight * 2 / 5 + facesY;
                    LeftEYEBROW_Width = eyesZeroWidth;
                    LeftEYEBROW_Height = eyesZeroHeight / 2;
                    //计算眉毛素材尺寸
                    leftLineX =eyesZeroX -eyesOneWidth*1/5 + facesX;
                    leftLineY = eyesZeroY - eyesZeroHeight * 2 / 5+ facesY;
                    rightLine=eyesOneX+ eyesOneWidth*1.3 + facesX;
                    eyeBROWhight=eyesOneHeight / 2;//眉毛素材宽度
                    eyeBROWwidth=rightLine - leftLineX;
                    EYEBROWwidth=eyeBROWwidth;    //眉毛素材长度也是=实际眉毛长度
                    startPoint=leftLineX;
                    resize(logoBGR, logonew, Size(EYEBROWwidth, eyeBROWhight), 0, 0, INTER_AREA);

                }
            }else {
                //如果找到的眼睛个数不是成对的
                //把相应的face的宽度和高度设为0，表示识别的是错误的人脸
                faces[i].height = 0;
                faces[i].width = 0;
                continue;
            }
        }
    }


}

extern "C" {
JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_nativeSendFrame2Eyeborw(JNIEnv *env, jobject instance,
                                                                jlong inputFrame,jint type) {

    if (currentType != type){
        imgEyebrowPath = "/data/data/com.neu.beautydemo/files/eyebrow"+Int_to_String(type)+".png";
        logo = imread(imgEyebrowPath);
        cvtColor(logo, logoBGR,CV_BGR2RGBA);
    }
    if (!xmlState){
        face_cascade.load(xmlFacePath);
        eyes_cascade.load(xmlEyePath);
        xmlState = true;
    }
    Mat &mRgb = *(Mat*)inputFrame;
    main1(mRgb);

}
}

