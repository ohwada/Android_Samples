/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/vl2hmmp/detect-line/tree/master/DetectLine
 */

#include <stdio.h>
#include <malloc.h>
#include <CL/cl.h>

#include "opencl.h"

cl_device_id gDevice;
cl_command_queue gCommandQueue;
cl_context gContext;

cl_program gProgram;
cl_kernel gKernel;

cl_mem gResult;
cl_mem gOrigin;


/*
 * initialize
 */
int OpenCL::initialize(const char* programPath, int width, int height)
{

	int platformIdx =0;
	int deviceIdx =0;

	// GetPlatformID
	cl_uint platformNumber = 0;
	cl_platform_id platformIds[8];
	checkError(clGetPlatformIDs(8, platformIds, &platformNumber));
	cl_platform_id platform = platformIds[platformIdx];

	// GetDeviceID
	cl_uint deviceNumber = 0;
	cl_device_id deviceIds[1];
	checkError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, deviceIds, &deviceNumber));
	gDevice = deviceIds[deviceIdx];

	// Device Version
    char device_version[256];
 	size_t param_value_size_ret;
    checkError( clGetDeviceInfo(gDevice,
 	CL_DEVICE_VERSION,
 	256,
 	&device_version,
 	&param_value_size_ret));
    LOGD( "Device Version: %s", device_version );

	// CreateContext
	gContext = clCreateContext(NULL, 1, &gDevice, NULL, NULL, NULL);
    if (gContext == NULL)
    {
        LOGD("Error clCreateContext");
        return 1;
    }

	// CreateCommandQueue
	gCommandQueue = clCreateCommandQueue(gContext, gDevice, 0, NULL);
    if (gCommandQueue == NULL)
    {
        LOGD("Error clCreateCommandQueue");
        return 1;
    }

	// compile the kernel program
	gProgram = compileProgram(programPath);
    if (gProgram == NULL)
    {
        LOGD("Error compileProgram");
        return 1;
    }

	// createKernel
	gKernel = createKernel(gProgram, "detectLine");
    if (gProgram == NULL)
    {
        LOGD("Error createKernel");
        return 1;
    }

	// CreateBuffer Result
	gResult = clCreateBuffer(gContext, CL_MEM_READ_WRITE, sizeof(char) * width * height, NULL, NULL);
    if (gResult == NULL)
    {
        LOGD("Error clCreateBuffer Result");
        return 1;
    }

	// CreateBuffer Origin
	gOrigin = clCreateBuffer(gContext, CL_MEM_READ_WRITE, sizeof(char) * width * height * 3, NULL, NULL);
    if (gOrigin == NULL)
    {
        LOGD("Error clCreateBuffer Origin");
        return 1;
    }

    LOGD("OpenCL::initialize Successful");
    return 0;
}


/*
 * call OpenCL kernel to perform calculations
 */
int OpenCL::detectLine(char* result, char* origin,  int width, int height)
{

    // Memory size
	cl_int2 size = { width, height };

    // transfer Memory transfer from host to device
	checkError(clEnqueueWriteBuffer(gCommandQueue, gOrigin, CL_TRUE, 0, sizeof(char) * width * height * 3, origin, 0, NULL, NULL));

    // set memory object as argument of kernel function
	checkError(clSetKernelArg(gKernel, 0, sizeof(cl_mem), &gResult));
	checkError(clSetKernelArg(gKernel, 1, sizeof(cl_mem), &gOrigin));
	checkError(clSetKernelArg(gKernel, 2, sizeof(cl_int2), &size));

    // set the number of parallel executions of the kernel
	size_t workSize[2] = { (size_t)width, (size_t)height };

    // call the kernel
	checkError(clEnqueueNDRangeKernel(gCommandQueue, gKernel, 2, NULL, workSize, NULL, 0, NULL, NULL));
	
    // transfer Memory transfer from device to host 
	checkError(clEnqueueReadBuffer(gCommandQueue, gResult, CL_TRUE, 0, sizeof(char) * width * height, result, 0, NULL, NULL));

	return 0;
}


/*
 * release
 */
void OpenCL::release()
{
	clFinish(gCommandQueue);
	clReleaseMemObject(gResult);
	clReleaseMemObject(gOrigin);
	clReleaseCommandQueue(gCommandQueue);
	clReleaseContext(gContext);
}

/*
* compile the OpenCL kernel program 
* and return the generated program object
*/
cl_program OpenCL::compileProgram(const char* fileName)
{
	// read programs file
	FILE* fp;
	fp = fopen(fileName, "r");
	if (fp == NULL)
	{
		LOGD("%s load failed¥n", fileName);
		return NULL;
	}

	fseek(fp, 0, SEEK_END);
	const int filesize = ftell(fp);

	fseek(fp, 0, 0);
	char* sourceString = (char*)malloc(filesize);
	size_t sourceSize = fread(sourceString, sizeof(char), filesize, fp);
	fclose(fp);

	// compile the kernel program
	cl_program program = clCreateProgramWithSource(gContext, 1, (const char**)&sourceString, (const size_t*)&sourceSize, NULL);
	cl_int err = clBuildProgram(program, 1, &gDevice, NULL, NULL, NULL);

	if (err != CL_SUCCESS)
	{

        // logging error details, if fail to compile
		size_t logSize;
		clGetProgramBuildInfo(program, gDevice, CL_PROGRAM_BUILD_LOG, 0, NULL, &logSize);
		char* buildLog = (char*)malloc((logSize + 1));
		clGetProgramBuildInfo(program, gDevice, CL_PROGRAM_BUILD_LOG, logSize, buildLog, NULL);
		LOGD("ProgramBuildInfo: %s", buildLog);
		free(buildLog);
	}
	free(sourceString);

	return program;
}


/*
* Create a kernel object from a program object
*/
cl_kernel OpenCL::createKernel(cl_program program, char* kernelName)
{
	return clCreateKernel(program, kernelName, NULL);
}
