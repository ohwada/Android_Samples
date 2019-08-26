/**
 * Screen Capture Sample
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.screencapture2;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * class FileUtil
 * 
 */
public class FileUtil  {

        // debug
	private final static boolean D = true;
    private final static String TAG = "ScreenCapture";
    private final static String TAG_SUB = "FileUtil";


/**
 * File Name
 */ 
    private static final String FILE_PREFIX = "screenshot_";
    private static final String FILE_EXT_JPG =  ".jpg";

/**
 * DateTime Format for File Name
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";


/**
 * for JPEG file
 */ 
    private static final int JPEG_QUALITY = 100;



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
public FileUtil(Context context) {
        mContext = context;
        DisplayMetrics  metrics = DisplayUtil.getDisplayMetrics(context);
        mDisplayWidth = metrics.widthPixels;
        mDisplayHeight = metrics.heightPixels;
}


/**
 * saveScreenshot
 */
public  boolean saveScreenshot(Bitmap bitmap) {
        log_d("saveScreenshot");
        File file = getOutputFileInExternalFilesDir(mContext, FILE_PREFIX, FILE_EXT_JPG);
        return saveBitmapAsJpeg(bitmap, file);
}


/**
 * getOutputFileInExternalFilesDir
 */ 
private File getOutputFileInExternalFilesDir(Context context, String prefix, String ext) {

        String fileName = getOutputFileName(prefix, ext);
        File dir = context.getExternalFilesDir(null);
        File file = new File(dir, fileName);
        return file;
} // getOutputFileInExternalFilesDir


/**
 * getOutputFileName
 */
private static String getOutputFileName(String prefix, String ext) {
            String currentDateTime =  getStringDateTime( new Date() );
            String filename = prefix + currentDateTime + ext;
    return filename;

} // getOutputFileName


/**
 * getStringDateTime
 */ 
public static String getStringDateTime(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(date);
}


/**
 * saveBitmap with JPEG format
 */ 
private boolean saveBitmapAsJpeg(Bitmap bitmap, File file) {
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
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FileUtil
