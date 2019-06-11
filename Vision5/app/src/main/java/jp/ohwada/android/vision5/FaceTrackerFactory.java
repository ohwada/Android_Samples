/**
 * Vision Sample
 * FaceTrackerFactory
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision5;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;


import jp.ohwada.android.vision5.ui.GraphicOverlay;


/**
 * class FaceTrackerFactory
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/FaceTracker
 */ 
public class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

 /**
  *  basement class of graphic overlay 
  */
    private GraphicOverlay mGraphicOverlay;


/**
 * constractor
 */
public FaceTrackerFactory(GraphicOverlay graphicOverlay) {
    mGraphicOverlay = graphicOverlay;
    //mFaceGraphic = faceGraphic;
}


/**
 * MultiProcessor.Factory
 * create
 */
        @Override
        public Tracker<Face> create(Face face) {
            FaceTracker tracker = new FaceTracker(mGraphicOverlay);
            return tracker;
        }

} // class FaceTrackerFactory
