/**
 * Vision Sample
 * FaceTrackerFactory
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision7;



import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;



import jp.ohwada.android.vision7.ui.GraphicOverlay;

/**
 * class FaceTrackerFactory
 * Factory for creating a tracker and associated graphic to be associated with a new face.  The
 * multi-processor uses this factory to create face trackers as needed -- one for each individual.
 */
public class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

    private GraphicOverlay mGraphicOverlay;


/**
 *  constractor
  */
    public FaceTrackerFactory(GraphicOverlay graphicOverlay) {
        mGraphicOverlay = graphicOverlay;
    }

/**
 *  create
  */
    @Override
    public Tracker<Face> create(Face face) {
        FaceGraphic graphic = new FaceGraphic(mGraphicOverlay);
        return new GraphicTracker<>(mGraphicOverlay, graphic);
    } // create

} // class FaceTrackerFactory
