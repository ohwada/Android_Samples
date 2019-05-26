/**
 * Vision Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision2;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;


import jp.ohwada.android.vision2.R;
import jp.ohwada.android.vision2.util.Camera2Source;

import jp.ohwada.android.vision2.ui.GraphicOverlay;


/**
 * Graphic instance for rendering face position, orientation
 * and landmarks within an associated graphic overlay view.
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class FaceGraphic extends GraphicOverlay.Graphic {


/**
 * ScaleFactor
 * The image for detect The Faceace was reduced to quarter
 */
    private final static  int SCALE_FACTOR = Camera2Source.SCALE_FACTOR;


/**
 * Class Instancs that has Parameters of detected face
 */
    private volatile Face mFace;

/**
 * Identifier of detected face
 * not use
 */
    private int faceId;

/**
 * Bitmap Image of markers to display on landmark
 */
    private Bitmap mMarker;


/**
 * Landmark Positions
 */
     private PointF mFaceCenterPos;
     private PointF mNoseBasePos;
     private PointF mLeftEyePos;
     private PointF mRightEyePos;
     private PointF mLeftMouthCornerPos;
     private PointF mRightMouthCornerPos;
     private PointF mMouthBottomPos;
     private PointF mLeftEarPos;
   private PointF mRightEarPos;
     private PointF mLeftEarTipPos;
     private PointF mRightEarTipPos;
     private PointF mLeftCheekPos;
     private PointF mRightCheekPos;


/**
 * constractor
 */
    public FaceGraphic(GraphicOverlay overlay, Context context) {
        super(overlay);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inScaled = false;
        Resources resources = context.getResources();
        mMarker = BitmapFactory.decodeResource(resources, R.drawable.marker, opt);
    }


 /**
  * setId
  */
    public void setId(int id) {
        faceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

 /**
  * goneFace
  */
    public void goneFace() {
        mFace = null;
    }


 /**
  * draw
  */
    @Override
    public void draw(Canvas canvas) {
            clearLandmarks();
            if(mFace == null) {
                // clear canvas
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                return;
            }
            setLandmarks(mFace);
            drawLandmarks(canvas);
    } // draw


 /**
  * clearLandmarks
  */
private void clearLandmarks() {
    mFaceCenterPos = null;
     mNoseBasePos = null;
    mLeftEyePos = null;
    mRightEyePos = null;
    mMouthBottomPos = null;
    mLeftMouthCornerPos = null;
    mRightMouthCornerPos = null;
    mLeftEarPos = null;
    mRightEarPos = null;
    mLeftEarTipPos = null;
    mRightEarTipPos = null;
    mLeftCheekPos = null;
    mRightCheekPos = null;
} // clearLandmarks


 /**
  * clearLandmarks
  */
private void setLandmarks(Face face) {
            if(face == null) return;
        PointF position = face.getPosition();
        float pos_x = position.x;
        float pos_y = position.y;
        float faceWidth = face.getWidth() * SCALE_FACTOR;
        float faceHeight = face.getHeight() * SCALE_FACTOR;
        float center_x = pos_x + faceWidth/2;
        float center_y = pos_y + faceHeight/2;
        mFaceCenterPos = new PointF( translateX(center_x), translateY(center_y) );

        for(Landmark landmark : face.getLandmarks()) {
            switch (landmark.getType()) {
                case Landmark.LEFT_EYE:
                    mLeftEyePos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.RIGHT_EYE:
                    mRightEyePos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.NOSE_BASE:
                    mNoseBasePos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.LEFT_MOUTH:
                    mLeftMouthCornerPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.RIGHT_MOUTH:
                    mRightMouthCornerPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.BOTTOM_MOUTH:
                    mMouthBottomPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.LEFT_EAR:
                    mLeftEarPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.RIGHT_EAR:
                    mRightEarPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.LEFT_EAR_TIP:
                    mLeftEarTipPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.RIGHT_EAR_TIP:
                    mRightEarTipPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.LEFT_CHEEK:
                    mLeftCheekPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
                case Landmark.RIGHT_CHEEK:
                    mRightCheekPos = new PointF(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y));
                    break;
            } // switch
    } // for
} // setLandmarks


 /**
  * drawLandmarks
  */
private void drawLandmarks(Canvas  canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(4);
        if(mFaceCenterPos != null)
            canvas.drawBitmap(mMarker, mFaceCenterPos.x, mFaceCenterPos.y, null);
        if( mNoseBasePos != null)
            canvas.drawBitmap(mMarker,  mNoseBasePos.x,  mNoseBasePos.y, null);
        if(mLeftEyePos != null)
            canvas.drawBitmap(mMarker, mLeftEyePos.x, mLeftEyePos.y, null);
        if(mRightEyePos != null)
            canvas.drawBitmap(mMarker, mRightEyePos.x, mRightEyePos.y, null);
        if(mMouthBottomPos != null)
            canvas.drawBitmap(mMarker, mMouthBottomPos.x, mMouthBottomPos.y, null);
        if(mLeftMouthCornerPos != null)
            canvas.drawBitmap(mMarker, mLeftMouthCornerPos.x, mLeftMouthCornerPos.y, null);
        if(mRightMouthCornerPos != null)
            canvas.drawBitmap(mMarker, mRightMouthCornerPos.x, mRightMouthCornerPos.y, null);
        if(mLeftEarPos != null)
            canvas.drawBitmap(mMarker, mLeftEarPos.x, mLeftEarPos.y, null);
        if(mRightEarPos != null)
            canvas.drawBitmap(mMarker, mRightEarPos.x, mRightEarPos.y, null);
        if(mLeftEarTipPos != null)
            canvas.drawBitmap(mMarker, mLeftEarTipPos.x, mLeftEarTipPos.y, null);
        if(mRightEarTipPos != null)
            canvas.drawBitmap(mMarker, mRightEarTipPos.x, mRightEarTipPos.y, null);
        if(mLeftCheekPos != null)
            canvas.drawBitmap(mMarker, mLeftCheekPos.x, mLeftCheekPos.y, null);
        if(mRightCheekPos != null)
            canvas.drawBitmap(mMarker, mRightCheekPos.x, mRightCheekPos.y, null);
} // drawLandmarks


} // class FaceGraphic