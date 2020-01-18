/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 */


#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

#include "opencl.h"


using namespace cv;


/**
 *   processFrame
 */
int processFrame(Mat matIn, Mat &matOut)
{

        int width = matIn.cols;
        int height = matIn.rows;

    // convert Input Image to RGB format buffer
    Mat rgb;
    cvtColor(matIn, rgb, COLOR_RGBA2RGB);
    char *origin = reinterpret_cast<char *>(rgb.data);

	int buf_size = width * height;
    char *result = new char [buf_size];

	OpenCL::detectLine(result, origin, width, height);

    // createe Gray image from buffer
    Mat gray(height, width, CV_8U, result);

    // inverse color black and white
    Mat inv(height, width, CV_8U);
    bitwise_not(gray, inv);

    // convert to RGBA format
    cvtColor( inv, matOut, COLOR_GRAY2RGBA);

    return 0;
}


/**
 *   JNI  initCL
 */
extern "C"
JNIEXPORT jint JNICALL
Java_jp_ohwada_android_opencl6_MainActivity_initCL(
        JNIEnv *env,
        jobject /* this */,
        jstring programFilepath,
        jint width, jint height
)
{
    const char* programPath = 
    env->GetStringUTFChars(programFilepath, 0);
    LOGD("OpenCL program file path:\n%s", programPath);

	OpenCL::initialize(programPath, width, height);
    return 0;
}


/**
 *  JNI releaseCL
 */
extern "C"
JNIEXPORT void JNICALL
Java_jp_ohwada_android_opencl6_MainActivity_releaseCL(
        JNIEnv *env,
        jobject /* this */
)
{
    OpenCL::release();
}


/**
 *   JNI processFrame
 */
extern "C"
JNIEXPORT jint JNICALL
Java_jp_ohwada_android_opencl6_MainActivity_processFrame(
        JNIEnv *env,
        jobject /* this */,
        jlong addrInput,
        jlong addrOutput
        ) 
{

    Mat& matInput  = *(Mat*)addrInput;
    Mat& matOutput  = *(Mat*)addrOutput;

    int ret = processFrame(matInput, matOutput);
    return (jint)ret;

}

