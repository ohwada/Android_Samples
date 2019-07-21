/**
 * Camera2 Sample
 * ImageUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;


/**
 * class ImageUtil
 * save Image with  JPEG format
 */ 
public class ImageUtil{

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";


/**
 * for File Name
 */ 
    private static final String FILE_PREFIX = FileUtil.FILE_PREFIX;
    private static final String FILE_EXT_JPG =  FileUtil.FILE_EXT_JPG;


/**
 * for JPEG file
 */ 
    private static final int JPEG_QUALITY = 100;


/**
 * for thumbnail
 */ 
    private static final int THUMBNAIL_SIZE = 640;



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
 * getOutputFile
 */ 
public File getOutputFile(Date date, boolean use_storage) {

    String fileName = FileUtil.getOutputFileName(FILE_PREFIX, FILE_EXT_JPG, date);
    File file = FileUtil.getFileInDir(mContext, fileName,  use_storage );
    return file;

}


/**
 * saveImage with JPEG format
 */ 
public boolean saveImageAsJpeg(Image image, File file, boolean use_storage) {

        Bitmap bitmap = createBitmap(image );

        boolean ret = saveBitmapAsJpeg(bitmap, file);

        if(use_storage) {
                FileUtil.scanFile(mContext, file);
        }
        return ret;
}


/**
 * saveBitmap with JPEG format
 */ 
public boolean saveBitmapAsJpeg(Bitmap bitmap, File file) {
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
 * createBitmap
 * create Bitmap from the Pixel Plane
 */ 
private Bitmap createBitmap(Image image) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            return bitmap;
} 


/**
 * resizeBitmap
 * resize to desired size from source Btmap
 */ 
private Bitmap resizeBitmap(Bitmap srcBitmap, int desiredSize) {

    int bitmap_width = srcBitmap.getWidth();
    int bitmap_height = srcBitmap.getHeight();

    int max = Math.max(bitmap_width, bitmap_height);
    double scale = (double)desiredSize/ (double)max;

    int scaled_width =  (int) ( (double)bitmap_width * scale );
    int scaled_height =  (int) ( (double)bitmap_height * scale );

    Bitmap resized_bitmap = Bitmap.createScaledBitmap(srcBitmap,
        scaled_width, scaled_height, true);

    return resized_bitmap;
} 


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class ImageUtil

