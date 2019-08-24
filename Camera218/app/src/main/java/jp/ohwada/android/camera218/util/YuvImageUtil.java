/**
 * Camera2 Sample
 * YuvImageUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;


/**
 * class YuvImageUtil
 */ 
public class YuvImageUtil {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "YuvImageUtil";


/**
 * for File Name
 */ 
    private static final String FILE_PREFIX_YUV = "yuv_";
    private static final String FILE_PREFIX_LIST = "yuv_list_";
    private static final String FILE_EXT_JPG =  FileUtil.FILE_EXT_JPG;


/**
 * for JPEG file
 */ 
    private static final int JPEG_QUALITY = 100;


/**
 * Context
 */ 
    private Context mContext;


/**
 * constractor
 */ 
public  YuvImageUtil(Context context) {
    mContext = context;
}


/**
  * saveImageAsJpeg
 */
public static boolean  saveImageAsJpeg(Image image, int jpegOrientation, File file) {

        log_d("saveImageAsJpeg");
        int format = image.getFormat();
        if(format != ImageFormat.YUV_420_888) {
                // nop, if not YUV format
                return false;
        }

        YuvImage yuvImsge = convYuv420ToYuvImage(image);
        return saveYuvImageAsJpeg(yuvImsge, jpegOrientation, file);
}


/**
  * saveImageBurst
 */
public File  saveImageBurst(List<YuvImage> list, int jpegOrientation, boolean use_storage) {

        log_d("saveImageBurst");
        String dateTime = FileUtil.getStringDateTime( new Date() );
        int size = list.size();

    File file = null;
    for(int i=0; i < size; i++ ) {
            YuvImage image = list.get(i);
            file = getBurstLIstOutputFile(dateTime, i, use_storage );
            saveYuvImageAsJpeg(image, jpegOrientation, file);
            if(use_storage ) {
                MediaScanner.scanFile(mContext , file);
            }
        } // for

        return file;
}


/**
  * saveYuvImageAsJpeg
 */
private static boolean  saveYuvImageAsJpeg(YuvImage image, int jpegOrientation, File file) {

    log_d("saveYuvImageAsJpeg");
    if(image == null) {
        log_d("image == null");
        return false;
    }

    byte[] bytes =  convYuvImageToJpegBytes(image);
    if ((bytes == null)||(bytes.length == 0)) {
        log_d("NOT conv bytes");
        return false;
    }

    Bitmap bitmap_orig = BitmapUtil.createBitmapFromByteArray(bytes );
    if (bitmap_orig == null) {
        log_d("NOT create bitmap");
        return false;
    }

    Bitmap bitmap_rotate = BitmapUtil.rotateBitmap(bitmap_orig, jpegOrientation );
    if (bitmap_rotate == null) {
        log_d("NOT rotate bitmap");
        return false;
    }

    return BitmapUtil.saveBitmapAsJpeg(bitmap_rotate, file);

} // saveYuvImageAsJpeg


/**
  * convYuv420ToYuvImage
 */
public static YuvImage convYuv420ToYuvImage(Image image_yuv) {

    log_d("convYuv420ToYuvImage");
    if(image_yuv == null) {
                log_d("image_yuv == nul");
                return null;
    }

    int format = image_yuv.getFormat();
    if(format != ImageFormat.YUV_420_888) {
                // nop, if not YUV format
                log_d("NOT YUV_420_888");
                return null;
    }

    int width = image_yuv.getWidth();
    int height = image_yuv.getHeight();
    byte[] bytes_nv21 = convYuv420ToNv21Bytes(image_yuv);
    if ((bytes_nv21 == null)||(bytes_nv21.length==0)) {
                log_d("NOT conv Nv21Bytes");
                return null;
    }

    YuvImage yuvImsge =  convNV21BytesToYuvImage(bytes_nv21,  width, height);
    if (yuvImsge == null) {
                log_d("NOT conv YuvImage");
                return null;
    }

    return yuvImsge;

} // convYuv420ToYuvImage


/**
  * convNV21BytesToYuvImage
 */
private static YuvImage convNV21BytesToYuvImage(byte[] bytes,  int width, int height) {

    log_d("convNV21BytesToYuvImage");
    int[] STRIDES = null;
    YuvImage yuvImsge = new YuvImage(bytes, ImageFormat.NV21, width, height, STRIDES);
    return yuvImsge;
}


/**
  * convYuv420ToNv21Bytes
  * refrence : https://stackoverflow.com/questions/29653927/green-images-when-doing-a-jpeg-encoding-from-yuv-420-888-using-the-new-android-c
 */
public static byte[] convYuv420ToNv21Bytes(Image imgYUV420) {

    log_d("convYuv420ToNv21Bytes");
    ByteBuffer buffer0 = imgYUV420.getPlanes()[0].getBuffer();
    ByteBuffer buffer2 = imgYUV420.getPlanes()[2].getBuffer();
    int buffer0_size = buffer0.remaining();
    int buffer2_size = buffer2.remaining();
   byte[] bytes = new byte[buffer0_size + buffer2_size];

    buffer0.get(bytes, 0, buffer0_size);
    buffer2.get(bytes, buffer0_size, buffer2_size);
    return bytes;

} // convYuv420ToNv21Bytes



/**
  * convYuvImageToJpegBytes
 */
private static byte[] convYuvImageToJpegBytes(YuvImage image) {

    log_d("convYuvImageToJpegBytes");
    Rect rect = createJpegRect( image);
    ByteArrayOutputStream stream
        = new ByteArrayOutputStream();
    image.compressToJpeg(rect, JPEG_QUALITY, stream);
     byte[] bytes = stream.toByteArray();
    return bytes;

} // convYuvImageToJpegBytes


/**
  * createJpegRect
 */
private static Rect createJpegRect(YuvImage image) {

    int LEFT = 0;
    int TOP = 0;
    int w = image.getWidth();
    int h = image.getHeight();
    Rect rect = new Rect(LEFT, TOP, w, h);
    return rect;
}


/**
  * getBurstOutputFile
 */
public static File getBurstOutputFile(Context context, String dateTime, int index, boolean use_storage ) {

    String fileName = FileUtil.getBurstOutputFileName(FILE_PREFIX_YUV, FILE_EXT_JPG, dateTime, (index+1) );

    File file = FileUtil.getFileInDir(context, fileName, use_storage );
    return file;

} // getBurstOutputFile

/**
  * getBurstLIstOutputFile
 */
public File getBurstLIstOutputFile(String dateTime, int index, boolean use_storage ) {

    String fileName = FileUtil.getBurstOutputFileName(FILE_PREFIX_LIST, FILE_EXT_JPG, dateTime, (index+1) );

    File file = FileUtil.getFileInDir(mContext, fileName, use_storage );
    return file;

} // getBurstLIstOutputFile


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class YuvImageUtil

