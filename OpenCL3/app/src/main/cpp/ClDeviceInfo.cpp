/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 */

#define __CL_ENABLE_EXCEPTIONS
#define CL_USE_DEPRECATED_OPENCL_1_1_APIS /*let's give a chance for OpenCL 1.1 devices*/


#include <CL/cl.hpp>

#include <jni.h>
#include <stdlib.h>
#include <string>
#include <string.h>
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


/**
 * getDeviceInfo
 */
int getDeviceInfo(
        std::string &platform_name, 
        std::string &platform_version, 
        std::string &device_vendor, 
        std::string &device_name, 
        std::string &device_version,
        bool &cl_gl_not,
        bool &images_not ) 
{

    LOGD("OpenCL getDeviceInfo");

    try
    {
        std::vector<cl::Platform> platforms;
        cl::Platform::get(&platforms);

        if( platforms.size() == 0 ) {
            LOGD("can not found OpenCL platforms");
            return 1;
        }

        cl::Platform platform = platforms[0];
 
        platform_name = platform.getInfo<CL_PLATFORM_NAME>();
        platform_version = platform.getInfo<CL_PLATFORM_VERSION>();
        std::string platform_profile = platform.getInfo<CL_PLATFORM_PROFILE>();
        std::string platform_extensions = platform.getInfo<CL_PLATFORM_EXTENSIONS>();

        LOGD( "Platform name: %s", platform_name.c_str() );
        LOGD( "Platform version: %s", platform_version.c_str() );
        LOGD( "Platform profile: %s", platform_profile.c_str() );
        LOGD( "Platform extensions: %s", platform_extensions.c_str() );

        if(platform_extensions.find("cl_khr_gl_sharing") == std::string::npos)
        {
            cl_gl_not = true;
            LOGD("Warning: CL-GL sharing isn't supported by PLATFORM");
        }

        std::vector<cl::Device> devices;
        platform.getDevices(CL_DEVICE_TYPE_GPU, &devices);

        if( devices.size() == 0 ) {
            LOGD("can not found OpenCL devices");
            return 1;
        }


        cl::Device device = devices[0];

       device_vendor = device.getInfo<CL_DEVICE_VENDOR>();
        device_name = device.getInfo<CL_DEVICE_NAME>();
        device_version = device.getInfo<CL_DEVICE_VERSION>();
        std::string device_extensions = device.getInfo<CL_DEVICE_EXTENSIONS>();



        LOGD( "Device vendor: %s", device_vendor.c_str() );
        LOGD( "Device name: %s", device_name.c_str() );
        LOGD( "Device version: %s", device_version.c_str() );
        LOGD( "Device extensions: %s", device_extensions.c_str() );

        cl_bool  image_support = device.getInfo<CL_DEVICE_IMAGE_SUPPORT>();
        if( !image_support ) {
            LOGD( " images: not suport");
            images_not = true;
        }

    }
    catch(const cl::Error& e)
    {
        LOGD( "OpenCL info: error while gathering OpenCL info: %s (%d)", e.what(), e.err() );
        return 1;
    }
    catch(const std::exception& e)
    {
        LOGD( "OpenCL info: error while gathering OpenCL info: %s", e.what() );
        return 1;
    }
    catch(...)
    {
        LOGD( "OpenCL info: unknown error while gathering OpenCL info" );
        return 1;
    }

    return 0;
}


/**
 *  NativeClDeviceInfo
 */
extern "C"
JNIEXPORT jint JNICALL
Java_jp_ohwada_android_opencl3_MainActivity_NativeClDeviceInfo(
        JNIEnv *env,
        jobject, /* this */
        jcharArray result,
        jintArray flags
)
 {

        LOGD("NativeClDeviceInfo");

        jchar *p_result = env->GetCharArrayElements(result, 0);
        jint *p_flags = env->GetIntArrayElements(flags, 0);

            std::string platform_name;
            std::string platform_version;
            std::string device_vendor;
            std::string device_name;
            std::string device_version;
            //int cl_gl_not = 0;
            bool cl_gl_not = false;
            bool images_not = false;

        int ret = getDeviceInfo(platform_name, platform_version, device_vendor, device_name, device_version, cl_gl_not, images_not);

            // Failed
            if(ret != 0) {
                return ret;
            }

        std::string str = "platform_name: ";
        str += platform_name;
        str += "\n ";
        str += "platform_version: ";
        str += platform_version;
        str += "\n\n ";
        str += "Device Vender: ";
        str += device_vendor;
        str += "\n";
        str += "Device Name: ";
        str += device_name;
        str += "\n";
        str += "Device Version: ";
        str += device_version;
        str += "\n";


        int len = str.length();
        for(int i=0; i<len; i++) {
            p_result[i] = str[i];
        }


        if( cl_gl_not) {
            p_flags[0] = 1;
        }

        if(images_not) {
            p_flags[1] = 1;
        }

        // Succesfully
        env->ReleaseCharArrayElements(result, p_result, 0);
        env->ReleaseIntArrayElements(flags, p_flags, 0);

        return 0;

}

