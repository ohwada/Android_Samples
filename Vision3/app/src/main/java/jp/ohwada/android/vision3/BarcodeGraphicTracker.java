/**
 * Vision Sample
 * Barcode Detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision3;


import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;


import jp.ohwada.android.vision3.ui.CameraSourcePreview;
import jp.ohwada.android.vision3.ui.GraphicOverlay;



/**
 * Generic tracker which is used for tracking or reading a Barcode 
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader
 */
public class BarcodeGraphicTracker extends Tracker<Barcode> {
    // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "BarcodeGraphicTracker";

    private GraphicOverlay mGraphicOverlay;
    private BarcodeGraphic mBarcodeGraphic;

    private BarcodeUpdateListener mBarcodeUpdateListener;


/**
 * Consume the item instance detected from an Activity or Fragment level by implementing the
  * BarcodeUpdateListener interface method onBarcodeDetected.
 */
    public interface BarcodeUpdateListener {
        @UiThread
        void onBarcodeDetected(Barcode barcode);
    }

 /**
  * constractor
  */
    public BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> graphicOverlay, BarcodeGraphic barcodeGraphic,
                          Context context) {
        this.mGraphicOverlay = graphicOverlay;
        this.mBarcodeGraphic = barcodeGraphic;
            if (context instanceof BarcodeUpdateListener) {
                this.mBarcodeUpdateListener = (BarcodeUpdateListener) context;
            } else {
                throw new RuntimeException("Hosting activity must implement BarcodeUpdateListener");
            }
    }

 /**
  * Start tracking the detected item instance within the item overlay.
  */
    @Override
    public void onNewItem(int id, Barcode item) {
            log_d("onNewItem");
        mBarcodeGraphic.setId(id);
        mBarcodeUpdateListener.onBarcodeDetected(item);
    }

 /**
  * Update the position/characteristics of the item within the overlay.
  */
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
            log_d("onUpdate" );
        mGraphicOverlay.add(mBarcodeGraphic);
        mBarcodeGraphic.updateItem(item);
    }

 /**
  * Hide the graphic when the corresponding object was not detected.  This can happen for
  * intermediate frames temporarily, for example if the object was momentarily blocked from
  * view.
  */
    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
            log_d("onMissing");
        mGraphicOverlay.remove(mBarcodeGraphic);
    }

/**
   * Called when the item is assumed to be gone for good. Remove the graphic annotation from
  * the overlay.
   */
    @Override
    public void onDone() {
            log_d("onDone");
        mGraphicOverlay.remove(mBarcodeGraphic);
    }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class BarcodeGraphicTracker
