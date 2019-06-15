/**
 * Camera2 Sample
 * FileUtil 
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera213.util;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * class FileUtil
 */ 
public class FileUtil {


/**
 * directory name in DCIM
 */ 
    private static final String DIR_NAME = "Video";


/**
 * Format for Current DateTime
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";


/**
 * make directory in DCIM
 */ 
public static void mkDirInDCIM() {
            File dir = getDirInDCIM();
        if(!dir.exists() ){
            dir.mkdir();
        }
}


/**
 * get directory in DCIM
 */ 
private static File getDirInDCIM() {
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File dir = new File(root, DIR_NAME);
        return dir;
}


/**
 * getOutputFileInExternalFilesDir
 */ 
public  static File getOutputFileInExternalFilesDir(Context context, String prefix, String ext) {
            File dir = context.getExternalFilesDir(null);
            String filename = getOutputFileName(prefix, ext);
        File file = new File(dir, filename);
    return file;
} // getOutputFileInExternalFilesDir


/**
 * getOutputFileInDCIM
 */ 
public static File getOutputFileInDCIM(String prefix, String ext) {
            File dir =  getDirInDCIM();
            String filename = getOutputFileName(prefix, ext);
        File file = new File(dir, filename);
    return file;
} // getOutputFileInDCIM


/**
 * getOutputFileName
 */
public static String getOutputFileName(String prefix, String ext) {
            String  currentDateTime= getCurrentDateTime();
            String filename = prefix + currentDateTime + ext;
    return filename;
} // getOutputFileName


/**
  *getCurrentDateTime
 */
public static String getCurrentDateTime() {
   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    String currentDateTime =  sdf.format(new Date());
    return currentDateTime;
}

} // class FileUtil
