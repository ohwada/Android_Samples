/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera216;


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
 * Dir Name
 */ 
   public static final String DIR_NAME =  "Exif";


/**
 * DateTime Format for File Name
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";


/**
 * getDate
 */ 
public static Date getDate() {
        Date date = new Date();
        return date;
}


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
 * createOutputFileInExternalStoragePublicDCIM
 */ 
public static File createOutputFileInExternalStoragePublicDCIM(String prefix, String ext, Date date) {
    return createOutputFileInExternalStoragePublicDCIMWithDate(prefix, ext, date);
} // createOutputFileInExternalStoragePublicDCIM


/**
 * createOutputFileInExternalStoragePublicDCIMWithDate
 */ 
private static File createOutputFileInExternalStoragePublicDCIMWithDate(String prefix, String ext, Date date) {
        File dir = getDirInExternalStoragePublicDCIM();
        String filename = createOutputFileName(prefix, ext, date);
        File file = new File(dir, filename);
        return file;
} // createOutputFileInExternalStoragePublicDCIMWithDate


/**
 * get Directory in ExternalStoragePublicDCIM
 */ 
private static File getDirInExternalStoragePublicDCIM() {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File dir = new File(root, DIR_NAME);
        return dir;
} // getDirInExternalStoragePublicDCIM


/**
 * createOutputFileInExternalFilesDir
 */ 
public static File createOutputFileInExternalFilesDir(Context context, String prefix, String ext, Date date) {
        File dir = context.getExternalFilesDir(null);
        String filename = createOutputFileName(prefix, ext, date);
        File file = new File(dir, filename);
    return file;
} // createOutputFileInExternalFilesDir


/**
 * createOutputFileName
 */
public static String createOutputFileName(String prefix, String ext) {
    Date date = new Date();
    return createOutputFileNameWithDate(prefix, ext, date);
} // createOutputFileName


/**
 * createOutputFileName
 */
public static String createOutputFileName(String prefix, String ext, Date date) {
    return createOutputFileNameWithDate(prefix, ext, date);
} // createOutputFileName


/**
 * createOutputFileNameWithDate
 */
private static String createOutputFileNameWithDate(String prefix, String ext, Date date) {
   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            String currentDateTime =  sdf.format(date);
            String filename = prefix + currentDateTime + ext;
    return filename;
} // createOutputFileNameWithDate


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

