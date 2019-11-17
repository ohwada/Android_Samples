/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/tutorial-2-mixedprocessing
 * 
 * change org_opencv_samples_tutorial2_Tutorial2Activity
 * to jp_ohwada_android_opencv48_MainActivity
 */

#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/features2d.hpp>
#include <vector>

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_jp_ohwada_android_opencv48_MainActivity_FindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba);

/**
 * detect interest points
 * and draw small red circle on interest points
 */
JNIEXPORT void JNICALL Java_jp_ohwada_android_opencv48_MainActivity_FindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba)
{
    Mat& mGr  = *(Mat*)addrGray;
    Mat& mRgb = *(Mat*)addrRgba;
    vector<KeyPoint> v;

/**
 * detect interest points
 */
    Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
    detector->detect(mGr, v);

/**
 * draw small red circle on interest points
 */
    for( unsigned int i = 0; i < v.size(); i++ )
    {
        const KeyPoint& kp = v[i];
        circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255,0,0,255));
    }
}
}
