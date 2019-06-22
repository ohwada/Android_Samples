/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera214.util;


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
   public static final String DIR_NAME =  "Raw";


/**
 * DateTime Format for File Name
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";


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
 * getOutputFileInExternalStoragePublicDCIM
 */ 
public static File getOutputFileInExternalStoragePublicDCIM(String prefix, String ext, Date date) {
    return getOutputFileInExternalStoragePublicDCIMWithDate(prefix, ext, date);
} // getOutputFileInExternalStoragePublicDCIM


/**
 * getOutputFileInExternalStoragePublicDCIMWithDate
 */ 
private static File getOutputFileInExternalStoragePublicDCIMWithDate(String prefix, String ext, Date date) {
        File dir = getDirInExternalStoragePublicDCIM();
        String filename = getOutputFileName(prefix, ext, date);
        File file = new File(dir, filename);
        return file;
} // getOutputFileInExternalStoragePublicDCIMWithDate


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
        File dir = context.getExternalFilesDir(null);
        String filename = getOutputFileName(prefix, ext);
        File file = new File(dir, filename);
    return file;
} // getOutputFileInExternalFilesDir


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
   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            String currentDateTime =  sdf.format(date);
            String filename = prefix + currentDateTime + ext;
    return filename;
} // getOutputFileNameWithDate


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

