/**
 * Camera2 Sample
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.camera224;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * class ImageUtil
 */ 
public class ImageUtil{

    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT = ".jpg";

    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";

    private static final int JPEG_QUALITY = 100;


    private Context mContext;


/**
 * constractor
 */ 
public  ImageUtil(Context context) {
    mContext = context;
}


/**
 * getOutputFileInExternalFilesDir
 */ 
public File getOutputFileInExternalFilesDir(String effect) {

File dir = mContext.getExternalFilesDir(null);
            String filename = getOutputFileName(FILE_PREFIX, FILE_EXT, effect);
        File file = new File(dir, filename);
    return file;
} // getOutputFileInExternalFilesDir


/**
 *getOutputFileName
 */
public String getOutputFileName(String prefix, String ext, String effect) {
    String post_fix = "";
    if(effect != null) {
        post_fix =  "_" + effect ;
    }

    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    String currentDateTime =  sdf.format(new Date());
    String filename = prefix + currentDateTime + post_fix + ext;
    return filename;
} // getOutputFileName


/**
 * saveImageAsJpeg
 */ 
public boolean saveImageAsJpeg(Image image, File file, Size size) {
        Bitmap bitmap_orig = convImageToBitmap(image);
        Bitmap bitmap_resize = resizeBitmap(bitmap_orig, size);
        return saveImageAsJpeg(bitmap_resize, file);

} // saveImageAsJpeg


/**
 * convImageToBitmap
 */ 
private  Bitmap convImageToBitmap(Image image) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            int capacity = buffer.capacity();
            byte[] bytes = new byte[capacity];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, capacity, null);
            return bitmap;
}


/**
 * resizeBitmap
 */ 
private  Bitmap  resizeBitmap(Bitmap source, Size size) {

    if (size == null) {
        return source;
    }

        int width = size.getWidth();
        int  height = size.getHeight();
        Bitmap bitmap = Bitmap.createScaledBitmap(
        source, width, height, true );
        return bitmap;
}


/**
 * saveImageAsJpeg
 */ 
private boolean saveImageAsJpeg(Bitmap bitmap, File file) {

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
                    // nop
            }
        return ! is_error;
} //  saveImageAsJpeg


} // class ImageUtil

