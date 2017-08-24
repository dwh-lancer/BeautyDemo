#include<opencv2/opencv.hpp>
#include <iostream>
#include <stdio.h>
#include <android/log.h>
#include <jni.h>

#define LOG_TAG "***pimple"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
using namespace std;
using namespace cv;

/** Function Headers */
void detect(Mat img);

void detectAndDisplay(Mat frame);

/** Global variables */
//-- Note, either copy these two files from opencv/data/haarscascades to your current folder, or change these locations
//string face_cascade_name = "haarcascade_frontalface_alt.xml";
//string eyes_cascade_name = "haarcascade_eye_tree_eyeglasses.xml";
//string nose_cascade_name = "haarcascade_mcs_nose.xml";
//string mouth_cascade_name = "haarcascade_mcs_mouth.xml";
string face_cascade_name;
string eyes_cascade_name;
string nose_cascade_name;
string mouth_cascade_name;
string imgGlassesPath;

CascadeClassifier face_cascade;
CascadeClassifier eyes_cascade;
CascadeClassifier nose_cascade;
CascadeClassifier mouth_cascade;

vector<Rect> eyes;
vector<Rect> nose;
vector<Rect> mouth;


//void main1() {
//void main1() {
//    Mat frame;
//
//    //-- 1. Load the cascades
//    if (!face_cascade.load(face_cascade_name)) {
//        LOGD("--(!)Error loading--face_cascade_name");
//        return;
//    };
//    if (!eyes_cascade.load(eyes_cascade_name)) {
//        LOGD("--(!)Error loading--eyes_cascade_name");
//        return;
//    };
//    if (!nose_cascade.load(nose_cascade_name)) {
//        LOGD("--(!)Error loading--nose_cascade_name");
//        return;
//    };
//    if (!mouth_cascade.load(mouth_cascade_name)) {
//        LOGD("--(!)Error loading--mouth_cascade_name");
//        return;
//    };
//
//
//    Mat sct;
//
//    double fscale = 0.9;
//
//    sct = imread(imgGlassesPath); //为单张图片程序
//    Size outSize;
//
//    outSize.width = sct.cols * fscale;
//    outSize.height = sct.rows * fscale;
//    resize(sct, frame, outSize, 0, 0, INTER_AREA);
//
//    //-- 3. Apply the classifier to the frame
//    if (!frame.empty()) {
//        detectAndDisplay(frame);
//    }
//    else {
//        LOGD("--(!) No captured frame -- Break!");
//        return;
//    }// break; }
//
//}
//
//void detect(Mat img) {
//
//    SimpleBlobDetector::Params params;
//    params.minRepeatability = 0;
//    params.minCircularity = 0.0f;
//    params.maxCircularity = std::numeric_limits<float>::max();
//    params.minDistBetweenBlobs = 0;
//    params.minThreshold = 30;
//    params.maxThreshold = 220;
//    params.thresholdStep = 5;
//    params.minArea = 800;
//    params.maxArea = 50000;
//    params.minConvexity = .00f;
//    params.maxConvexity = std::numeric_limits<float>::max();
//    params.minInertiaRatio = .00f;
//
//
//    Ptr<SimpleBlobDetector> detector = SimpleBlobDetector::create(params);
//
//    vector<KeyPoint> key_points;
//
//    detector->detect(img, key_points);
//    drawKeypoints(img, key_points, img, Scalar(0, 0, 255), DrawMatchesFlags::DRAW_RICH_KEYPOINTS);
////    imshow("SimpleBlobDetector", img);
////    waitKey(0);
//}
//
///**
//* @function detectAndDisplay
//*/
//void detectAndDisplay(Mat frame) {
//    LOGD("detectAndDisplay");
//    vector<Rect> faces;
//    Mat frame_gray;
//
//    cvtColor(frame, frame_gray, CV_BGR2GRAY);
//    equalizeHist(frame_gray, frame_gray);
//    //-- Detect faces
//    face_cascade.detectMultiScale(frame_gray, faces, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(60, 60));
//
//    for (int i = 0; i < faces.size(); i++) {
//
//
//        Point center(int(faces[i].x + faces[i].width * 0.5),
//                     int(faces[i].y + faces[i].height * 0.5));
//
//        Mat faceROI = frame_gray(faces[i]);
//
//        //-- In each face, detect eyes
//        eyes_cascade.detectMultiScale(faceROI, eyes, 1.1, 3, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));
//        nose_cascade.detectMultiScale(faceROI, nose, 1.1, 3, 0 | CV_HAAR_DO_CANNY_PRUNING,
//                                      Size(60, 60));
//        mouth_cascade.detectMultiScale(faceROI, mouth, 1.1, 3, 0 | CV_HAAR_DO_CANNY_PRUNING,
//                                       Size(80, 80));
//        int coordinate_eye_X[2];    //保存眼睛水平坐标
//        int coordinate_eye_y[2];      //保存眼睛纵坐标
//        for (int j = 0; j < eyes.size(); j++) {
//            Point eye_center(int(faces[i].x + eyes[j].x + eyes[j].width * 0.5),
//                             int(faces[i].y + eyes[j].y + eyes[j].height * 0.5));
//
//            if ((faces[i].y + faces[i].height * 0.5) >
//                (faces[i].y + eyes[j].y + eyes[j].height * 0.5)) {
//
//                coordinate_eye_X[j] = eye_center.x;   //眼睛水平坐标
//                coordinate_eye_y[j] = eye_center.y;
//            }
//        }
//        int avr_x = (coordinate_eye_X[0] + coordinate_eye_X[1]) / 2;
//        int avr_y = (coordinate_eye_y[0] + coordinate_eye_y[1]) / 2;
//
//
//        int coordinate_mouth_x;
//        int coordinate_mouth_y;
//        for (int jjj = 0; jjj < mouth.size(); jjj++) {
//            Point mouth_center(int(faces[i].x + mouth[jjj].x + mouth[jjj].width * 0.5),
//                               int(faces[i].y + mouth[jjj].y + mouth[jjj].height * 0.5));
//            coordinate_mouth_x = mouth_center.x;
//            coordinate_mouth_y = mouth_center.y;
//        }
//
//        int coordinate_nose_x;
//        int coordinate_nose_y;
//        for (int jj = 0; jj < nose.size(); jj++) {
//            Point nose_center(int(faces[i].x + nose[jj].x + nose[jj].width * 0.5),
//                              int(faces[i].y + nose[jj].y + nose[jj].height * 0.5));
//            coordinate_nose_x = nose_center.x;
//            coordinate_nose_y = nose_center.y;
//        }
//
//
//        int distance = avr_x - coordinate_eye_X[0];  //眼睛中心与鼻梁距离
//        int left_corner_x = coordinate_eye_X[0] - distance;//左眼外角坐标
//        int left_corner_y = (coordinate_eye_y[0] + coordinate_eye_y[1]) / 2;
//
//        int start_coox[4];
//        int start_cooy[4];
//        int col[4];
//        int row[4];
//        //left half face
//        start_coox[0] = left_corner_x + 10;
//        start_cooy[0] = left_corner_y + 20;
//        col[0] = coordinate_mouth_x - 15 - left_corner_x;
//        row[0] = coordinate_mouth_y - left_corner_y + 25;
//
//        Mat roi1 = frame(Rect(left_corner_x, start_cooy[0], col[0], row[0]));
//
//        detect(roi1);
//        imwrite("/storage/emulated/0/roil.jpg",roi1);
//
//        //right half face
//        start_coox[1] = coordinate_eye_X[1] - 30;
//        start_cooy[1] = coordinate_eye_y[1] + 20;
//        col[1] = coordinate_mouth_x - 15 - left_corner_x;
//        row[1] = coordinate_mouth_y - left_corner_y + 20;
//
//        Mat roi2 = frame(Rect(start_coox[1], start_cooy[1], col[1], row[1]));
//
//        detect(roi2);
//        imwrite("/storage/emulated/0/roi2.jpg",roi2);
//        //下巴
//        start_coox[2] = left_corner_x + 20;
//        start_cooy[2] = coordinate_mouth_y + 80;
//        col[2] = 2 * (coordinate_mouth_x - 5 - left_corner_x) - 10;
//        row[2] = coordinate_mouth_y - (left_corner_y - 20);
//
//        Mat roi3 = frame(Rect(start_coox[2], start_cooy[2], col[2], row[2]));
//        detect(roi3);
//        imwrite("/storage/emulated/0/roi3.jpg",roi3);
//
//        //额头
//        Mat roi4 = frame(Rect(left_corner_x, avr_y - (coordinate_nose_y - avr_y) - 30,
//                              2 * (coordinate_mouth_x - 5 - left_corner_x),
//                              coordinate_mouth_y - (left_corner_y - 20)));
//        detect(roi4);
//        imwrite("/storage/emulated/0/roi4.jpg",roi4);
//
//
//
//
//        //std::cout<<left_corner_x<<endl;
//        //std::cout<<left_corner_y<<endl;
//
//        //Mat glasses_pic = imread("4.jpg");
//        //Mat roi = frame(Rect(left_corner_x,(left_corner_y - 10), glasses_pic.cols,glasses_pic.rows ));
//        //0Mat mask = imread("4.jpg",0);
//        //0glasses_pic.copyTo(roi,mask);
//        //glasses_pic.copyTo(roi);
//    }
//
//}

