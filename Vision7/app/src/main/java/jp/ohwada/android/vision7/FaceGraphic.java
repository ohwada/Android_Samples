/**
 * Vision Sample
 * FaceGraphic
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision7;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;

import jp.ohwada.android.vision7.ui.GraphicOverlay;



/**
 * FaceGraphic
 * Graphic instance for rendering face position, size, and ID within an associated graphic overlay
 * view.
 */
public class FaceGraphic extends TrackedGraphic<Face> {

    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

 /**
 * Color for circle and infos on detected Faces
 */
    private static final int COLOR_CHOICES[] = {
        Color.MAGENTA,
        Color.RED,
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

/**
 *  constractor
  */
    public FaceGraphic(GraphicOverlay overlay) {
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

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position, size, and ID on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float cx = translateX(face.getPosition().x + face.getWidth() / 2);
        float cy = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(cx, cy, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + getId(), cx + ID_X_OFFSET, cy + ID_Y_OFFSET, mIdPaint);

        // Draws an oval around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = cx - xOffset;
        float top = cy - yOffset;
        float right = cx + xOffset;
        float bottom = cy + yOffset;
        canvas.drawOval(left, top, right, bottom, mBoxPaint);
    } // draw

} //  class FaceGraphic
