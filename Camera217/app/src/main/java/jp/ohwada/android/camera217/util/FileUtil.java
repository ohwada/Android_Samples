/**
 * Camera2 Sample
 * FileUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217.util;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * class FileUtil
 */ 
public class FileUtil {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "FileUtil";


/**
 * File Name
 */ 
    public static final String FILE_PREFIX = "camera_";
    public static final String FILE_EXT_JPG =  ".jpg";
    public static final String FILE_EXT_DNG =  ".dng";


/**
 * Dir Name
 */ 
   public static final String DIR_NAME =  "Manual";


/**
 * DateTime Format for File Name
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";


/**
 * Char
 */ 
    private static final String UNDER_BAR = "_" ;


/**
 * make Directory in ExternalStoragePublicDCIM
 */ 
public static void mkDirInExternalStoragePublicDCIM() {
        File dir = getDirInExternalStoragePublicDCIM();
        if(!dir.exists()) {
            // make Directory if not exist
            dir.mkdir();
        }
} // mkDirInExternalStoragePublicDCIM

/**
  * getFileInDir
 */
public static File getFileInDir(Context context, String fileName, boolean use_storage ) {

    File file = null;
    if(use_storage) {
            file = getFileInExternalStoragePublicDCIM(fileName);
    } else {
            file = getFileInExternalFilesDir(context,  fileName);
    }
    return file;
}


/**
 * getFileInExternalStoragePublicDCIM
 */ 
public static File getFileInExternalStoragePublicDCIM(String fileName) {

    File dir = getDirInExternalStoragePublicDCIM();
    File file = new File(dir, fileName);
    return file;

} // getFileInExternalStoragePublicDCIM


/**
 * get Directory in ExternalStoragePublicDCIM
 */ 
private static File getDirInExternalStoragePublicDCIM() {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File dir = new File(root, DIR_NAME);
        return dir;
} // getDirInExternalStoragePublicDCIM





/**
 * getOutputFileInExternalFilesDir
 */ 
public static File getOutputFileInExternalFilesDir(Context context, String prefix, String ext) {

        String fileName = getOutputFileName(prefix, ext);
        File file = getFileInExternalFilesDir(context, fileName);
        return file;
} // getOutputFileInExternalFilesDir


/**
 * getFileInExternalFilesDir
 */ 
public static File getFileInExternalFilesDir(Context context, String fileName) {
        File dir = context.getExternalFilesDir(null);
        File file = new File(dir, fileName);
    return file;
} // getFileInExternalFilesDir


/**
 * getOutputFileName
 */
public static String getOutputFileName(String prefix, String ext) {
    Date date = new Date();
    return getOutputFileNameWithDate(prefix, ext, date);
} // getOutputFileName


/**
 * getOutputFileName
 */
public static String getOutputFileName(String prefix, String ext, Date date) {
    return getOutputFileNameWithDate(prefix, ext, date);
} // getOutputFileName


/**
 * getOutputFileNameWithDate
 */
private static String getOutputFileNameWithDate(String prefix, String ext, Date date) {

            String currentDateTime =  getStringDateTime(date);
            String filename = prefix + currentDateTime + ext;
    return filename;

} // getOutputFileNameWithDate


/**
 * getStringDateTime
 */ 
public static String getStringDateTime(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(date);
}


/**
 * scanFile
 * egister into Content Provider
 */ 
public static void scanFile(Context context, File file) {
        String[] paths = new String[]{ file.getPath() };
        MediaScannerConnection.scanFile(context, paths,
                /*mimeTypes*/null, mMediaScannerConnectionClient );
} // scanFile


/**
  * writeBytes
 */
public static boolean writeBytes(byte[] bytes, File file) {

    boolean is_error = false;
    FileOutputStream out = null;
    try {
        out = new FileOutputStream(file);
        out.write(bytes);
    } catch (IOException e) {
                is_error = true;
                e.printStackTrace();
    }

    try {
        if (out != null) out.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return ! is_error;

} // writeBytes


/**
  * getBurstOutputFileName
 */
public static String getBurstOutputFileName(String prefix, String ext, String dateTime, int index ) {
            String fileName = prefix + dateTime + UNDER_BAR + index + ext;
            return fileName;
} // getBurstOutputFileName


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * MediaScannerConnection.MediaScannerConnectionClient
 */ 
private static MediaScannerConnection.MediaScannerConnectionClient mMediaScannerConnectionClient =
new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        // Do nothing
                    }
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        String msg = "Scanned " + path + " -> uri=" + uri;
                        log_d(msg);
                    }
}; // MediaScannerConnectionClient


} // class FileUtil

