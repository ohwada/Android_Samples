/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/wf9a5m75/opencl_test
 */

#include "main.h"

/**
  * stringFromCL
 */
extern "C" JNIEXPORT jstring JNICALL
Java_jp_ohwada_android_opencl2_MainActivity_stringFromCL(JNIEnv *env, jobject thiz) {

    char result[MEM_SIZE];
    int ret = helloworld(result);
    if(ret != 0) {
        return env->NewStringUTF("OpenCL Failed");
    }

    std::string str = "OpenCL Succesfully \n\n";
    str += result;
    str += " from CL\n";

    return env->NewStringUTF(str.c_str());
}

