/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/bgaster/opencl-book-samples/tree/master/src/Chapter_8/ImageFilter2D
 */


//
// Book:      OpenCL(R) Programming Guide
// Authors:   Aaftab Munshi, Benedict Gaster, Timothy Mattson, James Fung, Dan Ginsburg
// ISBN-10:   0-321-74964-2
// ISBN-13:   978-0-321-74964-2
// Publisher: Addison-Wesley Professional
// URLs:      http://safari.informit.com/9780132488006/
//            http://www.openclprogrammingguide.com
//

// ImageFilter2D.cpp
//
//    This example demonstrates performing gaussian filtering on a 2D image using
//    OpenCL
//
//    Requires FreeImage library for image I/O:
//      http://freeimage.sourceforge.net/


#include <jni.h>
#include <stdlib.h>
#include <string>
#include <string.h>
#include <android/log.h>

#include <iostream>
#include <fstream>
#include <sstream>
#include <opencl.h>

#include <opencv2/core.hpp>



#define TAG OpenCL
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,"OPENCL",__VA_ARGS__)


// using namespace std;
using namespace cv;


///
//  Create an OpenCL context on the first available platform using
//  either a GPU or CPU depending on what is available.
//
cl_context CreateContext()
{

    LOGD("CreateContext");

    cl_int errNum;
    cl_uint numPlatforms;
    cl_platform_id firstPlatformId;
    cl_context context = NULL;

    // First, select an OpenCL platform to run on.  For this example, we
    // simply choose the first available platform.  Normally, you would
    // query for all available platforms and select the most appropriate one.
    errNum = clGetPlatformIDs(1, &firstPlatformId, &numPlatforms);
    if (errNum != CL_SUCCESS || numPlatforms <= 0)
    {
        LOGD("Error clGetPlatformIDs:%d", errNum);
        return NULL;
    }

	// GetDeviceID
    cl_device_id firstDeviceId;
	cl_uint numDevices = 0;
	errNum = clGetDeviceIDs(firstPlatformId, CL_DEVICE_TYPE_GPU, 1, &firstDeviceId, &numDevices);
    if (errNum != CL_SUCCESS || numDevices <= 0)
    {
        LOGD("Error clGetDeviceIDs:%d", errNum);
        return NULL;
    }

	// Device Version
    char device_version[256];
 	size_t param_value_size_ret;
    errNum = clGetDeviceInfo(firstDeviceId,
 	CL_DEVICE_VERSION,
 	256,
 	&device_version,
 	&param_value_size_ret);
    if (errNum != CL_SUCCESS )
    {
        LOGD("Error clGetDeviceInfo:%d", errNum);
    }

    LOGD( "Device version: %s", device_version );

    // Next, create an OpenCL context on the platform.  Attempt to
    // create a GPU-based context, and if that fails, try to create
    // a CPU-based context.
    cl_context_properties contextProperties[] =
    {
        CL_CONTEXT_PLATFORM,
        (cl_context_properties)firstPlatformId,
        0
    };

    context = clCreateContextFromType(contextProperties, CL_DEVICE_TYPE_GPU,
                                      NULL, NULL, &errNum);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error clCreateContextFromType GPU:%d", errNum);
        LOGD("Could not create GPU context, trying CPU...");

        context = clCreateContextFromType(contextProperties, CL_DEVICE_TYPE_CPU,
                                          NULL, NULL, &errNum);
        if (errNum != CL_SUCCESS)
        {
            LOGD("Error clCreateContextFromType CPU:%d", errNum);
            return NULL;
        }
    }

    return context;
}



///
//  Create a command queue on the first device available on the
//  context
//
cl_command_queue CreateCommandQueue(cl_context context, cl_device_id *device)
{
    LOGD("CreateCommandQueue");

    cl_int errNum;
    cl_device_id *devices;
    cl_command_queue commandQueue = NULL;
    size_t deviceBufferSize = -1;

    // First get the size of the devices buffer
    errNum = clGetContextInfo(context, CL_CONTEXT_DEVICES, 0, NULL, &deviceBufferSize);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error clGetContextInfo:%d", errNum);
        return NULL;
    }

    if (deviceBufferSize <= 0)
    {
        LOGD("No devices available.");
        return NULL;
    }

    // Allocate memory for the devices buffer
    devices = new cl_device_id[deviceBufferSize / sizeof(cl_device_id)];
    errNum = clGetContextInfo(context, CL_CONTEXT_DEVICES, deviceBufferSize, devices, NULL);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error clGetContextInfo:%d", errNum);
        return NULL;
    }

    // In this example, we just choose the first available device.  In a
    // real program, you would likely use all available devices or choose
    // the highest performance device based on OpenCL device queries
    commandQueue = clCreateCommandQueue(context, devices[0], 0, NULL);
    if (commandQueue == NULL)
    {
        LOGD("Failed to create commandQueue for device 0");
        return NULL;
    }

    *device = devices[0];
    delete [] devices;
    return commandQueue;
}



