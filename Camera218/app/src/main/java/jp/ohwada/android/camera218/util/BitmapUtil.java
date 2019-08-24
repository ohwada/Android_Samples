/**
 * Camera2 Sample
 * BitmapUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;



/**
 * class BitmapUtil
 */ 
public class BitmapUtil {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "BitmapUtil";


/**
 * for JPEG file
 */ 
    private static final int JPEG_QUALITY = 100;


/**
 * createBitmapFromByteBuffer
 */
public static Bitmap createBitmapFromByteBuffer(ByteBuffer buffer ) {
    byte[] bytes = createByteArray(buffer);
    return createBitmapFromByteArray( bytes );
}


/**
 * createByteArray
 */
private  static byte[] createByteArray(ByteBuffer buffer) {

        int size = buffer.capacity();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
}

/**
 * createBitmapFromByteArray
 */
public static Bitmap createBitmapFromByteArray(byte[] bytes ) {

        log_d( "createBitmapFromByteArray" );
        int OFFSET = 0;
        int length = bytes.length;
        return BitmapFactory.decodeByteArray(
            bytes, OFFSET, length);

}


/**
 * resizeBitmap
 */ 
public Bitmap resizeBitmap(Bitmap source, int desiredSize) {

    boolean FILTER = true;

    int bitmap_width = source.getWidth();
    int bitmap_height = source.getHeight();

    int max = Math.max(bitmap_width, bitmap_height);
    double scale = (double)desiredSize/ (double)max;

    int scaled_width =  (int) ( (double)bitmap_width * scale );
    int scaled_height =  (int) ( (double)bitmap_height * scale );

    return Bitmap.createScaledBitmap(
        source, scaled_width, scaled_height, FILTER );
} 


/**
 * rotateBitmap
 */ 
public static Bitmap rotateBitmap(Bitmap source, float degrees  ) {

    log_d("rotateBitmap");
    int SRC_X = 0;
    int SRC_Y = 0;
    boolean FILTER = true;

    Matrix matrix = new Matrix();
    matrix.postRotate(degrees);  
    int width = source.getWidth();
    int height = source.getHeight();

    return Bitmap.createBitmap(
        source, SRC_X, SRC_Y, width, height, matrix, FILTER);
}


/**
 * saveBitmap with JPEG format
 */ 
public static boolean saveBitmapAsJpeg(Bitmap bitmap, File file) {
            log_d("saveBitmapAsJpeg");
            boolean is_error = false;
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
            } catch (Exception e) {
                e.printStackTrace();
                is_error = true;
            }

            try {
                    if (out != null) out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return !is_error;
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class BitmapUtil

