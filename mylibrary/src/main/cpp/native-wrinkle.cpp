#include<iostream>
#include <string>
#include<opencv2/opencv.hpp>
#include <jni.h>
#include <android/log.h>

#define LOG_TAG "***Wrinkle"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))


#define FORE_HEAD_FLAG 1 //额头
#define FORE_HEAD_SIZE_X 50 //宽
#define FORE_HEAD_SIZE_Y 50 //高
#define FORE_HEAD_BLOCK_SIZE 10 //分块的块的大小
#define FORE_HEAD_BLOCK_COUNT ((FORE_HEAD_SIZE_X*FORE_HEAD_SIZE_Y) / (FORE_HEAD_BLOCK_SIZE*FORE_HEAD_BLOCK_SIZE))//注意此处要跟着上边三个一起变
//#define FORE_HEAD_MODEL_FILE_PATH "/storage/emulated/0/Pictures/wrinkleModel/ForeHeadPOLY.xml" //抬头纹模型文件路径


#define EYES_FLAG 2 //眼角-鱼尾纹
#define EYES_SIZE_X 30 //宽
#define EYES_SIZE_Y 30 //高
#define EYES_BLOCK_SIZE 10 //分块的块的大小
#define EYES_BLOCK_COUNT ((EYES_SIZE_X*EYES_SIZE_Y) / (EYES_BLOCK_SIZE*EYES_BLOCK_SIZE))//注意此处要跟着上边三个一起变
//#define EYES_MODEL_FILE_PATH "/storage/emulated/0/Pictures/wrinkleModel/FishTail.xml" //鱼尾纹模型文件路径

#define POUCH_FLAG 3 //眼袋纹
#define POUCH_SIZE_X 30 //宽
#define POUCH_SIZE_Y 30 //高
#define POUCH_BLOCK_SIZE 10 //分块的块的大小
#define POUCH_BLOCK_COUNT ((POUCH_SIZE_X*POUCH_SIZE_Y) / (POUCH_BLOCK_SIZE*POUCH_BLOCK_SIZE))//注意此处要跟着上边三个一起变
//#define POUCH_MODEL_FILE_PATH "/storage/emulated/0/Pictures/wrinkleModel/PouchRBF.xml" //眼袋纹模型文件路径

#define EXPRESSION_FLAG 4 //表情纹
#define EXPRESSION_SIZE_X 40
#define EXPRESSION_SIZE_Y 40
#define EXPRESSION_BLOCK_SIZE 10
#define EXPRESSION_BLOCK_COUNT ((EXPRESSION_SIZE_X*EXPRESSION_SIZE_Y)/(EXPRESSION_BLOCK_SIZE*EXPRESSION_BLOCK_SIZE))
//#define EXPRESSION_MODEL_FILE_PATH "/storage/emulated/0/Pictures/wrinkleModel/ExpressionRBF.xml"//表情纹


using namespace std;
using namespace cv::ml;
using namespace cv;

int imgCount = 1;

string imgRootPath;
string foreHeadModelFilePath;
string eyesModelFilePath;
string pouchModelFilePath;
string expressionModelFilePath;
string face_cascade_name ;
//= "/storage/emulated/0/headwrinkle/haarcascade_frontalface_alt.xml";
//人脸分类器
string eyes_cascade_name ;
//= "/storage/emulated/0/headwrinkle/haarcascade_eye_tree_eyeglasses.xml";//人眼分类器

CascadeClassifier faces_cascade;   //创建分类器对象
CascadeClassifier eyes_cascade;

int ForeHead_Label_Count = 0;
int ForeHead_Labels[2000];
float ForeHead_TrainData[2000][FORE_HEAD_BLOCK_COUNT * 256];
float ForeHead_TestData[1][FORE_HEAD_BLOCK_COUNT * 256];

float foreHeadTestResult = 0;                    //抬头纹
float eyeLeftCornerTestResult = 0;               //左眼角纹
float eyeRightCornerTestResult = 0;              //右眼角纹
float pouchLeftTestResult = 0;                   //左眼袋纹
float pouchRightTestResult = 0;                  //右眼袋纹
float FaceROILeftTestResult = 0;
float FaceROIRightTestResult = 0;

string result_1 = "";                    //抬头纹
string result_2 = "";               //左眼角纹
string result_3 = "";              //右眼角纹
string result_4 = "";                   //左眼袋纹
string result_5 = "";                  //右眼袋纹
string result_6 = "";
string result_7 = "";

int errorResult = 0;

//鱼尾纹-眼角区域
int Eyes_Label_Count = 0;
int Eyes_Labels[2000];
//用于存储训练样本的标签
float Eyes_TrainData[2000][EYES_BLOCK_COUNT * 256];
float Eyes_TestData[1][EYES_BLOCK_COUNT * 256];

//眼袋纹
int Pouch_Label_Count = 0;
int Pouch_Labels[2000];
float Pouch_TrainData[2000][POUCH_BLOCK_COUNT * 256];
float Pouch_TestData[1][POUCH_BLOCK_COUNT * 256];


//表情纹
int Expression_Label_Count = 0;
int Expression_Labels[2000];
float Expression_TrainData[2000][EXPRESSION_BLOCK_COUNT * 256];
float Expression_TestData[1][EXPRESSION_BLOCK_COUNT * 256];