///
//  Create an OpenCL program from the kernel source file
//
cl_program CreateProgram(cl_context context, cl_device_id device, const char* fileName)
{
    cl_int errNum;
    cl_program program;

    std::ifstream kernelFile(fileName, std::ios::in);
    if (!kernelFile.is_open())
    {
        LOGD("Failed to open file for reading: %s", fileName);
        return NULL;
    }

    std::ostringstream oss;
    oss << kernelFile.rdbuf();

    std::string srcStdStr = oss.str();
    const char *srcStr = srcStdStr.c_str();
    program = clCreateProgramWithSource(context, 1,
                                        (const char**)&srcStr,
                                        NULL, NULL);
    if (program == NULL)
    {
        LOGD("Failed to create CL program from source.");
        return NULL;
    }

    errNum = clBuildProgram(program, 0, NULL, NULL, NULL, NULL);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error clBuildProgram: %d", errNum);

        // Determine the reason for the error
        char buildLog[16384];
        clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG,
                              sizeof(buildLog), buildLog, NULL);

        LOGD("Error in kernel: %s", buildLog);
        clReleaseProgram(program);
        return NULL;
    }

    return program;
}


///
//  Cleanup any created OpenCL resources
//
void Cleanup(cl_context context, cl_command_queue commandQueue,
             cl_program program, cl_kernel kernel, cl_mem imageObjects[2],
             cl_sampler sampler)
{
    for (int i = 0; i < 2; i++)
    {
        if (imageObjects[i] != 0)
            clReleaseMemObject(imageObjects[i]);
    }
    if (commandQueue != 0)
        clReleaseCommandQueue(commandQueue);

    if (kernel != 0)
        clReleaseKernel(kernel);

    if (program != 0)
        clReleaseProgram(program);

    if (sampler != 0)
        clReleaseSampler(sampler);

    if (context != 0)
        clReleaseContext(context);

}


///
//  Round up to the nearest multiple of the group size
//
size_t RoundUp(int groupSize, int globalSize)
{
    int r = globalSize % groupSize;
    if(r == 0)
    {
     	return globalSize;
    }
    else
    {
     	return globalSize + groupSize - r;
    }
}



/**
 * createInputImage
 */
cl_mem createInputImage(cl_context context, Mat mat, int width, int height)
{

    LOGD("createInputImage");

// https://stackoverflow.com/questions/18687178/opencl-leading-with-opencv

    char *buffer = reinterpret_cast<char *>(mat.data);


    // Create OpenCL image
    cl_image_format clImageFormat;
    clImageFormat.image_channel_order = CL_RGBA;

    clImageFormat.image_channel_data_type = CL_UNORM_INT8;
    //clImageFormat.image_channel_data_type = CL_FLOAT;


    cl_int errNum;
    cl_mem image = clCreateImage2D(context,
                            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                            &clImageFormat,
                            width,
                            height,
                            0,
                            buffer,
                            &errNum);

    if (errNum != CL_SUCCESS)
    {
        LOGD("Error createInputImage: %d", errNum);
        return 0;
    }

    return image;
}


/**
 *  createOutputImage
 */
cl_mem createOutputImage(cl_context context, int width, int height) {

    LOGD("createOutputImage");

    // Create ouput image object
    cl_image_format clImageFormat;
    clImageFormat.image_channel_order = CL_RGBA;

    clImageFormat.image_channel_data_type = CL_UNORM_INT8;
    //clImageFormat.image_channel_data_type = CL_FLOAT;

	cl_int errNum;

    cl_mem image = clCreateImage2D(context,
                                       CL_MEM_WRITE_ONLY,
                                       &clImageFormat,
                                       width,
                                       height,
                                       0,
                                       NULL,
                                       &errNum);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error createOutputImage:%d", errNum);
        return 0;
    }

    return image;
}


/**
 *  convert_mat
 */
template<typename T>
Mat convert_mat(T *image, int width, int height) {

    LOGD("convert_mat");

// https://stackoverflow.com/questions/48207847/how-to-create-cvmat-from-buffer-array-of-t-data-using-a-template-function/48207940

    // Here we need to match T to cv_types like CV_32F, CV_8U and etc.
    // The key point is how to connect these two
    Mat mat(height, width, CV_8UC4, image);

    return mat;
}


/**
 *  ImageFilter
 *  @param matOutput : return modified
 */
