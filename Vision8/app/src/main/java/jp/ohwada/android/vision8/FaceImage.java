/**
 * Vision Sample
 * FaceImage
 * 2019-02-01 K.OHWADA 
 */
package jp.ohwada.android.vision8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseArray;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;


/**
 * class FaceImage
 * FaceTrimming
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/photo-demo/app/src/main/java/com/google/android/gms/samples/vision/face
 */
public class FaceImage {

   	// dubug
	private final static boolean D = true; 
	private final static  String TAG = "Vision";
    private final static String TAG_SUB = "FaceImage";

 /**
  * for Paint
  */
	private final static int MARK_STROKE_WIDTH = 5;

	private final static float MARK_CIRCLE_RADIUS = 10f;

	private final static int LINE_STROKE_WIDTH = 5;


 /**
  * createLandmarkBitmap
  * draw landmarks on the Face
  */
public static Bitmap createLandmarkBitmap(Bitmap bitmap, SparseArray<Face> faces) {

    boolean ret = checkParam(bitmap, faces);
    if(!ret) return null;

    Bitmap image = createCanvasBitmap(bitmap);
    Canvas canvas = new Canvas(image);
    double scale = calcScale(canvas, bitmap);
    drawScaleBitmap(canvas, bitmap, scale);
     drawLandmarkOnFaces(canvas, faces, scale);
    return image;

} // createLandmarkBitmap


 /**
  * checkParam
  */
private static boolean checkParam(Bitmap bitmap, SparseArray<Face> faces) {
    if(bitmap == null) return false;
    if(faces == null) return false;
    if( faces.size() == 0 ) return false;
    return true;
} // checkParam


 /**
  * createRectangleBitmap
  * draw rectangle around the Face
  */
public static Bitmap createRectangleBitmap(Bitmap bitmap, SparseArray<Face> faces) {

    boolean ret = checkParam(bitmap, faces);
    if(!ret) return null;

    Bitmap image = createCanvasBitmap(bitmap);
    Canvas canvas = new Canvas(image);
    double scale = calcScale(canvas, bitmap);
    drawScaleBitmap(canvas, bitmap, scale);
    drawRectangleOnFaces(canvas, faces, scale);
    return image;

} // createRectangleBitmap


