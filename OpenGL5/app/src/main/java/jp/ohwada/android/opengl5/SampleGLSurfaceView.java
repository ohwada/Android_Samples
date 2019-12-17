/** 
 *  OpenGL eS2.0 Sample
 *  draw Rectangle with Texture
 *  2019-10-01 K.OHWADA
 * original : https://github.com/benosteen/opengles-book-samples
 */
package jp.ohwada.android.opengl5;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 *  class SampleGLSurfaceView
 */
public class SampleGLSurfaceView extends GLSurfaceView {

	// OpenGL ES2.0
    private final static int EGL_VERSION = 2;


/** 
 *  constractor
 */
	public SampleGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
        initView(context);
	}


/** 
 *  constractor
 */
	public SampleGLSurfaceView(Context context) {
		super(context);
        initView(context);
	}


/** 
 *  initView
 */
private void initView(Context context) {
		setEGLContextClientVersion(EGL_VERSION);
		setRenderer(new SimpleTexture2DRenderer(context));
}


} // class SampleGLSurfaceView