//分别是左眼、右眼、左眼角、右眼角、左眼袋、右眼袋相应区域
Mat leftEYE, rightEYE, leftEyeCorner, rightEyeCorner, leftPouchCorner, rightPouchCorner, leftFaceROI, rightFaceROI, foreHead;

//各个区域的坐标以及长和宽
string x_1;
string x_2;
string x_3;
string x_4;
string x_5;
string x_6;
string x_7;

string y_1;
string y_2;
string y_3;
string y_4;
string y_5;
string y_6;
string y_7;
//额头区域坐标
int ForeHead_X = 0;
int ForeHead_Y = 0;
int ForeHead_Width = 0;
int ForeHead_Height = 0;

//左眼角区域
int LeftEyeCorner_X = 0;
int LeftEyeCorner_Y = 0;
int LeftEyeCorner_Width = 0;
int LeftEyeCorner_Height = 0;

//右眼角区域
int RightEyeCorner_X = 0;
int RightEyeCorner_Y = 0;
int RightEyeCorner_Width = 0;
int RightEyeCorner_Height = 0;

//左眼袋区域
int LeftPouchCorner_X = 0;
int LeftPouchCorner_Y = 0;
int LeftPouchCorner_Width = 0;
int LeftPouchCorner_Height = 0;

//右眼袋区域
int RightPouchCorner_X = 0;
int RightPouchCorner_Y = 0;
int RightPouchCorner_Width = 0;
int RightPouchCorner_Height = 0;

//左脸区域坐标
int LeftFaceROI_X = 0;
int LeftFaceROI_Y = 0;
int LeftFaceROI_Width = 0;
int LeftFaceROI_Height = 0;

//右脸区域坐标
int RightFaceROI_X = 0;
int RightFaceROI_Y = 0;
int RightFaceROI_Width = 0;
int RightFaceROI_Height = 0;

string Int_to_String(int n)
{
    ostringstream stream;
    stream<<n;  //n为int类型
    return stream.str();
}

Mat OLBP(Mat srcImage) {
    //计算原始LBP特征
    const int nRows = srcImage.rows;
    const int nCols = srcImage.cols;
    Mat resultMat(srcImage.size(), srcImage.type());
    for (int y = 1; y < nRows - 1; y++) {
        for (int x = 1; x < nCols - 1; x++) {
            //定义邻域
            uchar neighbor[8] = {0};
            neighbor[0] = srcImage.at<uchar>(y - 1, x - 1);
            neighbor[1] = srcImage.at<uchar>(y - 1, x);
            neighbor[2] = srcImage.at<uchar>(y - 1, x + 1);
            neighbor[3] = srcImage.at<uchar>(y, x + 1);
            neighbor[4] = srcImage.at<uchar>(y + 1, x + 1);
            neighbor[5] = srcImage.at<uchar>(y + 1, x);
            neighbor[6] = srcImage.at<uchar>(y + 1, x - 1);
            neighbor[7] = srcImage.at<uchar>(y, x - 1);
            //当前图像的处理中心
            uchar center = srcImage.at<uchar>(y, x);
            uchar temp = 0;
            //计算LBP的值
            for (int k = 0; k < 8; k++) {
                //遍历中心邻域
                temp += (neighbor[k] >= center) * (1 << k);
            }
            resultMat.at<uchar>(y, x) = temp;
        }
    }
    return resultMat;
}

void CalcHistArray(Mat srcImage1, float *buff) {
    //定义变量
    MatND dstHist;
    int dims = 1;
    float hranges[] = {0, 255};
    const float *ranges[] = {hranges};
    int size = 256;
    int channels = 0;
    //计算图像的直方图
    //原图像，图像个数，通道数，Mat(),“MatND dstHist;”, 1, 256,
    calcHist(&srcImage1, 1, &channels, Mat(), dstHist, dims, &size, ranges);
    //归一化
    normalize(dstHist, dstHist, 1.0);

    //获取最大值和最小值
    double minValue = 0;
    double maxValue = 0;
    minMaxLoc(dstHist, &minValue, &maxValue, 0, 0);

    for (int i = 0; i < 256; i++) {
        buff[i] = dstHist.at<float>(i);
    }
}


//srcImage 原始图像（灰度化后的）
//blockSize 分块大小
//是否是训练数据
void getBlocksHist(Mat srcImage, int blockSize, int flag, bool isTraining) {
    int sizeX = srcImage.cols, sizeY = srcImage.rows;
    int w = 0;//用来标记是第几个块
    //图片划分为blockSize*blockSize的区域
    for (int x = 0; x < sizeX; x += blockSize) {
        for (int y = 0; y < sizeY; y += blockSize) {
            Mat srcImage_block = srcImage(Range(y, y + blockSize), Range(x, x + blockSize));//从原始图像中选取一块
            Mat resultMat = OLBP(srcImage_block); //对选取的块提取LBP特征
            if (flag == FORE_HEAD_FLAG) {
                if (isTraining) {
                    CalcHistArray(resultMat.clone(),
                                  ForeHead_TrainData[ForeHead_Label_Count] + w * 256);//二位数组第一个方括号是第几行，后边加的值是在该行的偏移量
                }
                else {
                    CalcHistArray(resultMat.clone(), ForeHead_TestData[0] + w * 256);
                }
            }
            if (flag == EYES_FLAG) {
                if (isTraining) {
                    CalcHistArray(resultMat.clone(),
                                  Eyes_TrainData[Eyes_Label_Count] + w * 256);//二位数组第一个方括号是第几行，后边加的值是在该行的偏移量
                }
                else {
                    CalcHistArray(resultMat.clone(), Eyes_TestData[0] + w * 256);
                }
            }
            if (flag == POUCH_FLAG) {
                if (isTraining) {
                    CalcHistArray(resultMat.clone(),
                                  Pouch_TrainData[Pouch_Label_Count] + w * 256);//二位数组第一个方括号是第几行，后边加的值是在该行的偏移量
                }
                else {
                    CalcHistArray(resultMat.clone(), Pouch_TestData[0] + w * 256);
                }
            }
            if (flag == EXPRESSION_FLAG) {
                if (isTraining) {
                    CalcHistArray(resultMat.clone(), Expression_TrainData[Expression_Label_Count] + w * 256);
                }
                else {
                    CalcHistArray(resultMat.clone(), Expression_TestData[0] + w * 256);
                }
            }
            w++;
        }
    }
}

