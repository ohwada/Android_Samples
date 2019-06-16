/**
 * Vision Sample
 * BarcodeTrackerFactory
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.vision7;



import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;


import jp.ohwada.android.vision7.ui.GraphicOverlay;


/**
 * BarcodeTrackerFactory
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay mGraphicOverlay;

/**
 *  constractor
  */
    public BarcodeTrackerFactory(GraphicOverlay graphicOverlay) {
        mGraphicOverlay = graphicOverlay;
    }

/**
 *  create
  */
    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new GraphicTracker<>(mGraphicOverlay, graphic);
    }

} // BarcodeTrackerFactory