void main1() {
    Mat frame;

    //-- 1. Load the cascades
    if (!face_cascade.load(face_cascade_name)) {
        LOGD("--(!)Error loading--face_cascade_name");
        return;
    };
    if (!eyes_cascade.load(eyes_cascade_name)) {
        LOGD("--(!)Error loading--eyes_cascade_name");
        return;
    };
    if (!nose_cascade.load(nose_cascade_name)) {
        LOGD("--(!)Error loading--nose_cascade_name");
        return;
    };
    if (!mouth_cascade.load(mouth_cascade_name)) {
        LOGD("--(!)Error loading--mouth_cascade_name");
        return;
    };

    Mat sct;

    double fscale = 0.9;

    sct = imread(imgGlassesPath);

//    resize(sct, sct, Size(300, 400), 0, 0, INTER_AREA);
    Size outSize;

    outSize.width = sct.cols * fscale;
    outSize.height = sct.rows * fscale;
    resize(sct, frame, outSize, 0, 0, INTER_AREA);

    //-- 3. Apply the classifier to the frame
    if (!frame.empty()) {
        detectAndDisplay(frame);
    }
    else {
        LOGD("--(!) No captured frame -- Break!");
        return;
    }// break; }


}

void detect(Mat img) {
    LOGD("detect");
    SimpleBlobDetector::Params params;
    params.minRepeatability = 0;
    params.minCircularity = 0.0f;
    params.maxCircularity = std::numeric_limits<float>::max();
    params.minDistBetweenBlobs = 0;
    params.minThreshold = 30;
    params.maxThreshold = 220;
    params.thresholdStep = 5;
    params.minArea = 50;
    params.maxArea = 500;
    params.minConvexity = .20f;
    params.maxConvexity = std::numeric_limits<float>::max();
    params.minInertiaRatio = .40f;


    Ptr<SimpleBlobDetector> detector = SimpleBlobDetector::create(params);

    vector<KeyPoint> key_points;

    detector->detect(img, key_points);
    drawKeypoints(img, key_points, img, Scalar(0, 0, 255), DrawMatchesFlags::DRAW_RICH_KEYPOINTS);
//    imshow("SimpleBlobDetector", img);
//    waitKey(0);

}

