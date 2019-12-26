/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/myhouselove/OpenCL-android
 */


#include <jni.h>
#include <stdlib.h>
#include <string>
#include <string.h>
#include <opencl.h>
#include <android/log.h>

#include <iostream>
#include <fstream>
#include <sstream>
#include <unistd.h>
#include <sys/time.h>
#include<time.h>
#include<stdio.h>
#include<stdlib.h>


#define TAG OpenCL
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,"OPENCL",__VA_ARGS__)

const int ARRAY_SIZE = 100000;


/**
 *  CreateContext
 *  1 : choose the OpenCL platform and create a context
 */
cl_context CreateContext()
{
    cl_int errNum;
    cl_uint numPlatforms;
    cl_platform_id firstPlatformId;
    cl_context context = NULL;

    // Choose the first of the available platforms
    errNum = clGetPlatformIDs(1, &firstPlatformId, &numPlatforms);
    if (errNum != CL_SUCCESS || numPlatforms <= 0)
    {
        LOGD("Failed to find any OpenCL platforms.");
        return NULL;
    }

    // Create an OpenCL context
    cl_context_properties contextProperties[] =
            {
                    CL_CONTEXT_PLATFORM,
                    (cl_context_properties)firstPlatformId,
                    0
            };
    context = clCreateContextFromType(contextProperties, CL_DEVICE_TYPE_GPU,
                                      NULL, NULL, &errNum);

    return context;
}


/**
 *  CreateCommandQueue
 *  1 : create a device and create a command queue
 */
cl_command_queue CreateCommandQueue(cl_context context, cl_device_id *device)
{
    cl_int errNum;
    cl_device_id *devices;
    cl_command_queue commandQueue = NULL;
    size_t deviceBufferSize = -1;

    // 获取设备缓冲区大小
    // Get device buffer size
    errNum = clGetContextInfo(context, CL_CONTEXT_DEVICES, 0, NULL, &deviceBufferSize);

    if (deviceBufferSize <= 0)
    {
        LOGD("No devices available.");
        return NULL;
    }

    // Allocate cache space for devices
    devices = new cl_device_id[deviceBufferSize / sizeof(cl_device_id)];
    errNum = clGetContextInfo(context, CL_CONTEXT_DEVICES, deviceBufferSize, devices, NULL);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Failed call to clGetContextInfo(...,GL_CONTEXT_DEVICES,...)");
        return NULL;
    }

    // Pick the first of the available devices
    commandQueue = clCreateCommandQueue(context, devices[0], 0, NULL);
    if (commandQueue == NULL)
    {
         LOGD("Failed to create commandQueue for device 0");
        return NULL;
    }
    *device = devices[0];
    delete[] devices;
    return commandQueue;
}


/**
 *  CreateProgram
 *  3 : create and build program objects
 */
cl_program CreateProgram(cl_context context, cl_device_id device, const char* fileName)
{
    cl_int errNum;
    cl_program program;

    std::ifstream kernelFile(fileName, std::ios::in);
    if (!kernelFile.is_open())
    {
        LOGD("Failed to open file for reading: %s\n" , fileName );
        return NULL;
    }

    std::ostringstream oss;
    oss << kernelFile.rdbuf();

    std::string srcStdStr = oss.str();
    const char *srcStr = srcStdStr.c_str();
    program = clCreateProgramWithSource(context, 1,
                                        (const char**)&srcStr,
                                        NULL, NULL);

    errNum = clBuildProgram(program, 0, NULL, NULL, NULL, NULL);

    return program;
}


/**
 *  CreateMemObject
 *  Create and build program objects
 */
bool CreateMemObjects(cl_context context, cl_mem memObjects[3],
                      float *a, float *b)
{
    memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                   sizeof(float) * ARRAY_SIZE, a, NULL);
    memObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                   sizeof(float) * ARRAY_SIZE, b, NULL);
    memObjects[2] = clCreateBuffer(context, CL_MEM_READ_WRITE,
                                   sizeof(float) * ARRAY_SIZE, NULL, NULL);
    return true;
}


/**
 *  Cleanup
 *  Release OpenCL resources
 */
void Cleanup(cl_context context, cl_command_queue commandQueue,
             cl_program program, cl_kernel kernel, cl_mem memObjects[3])
{
    for (int i = 0; i < 3; i++)
    {
        if (memObjects[i] != 0)
            clReleaseMemObject(memObjects[i]);
    }
    if (commandQueue != 0)
        clReleaseCommandQueue(commandQueue);

    if (kernel != 0)
        clReleaseKernel(kernel);

    if (program != 0)
        clReleaseProgram(program);

    if (context != 0)
        clReleaseContext(context);
}

int test(const char *filepath, double *time);

/**
 * test
 */
