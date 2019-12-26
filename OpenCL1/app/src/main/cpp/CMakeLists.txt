#
# OpenCL Sample
# 2019-10-01 K.OHWADA
# original : https://github.com/myhouselove/OpenCL-android
#

# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(OPENCL_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../opencl)


set(CMAKE_VERBOSE_MAKEFILE on)
set(LIB "${OPENCL_DIR}/lib")
include_directories(${OPENCL_DIR}/include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#--------------------------------------------------- import ---------------------------------------------------#
add_library(libOpenCL SHARED IMPORTED )
set_target_properties(libOpenCL PROPERTIES
    IMPORTED_LOCATION "${LIB}/${ANDROID_ABI}/libOpenCL.so")


add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib libOpenCL

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

# Copy "libOpenCL.so" and "libGLES_mali.so" to Android Studio output directory
  configure_file(
    "${LIB}/${ANDROID_ABI}/libOpenCL.so" 
    "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libOpenCL.so" 
    COPYONLY)
  configure_file(
    "${LIB}/${ANDROID_ABI}/libGLES_mali.so" 
    "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libGLES_mali.so" 
    COPYONLY)
