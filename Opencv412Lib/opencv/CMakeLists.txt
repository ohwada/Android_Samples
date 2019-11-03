#
# OpenCV Sample
# 2019-10-01 K.OHWADA
# packaged "libc++_shared.so" to AAR(Android Library)
#

cmake_minimum_required(VERSION 3.4.1)

# 
# reference : Android Studio CMake - shared library missing libc++_shared.so
#  https://stackoverflow.com/questions/39620739/android-studio-cmake-shared-library-missing-libc-shared-so-can-cmake-bundle
# 

# modify to suit YOUR environment
set(USER "ohwada")
set(NDK_VERSION "20.0.5594570")
set(ANDROID_NDK "/Users/${USER}/Library/Android/sdk/ndk/${NDK_VERSION}/")
set(LIB_PATH "llvm-libc++")
set(SO_NAME "libc++_shared.so")

# Copy "libc++_shared.so" to Android Studio output directory
  configure_file(
    "${ANDROID_NDK}/sources/cxx-stl/${LIB_PATH}/libs/${ANDROID_ABI}/${SO_NAME}" 
    "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/${SO_NAME}" 
    COPYONLY)
