/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/vl2hmmp/detect-line/tree/master/DetectLine
 */

#ifndef OPENCL_H
#define OPENCL_H

#include <jni.h>
#include <android/log.h>

#include <stdio.h>

#include <opencl.h>


#define TAG OpenCL
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,"OPENCL",__VA_ARGS__)


#define checkError(openclFunction)	        \
	if (cl_int err = (openclFunction))	    \
	{                                       \
		printf("error : %d\n", err);        \
	}

namespace OpenCL
{

	int initialize(const char* programPath, int width, int height);
	int detectLine(char* result, char* origin,  int width, int height);
	void release();
	cl_program compileProgram(const char* fileName);
	cl_kernel createKernel(cl_program program, char* kernelName);

}

#endif