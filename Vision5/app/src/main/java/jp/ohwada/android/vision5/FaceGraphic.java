/**
 * Vision Sample
 * FaceTracker
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision5;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import jp.ohwada.android.vision5.ui.GraphicOverlay;


/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated graphic overlay view.
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/FaceTracker
 */
class FaceGraphic extends GraphicOverlay.Graphic {

     // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "FaceGraphic";

    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;


 /**
 * Color for rectangle and infos on detected Faces
 */
    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };


 /**
 * index for COLOR_CHOICES
 * This variable must be static
 * make different value ​​and color for Faces
 */
    private static int mCurrentColorIndex = 0;


    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        log_d( "x=" + x + " y=" + y );

        String text_id = "id: " + mFaceId;
        canvas.drawText(text_id, (x + ID_X_OFFSET), (y + ID_Y_OFFSET), mIdPaint);
        log_d( text_id );

        String text_happiness = "happiness: " + String.format("%.2f", face.getIsSmilingProbability());
        canvas.drawText(text_happiness, (x - ID_X_OFFSET), (y - ID_Y_OFFSET), mIdPaint);
        log_d( text_happiness );

        String text_right_eye = "right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability());
        canvas.drawText(text_right_eye, (x + ID_X_OFFSET * 2), (y + ID_Y_OFFSET * 2), mIdPaint);
        log_d( text_right_eye );

        String text_left_eye = "left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability());
        canvas.drawText(text_left_eye, (x - ID_X_OFFSET*2), (y - ID_Y_OFFSET*2), mIdPaint);
        log_d( text_left_eye );

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRect(rect, mBoxPaint);
        log_d( rect.toString() );

    }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FaceGraphic
