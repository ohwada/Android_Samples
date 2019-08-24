/**
 * Camera2 Sample
 * ImageUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.util.Log;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;


/**
 * class ImageUtil
 */ 
public class ImageUtil{

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "ImageUtil";


/**
 * for File Name
 */ 
    private static final String FILE_PREFIX = "jpeg_";
    private static final String FILE_EXT_JPG =  FileUtil.FILE_EXT_JPG;


/**
 * saveImage with JPEG format
 */ 
public static boolean saveImageAsJpeg(Image image, File file) {

        log_d("saveImageAsJpeg");
        int format = image.getFormat();
        if(format != ImageFormat.JPEG) {
                // nop, if not JPEG format
                return false;
        }
        Bitmap bitmap = createBitmap(image );
        return BitmapUtil.saveBitmapAsJpeg(bitmap, file);

}


/**
 * createBitmap
 * create Bitmap from the Pixel Plane
 */ 
private static Bitmap createBitmap(Image image) {

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();

        return BitmapUtil.createBitmapFromByteBuffer(buffer);
} 


/**
  * getBurstOutputFile
 */
public static File getBurstOutputFile(Context context, String dateTime, int index, boolean use_storage ) {

    String fileName = FileUtil.getBurstOutputFileName(FILE_PREFIX, FILE_EXT_JPG, dateTime, (index+1) );

    File file = FileUtil.getFileInDir(context, fileName, use_storage );
    return file;

} // getBurstOutputFile


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class ImageUtil