int test(const char *filepath, double *time)
{

    cl_context context = 0;
    cl_command_queue commandQueue = 0;
    cl_program program = 0;
    cl_device_id device = 0;
    cl_kernel kernel = 0;
    cl_mem memObjects[3] = { 0, 0, 0 };
    cl_int errNum;
    clock_t t1,t2,t3,t4;


    // 1 : choose the OpenCL platform and create a context
    context = CreateContext();

    // 2 : create a device and create a command queue
    commandQueue = CreateCommandQueue(context, &device);
    if (commandQueue == NULL)
    {
        Cleanup(context, commandQueue, program, kernel, memObjects);
        return 1; // failed
    }

    // Create and build program objects
    program = CreateProgram(context, device, (const char*)filepath );
    if (program == NULL)
    {
        LOGD("Failed to create CL program from file");
        return 1;  // failed
    }

    // 4 : create an OpenCL kernel and allocate memory space
    kernel = clCreateKernel(program, "hello_kernel", NULL);
    if (kernel == NULL)
    {
        LOGD("Failed to create kernel");
        Cleanup(context, commandQueue, program, kernel, memObjects);
        return 1;
    }

    // Create data to process
    float result1[ARRAY_SIZE]; // cpu
    float result2[ARRAY_SIZE]; // gpu
    float a[ARRAY_SIZE];
    float b[ARRAY_SIZE];

    // initialize array variables
    for (int i = 0; i < ARRAY_SIZE; i++)
    {
        a[i] = (float)i;
        b[i] = (float)(ARRAY_SIZE - i);
    }

    t1 = clock();
    LOGD("t1 = %.8f\n",(double)t1);

    // calculate with CPU
    for(int j = 0;j <  ARRAY_SIZE;j++){
        result1[j] = a[j]+b[j];
    }

    t2 = clock();
    LOGD("t2 = %.8f\n",(double)t2);

    // Create a memory object
    if (!CreateMemObjects(context, memObjects, a, b))
    {
        Cleanup(context, commandQueue, program, kernel, memObjects);
        LOGD("cannot create memory object.");
        return 1; // failed
    }

    // 5 : set the kernel data and execute the kernel
    errNum = clSetKernelArg(kernel, 0, sizeof(cl_mem), &memObjects[0]);
    errNum |= clSetKernelArg(kernel, 1, sizeof(cl_mem), &memObjects[1]);
    errNum |= clSetKernelArg(kernel, 2, sizeof(cl_mem), &memObjects[2]);

    t3 = clock();
    LOGD("t3 = %.8f\n",(double)t3);

    size_t globalWorkSize[1] = { ARRAY_SIZE };
    size_t localWorkSize[1] = { 1 };

    // calculate with GPU
    errNum = clEnqueueNDRangeKernel(commandQueue, kernel, 1, NULL,
                                    globalWorkSize, localWorkSize,
                                    0, NULL, NULL);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error queuing kernel for execution: %d", errNum);
        Cleanup(context, commandQueue, program, kernel, memObjects);
        return 1; // failed
    }

    t4 = clock();
    LOGD("t4 = %.8f\n",(double)t4);

    // 6 : Read the execution result and release the OpenCL resource
    errNum = clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE,
                                 0, ARRAY_SIZE * sizeof(float), result2,
                                 0, NULL, NULL);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error reading result buffer.");
        Cleanup(context, commandQueue, program, kernel, memObjects);
        return 1; // failed
    }

    // calc unit sec
    double cpu = (double)(t2-t1)/CLOCKS_PER_SEC;
    double gpu = (double)(t4-t3)/CLOCKS_PER_SEC;
    time[0] = cpu;
    time[1] = gpu;
    LOGD("cpu t = %.8f\n", cpu);
    LOGD("gpu t = %.8f \n", gpu);

    // check the calculate result of CPU and GPU
    for(int k = 0;k <  ARRAY_SIZE;k++){
        float f1 = result1[k];
        float f2 = result2[k];
        int r1 = (int)f1;
        int r2 = (int)f2;
        // LOGD("result: %f", f2);
        if (r1 != r2) {
             LOGD("unmstch: %f , %f", f1, f2);
            return 1; // failed
        }
    }

    LOGD("Executed program succesfully.");
    getchar();
    Cleanup(context, commandQueue, program, kernel, memObjects);

    return 0; // succesfully
}


/**
 *  stringFromJNI
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_jp_ohwada_android_opencl1_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */,
        jstring cLFilePath) {

    const char* path = env->GetStringUTFChars(cLFilePath, 0);
    LOGD("OpenCL program text:\n%s", path);

    double time[2];
    int ret = test(path, time);
    if(ret != 0) {
        std::string ret1 = "OpenCL Failed";
        return env->NewStringUTF(ret1.c_str());
    }

    std::string ret2 = "OpenCL Succesfully \n";
	char buffer[50];

    // calc unit msec
    float cpu = 1000 * time[0];
    float gpu = 1000 * time[1];

	sprintf (buffer, " CPU: %.3f (msec)\n GPU: %.3f (msec)\n ", cpu , gpu );
    ret2 += buffer;
    return env->NewStringUTF(ret2.c_str());
}
