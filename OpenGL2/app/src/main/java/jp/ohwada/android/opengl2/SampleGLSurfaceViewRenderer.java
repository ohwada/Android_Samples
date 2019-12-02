/** 
 *  OpenGL and GLSurfaceView Sample
 *  draw Triangle
 *  2019-10-01 K.OHWADA
 * original : https://qiita.com/shunjiro/items/f3bfca727b76350ee23a
 */
package jp.ohwada.android.opengl2;


import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 *  class SampleGLSurfaceViewRenderer
 */
class SampleGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    // Blue
    private final static float CLEAR_COLOR_RED = 0.0f;
    private final static float CLEAR_COLOR_GREEN = 0.0f;
    private final static float CLEAR_COLOR_BLUE = 1.0f;
    private final static float CLEAR_COLOR_ALPHA = 1.0f;

    Triangle triangle;


/** 
 *  onSurfaceCreated
 */
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                triangle = new Triangle();     
    }


/** 
 *  onDrawFrame
 */
    public void onDrawFrame(GL10 gl) {
        // fill the background with the specified color
        GLES20.glClearColor(CLEAR_COLOR_RED, CLEAR_COLOR_GREEN, CLEAR_COLOR_BLUE, CLEAR_COLOR_ALPHA);
        // clear each buffer
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        triangle.draw();
    }


/** 
 *  onSurfaceChanged
 */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //  nop     
    }





} // class SampleGLSurfaceViewRenderer