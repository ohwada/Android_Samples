/** 
 *  OpenGL and GLSurfaceView Sample
 *  draw Triangle
 *  2019-10-01 K.OHWADA
 * original : https://qiita.com/shunjiro/items/f3bfca727b76350ee23a
 */
package jp.ohwada.android.opengl2;


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
        initView();
	}


/** 
 *  constractor
 */
	public SampleGLSurfaceView(Context context) {
		super(context);
        initView();
	}


/** 
 *  initView
 */
private void initView() {
		this.setEGLContextClientVersion(EGL_VERSION);
		this.setRenderer(new SampleGLSurfaceViewRenderer());
}


} // class SampleGLSurfaceView
