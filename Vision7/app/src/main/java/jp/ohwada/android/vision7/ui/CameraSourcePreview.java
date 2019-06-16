/**
 * Vision Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision7.ui;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Size;

import java.io.IOException;

import jp.ohwada.android.vision7.util.Camera2Source;
import jp.ohwada.android.vision7.util.DisplayUtil ;


/**
 *  class CameraSourcePreview
 */
public class CameraSourcePreview extends ViewGroup {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "CameraSourcePreview";

     private final static  boolean USE_TEXTURE_VIEW = true;

        // dimen/linear_layout_control_height
        private final static  int LINEAR_LAYOUT_CONTROL_HEIGHT = 92;


    // view
    private AutoFitTextureView mAutoFitTextureView;
    private SurfaceView mSurfaceView;
    private GraphicOverlay mOverlay;

    private boolean isTextureViewAvailable = false;
    private boolean isSurfaceViewAvailable = false;


    private boolean isStartRequested;


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
    if(USE_TEXTURE_VIEW) {
        mAutoFitTextureView = new  AutoFitTextureView(context);
        addView(mAutoFitTextureView);
        mAutoFitTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    } else {
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
        mSurfaceView.getHolder().addCallback(mSurfaceViewListener);
    }
} // initPreview


    /**
     * start
     */
    public void start(Camera2Source camera2Source, GraphicOverlay overlay) {
        mOverlay = overlay;
        startCamera(camera2Source);
    } // start


    /**
     * start
     */
    public void start(Camera2Source camera2Source) {
        startCamera(camera2Source);
    } // start


/**
  * startCamera
  */
    private void startCamera(Camera2Source camera2Source) {
        log_d("startCamera");
        if(camera2Source == null) {
            log_d("camera2Source == null");
            return;
        }
           mCamera2Source = camera2Source;
            isStartRequested = true;
            startIfReady();
    } // startCamera


/**
 * stop
 */ 
    public void stop() {
        log_d("stop");
        isStartRequested = false;
            if(mCamera2Source != null) {
                mCamera2Source.stop();
            }
} // stop


    /**
     * startIfReady
     */
    private void startIfReady() {
        log_d("startIfReady");
        if (!isStartRequested) {
        log_d("not StartRequested");
            return;
        }
                if( isTextureViewAvailable ) {
                    mCamera2Source.start(mAutoFitTextureView);
                } else if( isSurfaceViewAvailable ) {
                    mCamera2Source.start(mSurfaceView);
                } else {
                     log_d("View not Available");
                }
                setCameraInfo();
 } // startIfReady


    /**
     * setCameraInfo
     */
private void setCameraInfo() {
                if (mOverlay == null) return;
                int facing = mCamera2Source.getCameraFacing();
                boolean isSwapped = mCamera2Source.isSwappedDimensions();
                Size size = mCamera2Source.getImagePreviewSize();
                if(size == null) return;
                    int imageWidth = size.getWidth();
                    int imageHeight = size.getHeight();

                    int previewWidth = imageWidth;
                    int previewHeight = imageHeight ;

                    if(isSwapped) {
                        previewWidth = imageHeight ;
                        previewHeight = imageWidth ;
                    }

                    mOverlay.bringToFront();
                    mOverlay.setCameraInfo(previewWidth, previewHeight, facing);
                    mOverlay.clear();

 } // setCameraInfo


/**
 *  onLayout
 */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int layoutWidth = right - left;
        int layoutHeight = bottom - top;
        int childWidth = layoutWidth;
        int childHeight = layoutHeight - DisplayUtil.dpToPx(LINEAR_LAYOUT_CONTROL_HEIGHT);

        for (int i = 0; i < getChildCount(); i++){
            getChildAt(i).layout(0, 0, childWidth, childHeight);
        }
} // onLayout


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

    /**
     * SurfaceHolder.Callback
     */
    private final SurfaceHolder.Callback mSurfaceViewListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            log_d("surfaceCreated");
            isSurfaceViewAvailable = true;
            ///mOverlay.bringToFront();
            startIfReady();
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            log_d("surfaceDestroyed");
            isSurfaceViewAvailable = false;
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            log_d("surfaceChanged");
        }
}; // SurfaceHolder.Callback


    /**
     * TextureView.SurfaceTextureListener
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            log_d("onSurfaceTextureAvailable");
            isTextureViewAvailable = true;
            //mOverlay.bringToFront();
            startIfReady();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            log_d("onSurfaceTextureSizeChanged");
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
        }
    }; // TextureView.SurfaceTextureListener


} // class CameraSourcePreview
