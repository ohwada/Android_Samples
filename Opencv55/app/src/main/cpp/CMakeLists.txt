#
# OpenCV Sample
# 2020-01-01 K.OHWADA
#


cmake_minimum_required(VERSION 3.6)

# Enable C++11
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED TRUE)
set(CMAKE_VERBOSE_MAKEFILE on)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")


set(target CvProcessor)
project(${target} CXX)


# Searches for a specified prebuilt library and stores the path
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )


# OpenCV 
set(ANDROID_OPENCV_COMPONENTS "opencv_java" CACHE STRING "")
message(STATUS "ANDROID_ABI=${ANDROID_ABI}")
find_package(OpenCV REQUIRED COMPONENTS ${ANDROID_OPENCV_COMPONENTS})


include_directories("${CMAKE_CURRENT_LIST_DIR}")


# generate library from C++ code
add_library(${target} SHARED
        CvProcessor.cpp
)


# Specifies libraries CMake should link to your target library. 
target_link_libraries(${target} 
    ${ANDROID_OPENCV_COMPONENTS}
    EGL
    GLESv2
    ${log-lib}
)