/**
* @function detectAndDisplay
*/
void detectAndDisplay(Mat frame) {
    LOGD("detectAndDisplay");
    vector<Rect> faces;
    Mat frame_gray;

    cvtColor(frame, frame_gray, CV_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);
    //-- Detect faces
    face_cascade.detectMultiScale(frame_gray, faces, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(60, 60));

    for (int i = 0; i < faces.size(); i++) {


        Point center(int(faces[i].x + faces[i].width * 0.5),
                     int(faces[i].y + faces[i].height * 0.5));

        Mat faceROI = frame_gray(faces[i]);

        //-- In each face, detect eyes
        eyes_cascade.detectMultiScale(faceROI, eyes, 1.1, 3, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));
        nose_cascade.detectMultiScale(faceROI, nose, 1.1, 3, 0 | CV_HAAR_DO_CANNY_PRUNING,
                                      Size(60, 60));
        mouth_cascade.detectMultiScale(faceROI, mouth, 1.1, 3, 0 | CV_HAAR_DO_CANNY_PRUNING,
                                       Size(80, 80));
        int coordinate_eye_X[2];       //保存眼睛水平坐标
        int coordinate_eye_y[2];      //保存眼睛纵坐标
        for (int j = 0; j < eyes.size(); j++) {
            Point eye_center(int(faces[i].x + eyes[j].x + eyes[j].width * 0.5),
                             int(faces[i].y + eyes[j].y + eyes[j].height * 0.5));

            if ((faces[i].y + faces[i].height * 0.5) >
                (faces[i].y + eyes[j].y + eyes[j].height * 0.5)) {

                coordinate_eye_X[j] = eye_center.x;   //眼睛水平坐标
                coordinate_eye_y[j] = eye_center.y;
            }
        }
        int avr_x = (coordinate_eye_X[0] + coordinate_eye_X[1]) / 2;
        int avr_y = (coordinate_eye_y[0] + coordinate_eye_y[1]) / 2;
        int distance, left_corner_x, left_corner_y;
        if (coordinate_eye_X[0] < coordinate_eye_X[1]) {
            distance = avr_x - coordinate_eye_X[0];  //眼睛中心与鼻梁距离
            left_corner_x = coordinate_eye_X[0] - distance;//左眼外角坐标
            left_corner_y = (coordinate_eye_y[0] + coordinate_eye_y[1]) / 2;
        }
        else {
            distance = avr_x - coordinate_eye_X[1];  //眼睛中心与鼻梁距离
            left_corner_x = coordinate_eye_X[1] - distance;//左眼外角坐标
            left_corner_y = (coordinate_eye_y[0] + coordinate_eye_y[1]) / 2;
        }
        /*
        int coordinate_mouth_x;
        int coordinate_mouth_y;
        for (int jjj = 0; jjj < mouth.size(); jjj++)
        {
            Point mouth_center(int(faces[i].x + mouth[jjj].x + mouth[jjj].width*0.5), int(faces[i].y + mouth[jjj].y + mouth[jjj].height*0.5));
            if ((faces[i].y + faces[i].height*0.5)< (faces[i].y + mouth[jjj].y + mouth[jjj].height*0.5))
            {
            coordinate_mouth_x = mouth_center.x;
            coordinate_mouth_y = mouth_center.y;
            }
        }
        */
        int coordinate_nose_x;
        int coordinate_nose_y;
        for (int jj = 0; jj < nose.size(); jj++) {
            Point nose_center(int(faces[i].x + nose[jj].x + nose[jj].width * 0.5),
                              int(faces[i].y + nose[jj].y + nose[jj].height * 0.5));
            if ((faces[i].y + faces[i].height * 0.5) <
                (faces[i].y + nose[jj].y + nose[jj].height * 0.8)) {
                coordinate_nose_x = nose_center.x;
                coordinate_nose_y = nose_center.y;
            }
        }

        int start_coox[4];
        int start_cooy[4];
        int col[4];
        int row[4];
        //left half face
        start_coox[0] = left_corner_x + 10;
        start_cooy[0] = left_corner_y + 20;
        col[0] = coordinate_nose_x - 15 - left_corner_x;
        row[0] = 2 * (coordinate_nose_y - left_corner_y);

        Mat roi1 = frame(Rect(left_corner_x, start_cooy[0], col[0], row[0]));

        detect(roi1);
        imwrite("/storage/emulated/0/roi1.jpg",roi1);


        //right half face
        if (coordinate_eye_X[0] < coordinate_eye_X[1]) {
            start_coox[1] = coordinate_eye_X[1] - 30;
            start_cooy[1] = coordinate_eye_y[1] + 20;
        }
        else {
            start_coox[1] = coordinate_eye_X[0] - 30;
            start_cooy[1] = coordinate_eye_y[0] + 20;
        }
        col[1] = coordinate_nose_x - 15 - left_corner_x;
        row[1] = 2 * (coordinate_nose_y - left_corner_y);

        Mat roi2 = frame(Rect(start_coox[1], start_cooy[1], col[1], row[1]));

        detect(roi2);
        imwrite("/storage/emulated/0/roi2.jpg",roi2);

        //下巴
        if (coordinate_eye_X[0] < coordinate_eye_X[1])
            start_coox[2] = coordinate_eye_X[0];
        else start_coox[2] = coordinate_eye_X[1];
        start_cooy[2] = avr_y + 2 * (coordinate_nose_y - avr_y) - 10;
        col[2] = abs(coordinate_eye_X[0] - coordinate_eye_X[1]);
        row[2] = coordinate_nose_y - avr_y;

        Mat roi3 = frame(Rect(start_coox[2], start_cooy[2], col[2], row[2]));
        detect(roi3);
        imwrite("/storage/emulated/0/roi3.jpg",roi3);
        //额头

        Mat roi4 = frame(Rect(left_corner_x, avr_y - (coordinate_nose_y - avr_y) - 30,
                              2 * abs(coordinate_eye_X[0] - coordinate_eye_X[1]),
                              coordinate_nose_y - avr_y));
        detect(roi4);
        imwrite("/storage/emulated/0/roi4.jpg",roi4);


    }

}



