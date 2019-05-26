/**
 * Vision Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision2;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import jp.ohwada.android.vision2.ui.GraphicOverlay;

/**
 * class FaceTracker
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */ 
public class FaceTracker extends Tracker<Face> {

     // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "FaceTracker";


 /**
  *  basement class of graphic overlay 
  */
        private GraphicOverlay mGraphicOverlay;


 /**
  *  custamize class of graphic overlay 
  */
        private FaceGraphic  mFaceGraphic;


/**
 * constractor
 */ 
public FaceTracker(GraphicOverlay graphicOverlay, FaceGraphic faceGraphic) {
            mGraphicOverlay = graphicOverlay;
            mFaceGraphic = faceGraphic;
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            log_d("onNewItem");
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            // log_d("onUpdate");
            mGraphicOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            log_d("onMissing");
            mFaceGraphic.goneFace();
            mGraphicOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            log_d("onDone");
            mFaceGraphic.goneFace();
            mGraphicOverlay.remove(mFaceGraphic);
        }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FaceTracker
