/**
 * Cloud Vision Sample
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision4;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;


import com.google.api.services.vision.v1.model.Landmark;
import com.google.api.services.vision.v1.model.Position;

import java.util.ArrayList;
import java.util.List;


/**
 * class FaceImageUtil
 */
public class FaceImageUtil {

   	// dubug
	private final static boolean D = true; 
	private final static  String TAG = "Vision";
    private final static String TAG_SUB = "FaceImageUtil";


 /**
  * for Paint
  */
	private final static int MARK_STROKE_WIDTH = 5;

	private final static float MARK_CIRCLE_RADIUS = 10f;

	private final static int LINE_STROKE_WIDTH = 10;

/**
 * 
The number of vertices of the rectangle
 */ 
    private final static int  NUM_VERTICES = 4;


 /**
  * reateBoundingBitmap
  */
public static Bitmap createBoundingBitmap(Bitmap bitmap, List<Point> list) {

    log_d("createBoundingBitmap");
    if(list == null) {
        log_d("List<Point>: null");
        return null;
    }

    int size = list.size();
    if(size < NUM_VERTICES) {
        log_d("List<Point>: size = " + size);
        return null;
    }

    Bitmap image = createCanvasBitmap(bitmap);
   Canvas canvas = new Canvas(image);
    double scale = drawBitmap(canvas, bitmap);
    drawBounding(canvas, list, scale);
    return image;

} // createBoundingBitmap


 /**
  * createLandmarkBitmap
  */
public static Bitmap createLandmarkBitmap(Bitmap bitmap, List<Landmark> list) {
    log_d("createLandmarkBitmap");
    Bitmap image = createCanvasBitmap(bitmap);
   Canvas canvas = new Canvas(image);
    double scale = drawBitmap(canvas, bitmap);
    drawLandmark(canvas, list, scale);
    return image;
} //createLandmarkBitmap



/**
  * createCanvasBitmap
  */
private static Bitmap createCanvasBitmap(Bitmap bitmap_orig) {

    int width = bitmap_orig.getWidth();
    int height = bitmap_orig.getHeight();
    Bitmap bitmap_canvas = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    return bitmap_canvas;

} // createCanvasBitmap


/**
 * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
  * positioning the facial landmark graphics.
  */
private static double drawBitmap(Canvas canvas,  Bitmap bitmap) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = bitmap.getWidth();
        double imageHeight = bitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(bitmap, null, destBounds, null);
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
private static void drawLandmark(Canvas canvas, List<Landmark> list, double scale) {

        log_d("drawLandmark");
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(MARK_STROKE_WIDTH);

        for (Landmark mark : list) {
                    Position position = mark.getPosition();
                    int cx = (int) (position.getX() * scale);
                    int cy = (int) (position.getY() * scale);
                    canvas.drawCircle(cx, cy, MARK_CIRCLE_RADIUS, paint);
        } // for

} // drawLandmark


/**
 * drawBounding
 */
private static void drawBounding(Canvas canvas, List<Point> list, double scale ) {

        log_d("drawBounding");
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_STROKE_WIDTH);

        Path path = convPath( list, scale );
        canvas.drawPath(path, paint);

} // drawBounding


/**
 * convPath
 */ 
private static Path convPath( List<Point> list, double scale ) {

        Path path = new Path();
        int size = list.size();

        float x0 = 0;
        float y0 = 0;
        for (int i=0; i<size; i++) {
            Point point = list.get(i);
            float x = (float)(point.x * scale);
            float y = (float)(point.y * scale);
            if(i == 0) {
                    x0 = x;
                    y0 = y;
                    // move to start point
                    path.moveTo(x, y);
            } else {
                    // draw a line to next point
                    path.lineTo(x, y);
            }
        } // for

        // to close the polygon
        // draw a line end point to start point
        path.lineTo(x0, y0);

    return path;

}


/**
 * write into logcat
 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class FaceImageUtil