extern "C" {
/**
 * 粉刺测试图片地址
 */
JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_pimplePicPath(JNIEnv *env, jclass type,
                                                      jstring pimplePicPath_) {
    const char *pimplePicPath = env->GetStringUTFChars(pimplePicPath_, 0);

    // TODO
    imgGlassesPath = pimplePicPath;
    if (!imgGlassesPath.empty()) {
        LOGD("传入了图片地址jni");
        main1();
    }

    env->ReleaseStringUTFChars(pimplePicPath_, pimplePicPath);
}
/**
 * 模型文件地址
 */
JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_pimpleXmlPath(JNIEnv *env, jclass type, jstring xml_1_,
                                                      jstring xml_2_, jstring xml_3_,
                                                      jstring xml_4_) {
    const char *xml_1 = env->GetStringUTFChars(xml_1_, 0);
    const char *xml_2 = env->GetStringUTFChars(xml_2_, 0);
    const char *xml_3 = env->GetStringUTFChars(xml_3_, 0);
    const char *xml_4 = env->GetStringUTFChars(xml_4_, 0);

    // TODO
    LOGD("传入了模型地址jni");
// TODO
    face_cascade_name = xml_1;
    eyes_cascade_name = xml_2;
    nose_cascade_name = xml_3;
    mouth_cascade_name = xml_4;

    env->ReleaseStringUTFChars(xml_1_, xml_1);
    env->ReleaseStringUTFChars(xml_2_, xml_2);
    env->ReleaseStringUTFChars(xml_3_, xml_3);
    env->ReleaseStringUTFChars(xml_4_, xml_4);
}
}
