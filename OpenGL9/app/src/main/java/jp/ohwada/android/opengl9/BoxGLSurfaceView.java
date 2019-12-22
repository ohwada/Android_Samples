/** 
 *  OpenGL ES2.0 Sample
 *  draw Images continuously like slot machine
 *  2019-10-01 K.OHWADA
 * original : http://junkcode.aakaka.com/archives/500
 */
package jp.ohwada.android.opengl9;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


/** 
 *  class BoxGLSurfaceView
 */
class BoxGLSurfaceView extends GLSurfaceView implements OnTouchListener {

	// OpenGL ES2.0
    private final static int EGL_VERSION = 2;

	private BoxGLSurfaceViewRenderer mSurfaceRenderer;

    private GestureDetector mGestureDetector;


/** 
 *  constractor
 */
	public BoxGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}


/** 
 *  constractor
 */
	public BoxGLSurfaceView(Context context) {
		super(context);
		initView(context);
	}


/** 
 *  initView
 */
	private void initView(Context contex) {

		setEGLContextClientVersion(EGL_VERSION);

        // Renderer settings
		mSurfaceRenderer = new BoxGLSurfaceViewRenderer(contex);
		setRenderer(mSurfaceRenderer);

        mGestureDetector = new GestureDetector(contex, new GestureListener() );

		// GestureDetector settings
		setClickable(true);
		setOnTouchListener(this);
	}
	

/** 
 *  onTouch
 */
    @Override
	public boolean onTouch(View view, MotionEvent event) {
        boolean b = mGestureDetector.onTouchEvent(event);
		return b;
	}


/**
 * class GestureListener
 */
private class GestureListener extends GestureDetector.SimpleOnGestureListener {

/** 
 *  onFling
 */
    @Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
			mSurfaceRenderer.fling(e1, e2, velocityX,
			velocityY );
		    return false;
	}

/**
 * onScroll
 */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
            float distanceX, float distanceY) {
            return false;
    }

/** 
 *  onDown
 */
    @Override
	public boolean onDown(MotionEvent arg0) {
		// nop
		return false;
	}

/** 
 *  onShowPress
 */
    @Override
	public void onShowPress(MotionEvent e) {
		// nop	
	}

/** 
 *  onSingleTapUp
 */
    @Override
	public boolean onSingleTapUp(MotionEvent e) {
		// nop
		return false;
	}

/**
 * onContextClick
 */
    @Override
    public boolean onContextClick(MotionEvent e) {
        return false;
    }

/**
 * onDoubleTap
 */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

/**
 * onDoubleTapEvent
 */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

/**
 * onSingleTapConfirmed
 */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }



/** 
 *  onLongPress
 */
	public void onLongPress(MotionEvent arg0) {
		// nop	
	}



} // class  GestureListener


} // class BoxGLSurfaceView

