#
# OpenCL Sample
# 2019-10-01 K.OHWADA
# reference : https://github.com/myhouselove/OpenCL-android
#

cmake_minimum_required(VERSION 3.4.1)

set(target native-lib)

set(CMAKE_VERBOSE_MAKEFILE on)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# OpenCL Header 
set(OPENCL_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../opencl)
set(LIB "${OPENCL_DIR}/lib")
include_directories(${OPENCL_DIR}/include)

# OpenCL Driver 
add_library(libOpenCL SHARED IMPORTED )
set_target_properties(libOpenCL PROPERTIES
    IMPORTED_LOCATION "${LIB}/${ANDROID_ABI}/libOpenCL.so")


# generate library from C++ code
add_library( # Sets the name of the library.
             ${target}

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             main.cpp helloworld.c )

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
                        ${target} 
                       libOpenCL

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )


# Copy "libOpenCL.so" to Android Studio output directory
  configure_file(
    "${LIB}/${ANDROID_ABI}/libOpenCL.so" 
    "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libOpenCL.so" 
    COPYONLY)

