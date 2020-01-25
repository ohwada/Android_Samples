/**
 * OpenCV Sample
 * 2020-01-01 K.OHWADA
 */


#include <jni.h>
#include <android/log.h>

#include <GLES2/gl2.h>
#include <EGL/egl.h>

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/core/cvstd.hpp>


#define LOG_TAG "CVprocessor"
//#define LOGD(...)
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))


 enum Mode {
    MODE_PREVIEW=0, 
    MODE_RGBA=1, 
    MODE_GRAY=2, 
    MODE_CANNY=3, 
    MODE_SEPIA=4,
    MODE_ZOOM=5 };


/**
 *  drawImage
 */
void drawImage( int texOut, int w, int h, cv::Mat mat ) 
{

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texOut);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h,
		0, GL_RGBA, GL_UNSIGNED_BYTE, mat.data );

}


/**
 *  convGray
 */
void convGray( cv::Mat matIn, int texOut, int w, int h)
{

    static cv::Mat matOut;
    static cv::Mat matGray;
    matOut.create(h, w, CV_8UC4);
    matGray.create(h, w, CV_8UC4);

    cv::cvtColor(matIn, matGray, cv::COLOR_RGBA2GRAY);

    cv::cvtColor(matGray, matOut, cv::COLOR_GRAY2RGBA);

    drawImage( texOut, w, h, matOut ) ;

}



/**
 *  convCanny
 *  detect dge by Canny　algorithm
 */
void convCanny( cv::Mat matIn, int texOut, int w, int h)
{

// reference
// https://qiita.com/int_main_Nick/items/4f3cc42cb67496ef12b4


    static cv::Mat matOut;
    static cv::Mat matConv;
    matOut.create(h, w, CV_8UC4);
    matConv.create(h, w, CV_8UC4);

    cv::Canny(matIn, matConv, 125, 255);

    cv::cvtColor(matConv, matOut, cv::COLOR_RGB2RGBA);

    drawImage( texOut, w, h, matOut ) ;

}


/**
 *  convSepia
 */
void convSepia( cv::Mat matIn, int texOut, int w, int h) 
{

// reference
// https://amin-ahmadi.com/2016/03/24/sepia-filter-opencv/
// https://stackoverflow.com/questions/15869198/opencv-help-me-with-sepia-kernel


    static cv::Mat matOut;
    static cv::Mat matConv;
    static cv::Mat matRgb;
    matOut.create(h, w, CV_8UC4);
    matConv.create(h, w, CV_8UC4);
    matRgb.create(h, w, CV_8UC4);


   cv::Mat kernel = (cv::Mat_<float>(4,4) <<  0.272, 0.534, 0.131, 0,
                                             0.349, 0.686, 0.168, 0,
                                             0.393, 0.769, 0.189, 0,
                                             0, 0, 0, 1);


    cv::cvtColor(matIn, matRgb, cv::COLOR_RGBA2RGB);

    cv::transform(matRgb, matConv, kernel);

    cv::cvtColor(matConv, matOut, cv::COLOR_RGB2RGBA);

    drawImage( texOut, w, h, matOut ) ;

}


/**
 *  convZoom
 *  enlarge the image in center of the screen
 *  and display it in the upper left
 */
void convZoom( cv::Mat matIn, int texOut, int w, int h)
{

        static cv::Mat matOut;
        static cv::Mat matConv;
        matOut.create(h, w, CV_8UC4);
        matConv.create(h, w, CV_8UC4);

        int rows = matIn.rows;
        int cols = matIn.cols;

        // center
        int cx  = cols / 2;
        int cy = rows / 2;

        // zoom area
        int zw =  cols / 10;
        int zh = rows / 10;

        int zx =  cx - zw/2;
        int zy =  cy - zh/2;

        // Region of Interest
        cv::Rect roi(zx, zy, zw, zh );
        cv::Mat sub = matIn(roi);

  
        cv::resize(sub, matConv, cv::Size(), 5.0, 5.0, cv::INTER_LINEAR);

        cv::cvtColor(matConv, matOut, cv::COLOR_RGB2RGBA);

        // camera imsge
        drawImage( texOut, w, h, matIn ) ;

        // sub image
	    GLsizei sw = w / 4;
 	   GLsizei sh = h / 4;

        // left top
 	   GLint sx = 10;
 	   GLint sy = h - sh - 10;
    
        glTexSubImage2D(GL_TEXTURE_2D, 0, sx, sy, sw, sh, GL_RGBA, GL_UNSIGNED_BYTE, matOut.data);

    }


/**
 *  processFrame
 *  @param texIn : not use
 */
int processFrame(int texIn, int texOut, int w, int h, int mode)
{

    static cv::Mat matIn;
    matIn.create(h, w, CV_8UC4);

    // read image from active Texture
    glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, matIn.data);

    switch(mode)
    {
        case MODE_PREVIEW:
            // nothing to do
            return 1;
            break;

        case MODE_RGBA:
                // read image from preview Texture
                // and write image to output  Texture
                drawImage( texOut, w, h, matIn );
                break;

        case MODE_GRAY:
                convGray(matIn, texOut, w, h);
                break;

        case MODE_CANNY:
            convCanny(matIn, texOut, w, h);
            break;

        case MODE_SEPIA:
            convSepia( matIn, texOut, w, h) ;
            break;

        case MODE_ZOOM:
            convZoom( matIn, texOut, w, h);
            break;

        default:
            LOGD("Unexpected processing mode: %d", mode);
            break;

    } // switch

    return 0;
}


/**
 *  JNI processFrame
 */
extern "C"
JNIEXPORT int JNICALL Java_jp_ohwada_android_opencv55_MainActivity_processFrame(
JNIEnv * env,  jobject thiz,  jint texIn, jint texOut,  jint w,  jint h,  jint mode)
{
    int ret = processFrame(texIn, texOut, w, h, mode);
    return (jint)ret;
}

