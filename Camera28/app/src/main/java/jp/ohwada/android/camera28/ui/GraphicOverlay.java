/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera28.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 *  class GraphicOverlay
 * original : https://github.com/googlesamples/android-vision
  */
public class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "GraphicOverlay";

    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mFacing = CameraCharacteristics.LENS_FACING_BACK;
    private Set<T> mGraphics = new HashSet<>();


/**
 *  class Graphic
  */
    public static abstract class Graphic {
        private GraphicOverlay mOverlay;

/**
 *  constractor
  */
public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
}

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         * <ol>
         * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of
         * the supplied value from the preview scale to the view scale.</li>
         * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.</li>
         * </ol>
         *
         * @param canvas drawing canvas
         */
        public abstract void draw(Canvas canvas);

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateX(float x) {
            if (mOverlay.mFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateY(float y) {
            return scaleY(y);
        }

/**
 *  postInvalidate
  */
        public void postInvalidate() {
            mOverlay.postInvalidate();
        }

/**
 * invalidate
  */
        public void invalidate() {
            mOverlay.invalidate();
        }

    } // class Graphic

/**
 *  constractor
  */
    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

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
     * @return list of all active graphics.
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
        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        } //  synchronized
    } // onDraw

/**
 * write into logcat
 */ 
protected void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class GraphicOverlay