Mat getFormatImage(Mat srcImage, int sizeX, int sizeY)//格式化图片
{
    //cvtColor(srcImage, srcImage, CV_BGR2GRAY);//转换成灰度图像
    Mat dstImage = Mat::zeros(sizeY, sizeX, CV_8UC1);//一个灰度、指定尺寸的容器，注意此处顺序是先行数（Y），后列数（X）
    resize(srcImage, dstImage, dstImage.size());//尺寸统一
    return dstImage;
}

//找到眼睛的上边界
int findToppest(vector<Rect> eyes) {
    int min = eyes[0].y;
    for (int i = 1; i < eyes.size(); i++) {
        if (eyes[i].y < min) {
            min = eyes[i].y;
        }
    }
    return min;
}

//找到眼睛的左边界
int findEyeLeftMost(vector<Rect> eyes) {
    int leftMost = eyes[0].x;

    for (int i = 0; i < eyes.size(); i++) {
        if (leftMost > eyes[i].x) {
            leftMost = eyes[i].x;
        }
    }
    return leftMost;
}

//找到眼睛的右边界
int findEyeRightMost(vector<Rect> eyes) {
    int rightMost = eyes[0].x + eyes[0].width;
    for (int i = 0; i < eyes.size(); i++) {
        if (rightMost < eyes[i].x + eyes[i].width) {
            rightMost = eyes[i].x + eyes[i].width;
        }
    }
    return rightMost;
}

//下边界
int findEyesLowest(vector<Rect> eyes) {
    int lowMost = eyes[0].x + eyes[0].height;
    for (int i = 0; i < eyes.size(); i++) {
        if (lowMost < eyes[i].y + eyes[i].height) {
            lowMost = eyes[i].y + eyes[i].height;
        }
    }
    return lowMost;
}

