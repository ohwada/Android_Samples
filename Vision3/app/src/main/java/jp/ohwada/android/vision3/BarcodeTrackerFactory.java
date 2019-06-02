/**
 * Vision Sample
 * Barcode Detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision3;


import android.content.Context;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import jp.ohwada.android.vision3.ui.GraphicOverlay;


/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader
 */
 public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private Context mContext;


/**
 * constractor
  */
    public BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> graphicOverlay,
                                 Context context) {
        this.mGraphicOverlay = graphicOverlay;
        this.mContext = context;
    }


/**
 * create
  */
    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new BarcodeGraphicTracker(mGraphicOverlay, graphic, mContext);
    }

} // class BarcodeTrackerFactory

