#include <jni.h>
#include <string>
#include <android/log.h>
#include <iostream>
#include <stdio.h>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

/** Function Headers */
void detectAndDisplay(Mat frame);
/** Global variables */
//-- Note, either copy these two files from opencv/data/haarscascades to your current folder, or change these locations
CascadeClassifier face_cascade;
CascadeClassifier eyes_cascade;
int leftLineX, leftLineY, rightLine, glasswidth, eyessize, startPoint;
std::vector<Rect> eyes;
Mat logo;
Mat logonew;
Mat logoBGR;

// 定义一个Mat类型，用于存放，图像的ROI
Mat imageROI;

bool xmlState = false;
int currentType = 0;
string imgGlassesPath ;
string xmlFacePath    = "/data/data/com.neu.beautydemo/app_neu/haarcascade_frontalface_alt.xml";
string xmlEyePath     = "/data/data/com.neu.beautydemo/app_neu/haarcascade_eye_tree_eyeglasses.xml";

string Int_to_String(int n)
{
    ostringstream stream;
    stream<<n;  //n为int类型
    return stream.str();
}

void main1(Mat frame) {

    detectAndDisplay(frame);
    if (eyes.size() == 2) {
        imageROI = frame(Rect(startPoint, leftLineY, logonew.cols, logonew.rows));
        addWeighted(imageROI, 1, logonew, 1, 0., imageROI);
    }
    else {
//            imageROI = frame(Rect(startPoint, leftLineY, logoBGR.cols, logoBGR.rows));
//            addWeighted(imageROI, 1, logoBGR, 1, 0., imageROI);
        __android_log_print(ANDROID_LOG_DEBUG, "***", "eyes.size()%d", eyes.size());
    }
}

void detectAndDisplay(Mat frame) {

    int eyewidth, eyehight, rightLineX;

    eyes_cascade.detectMultiScale(frame, eyes, 1.1, 3, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));

    if (eyes.size() == 2) {
        if (eyes[0].x < eyes[1].x) {
            leftLineX = eyes[0].x;
            leftLineY = eyes[0].y;
            eyessize = eyes[1].width;
            rightLine = eyes[1].x + eyes[1].width;
            eyehight = eyes[0].height;
            eyewidth = rightLine - leftLineX;
            glasswidth = ((eyessize * 2 / 3) + eyewidth);
            startPoint = leftLineX - eyessize / 3;
            resize(logoBGR, logonew, Size(glasswidth, eyehight), 0, 0, INTER_AREA);

        }
        else {
            leftLineX = eyes[1].x;
            leftLineY = eyes[1].y;
            eyessize = eyes[0].width;
            rightLine = eyes[0].x + eyes[0].width;
            eyehight = eyes[1].height;
            eyewidth = rightLine - leftLineX;
            glasswidth = ((eyessize * 2 / 3) + eyewidth);
            startPoint = leftLineX - eyessize / 3;
            resize(logoBGR, logonew, Size(glasswidth, eyehight), 0, 0, INTER_AREA);
        }
    }

}

extern "C" {
JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_nativeSendFrame2Glasses2(JNIEnv *env, jobject instance,
                                                                 jlong inputFrame,
                                                                 jlong outputFrame, jint type) {

    if (currentType != type){
        imgGlassesPath = "/data/data/com.neu.beautydemo/files/glasses"+Int_to_String(type)+".png";
        logo = imread(imgGlassesPath,-1);
        resize(logo, logo, Size(220, 50), 0, 0, INTER_AREA);
        cvtColor(logo, logoBGR, CV_BGR2RGBA);
    }
    if (!xmlState){
        eyes_cascade.load(xmlEyePath);
        xmlState = true;
    }
    // TODO
    Mat &mRgb1 = *(Mat *) inputFrame;
    Mat &mRgb2 = *(Mat *) outputFrame;
    detectAndDisplay(mRgb1);
    if (eyes.size() == 2) {
        imageROI = mRgb1(Rect(startPoint, leftLineY, logonew.cols, logonew.rows));
        addWeighted(imageROI, 1, logonew, 0, 0., imageROI);
    }

}
JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_nativeSendFrame2Glasses(JNIEnv *env, jobject instance,
                                                                jlong inputFrame, jint type) {

    if (currentType != type){
        imgGlassesPath = "/data/data/com.neu.beautydemo/files/glasses"+Int_to_String(type)+".png";
        logo = imread(imgGlassesPath);
        resize(logo, logo, Size(220, 50), 0, 0, INTER_AREA);
        cvtColor(logo, logoBGR, CV_BGR2RGBA);
    }
    if (!xmlState){
        eyes_cascade.load(xmlEyePath);
        xmlState = true;
    }
    // TODO
    Mat &mRgb = *(Mat *) inputFrame;
    main1(mRgb);
//    detectAndDisplay(mRgb);
}
}



