/**
 * Vision Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.vision1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;


/**
 * class FaceLandmark
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/photo-demo/app/src/main/java/com/google/android/gms/samples/vision/face
 */
public class FaceLandmark {

   		// dubug
	private final static boolean D = true; 
	private final static  String TAG = "Vision";
    private final static String TAG_SUB = "FaceLandmark";

	private final static int ANNO_STROKE_WIDTH = 5;

	private final static float ANNO_CIRCLE_RADIUS = 10f;

    private  Context mContext;
    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;


 /**
  * constractor
  */
 public FaceLandmark(Context context) {
       mContext = context;
} // FaceLandmark


 /**
  * transformLandmarkdetections.
  */
public Bitmap transformLandmark(Bitmap bitmap, SparseArray<Face> faces) {
    mBitmap = bitmap;
    mFaces = faces;
    Bitmap image = createCanvasBitmap();
   Canvas canvas = new Canvas(image);
   drawCanvas(canvas);
    return image;
} // transformLandmark

/**
  * createCanvasBitmap
  */
private Bitmap createCanvasBitmap() {

    if (mBitmap == null) return null;

    int width = mBitmap.getWidth();
    int height = mBitmap.getHeight();
    Bitmap bitmap_canvas = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    return bitmap_canvas;

} // createCanvasBitmap


/**
  * drawCanvas
  */
private void drawCanvas(Canvas canvas ) {

        if (mBitmap == null) return;
        if (mFaces == null ) return;

        double scale = drawBitmap(canvas);
        drawFaceAnnotations(canvas, scale);

} // drawCanvas

/**
 * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
  * positioning the facial landmark graphics.
  */
private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;

} // drawBitmap

/**
   * Draws a small circle for each detected landmark, centered at the detected landmark position.
   * <p>
   *
   * Note that eye landmarks are defined to be the midpoint between the detected eye corner
  * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
  * pupil position.
  */
private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ANNO_STROKE_WIDTH);



        for (int i = 0; i < mFaces.size(); ++i) {
            Face face = mFaces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, ANNO_CIRCLE_RADIUS, paint);
            } // for
        } // for

} // drawFaceAnnotations

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class FaceBitmap

