/**
 * Vision Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision7.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import jp.ohwada.android.vision7.util.Camera2Source;


/**
 * A view which renders a series of custom graphics to be overlayed on top of an associated preview
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class GraphicOverlay extends View {


/**
 * Lock object for Graphics 
 */ 
    private final Object mLock = new Object();


/**
 * Preview Size
 */ 
    private int mPreviewWidth;
    private int mPreviewHeight;


/**
 * Scale Factor 
 */ 
    private float mWidthScaleFactor = 1.0f;
    private float mHeightScaleFactor = 1.0f;


/**
 * Camera Faceing
 */ 
    private int mFacing = Camera2Source.CAMERA_FACING_BACK;


/**
 * Graphic set for draw the overlay.
 */ 
    private Set<Graphic> mGraphics = new HashSet<>();

/**
 * flag of resume or pause to draw
 */ 
    private boolean isDrawRunning = false;


/**
 * constractor
 */ 
    public GraphicOverlay(Context context) {
        super(context);
    }

/**
 * constractor
 */ 
    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


/**
 * setDrawRuning
 */ 
public void setDrawRuning(boolean is_running) {
    isDrawRunning = is_running;
} // setDrawRuning


 /**
 * Removes all graphics from the overlay.
  */
    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }

 /**
   * Adds a graphic to the overlay.
  */
    public void add(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }

/**
   * Removes a graphic from the overlay.
   */
    public void remove(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }


/**
  * Sets the camera attributes for size and facing direction
  */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }


/**
  * Draws the overlay with its associated graphic objects.
 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // skip, if pause
        if(!isDrawRunning) return;

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            // draw Graphic set
            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            } // for
        } // synchronized
    } // onDraw


/**
  * Base class for a custom graphics object
 */
    public static abstract class Graphic {
        private GraphicOverlay mOverlay;


/**
  * constractor
  */
        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

/**
  * Draw the graphic on the supplied canvas.  
  */
        public abstract void draw(Canvas canvas);

/**
  * Adjusts a horizontal value 
  */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

/**
  * Adjusts a vertical value 
  */
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

/**
  * Adjusts the x coordinate 
  */
        public float translateX(float x) {
            if (mOverlay.mFacing == Camera2Source.CAMERA_FACING_FRONT) {
                // left and right are reversed, in the front camera
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

/**
  * Adjusts the y coordinate 
  */
        public float translateY(float y) {
            return scaleY(y);
        }

/**
  * postInvalidate
  */
        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
} // class Graphic


} // class GraphicOverlay
