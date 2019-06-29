/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera215.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

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

/**
 * Constant for File Name
 */ 
    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT_JPG = ".jpg";


/**
 * Date Format for File Name
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";


/**
 * JPEG Quality
 */ 
    private static final int JPEG_QUALITY = 100;

/**
 * Context
 */ 
    private Context mContext;


/**
 * constractor
 */ 
public  ImageUtil(Context context) {
    mContext = context;
}


/**
 * createOutputFileInExternalFilesDir
 */ 
public File createOutputFileInExternalFilesDir() {
File dir = mContext.getExternalFilesDir(null);
            String filename = createOutputFileName(FILE_PREFIX, FILE_EXT_JPG);
        File file = new File(dir, filename);
    return file;
} // createOutputFileInExternalFilesDir


/**
 * createOutputFileName
 */
public String createOutputFileName(String prefix, String ext) {
   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            String currentDateTime =  sdf.format(new Date());
            String filename = prefix + currentDateTime + ext;
    return filename;
} // createOutputFileName


/**
 * saveImageAsJpeg
 */ 
public boolean saveImageAsJpeg(Image image, File file) {
        boolean is_error = false;
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
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
        return ! is_error;
} // saveImageAsJpeg

} // class ImageUtil

