
#include <opencv2/opencv.hpp>
#include <jni.h>
#include <android/log.h>


#define LOG_TAG "***FaceRec"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
jobject obj;
jclass jcls;
jmethodID faceCallBack;
using namespace cv;
using namespace std;

bool xmlState = false;
bool callbackState = false;

CascadeClassifier face_cascade;

string face_cascade_name = "/data/data/com.neu.beautydemo/app_neu/haarcascade_frontalface_alt.xml";

void main1(JNIEnv* env,Mat frame)

{
    if (!frame.data==NULL)
    {
        vector<Rect> faces;
        Mat  frameGray;
        cvtColor(frame,frameGray,CV_RGB2GRAY);
        equalizeHist(frameGray,frameGray);
        face_cascade.detectMultiScale(frameGray, faces, 1.1, 2, 0);//检测人脸
        if (faces.size() == 0) {
             LOGD("can not find faces!!!");
            return;
        } else {
             LOGD("find face");
            for (size_t i = 0; i < faces.size(); i++) {
                //在原图像中找到人脸区域，灰度化、直方图均衡化
                Mat faceROI = frame(faces[i]);
                rectangle(frame,cvPoint(faces[i].x,faces[i].y),cvPoint(faces[i].x+faces[i].width,faces[i].y+faces[i].height*1.1),Scalar( 255, 0,0 ),1,8, 0 );
                //画出人脸框，faces[i].x,faces[i].y为人脸左上角坐标，faces[i].width，faces[i].height为人脸的宽和高
                int x1 = (frame.cols / 3  -30);
                int x2 = (frame.cols / 3  +30);
                int y1 = (frame.rows / 6  -30);
                int y2 = (frame.rows / 6  +30);
                __android_log_print(ANDROID_LOG_DEBUG,"人脸像素","x1:%d-x2:%d*y1:%d-y2:%d",x1,x2,y1,y2);
                if (faces[i].x > x1 && faces[i].x < x2 ){
                    if (faces[i].y > y1 && faces[i].y < y2 ){
                        env->CallVoidMethod(obj,faceCallBack,2);
                    }
                }
            }

        }
    }

	}


extern "C" {

JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_nativeSendFrame2FaceRec(JNIEnv *env, jobject instance,
                                                     jlong inputFrame) {

    if (!xmlState){
        face_cascade.load(face_cascade_name);
        xmlState = true;
    }
    if (!callbackState){
        obj  = instance;
        jcls = env->GetObjectClass(obj);
        faceCallBack = env->GetMethodID(jcls,"faceCallBack","(I)V");
//    env->CallVoidMethod(obj,faceCallBack,2);
        callbackState = true;
    }
    LOGD("人脸检测是否中央");
    Mat &frame = *(Mat*)inputFrame;
    main1(env,frame);

}
}
