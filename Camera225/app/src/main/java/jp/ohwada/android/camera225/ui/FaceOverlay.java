/**
 * Camera2 Sample
 * Face Detection and overlay image
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.camera225.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;


import android.hardware.camera2.params.Face;


/**
 * class OverlayView
 * draw bitmap over detected face
 * overlay on camera preview
 */
public class FaceOverlay extends GraphicOverlay.Graphic {

	// debug
	private final static boolean D = true;
	private final static String TAG = "Camera2";
	private final static String TAG_SUB = "FaceOverlay";

	private final static boolean USE_BITMAP = true;

    // face detect
    private final static int SCORE_THRESHOLD = Face.SCORE_MAX / 2;

	// paint
	private final static int BOUNDS_STROKE_WIDTH = 5;


        private GraphicOverlay mOverlay;

    private Rect mSensorArraySize;

	private Rect mBounds;

	private Paint mPaintBounds;

    private int mSensorOrientation = 0;

    private int mDisplayRotation = 0;

    private boolean isCameraFront = false;

    private Face[] mFaces;

    private Bitmap mBitmap;


/**
 * constractor
 */
	public FaceOverlay(GraphicOverlay overlay) {
        super(overlay);
        mOverlay = overlay;
		initOverlay();
	} // ScoreOverlay


/**
 *  initOverlay
 */
private void initOverlay() {
	mPaintBounds = new Paint();
      mPaintBounds.setColor(Color.GREEN);
      mPaintBounds.setStyle(Paint.Style.STROKE);
      mPaintBounds.setStrokeWidth(BOUNDS_STROKE_WIDTH);
}


	/**
	 * draw
	 */
@Override
public void draw(Canvas canvas) {
	if(mFaces == null) return;
    int len = mFaces.length;
    for(int i=0; i<len; i++) {
        Face face = mFaces[i];
        drawFace(canvas, face);
    }

} // draw


 /**
  * drawFace
  */
private void drawFace(Canvas canvas, Face face) {
    //log_d("drawFace");
    int score = face.getScore();
    Rect bounds = face.getBounds() ;
    if(score < SCORE_THRESHOLD) return;
    if(bounds == null) return;
	drawBounds(canvas, bounds);
} // drawFace


 /**
  * drawBounds
  */
private void drawBounds(Canvas canvas, Rect bounds) {
        //log_d("drawBounds");
        Rect rect = convBounds(canvas, bounds);
    if (USE_BITMAP &&(mBitmap != null)) {
        canvas.drawBitmap( mBitmap, null, rect, null );
    } else {
		canvas.drawRect(rect, mPaintBounds);
    }
} // drawBounds


/**
 * setSensorOrientation
  */
    public void setSensorOrientation(int orientation) {
        log_d("setSensorOrientation: " +  orientation);
        mSensorOrientation = orientation;
    }


    /**
     * setDisplayRotation
     */
    public void setDisplayRotation(int rotation) {
        log_d("setDisplayRotation: " +  rotation);
        mDisplayRotation = rotation;
    }


/**
 * setSensorArraySize
 */
public void setSensorArraySize(Rect rect) {
    mSensorArraySize = rect;
}


/**
 * setCameraFacingFront
 */
public void setCameraFacingFront(boolean isFront) {
    isCameraFront = isFront;
}


/**
 * setBitmap
 */
public void setBitmap(Bitmap bitmap) {
    mBitmap = bitmap;
}


/**
 * setFace
 */
public void setFaces(Face[] faces) {
    mFaces = faces;
    postInvalidate();
}


/**
 * convert rectangule xy coordinates of sensor to rectangule xy coordinates of view
 */ 
private Rect convBounds(Canvas canvas, Rect r) {
        int viewWidth = canvas.getWidth();
        int viewHeight = canvas.getHeight();
        int sensorWidth = viewWidth;
        int sensorHeight = viewHeight;
        if(  mSensorArraySize != null ) {
            sensorWidth = mSensorArraySize.width();
            sensorHeight = mSensorArraySize.height();
        }

        Point pointLeftTop = convBoundsPoint(r.left,  r.top, sensorWidth, sensorHeight, viewWidth, viewHeight );
        Point pointRightBottom = convBoundsPoint(r.right,  r.bottom,  sensorWidth, sensorHeight, viewWidth, viewHeight );

        // adjust the end point (right bottom) larger than the start point (left top)
        int p_left = pointLeftTop.x;
        int p_right = pointRightBottom.x;
        int p_top = pointLeftTop.y;
        int p_bottom = pointRightBottom.y;
        int left = Math.min(p_left, p_right);
        int right = Math.max(p_left, p_right);
        int top = Math.min(p_top, p_bottom);
        int bottom = Math.max(p_top, p_bottom);
        Rect rect = new Rect(left, top, right, bottom);
        return rect;
}


/**
 * convert xy coordinates of sensor to xy coordinates of view
 */ 
private Point convBoundsPoint(int x, int y, int sensorWidth, int sensorHeight, int viewWidth, int viewHeight ) {
    float x_ratio = (float)x /  (float)sensorWidth;
    float y_ratio =  (float)y /  (float)sensorHeight;
    int new_x = (int)(x_ratio * viewWidth);
    int new_y  = (int)(y_ratio * viewHeight);

    if((mDisplayRotation == Surface.ROTATION_0)&&(mSensorOrientation == 90 )) {
        // Nexus5 back camera
        new_x = (int) ((1 - y_ratio) * viewWidth);
        new_y = (int) (x_ratio * viewHeight);
    } else if((mDisplayRotation == Surface.ROTATION_0)&&(mSensorOrientation == 270 )) {
    // Nexus5 front camera
        new_x = (int)((y_ratio) * viewWidth);
        new_y = (int)((1- x_ratio) * viewHeight);
    }
    if (isCameraFront) {
        // left and right are reversed, in the front camera
        new_x = viewWidth -  new_x;
    }
    Point point = new Point(new_x, new_y);
    return point;
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class OverlayView
