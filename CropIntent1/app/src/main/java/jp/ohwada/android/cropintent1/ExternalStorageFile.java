/**
 * Crop Intent Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.cropintent1;



import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;


/**
 * class ExternalStorageFile
 */
public class ExternalStorageFile {

    // debug
    private final static String TAG = "ExternalStorageFile";


    private static final String DIR_NAME =  "crop";

    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private static final String FILE_PREFIX = "scv";


/**
  * constractor 
 */	    
public ExternalStorageFile() {
        // nop
} 



/**
 * make Directory in ExternalStoragePublicPictures
 */ 
public static boolean mkDirInPictures() {

    File dir = getDirInPictures();
    if (dir.exists()) {
        log_d("mkDirInPictures: directory already exists: " +  dir.toString());
        return false;
    }

    return dir.mkdir();
}


/**
 * get Directory in ExternalStoragePublicPictures
 */ 
public static File getDirInPictures() {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File dir = new File(root, DIR_NAME);
        return dir;
}


/**
 * get Directory of ExternalStoragePublicPictures
 */ 
public static File getPicturesDir() {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    Log.d( TAG, msg );
}


} // class ExternalStorageFile