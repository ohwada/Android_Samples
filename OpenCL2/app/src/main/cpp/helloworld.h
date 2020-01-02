/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/wf9a5m75/opencl_test
 */

#include <jni.h>
#include <CL/cl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>

#define MY_APP_LOG_TAG "opencl_test"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, MY_APP_LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, MY_APP_LOG_TAG, __VA_ARGS__)

#define MEM_SIZE 128

void print_error(char *name, cl_int ret);