//检测人脸和人眼区域
void detectFaceAndEyes(Mat srcImage, vector<Rect> &faces, vector<int> &eyeToppest) {
    Mat grayImage;

    //cvtColor(srcImage, grayImage, CV_BGR2GRAY); //转换成灰度图，因为harr特征从灰度图中提取
    //equalizeHist(grayImage, grayImage);  //直方图均衡行
    faces_cascade.detectMultiScale(srcImage, faces, 1.1, 2, 0);//检测人脸
    __android_log_print(ANDROID_LOG_ERROR, "***wqz", "人脸个数%d", faces.size());

    if (faces.size() == 0 ||faces.size() >1) {
        LOGD("can not find faces!!!重拍");
        errorResult = 1;
        return;
    } else {
        LOGD("find face");

        eyeToppest.clear();
        for (size_t i = 0; i < faces.size(); i++) {
            //在原图像中找到人脸区域，灰度化、直方图均衡化
            Mat faceROI = srcImage(faces[i]);
            // Mat faceROI_Gray;
            //cvtColor(faceROI, faceROI_Gray, CV_BGR2GRAY);
            //equalizeHist(faceROI_Gray, faceROI_Gray);

            vector<Rect> eyes;
            //-- In each face, detect eyes
            eyes_cascade.detectMultiScale(faceROI, eyes, 1.1, 2, 0);
            __android_log_print(ANDROID_LOG_ERROR, "***wqz", "眼睛个数%d", eyes.size());

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

                if (eyes[0].x >= eyes[1].x)//1是左眼，0是右眼
                {
                    leftEYE = srcImage(Rect(eyesOneX + facesX, eyesOneY + facesY, eyesOneWidth, eyesOneHeight));

                    leftEyeCorner = srcImage(
                            Rect(facesWidth / 15 + facesX, eyesOneY + facesY, eyesOneX * 4 / 5 - facesWidth / 15,
                                 eyesOneHeight));//在原图像上截取眼角区域
                    //左眼角区域坐标
                    LeftEyeCorner_X = facesWidth / 15 + facesX;
                    LeftEyeCorner_Y = eyesOneY + facesY;
                    LeftEyeCorner_Width = eyesOneX * 4 / 5 - facesWidth / 15;
                    LeftEyeCorner_Height = eyesOneHeight;
                    x_2 = Int_to_String(LeftEyeCorner_X);
                    y_2 = Int_to_String(LeftEyeCorner_Y);
//                    string img = "/storage/emulated/0/headwrinkle/leftEyeCorner" + Int_to_String(imgCount) + ".jpg";
//                    if (imwrite(img, leftEyeCorner)) {
//                        __android_log_print(ANDROID_LOG_DEBUG, "wqz", "第%d个leftEyeCorner保存成功", imgCount);
//                    }
//                    __android_log_print(ANDROID_LOG_DEBUG, "wqz", "左眼角区域坐标x%dy%dWidth%dHegiht%d",
//                                        LeftEyeCorner_X, LeftEyeCorner_Y, LeftEyeCorner_Width, LeftEyeCorner_Height);

                    //左眼袋区域
                    leftPouchCorner = srcImage(
                            Rect(eyesOneX + facesX, eyesOneY + eyesOneHeight * 4 / 5 + facesY, eyesOneWidth,
                                 eyesOneHeight / 2));//原图像上的左眼袋
                    LeftPouchCorner_X = eyesOneX + facesX;
                    LeftPouchCorner_Y = eyesOneY + eyesOneHeight * 4 / 5 + facesY;
                    LeftPouchCorner_Width = eyesOneWidth;
                    LeftPouchCorner_Height = eyesOneHeight / 2;
                    x_4 = Int_to_String(LeftPouchCorner_X);
                    y_4 = Int_to_String(LeftPouchCorner_Y);
                    __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "左眼袋区域坐标x%dy%dWidth%dHegiht%d",
                                        LeftPouchCorner_X, LeftPouchCorner_Y, LeftPouchCorner_Width,
                                        LeftPouchCorner_Height);

                    rightEYE = srcImage(Rect(eyesZeroX + facesX, eyesZeroY + facesY, eyesZeroWidth, eyesZeroHeight));

                    rightEyeCorner = srcImage(Rect(eyesZeroX + eyesZeroWidth * 6 / 5 + facesX, eyesZeroY + facesY,
                                                   facesWidth * 9 / 10 - (eyesZeroX + eyesZeroWidth), eyesZeroHeight));
                    //右眼角区域坐标
                    RightEyeCorner_X = eyesZeroX + eyesZeroWidth * 6 / 5 + facesX;
                    RightEyeCorner_Y = eyesZeroY + facesY;
                    RightEyeCorner_Width = facesWidth * 9 / 10 - (eyesZeroX + eyesZeroWidth);
                    RightEyeCorner_Height = eyesZeroHeight;
                    x_3 = Int_to_String(RightEyeCorner_X);
                    y_3 = Int_to_String(RightEyeCorner_Y);

                    //右眼袋区域坐标
                    rightPouchCorner = srcImage(
                            Rect(eyesZeroX + facesX, eyesZeroY + eyesZeroHeight * 4 / 5 + facesY, eyesZeroWidth,
                                 eyesZeroHeight / 2));
                    RightPouchCorner_X = eyesZeroX + facesX;
                    RightPouchCorner_Y = eyesZeroY + eyesZeroHeight * 4 / 5 + facesY;
                    RightPouchCorner_Width = eyesZeroWidth;
                    RightPouchCorner_Height = eyesZeroHeight / 2;
                    x_5 = Int_to_String(RightPouchCorner_X);
                    y_5 = Int_to_String(RightPouchCorner_Y);
                    leftFaceROI = srcImage(
                            Rect(eyesOneX + facesX, eyesOneY + eyesOneHeight * 4 / 5 + facesY + eyesOneHeight / 2,
                                 eyesOneWidth * 4 / 5, facesWidth / 5));
                    cout << "左半脸坐标X：" << eyesOneX + facesX << "Y:" <<
                    eyesOneY + eyesOneHeight * 4 / 5 + facesY + eyesOneHeight / 2 << "宽：" << eyesOneWidth * 4 / 5 <<
                    "高：" << facesWidth / 5 << endl;
                    //rectangle(srcImage, Rect(eyesOneX + facesX, eyesOneY + eyesOneHeight * 4 / 5 + facesY + eyesOneHeight / 2, eyesOneWidth * 4 / 5, facesWidth / 5), Scalar(0, 0, 255), 1, 4);
                    LeftFaceROI_X = eyesOneX + facesX;
                    LeftFaceROI_Y = eyesOneY + eyesOneHeight * 4 / 5 + facesY + eyesOneHeight / 2;
                    LeftFaceROI_Width = eyesOneWidth * 4 / 5;
                    LeftFaceROI_Height = facesWidth / 5;
                    x_6 = Int_to_String(LeftFaceROI_X);
                    y_6 = Int_to_String(LeftFaceROI_Y);

                    rightFaceROI = srcImage(Rect(eyesZeroX + facesX + eyesZeroWidth / 4,
                                                 eyesZeroY + eyesZeroHeight * 4 / 5 + facesY + eyesZeroHeight / 2,
                                                 eyesZeroWidth * 3 / 4, facesWidth / 5));
                    //rectangle(srcImage, Rect(eyesZeroX + facesX + eyesZeroWidth / 4, eyesZeroY + eyesZeroHeight * 4 / 5 + facesY + eyesZeroHeight / 2, eyesZeroWidth * 3 / 4, facesWidth / 5), Scalar(0, 0, 255), 1, 4);
                    RightFaceROI_X = eyesZeroX + facesX + eyesZeroWidth / 4;
                    RightFaceROI_Y = eyesZeroY + eyesZeroHeight * 4 / 5 + facesY + eyesZeroHeight / 2;
                    RightFaceROI_Width = eyesZeroWidth * 3 / 4;
                    RightFaceROI_Height = facesWidth / 5;
                    x_7 = Int_to_String(RightFaceROI_X);
                    y_7 = Int_to_String(RightFaceROI_Y);

                }
                else//1是右眼，0是左眼
                {
                    leftEYE = srcImage(Rect(eyesZeroX + facesX, eyesZeroY + facesY, eyesZeroWidth, eyesZeroHeight));
                    //左眼角
                    leftEyeCorner = srcImage(
                            Rect(facesWidth / 15 + facesX, eyesZeroY + facesY, eyesZeroX * 4 / 5 - facesWidth / 15,
                                 eyesZeroHeight));
                    LeftEyeCorner_X = facesWidth / 15 + facesX;
                    LeftEyeCorner_Y = eyesZeroY + facesY;
                    LeftEyeCorner_Width = eyesZeroX * 4 / 5 - facesWidth / 15;
                    LeftEyeCorner_Height = eyesZeroHeight;
                    x_2 = Int_to_String(LeftEyeCorner_X);
                    y_2 = Int_to_String(LeftEyeCorner_Y);


//                    string img = "/storage/emulated/0/headwrinkle/leftEyeCorner" + Int_to_String(imgCount) + ".jpg";
//                    if (imwrite(img, leftEyeCorner)) {
//                        __android_log_print(ANDROID_LOG_DEBUG, "wqz", "第%d个leftEyeCorner保存成功", imgCount);
//                    }
//                    __android_log_print(ANDROID_LOG_DEBUG, "wqz", "左眼角区域坐标x%dy%dWidth%dHegiht%d",
//                                        LeftEyeCorner_X, LeftEyeCorner_Y, LeftEyeCorner_Width, LeftEyeCorner_Height);


                    leftPouchCorner = srcImage(
                            Rect(eyesZeroX + facesX, eyesZeroY + eyesZeroHeight * 4 / 5 + facesY, eyesZeroWidth,
                                 eyesZeroHeight / 2));
                    LeftPouchCorner_X = eyesZeroX + facesX;
                    LeftPouchCorner_Y = eyesZeroY + eyesZeroHeight * 4 / 5 + facesY;
                    LeftPouchCorner_Width = eyesZeroWidth;
                    LeftPouchCorner_Height = eyesZeroHeight / 2;
                    x_4 = Int_to_String(LeftPouchCorner_X);
                    y_4 = Int_to_String(LeftPouchCorner_Y);

                    rightEYE = srcImage(Rect(eyesOneX + facesX, eyesOneY + facesY, eyesOneWidth, eyesOneHeight));//右眼

                    rightEyeCorner = srcImage(Rect(eyesOneX + eyesOneWidth * 6 / 5 + facesX, eyesOneY + facesY,
                                                   facesWidth * 9 / 10 - (eyesOneX + eyesOneWidth),
                                                   eyesOneHeight));//右眼角
                    RightEyeCorner_X = eyesOneX + eyesOneWidth * 6 / 5 + facesX;
                    RightEyeCorner_Y = eyesOneY + facesY;
                    RightEyeCorner_Width = facesWidth * 9 / 10 - (eyesOneX + eyesOneWidth);
                    RightEyeCorner_Height = eyesOneHeight;
                    x_3 = Int_to_String(RightEyeCorner_X);
                    y_3 = Int_to_String(RightEyeCorner_Y);

                    rightPouchCorner = srcImage(
                            Rect(eyesOneX + facesX, eyesOneY + eyesOneHeight * 4 / 5 + facesY, eyesOneWidth,
                                 eyesOneHeight / 2));//右眼袋
                    RightPouchCorner_X = eyesOneX + facesX;
                    RightPouchCorner_Y = eyesOneY + eyesOneHeight * 4 / 5 + facesY;
                    RightPouchCorner_Width = eyesOneWidth;
                    RightPouchCorner_Height = eyesOneHeight / 2;
                    x_5 = Int_to_String(RightPouchCorner_X);
                    y_5 = Int_to_String(RightPouchCorner_Y);

                    leftFaceROI = srcImage(
                            Rect(eyesZeroX + facesX, eyesZeroY + eyesZeroHeight * 4 / 5 + facesY + eyesZeroHeight / 2,
                                 eyesZeroWidth * 4 / 5, facesWidth / 5));
                    cout << "左半脸坐标X：" << eyesZeroX + facesX << "Y:" <<
                    eyesZeroY + eyesZeroHeight * 4 / 5 + facesY + eyesZeroHeight / 2 << "宽：" << eyesZeroWidth * 4 / 5 <<
                    "高：" << facesWidth / 5 << endl;
                    //rectangle(srcImage, Rect(eyesZeroX + facesX, eyesZeroY + eyesZeroHeight * 4 / 5 + facesY + eyesZeroHeight / 2, eyesZeroWidth * 4 / 5, facesWidth / 5), Scalar(0, 0, 255), 1, 4);
                    LeftFaceROI_X = eyesZeroX + facesX;
                    LeftFaceROI_Y = eyesZeroY + eyesZeroHeight * 4 / 5 + facesY + eyesZeroHeight / 2;
                    LeftFaceROI_Width = eyesZeroWidth * 4 / 5;
                    LeftFaceROI_Height = facesWidth / 5;
                    x_6 = Int_to_String(LeftFaceROI_X);
                    y_6 = Int_to_String(LeftFaceROI_Y);

                    //右脸
                    rightFaceROI = srcImage(Rect(eyesOneX + facesX + eyesOneWidth / 4,
                                                 eyesOneY + eyesOneHeight * 4 / 5 + facesY + eyesOneHeight / 2,
                                                 eyesOneWidth * 3 / 4, facesWidth / 5));
                    //rectangle(srcImage, Rect(eyesOneX + facesX + eyesOneWidth / 4, eyesOneY + eyesOneHeight * 4 / 5 + facesY + eyesOneHeight / 2, eyesOneWidth * 3 / 4, facesWidth / 5), Scalar(0, 0, 255), 1, 4);
                    RightFaceROI_X = eyesOneX + facesX + eyesOneWidth / 4;
                    RightFaceROI_Y = eyesOneY + eyesOneHeight * 4 / 5 + facesY + eyesOneHeight / 2;
                    RightFaceROI_Width = eyesOneWidth * 3 / 4;
                    RightFaceROI_Height = facesWidth / 5;
                    x_7 = Int_to_String(RightFaceROI_X);
                    y_7 = Int_to_String(RightFaceROI_Y);
                }

            }
            else {
                //如果找到的眼睛个数不是成对的
                //把相应的face的宽度和高度设为0，表示识别的是错误的人脸
                faces[i].height = 0;
                faces[i].width = 0;
                LOGD("请重新拍摄");
                errorResult = 1;
                continue;
            }
            eyeToppest.push_back(findToppest(eyes));//找到所有眼睛中最高的那个矩形的上边界
            eyeToppest.push_back(findEyesLowest(eyes));//下边界
            eyeToppest.push_back(findEyeLeftMost(eyes));//左边界
            eyeToppest.push_back(findEyeRightMost(eyes));//右边界
        }
    }
}



