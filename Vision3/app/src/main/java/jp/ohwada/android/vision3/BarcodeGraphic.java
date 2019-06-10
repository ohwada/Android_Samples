/**
 * Vision Sample
 * Barcode Detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision3;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;


import com.google.android.gms.vision.barcode.Barcode;


import jp.ohwada.android.vision3.ui.CameraSourcePreview;
import jp.ohwada.android.vision3.ui.GraphicOverlay;


/**
 * Graphic instance for rendering barcode position, size, and ID within an associated graphic
 * overlay view.
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader
 */
public class BarcodeGraphic extends GraphicOverlay.Graphic {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "BarcodeGraphic";


 /**
 * Color for rectangle and infos on detected Barcodes
 */
    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN
    };


 /**
 * index for COLOR_CHOICES
 * This variable must be static
 * make different value ​​and color for Barcodes
 */
    private static int mCurrentColorIndex = 0;


    private int mId;

    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Barcode mBarcode;


/**
 * constractor
  */
public BarcodeGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);

        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(36.0f);
    }


/**
 * getId
  */
    public int getId() {
        return mId;
    }

/**
 * setId
  */
    public void setId(int id) {
        this.mId = id;
    }

/**
 * getBarcode
  */
    public Barcode getBarcode() {
        return mBarcode;
    }


    /**
     * Updates the barcode instance from the detection of the most recent frame.  
     */
    public void updateItem(Barcode barcode) {
            mBarcode = barcode;
            postInvalidate();
    }


    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode == null) {
            return;
        }

        // Draws the bounding box around the barcode.
        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, mRectPaint);

        // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
        canvas.drawText(barcode.rawValue, rect.left, rect.bottom, mTextPaint);
        log_d( "barcode= " + barcode.rawValue );
    }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class BarcodeGraphic
