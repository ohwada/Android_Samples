/**
 * Cloud Vision Sample
 * BitmapUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision4.util;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * class BitmapUtil
 */
public class BitmapUtil {

    // debug
	private final static boolean D = true;
    private final static String TAG = "CloudVision";
    private final static String TAG_SUB = "BitmapUtil";


/**
 * Maximum size of image to upload
 */ 
    private static final int MAX_DIMENSION = 1200;


/**
 * Image Quality converting to JPEG
 */ 
    private final static int JPEG_QUALITY = 90;


/**
  * Context
 */	
	private Context mContext;


/**
  * ContentResolver
 */	
	private ContentResolver mContentResolver;


/**
  * constractor 
 */	 
public BitmapUtil( Context context ) {
		mContext = context;
        mContentResolver = context.getContentResolver();
}


/** 
 *  getBitmapFromMediaStore
 */
public Bitmap getBitmapFromMediaStore(Uri uri) {
        if (uri == null) {
            log_d("Image picker gave us a null image.");
            return null;
        }
        Bitmap bitmap = null;
        try {
               bitmap =
                                MediaStore.Images.Media.getBitmap( mContentResolver, uri);
        } catch (IOException e) {
                e.printStackTrace();
        }
        return bitmap;
}


/** 
 *  getScaledBitmap
 */
public Bitmap getScaledBitmap(Bitmap bitmap) {
    return scaleBitmapDown(bitmap, MAX_DIMENSION);
}


/** 
 *  scaleBitmapDown
 */
public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        Bitmap bitmap_scaled = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
        return bitmap_scaled;
    }



/**
 * convJpegByteArray
 * convert the bitmap to JPEG
 */ 
public static byte[] convJpegByteArray(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Convert the bitmap to a JPEG
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return imageBytes;
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class class BitmapUtil
