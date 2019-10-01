/**
 * Camera2 Sample
 * FileUtil 
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera221;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
  *  File prefix for output file
  */
    public static final String VIDEO_FILE_PREFIX = "video_";


/**
 * Format for Current DateTime
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";


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
 * getOutputFile
 */ 
public  static File getOutputFile(Context context, int width, int height, String ext) {

        File dir = context.getExternalFilesDir(null);
        String filename = getOutputFileName(width, height, VIDEO_FILE_PREFIX, ext);
        File file = new File(dir, filename);
        return file;
}


/**
 * getOutputFileName
 */
public static String getOutputFileName(int width, int height , String prefix, String ext) {
            String  currentDateTime= getCurrentDateTime();
            String filename = prefix + currentDateTime + "_" + width  + "x" + height + ext;
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