void main1(const char *imgReadPath) {

    vector<Rect> faces;
    vector<int> eyesEdgesVector;
    //创建额头分类器，并加载模型文件
    Ptr<SVM> foreHeadSvm = SVM::create();
    foreHeadSvm = Algorithm::load<SVM>(foreHeadModelFilePath);

    //创建鱼尾纹分类器，并加载模型文件
    Ptr<SVM> EyesSvm = SVM::create();
    EyesSvm = Algorithm::load<SVM>(eyesModelFilePath);

    //创建眼袋纹分类器，并加载模型文件
    Ptr<SVM> PouchSvm = SVM::create();
    PouchSvm = Algorithm::load<SVM>(pouchModelFilePath);

    //创建表情纹分类器，并加载模型文件
    Ptr<SVM> ExpressionSvm = SVM::create();
    ExpressionSvm = Algorithm::load<SVM>(expressionModelFilePath);

    //加载模型文件
    if (!faces_cascade.load(face_cascade_name)) {
        LOGD("can not find the xml_face_cascade_name files!");

    } else {
        LOGD("find the xml_face_cascade_name files!");
    }

    if (!eyes_cascade.load(eyes_cascade_name)) {
        LOGD("can not find the xml_eyes_cascade_name files!");
    } else {
        LOGD("find the xml_eyes_cascade_name files!");
    }

    char readPath[300];

    for (int i=1;i<=1;i++)
    {
        //sprintf(readPath,imgReadPath,i);

        Mat srcFaceTestImage = imread(imgReadPath,0);//读入图片,注意此处读入的是彩色图像，后边要进行灰度化

        if (srcFaceTestImage.data == NULL) {
            __android_log_print(ANDROID_LOG_ERROR, "***wqz", "can not find the srcPicture!!!");
            return;
        }
//        __android_log_print(ANDROID_LOG_ERROR, "wqz", "%d图像%d*%d", i, srcFaceTestImage.rows, srcFaceTestImage.cols);

        Mat faceROI;
        //检测face和eyes
        detectFaceAndEyes(srcFaceTestImage, faces, eyesEdgesVector);

        if (eyesEdgesVector.size() == 0) {
            LOGD("can not find eyes!!!");
            return;
        }

        //对检测到的每一个face进行处理
        for (int i = 0; i < faces.size(); i++) {
            if (faces[i].width == 0) { continue; }//跳过检测错误的脸

            int eyesToppest = eyesEdgesVector[i * 4];
            int eyesLowest = eyesEdgesVector[i * 4 + 1];
            int eyesLeftMost = eyesEdgesVector[i * 4 + 2];
            int eyesRightMost = eyesEdgesVector[i * 4 + 3];

            int faceWidth = faces[i].width;

            rectangle(srcFaceTestImage, faces[i], Scalar(0, 0, 255), 2, 8); //在输入的人脸图像上画出脸部框图

            faceROI = srcFaceTestImage(faces[i]);//人脸区域
            //foreHeadROI = faceROI(Range(eyesToppest / 5, eyesToppest * 4 / 5), Range(faceWidth / 5, faceWidth * 4 / 5));//额头区域
            foreHead = srcFaceTestImage(
                    Rect(faces[i].x + eyesLeftMost, faces[i].y, faceWidth * 4 / 5 - eyesLeftMost, eyesToppest));
            ForeHead_X = faces[i].x + eyesLeftMost;
            ForeHead_Y = faces[i].y;
            ForeHead_Width = faceWidth * 4 / 5 - eyesLeftMost;
            ForeHead_Height = eyesToppest;
            x_1 = Int_to_String(ForeHead_X);
            y_1 = Int_to_String(ForeHead_Y);
//            string img = imgRootPath+"foreHead" + Int_to_String(imgCount) + ".jpg";
//            if (imwrite(img, foreHead)) {
//                __android_log_print(ANDROID_LOG_DEBUG, "wqz", "第%d个foreHead保存成功", imgCount);
//            }
//            __android_log_print(ANDROID_LOG_DEBUG, "wqz", "额头区域坐标x%dy%dWidth%dHegiht%d",
//                                ForeHead_X, ForeHead_Y, ForeHead_Width, ForeHead_Height);
        }
        //最后一个参数表示是训练数据还是测试数据，倒数第二个表示皱纹种类
        //对额头区域检测
        getBlocksHist(getFormatImage(foreHead, FORE_HEAD_SIZE_X, FORE_HEAD_SIZE_Y), FORE_HEAD_BLOCK_SIZE, FORE_HEAD_FLAG,
                      false);//得到测试数据
        Mat ForeHead_TestDataMat(1, FORE_HEAD_BLOCK_COUNT * 256, CV_32FC1, ForeHead_TestData);//转换成测试数据




        if (foreHeadSvm->isTrained()) {
            // cout << "加载成功" << endl;
            __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "foreHeadSvm加载成功");
        }
        else {
            //cout << "加载的foreHeadSvm尚未训练" << endl;
            __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "加载的foreHeadSvm尚未训练");
        }

        foreHeadTestResult = foreHeadSvm->predict(ForeHead_TestDataMat);
        __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "检测结果%f", foreHeadTestResult);

        if (foreHeadTestResult == 1) {
            //LOGD("额头识别结果：有皱纹--");
            __android_log_print(ANDROID_LOG_ERROR, "***wqz", "额头有皱纹");
            result_1 = "1";
        }
        if (foreHeadTestResult == 2) {
            //LOGD("额头识别结果：无皱纹--");
            __android_log_print(ANDROID_LOG_ERROR, "***wqz", "额头无皱纹");
            result_1 = "2";
        }

        //左眼角区域检测
        getBlocksHist(getFormatImage(leftEyeCorner, EYES_SIZE_X, EYES_SIZE_Y), EYES_BLOCK_SIZE, EYES_FLAG, false);//得到眼角测试数据
        Mat EyeLeftCornerTestDataMat(1, EYES_BLOCK_COUNT * 256, CV_32FC1, Eyes_TestData);
        eyeLeftCornerTestResult = EyesSvm->predict(EyeLeftCornerTestDataMat);
        __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "左眼角识别结果%f", eyeLeftCornerTestResult);
        if (eyeLeftCornerTestResult == 1) {
            LOGD("左眼角识别结果：有皱纹--");
            result_2 = "1";
        }
        if (eyeLeftCornerTestResult == 2) {
            LOGD("左眼角识别结果：无皱纹--");
            result_2 = "2";

        }

        //对右眼角区域检测
        //getBlocksHist(getFormatImage(eyeRightCornerROI, EYES_SIZE_X, EYES_SIZE_Y), EYES_BLOCK_SIZE, EYES_FLAG, false);//得到眼角测试数据
        getBlocksHist(getFormatImage(rightEyeCorner, EYES_SIZE_X, EYES_SIZE_Y), EYES_BLOCK_SIZE, EYES_FLAG,
                      false);//得到眼角测试数据
        Mat EyeRightCornerTestDataMat(1, EYES_BLOCK_COUNT * 256, CV_32FC1, Eyes_TestData);
        eyeRightCornerTestResult = EyesSvm->predict(EyeRightCornerTestDataMat);
        __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "右眼角识别结果%f", eyeRightCornerTestResult);
        if (eyeRightCornerTestResult == 1) {
            result_3 = "1";
            LOGD("右眼角识别结果：有皱纹--");
        }
        if (eyeRightCornerTestResult == 2) {
            LOGD("右眼角识别结果：无皱纹--");
            result_3 = "2";
        }

        //对左眼袋区域进行检测
        getBlocksHist(getFormatImage(leftPouchCorner, POUCH_SIZE_X, POUCH_SIZE_Y), POUCH_BLOCK_SIZE, POUCH_FLAG, false);
        Mat PouchLeftTestDataMat(1, POUCH_BLOCK_COUNT * 256, CV_32FC1, Pouch_TestData);
        pouchLeftTestResult = PouchSvm->predict(PouchLeftTestDataMat);
        __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "左眼袋识别结果%f", pouchLeftTestResult);
        if (pouchLeftTestResult == 1) {
            LOGD("左眼袋识别结果：有皱纹--");
            result_4 = "1";
        }
        if (pouchLeftTestResult == 2) {
            LOGD("左眼袋识别结果：无皱纹--");
            result_4 = "2";
        }

        //对右眼袋区域进行检测
        getBlocksHist(getFormatImage(rightPouchCorner, POUCH_SIZE_X, POUCH_SIZE_Y), POUCH_BLOCK_SIZE, POUCH_FLAG, false);
        Mat PouchRightTestDataMat(1, POUCH_BLOCK_COUNT * 256, CV_32FC1, Pouch_TestData);
        pouchRightTestResult = PouchSvm->predict(PouchRightTestDataMat);
        __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "右眼袋识别结果%f", pouchRightTestResult);
        if (pouchRightTestResult == 1) {
            LOGD("右眼袋识别结果：有皱纹--");
            result_5 = "1";
        }
        if (pouchRightTestResult == 2) {

            LOGD("右眼袋识别结果：无皱纹--");
            result_5 = "2";
        }

        //对左脸表情纹区域检测
        getBlocksHist(getFormatImage(leftFaceROI, EXPRESSION_SIZE_X, EXPRESSION_SIZE_Y), EXPRESSION_BLOCK_SIZE,
                      EXPRESSION_FLAG, false);
        Mat FaceROILeftTestDataMat(1, EXPRESSION_BLOCK_COUNT * 256, CV_32FC1, Expression_TestData);
        FaceROILeftTestResult = ExpressionSvm->predict(FaceROILeftTestDataMat);
        __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "左脸表情纹识别结果%f", FaceROILeftTestResult);
        if (FaceROILeftTestResult == 1) {
            LOGD("左脸表情纹识别结果：有皱纹--");
            result_6 = "1";
        }
        if (FaceROILeftTestResult == 2) {
            LOGD("左脸表情纹识别结果：无皱纹--");
            result_6 = "2";

        }

        //对右脸部区域进行检测
        getBlocksHist(getFormatImage(rightFaceROI, EXPRESSION_SIZE_X, EXPRESSION_SIZE_Y), EXPRESSION_BLOCK_SIZE,
                      EXPRESSION_FLAG, false);
        Mat FaceROIRightTestDataMat(1, EXPRESSION_BLOCK_COUNT * 256, CV_32FC1, Expression_TestData);
        FaceROIRightTestResult = ExpressionSvm->predict(FaceROIRightTestDataMat);
        __android_log_print(ANDROID_LOG_DEBUG, "***wqz", "右脸表情纹识别结果%f", FaceROIRightTestResult);
        if (FaceROIRightTestResult == 1) {
            LOGD("右脸表情纹识别结果：有皱纹--");
            result_7 = "1";
        }
        if (FaceROIRightTestResult == 2) {
            LOGD("右脸表情纹识别结果：无皱纹--");
            result_7 = "2";
        }

    }
}

