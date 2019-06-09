/**
 * Vision Sample
 * OCR Reader
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision4;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import jp.ohwada.android.vision4.ui.GraphicOverlay;


/**
 * A very simple Processor which receives detected TextBlocks  
* and adds them to the overlay as OcrGraphics.
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/ocr-reader
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;


/**
 * constractor
 */
public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

/**
  * Called by the detector to deliver detection results.
  */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }
    }


/**
  * Frees the resources associated with this detection processor.
   */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

} // OcrDetectorProcesso
