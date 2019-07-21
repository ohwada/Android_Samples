/**
 * Camera2 Sample
 * CameraSourcePreview
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;

import jp.ohwada.android.camera217.util.Camera2Source ;
import jp.ohwada.android.camera217.util.DisplayUtil ;
import jp.ohwada.android.camera217.R ;


/**
 *  class CameraSourcePreview
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class CameraSourcePreview extends ViewGroup {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "CameraSourcePreview";


/**
 *  Flag whether use TextureView 
 */
        private final static  boolean USE_TEXTURE_VIEW = true;


/**
 *  AutoFitTextureView
 */
    private AutoFitTextureView mTextureView;


/**
 *  SurfaceView
 */
    private SurfaceView mSurfaceView;

/**
 *  Camera2Source
 */
    private Camera2Source mCamera2Source;


/**
 *  Resources
 */
        private Resources mResources;


/**
 *  Flag whether TextureView is available
 */
    private boolean isTextureViewAvailable = false;


/**
 *  Flag whether SurfaceView is available
 */
    private boolean isSurfaceViewAvailable = false;


/**
 *  Flag whether the start requested
 */
    private boolean isStartRequested = false;


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

    mResources = context.getResources();  

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
        int controlWidth = mResources.getDimensionPixelSize(R.dimen.control_camera_width);
        int controlHeight = mResources.getDimensionPixelSize(R.dimen.control_exposure_height);

        int layoutWidth = right - left;
        int layoutHeight = bottom - top;
        int childWidth = layoutWidth - controlWidth;
        int childHeight = layoutHeight - controlHeight;

        for (int i = 0; i < getChildCount(); ++i) {getChildAt(i).layout(0, 0, childWidth, childHeight);
        }

} // onLayout


/**
 * rotateTextureView
 * reference: https://moewe-net.com/android/camera2-rotate-textureview
 */
private void rotateTextureView() {

    int degrees = DisplayUtil.getViewRotationDegrees(getContext());
    if(degrees == 0) return;

    int viewWidth = mTextureView.getWidth();
    int viewHeight = mTextureView.getHeight();
    Matrix matrix = new Matrix();
    float px = (float)( viewWidth / 2 );
    float py = (float)( viewHeight / 2 );
    matrix.postRotate( degrees, px, py);

    mTextureView.setTransform(matrix);
}


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
            rotateTextureView();
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
