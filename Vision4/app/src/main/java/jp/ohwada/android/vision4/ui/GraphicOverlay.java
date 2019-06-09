/**
 * Vision Sample
 * OCR Detection 
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision4.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import jp.ohwada.android.vision4.util.Camera2Source;


/**
 * A view which renders a series of custom graphics to be overlaid on top of an associated preview
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/ocr-reader
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
  * Returns the first graphic
  * ithat exists at the provided absolute screen coordinates.
  */
    public T getGraphicAtLocation(float rawX, float rawY) {
        synchronized (mLock) {
            //These coordinates will be offset by 
            // the relative screen position of this view.
            // Get the position of this View so the raw location can be offset relative to the view.
            int[] location = new int[2];
            this.getLocationOnScreen(location);
            for (T graphic : mGraphics) {
                if (graphic.contains(rawX - location[0], rawY - location[1])) {
                    return graphic;
                }
            }
            return null;
        }
    }


/**
  * Sets the camera attributes for size and facing direction, 
  * which informs how to transform image coordinates later.
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
  * Base class for a custom graphics object to be rendered within the graphic overlay.  
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
  * Returns true if the supplied coordinates are within this graphic.
  */
        public abstract boolean contains(float x, float y);


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
   * to the view coordinatesystem.
   */
        public float translateX(float x) {
            if (mOverlay.mFacing == Camera2Source.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }


/**
 * Adjusts the y coordinate from the preview's coordinate system 
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
