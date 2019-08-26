/**
 * Screen Capture Sample
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.screencapture2;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import android.util.Log;


import java.io.File;
import java.nio.ByteBuffer;



/**
 * class ImageUtil
 * original : https://github.com/TechBooster/AndroidSamples/tree/master/ScreenCapture
 */
public class ImageUtil  {

        // debug
	private final static boolean D = true;
    private final static String TAG = "ScreenCapture";
    private final static String TAG_SUB = "ImageUtil";


/**
 * for createImageReader
 */ 
    private static final int MAX_IMAGES = 2;


/**
 * Context
 */
private Context mContext;


/**
 * display size
 */
    private int mDisplayWidth;
    private int mDisplayHeight;


/**
 * constractor
 */
public ImageUtil(Context context) {
        mContext = context;
        DisplayMetrics  metrics = DisplayUtil.getDisplayMetrics(context);
        mDisplayWidth = metrics.widthPixels;
        mDisplayHeight = metrics.heightPixels;
}


/**
 * createImageReader
 */
public ImageReader createImageReader() {

        ImageReader imageReader = ImageReader.newInstance(mDisplayWidth, mDisplayHeight, ImageFormat.RGB_565, MAX_IMAGES);
        return imageReader;
}


/**
 * getScreenshot
 */
public  Bitmap getScreenshot(ImageReader imageReader) {

        log_d("getScreenshot");
        if(imageReader == null) {
            return null;
        }

// TODO : Exception occurs in the emulator
// https://teratail.com/questions/92469
            Image image = null;
        try {
            image = imageReader.acquireLatestImage();
        } catch (UnsupportedOperationException e) {
                e.printStackTrace();
        }
        if(image == null) {
            return null;
        }

        Bitmap bitmap = convToBitmap(image);
        image.close();

        return bitmap;
    }


/**
 * convToBitmap
 */
private  Bitmap convToBitmap(Image image) {

        log_d("convToBitmap");
    if(image == null) {
        return null;
    }

        int format = image.getFormat();
        if(format != ImageFormat.RGB_565) {
                // nop, if not RGB_565 format
                return null;
        }

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();

        int rowPadding = rowStride - pixelStride * mDisplayWidth;
        int width = mDisplayWidth + rowPadding / pixelStride;
        int height = mDisplayHeight;

        // createBitmap from buffer
        Bitmap bitmap = Bitmap.createBitmap(width , height, Bitmap.Config.RGB_565);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class ImageUtil
