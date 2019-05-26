/**
 * Vision Sample
 * Face Detection using Camera2 API and Vision API
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision2;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;


import jp.ohwada.android.vision2.ui.GraphicOverlay;


/**
 * class FaceTrackerFactory
 * original : https://github.com/EzequielAdrianM/Camera2Vision

 */ 
public class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

 /**
  *  basement class of graphic overlay 
  */
    private GraphicOverlay mGraphicOverlay;


 /**
  *  custamize class of graphic overlay 
  */
    private FaceGraphic mFaceGraphic;


/**
 * constractor
 */
public FaceTrackerFactory(GraphicOverlay graphicOverlay, FaceGraphic faceGraphic) {
    mGraphicOverlay = graphicOverlay;
    mFaceGraphic = faceGraphic;
}


/**
 * MultiProcessor.Factory
 * create
 */
        @Override
        public Tracker<Face> create(Face face) {
            FaceTracker tracker = new FaceTracker(mGraphicOverlay, mFaceGraphic);
            return tracker;
        }

} // class FaceTrackerFactory
