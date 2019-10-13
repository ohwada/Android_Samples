/**
 * MediaCodec and MediaMuxer Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.mediamuxer1;

import android.opengl.EGL14;


import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;


/**
  * class CodecInputSurfaceTest
  */
public class CodecInputSurfaceTest {

    // debug
    private static final String TAG = "CodecInputSurfaceTest";


    private static final int FRAME_RATE = EncodeAndMuxTest.FRAME_RATE;  


    // RGB color values for generated frames
    private static final int TEST_R0 = 0;
    private static final int TEST_G0 = 136;
    private static final int TEST_B0 = 0;
    private static final int TEST_R1 = 236;
    private static final int TEST_G1 = 50;
    private static final int TEST_B1 = 186;

    private static final long ONE_BILLION = 1000000000;


    private CodecInputSurface mInputSurface;


    // size of a frame, in pixels
    private int mWidth = -1;
    private int mHeight = -1;


/**
  * constractor
  */
public CodecInputSurfaceTest() {
    // nop
}


/**
  * setup
  */
public void setup(Surface surface) {
        Log.d(TAG, "setup");
         mInputSurface = new CodecInputSurface(surface);
}


/**
  * release
  */
public void release() {
        Log.d(TAG, "release");
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
}


/**
  * setParam
  */
public  void setParam(int width, int height) {
            Log.d(TAG, "setParam");
        mWidth = width;
        mHeight = height;
}


/**
  * makeCurrent
  */
public void makeCurrent() {
        Log.d(TAG, "makeCurrent");
          mInputSurface.makeCurrent();
}


/**
  * publishSurfaceFrame
  */
public void publishSurfaceFrame(int frameIndex) {
    generateSurfaceFrame(frameIndex);
    setPresentationTime(frameIndex);
    swapBuffers();
}


/**
  * swapBuffers
  */
public boolean swapBuffers() {
            Log.d(TAG, "swapBuffers");
            return mInputSurface.swapBuffers();
}


/**
  *  generateSurfaceFrame
  * 
  * Generates a frame of data using GL commands.  We have an 8-frame animation
   * sequence that wraps around.  It looks like this:
   * <pre>
    *   0 1 2 3
   *   7 6 5 4
   * </pre>
   * We draw one of the eight rectangles and leave the rest set to the clear color.
  */
public void generateSurfaceFrame(int _frameIndex) {
        Log.d(TAG, "generateSurfaceFrame");
        int frameIndex = _frameIndex %8;
        int startX = 0;
        int startY = 0;

        if (frameIndex < 4) {
            // (0,0) is bottom-left in GL
            startX = frameIndex * (mWidth / 4);
            startY = mHeight / 2;
        } else {
            startX = (7 - frameIndex) * (mWidth / 4);
            startY = 0;
        }

        GLES20.glClearColor(TEST_R0 / 255.0f, TEST_G0 / 255.0f, TEST_B0 / 255.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(startX, startY, mWidth / 4, mHeight / 2);
        GLES20.glClearColor(TEST_R1 / 255.0f, TEST_G1 / 255.0f, TEST_B1 / 255.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
} // generateSurfaceFrame


/**
  * setPresentationTime
  */
public void setPresentationTime(int frameIndex) {
        Log.d(TAG, "setPresentationTime");
        long nsecs = computePresentationTimeNsec(frameIndex);
        mInputSurface.setPresentationTime(nsecs);
}


/**
  * Generates the presentation time for frame N, in nanoseconds.
  */
private long computePresentationTimeNsec(int frameIndex) {
        Log.d(TAG, "computePresentationTimeNsec");
        long time = frameIndex * ONE_BILLION / FRAME_RATE;
        return time;
}


} // class CodecInputSurfaceTest

