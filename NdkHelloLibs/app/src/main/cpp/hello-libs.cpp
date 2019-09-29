/**
 * NDK Sample
 * 2019-08-01 K.OHWADA
 * original : https://github.com/googlesamples/android-ndk/tree/master/hello-libs
 */

#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <android/log.h>
#include <gmath.h>
#include <gperf.h>
#include <string>

#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, "hello-libs::", __VA_ARGS__))

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   app/src/main/java/jp/ohwada/android/ndkhellolibs/MainActivity.java
 */
extern "C" JNIEXPORT jstring JNICALL
Java_jp_ohwada_android_ndkhellolibs_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    // Just for simplicity, we do this right away; correct way would do it in
    // another thread...
    auto ticks = GetTicks();

    for (auto exp = 0; exp < 32; ++exp) {
        volatile unsigned val = gpower(exp);
        (void) val;  // to silence compiler warning
    }
    ticks = GetTicks() - ticks;

    LOGI("calculation time: %" PRIu64, ticks);

    return env->NewStringUTF("Hello from JNI LIBS!");
}
