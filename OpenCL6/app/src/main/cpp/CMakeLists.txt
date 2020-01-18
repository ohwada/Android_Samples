#
# OpenCL Sample
# 2020-01-01 K.OHWADA
#


cmake_minimum_required(VERSION 3.4.1)

# Enable C++11
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED TRUE)
set(CMAKE_VERBOSE_MAKEFILE on)


set(target detectLine)


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")


# OpenCL Header 
set(OPENCL_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../opencl)
set(LIB "${OPENCL_DIR}/lib")
include_directories(${OPENCL_DIR}/include)


# OpenCL Driver 
add_library(libOpenCL SHARED IMPORTED )
set_target_properties(libOpenCL PROPERTIES
    IMPORTED_LOCATION "${LIB}/${ANDROID_ABI}/libOpenCL.so")


# OpenCV 
set(ANDROID_OPENCV_COMPONENTS "opencv_java" CACHE STRING "")
message(STATUS "ANDROID_ABI=${ANDROID_ABI}")
find_package(OpenCV REQUIRED COMPONENTS ${ANDROID_OPENCV_COMPONENTS})

include_directories("${CMAKE_CURRENT_LIST_DIR}")


file(GLOB srcs *.cpp *.c)
file(GLOB hdrs *.hpp *.h)

# generate library from C++ code
add_library(${target} SHARED ${srcs} ${hdrs})


# Searches for a specified prebuilt library and stores the path
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )


# Specifies libraries CMake should link to your target library. 
target_link_libraries(${target} 
    ${ANDROID_OPENCV_COMPONENTS}
    libOpenCL
    ${log-lib}
)


# Copy "libOpenCL.so" to Android Studio output directory
  configure_file(
    "${LIB}/${ANDROID_ABI}/libOpenCL.so" 
    "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libOpenCL.so" 
    COPYONLY)
