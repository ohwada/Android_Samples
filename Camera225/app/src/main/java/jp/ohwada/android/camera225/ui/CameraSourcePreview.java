/**
 * Camera2 Sample
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.camera225.ui;


import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;

import jp.ohwada.android.camera225.util.Camera2Source ;
import jp.ohwada.android.camera225.util.DisplayUtil ;
import jp.ohwada.android.camera225.R ;


/**
 *  class CameraSourcePreview
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class CameraSourcePreview extends ViewGroup {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "CameraSourcePreview";


        private final static  boolean USE_TEXTURE_VIEW = true;

        // dimen/linear_layout_control_height
        private final static  int LINEAR_LAYOUT_CONTROL_HEIGHT = 92;

    private AutoFitTextureView mTextureView;

    private SurfaceView mSurfaceView;

    private boolean isTextureViewAvailable = false;

    private boolean isSurfaceViewAvailable = false;

    private boolean isStartRequested = false;

    private Camera2Source mCamera2Source;


/**
 *  constractor
 */
    public CameraSourcePreview(Context context) {
        super(context);
        initPreview(context);
    }
    
/**
 *  constractor
 */
    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreview(context);
    }


/**
 *  initPreview
 */
private void initPreview(Context context) {
        isStartRequested = false;
        isTextureViewAvailable = false;
        isSurfaceViewAvailable = false;
    if(USE_TEXTURE_VIEW ) {
        mTextureView = new  AutoFitTextureView(context);
        addView(mTextureView);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    } else {
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
        mSurfaceView.getHolder().addCallback(mSurfaceViewListener);
    }
} // initPreview


/**
 *  start
 */
    public void start(Camera2Source camera2Source)  {
        log_d("start");
        startCamera(camera2Source);
} //  start



/**
 *   startCamera
 */
    private void startCamera(Camera2Source camera2Source)  {
log_d("startCamera");
    if (camera2Source == null) {
        log_d("camera2Source == null");
        stop();
        return;
    }
        mCamera2Source = camera2Source;
        isStartRequested = true;
        startIfReady();

} //  startCamera


/**
 *  stop
 */
    public void stop() {
        isStartRequested = false;
            if(mCamera2Source != null) {
                mCamera2Source.stop();
            }
    } // stop


/**
 *  startIfReady
 */
    private void startIfReady()  {
        log_d("startIfReady");
        if (!isStartRequested) {
            log_d("not isStartRequested");
            return;
        }

        if (isSurfaceViewAvailable) {
            mCamera2Source.start(mSurfaceView);
        } else if (isTextureViewAvailable) {
            mCamera2Source.start(mTextureView);
        } else {
            log_d("not ViewAvailable");
            return;
        }
  
} // startIfReady


/**
 * onLayout
 */ 
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        log_d("onLayout");
        int layoutWidth = right - left;
        int layoutHeight = bottom - top;
        int childWidth = layoutWidth;
        int childHeight = layoutHeight - DisplayUtil.dpToPx(LINEAR_LAYOUT_CONTROL_HEIGHT);

        for (int i = 0; i < getChildCount(); ++i) {getChildAt(i).layout(0, 0, childWidth, childHeight);
        }

} // onLayout


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * TextureView.SurfaceTextureListener
 */ 
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            log_d(" onSurfaceTextureAvailable");
            isTextureViewAvailable = true;
            // mOverlay.bringToFront();
            startIfReady();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            log_d("onSurfaceTextureSizeChanged");
            // nop
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            log_d("onSurfaceTextureDestroyed");
             isTextureViewAvailable = false;
            return true;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
            //log_d("onSurfaceTextureUpdated");
            // nop
        }

}; // TextureView.SurfaceTextureListener


/**
 * SurfaceHolder.Callback
 */ 
    private final SurfaceHolder.Callback mSurfaceViewListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            log_d( "surfaceCreated" );
            isSurfaceViewAvailable = true;
            startIfReady();
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            log_d( " surfaceDestroyed" );
            isSurfaceViewAvailable = false;
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // log_d( "surfaceChanged" );
            // nop
    }

}; // SurfaceHolder.Callback


} // class CameraSourcePreview