extern "C" {
JNIEXPORT void JNICALL
Java_com_example_mylibrary_BeautyLoader_wrinkleXmlPath(JNIEnv *env, jclass type, jstring rootPath_,
                                                        jstring xml_1_, jstring xml_2_,
                                                        jstring xml_3_, jstring xml_4_,
                                                        jstring xml_5_, jstring xml_6_) {
    const char *rootPath = env->GetStringUTFChars(rootPath_, 0);
    const char *xml_1 = env->GetStringUTFChars(xml_1_, 0);
    const char *xml_2 = env->GetStringUTFChars(xml_2_, 0);
    const char *xml_3 = env->GetStringUTFChars(xml_3_, 0);
    const char *xml_4 = env->GetStringUTFChars(xml_4_, 0);
    const char *xml_5 = env->GetStringUTFChars(xml_5_, 0);
    const char *xml_6 = env->GetStringUTFChars(xml_6_, 0);
    // TODO
    LOGD("传入皱纹识别模型文件");
                imgRootPath = rootPath;
      foreHeadModelFilePath = xml_1;
          eyesModelFilePath = xml_2;
         pouchModelFilePath = xml_3;
    expressionModelFilePath = xml_4;
          face_cascade_name = xml_5;
          eyes_cascade_name = xml_6;

    env->ReleaseStringUTFChars(xml_1_, xml_1);
    env->ReleaseStringUTFChars(xml_2_, xml_2);
    env->ReleaseStringUTFChars(xml_3_, xml_3);
    env->ReleaseStringUTFChars(xml_4_, xml_4);
    env->ReleaseStringUTFChars(xml_5_, xml_5);
    env->ReleaseStringUTFChars(xml_6_, xml_6);
}


JNIEXPORT jstring JNICALL
Java_com_example_mylibrary_BeautyLoader_wrinkleRec(JNIEnv *env, jclass type, jstring svmpath_) {
    const char *svmpath = env->GetStringUTFChars(svmpath_, 0);
    string resultt;
    errorResult = 0;
    LOGD("开始进行皱纹识别jni");
    imgCount++;

    main1(svmpath);

    if (errorResult == 1){
        resultt = "error";
    } else{
        resultt = x_1+"*"+y_1+"*"+result_1+"/"+
                x_2+"*"+y_2+"*"+result_2+"/"+
                x_3+"*"+y_3+"*"+result_3+"/"+
                x_4+"*"+y_4+"*"+result_4+"/"+
                x_5+"*"+y_5+"*"+result_5+"/"+
                x_6+"*"+y_6+"*"+result_6+"/"+
                x_7+"*"+y_7+"*"+result_7;
    }

    std::string hello = resultt;
    return env->NewStringUTF(hello.c_str());
}
}