 /**
  * createTrimmingBitmap
  * cut out the Face
  */
public static Bitmap createTrimmingBitmap(Bitmap bitmap, SparseArray<Face> faces) {

    boolean ret = checkParam(bitmap, faces);
    if(!ret) return null;

    Face face = faces.get(0);

    Bitmap image = createCanvasBitmap(bitmap);
    Canvas canvas = new Canvas(image);
    double scale = calcScale(canvas,  bitmap);

    Rect rect = convTrimmingRect(bitmap, face);
    drawTrimmingBitmap(canvas, bitmap, rect);
    return image;

} // createTrimmingBitmap


/**
 * drawTrimmingBitmap
 */
private static void drawTrimmingBitmap(Canvas canvas, Bitmap bitmap, Rect src) {

        Rect dest = createBitmapRect(bitmap, src);

        Paint PAINT = null;
        canvas.drawBitmap(bitmap, src, dest, PAINT);

} // drawTrimmingBitmap


/**
 * createBitmapRect
 */ 
private static Rect createBitmapRect(Bitmap bitmap, Rect src) {

    int bitmap_width = bitmap.getWidth();
    int bitmap_height = bitmap.getHeight();
    int src_width = src.width();
    int src_height = src.height();

    float scale_width = (float)bitmap_width /  (float)src_width;
    float scale_height = (float)bitmap_height /  (float)src_height;
    float scale = Math.min(scale_width, scale_height);

    int LEFT = 0;
    int TOP = 0;
    int right = (int)(src_width * scale);
    int bottom = (int)(src_height * scale);
    Rect rect = new Rect(LEFT, TOP, right,bottom);
    return rect;
} // createBitmapRect


/**
 * convTrimmingRect
 * create new Rectangle that is one size larger than detected size 
 *  so that the face does not break
 */ 
private static Rect convTrimmingRect(Bitmap bitmap, Face face ) {

        int ZERO = 0;
        float SCALE = 1.1f;

        RectF rect = convFaceRectF(face);
        float	cx = rect.centerX();
        float	cy = rect.centerY();
        float	hw = SCALE * rect.width()/2;
        float	hh = SCALE * rect.height()/2;

        int left = (int)(cx - hw);
        int right = (int)(cx + hw);
        int top = (int)(cy - hh);
        int bottom = (int)(int)(cy + hh);
        if(left < ZERO ) {
                left = ZERO;
        }
        if(top < ZERO ) {
                top = ZERO;
        }

        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        if(right > bw ) {
                right = bw;
        }
        if(bottom > bh ) {
                bottom = bh;
        }

        Rect rect_trim = new Rect( left,  top, right, bottom);
        return rect_trim;
} // convTrimmingRect


/**
  * createCanvasBitmap
  */
private static Bitmap createCanvasBitmap(Bitmap bitmap) {

    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    Bitmap bitmap_canvas = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    return bitmap_canvas;

} // createCanvasBitmap


/**
 * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
  * positioning the facial landmark graphics.
  */
private static void drawScaleBitmap(Canvas canvas, Bitmap bitmap, double scale) {

    Rect rect = createBitmapRect(bitmap, scale);
    drawBitmap(canvas, bitmap, rect);

} // drawScaleBitmap


/**
 * drawBitmap
 */
private static void drawBitmap(Canvas canvas, Bitmap bitmap, Rect rect) {

        Rect SRC_RECT = null;
        Paint PAINT = null;
        canvas.drawBitmap(bitmap, SRC_RECT, rect, PAINT);

} // drawBitmap


/**
 * createBitmapRect
 */ 
private static Rect createBitmapRect(Bitmap bitmap, double scale) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int LEFT = 0;
        int TOP = 0;
        int right = (int)(width * scale);
        int bottom = (int)(height * scale);
        Rect rect = new Rect(LEFT, TOP, right, bottom);
        return rect;
} // createBitmapRect


/**
 * calcScale
 */ 
private static double calcScale(Canvas canvas, Bitmap bitmap) {

        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = bitmap.getWidth();
        double imageHeight = bitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
        return scale;
}


/**
   * Draws a small circle for each detected landmark, centered at the detected landmark position.
   * <p>
   *
   * Note that eye landmarks are defined to be the midpoint between the detected eye corner
  * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
  * pupil position.
  */
private static void  drawLandmarkOnFaces(Canvas canvas, SparseArray<Face> faces, double scale) {

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(MARK_STROKE_WIDTH);

        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                    drawLandmark(canvas,  landmark, scale, paint);
            } // for
        } // for

} // drawLandmark


/**
 * drawLandmark
 */ 
private static void drawLandmark(Canvas canvas,  Landmark landmark, double scale, Paint paint) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, MARK_CIRCLE_RADIUS, paint);
}


/**
 * drawRectangleOnFaces
 */ 
private static void drawRectangleOnFaces(Canvas canvas, SparseArray<Face> faces, double scale) {

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_STROKE_WIDTH);

        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            RectF rect = convScaleRectF(face, scale);
            canvas.drawRect(rect, paint);
        } // for

} // drawRectangleOnFaces


/**
 * convScaleRectF
 */ 
private static RectF convScaleRectF(Face face, double scale) {

    RectF rect = convFaceRectF(face);

    float left = (float)(rect.left * scale);
    float right = (float)(rect.right * scale);
    float top = (float)(rect.top * scale);
    float bottom = (float)(rect.bottom * scale);

        RectF rect_scale = new RectF( left,  top, right, bottom);
        return rect_scale;

} // convScaleRectF


/**
 * convFaceRectF
 * create new Rectangle from Face position and size
 */ 
private static RectF convFaceRectF(Face face) {

        PointF point = face.getPosition();
        float	width =  face.getWidth();
        float	height =  face.getHeight();
        float x = point.x;
        float y = point.y;
        float right = x + width;
        float bottom = y + height;
        RectF rect = new RectF( x,  y, right, bottom);
        return rect;
} // convRectF


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FaceBitmap