int ImageFilter(const char *programfFlepath,  
    Mat matInput, Mat &matOutput)
{

    LOGD("ImageFilter");

    int width = matInput.cols;
    int height = matInput.rows;

    LOGD("ImageFilter: width=%d , height=%d", width, height);

    cl_context context = 0;
    cl_command_queue commandQueue = 0;
    cl_program program = 0;
    cl_device_id device = 0;
    cl_kernel kernel = 0;
    cl_mem imageObjects[2] = { 0, 0 };
    cl_sampler sampler = 0;
    cl_int errNum;


    // Create an OpenCL context on first available platform
    context = CreateContext();
    if (context == NULL)
    {
        LOGD("Failed to create OpenCL context.");
        return 1;
    }

    // Create a command-queue on the first device available
    // on the created context
    commandQueue = CreateCommandQueue(context, &device);
    if (commandQueue == NULL)
    {
        Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
        LOGD("Error: CreateCommandQueue" );
        return 1;
    }

    // Make sure the device supports images, otherwise exit
    cl_bool imageSupport = CL_FALSE;
    clGetDeviceInfo(device, CL_DEVICE_IMAGE_SUPPORT, sizeof(cl_bool),
                    &imageSupport, NULL);
    if (imageSupport != CL_TRUE)
    {
        LOGD("OpenCL device does not support images." );
        return 2;
    }


    // Create input image object
        cl_mem inputImage = createInputImage(context,  matInput, width, height);
        if (inputImage  == 0) {
            LOGD("Error: createInputImage ");
            Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
            return 1;
        }

        imageObjects[0] = inputImage;

    // Create ouput image object
        cl_mem outputImage = createOutputImage(context, width, height);

        if(outputImage == 0) {
            Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
            LOGD("Error: createOutputImage");
            return 1;
        }

        imageObjects[1] = outputImage;


    // Create sampler for sampling image object

//This can be set to CL_ADDRESS_REPEAT, CL_ADDRESS_CLAMP_TO_EDGE, CL_ADDRESS_CLAMP, and CL_ADDRESS_NONE.
 //This can be CL_FILTER_NEAREST or CL_FILTER_LINEAR.

    sampler = clCreateSampler(context,
                              CL_TRUE, // Non-normalized coordinates
                              CL_ADDRESS_CLAMP_TO_EDGE,
                              CL_FILTER_NEAREST,
                              &errNum);

    if (errNum != CL_SUCCESS)
    {
        LOGD("Error: clCreateSampler:%d", errNum);
        Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
        return 1;
    }

    // Create OpenCL program
    program = CreateProgram(context, device, programfFlepath);
    if (program == NULL)
    {
        LOGD("Error: CreateProgram");
        Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
        return 1;
    }

    // Create OpenCL kernel
    kernel = clCreateKernel(program, "gaussian_filter", NULL);
    if (kernel == NULL)
    {
        LOGD("Error: clCreateKernel");
        Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
        return 1;
    }


    // Set the kernel arguments
    errNum = clSetKernelArg(kernel, 0, sizeof(cl_mem), &imageObjects[0]);
    errNum |= clSetKernelArg(kernel, 1, sizeof(cl_mem), &imageObjects[1]);
    errNum |= clSetKernelArg(kernel, 2, sizeof(cl_sampler), &sampler);
    errNum |= clSetKernelArg(kernel, 3, sizeof(cl_int), &width);
    errNum |= clSetKernelArg(kernel, 4, sizeof(cl_int), &height);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error: setting kernel arguments.l");
        Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
        return 1;
    }

    size_t localWorkSize[2] = { 16, 16 };
    size_t globalWorkSize[2] =  { RoundUp(localWorkSize[0], width),
                                  RoundUp(localWorkSize[1], height) };

    // Queue the kernel up for execution
    errNum = clEnqueueNDRangeKernel(commandQueue, kernel, 2, NULL,
                                    globalWorkSize, localWorkSize,
                                    0, NULL, NULL);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error: clEnqueueNDRangeKernel");
        Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
        return 1;
    }

    // Read the output buffer back to the Host
    int buf_size = 4 * width * height;
    char *buffer = new char [buf_size];
    size_t origin[3] = { 0, 0, 0 };
    size_t region[3] = { (size_t)width, (size_t)height, 1};

    errNum = clEnqueueReadImage(commandQueue, imageObjects[1], CL_TRUE,
                                origin, region, 0, 0, buffer,
                                0, NULL, NULL);
    if (errNum != CL_SUCCESS)
    {
        LOGD("Error: clEnqueueReadImage");
        Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);
        return 1;
    }

    LOGD("Executed program succesfully.");

    // set return object
    matOutput = convert_mat(buffer, width, height );

    //delete [] buffer;
    Cleanup(context, commandQueue, program, kernel, imageObjects, sampler);

    return 0;
}




/**
 *  NativeImageFilter
 */
extern "C"
JNIEXPORT jint JNICALL
Java_jp_ohwada_android_opencl5_MainActivity_NativeImageFilter(
        JNIEnv *env,
        jobject /* this */,
        jstring programFilepath,
        jlong addrInput,
        jlong addrOutput
        ) 
{

    const char* programPath = 
    env->GetStringUTFChars(programFilepath, 0);
    LOGD("OpenCL program file path:\n%s", programPath);

    Mat& matInput  = *(Mat*)addrInput;
    Mat& matOutput  = *(Mat*)addrOutput;

    int ret = ImageFilter(programPath, matInput, matOutput);
    return (jint)ret;

}

