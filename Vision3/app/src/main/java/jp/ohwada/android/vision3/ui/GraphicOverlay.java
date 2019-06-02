/**
 * Vision Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision3.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jp.ohwada.android.vision3.util.Camera2Source;


/**
 * A view which renders a series of custom graphics 
 * to be overlayed on top of an associated preview
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader
 */
public class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {

    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;

    private int mFacing = Camera2Source.CAMERA_FACING_BACK;

    private Set<T> mGraphics = new HashSet<>();

/**
 * flag of resume or pause to draw
 */ 
    private boolean isDrawRunning = false;


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
    public void add(T graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }

/**
  * Removes a graphic from the overlay.
 */
    public void remove(T graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }

/**
   * Returns a copy (as a list) of the set of all active graphics.
   */
    public List<T> getGraphics() {
        synchronized (mLock) {
            return new Vector(mGraphics);
        }
    }

/**
  * Returns the horizontal scale factor.
  */
    public float getWidthScaleFactor() {
        return mWidthScaleFactor;
    }

/**
  * Returns the vertical scale factor.
  */
    public float getHeightScaleFactor() {
        return mHeightScaleFactor;
    }

/**
   * Sets the camera attributes for size and facing direction, which informs how to transform
  * image coordinates later.
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

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            } // for
        } // synchronized
    } // onDraw


/**
  * Base class for a custom graphics object to be rendered within   * the graphic overlay Subclass
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
  * Adjusts a horizontal value of the supplied value 
  * from the preview scale to the view scale.
 */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

/**
  * Adjusts a vertical value of the supplied value 
  * from the preview scale to the view scale.
 */
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

/**
 * Adjusts the x coordinate from the preview's coordinate system 
  * to the view coordinate system.
 */
        public float translateX(float x) {
            if (mOverlay.mFacing == Camera2Source.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

/**
 * Adjusts the y coordinate from the preview's coordinate          system 
* to the view coordinate system.
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
