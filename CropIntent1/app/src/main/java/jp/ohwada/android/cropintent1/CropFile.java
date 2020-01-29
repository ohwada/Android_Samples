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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;


/**
 * class CropFile
 */
public class CropFile {

    // debug
    private final static String TAG = "CropFile";


/**
 *  Crop Intent
 */
    private final static String CROP_INTENT_NAME = "com.android.camera.action.CROP";
    private final static String CROP_INTENT_TYPE = "image/*";

    private final static int CROP_INTENT_ASPECT_X = 16;
    private final static int CROP_INTENT_ASPECT_Y = 9;

    private final static String CROP_INTENT_OUTPUT_FORMAT = Bitmap.CompressFormat.JPEG.name();

    private final static int CROP_INTENT_FLAGS = 
        Intent.FLAG_GRANT_READ_URI_PERMISSION | 
        Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 *  temp File
 */
	private final static String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private final static String FILE_PRIFIX = "img_";

    private final static String FILE_POSTFIX = "_crop";

    private final static String FILE_EXT = ".jpg";

    private final static int JPEG_QUALITY = 100;



/**
  * Constractor 
 */	    
public CropFile() {
    // nop
} 


/**
  * mkDir
 */
public static boolean mkDir(){
    return ExternalStorageFile.mkDirInPictures();
}


/**
  * createCropIntent 
  * reference : http://java-lang-programming.com/articles/74
 */	
public static Intent createCropIntent(File selectedFile, File outputFile) {

        if((selectedFile == null)||(outputFile == null)) {
            log_d("createCropIntent: not set file");
            return null;
        }

        Uri uriData = Uri.fromFile(selectedFile);
        Uri uriOutput = Uri.fromFile(outputFile);

        Intent intent = new Intent(CROP_INTENT_NAME);
        intent.setDataAndType(uriData, CROP_INTENT_TYPE);
        intent.putExtra("aspectX", CROP_INTENT_ASPECT_X);
        intent.putExtra("aspectY", CROP_INTENT_ASPECT_Y);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("scale", "true");
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", CROP_INTENT_OUTPUT_FORMAT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriOutput);
        intent.addFlags(CROP_INTENT_FLAGS);

        return intent;
}


/**
  * createTempFile
 */
public static File[] createTempFiles() {

    File dir = ExternalStorageFile.getDirInPictures();
    String timeStamp = getTimeStamp();
    String title = FILE_PRIFIX + timeStamp;
    String fileName1 = title + FILE_EXT;
    String fileName2 = title + FILE_POSTFIX + FILE_EXT;
    File file1 = new File(dir, fileName1);
    File file2 = new File(dir, fileName2);
    File[] files = {file1, file2};

    return files;
}


/**
  * createTempFile
 */
public static File createTempFile() {

    File dir = ExternalStorageFile.getDirInPictures();
    String fileName = createTempFileName();
    File file = new File(dir, fileName);
    return file;

}


/**
  * createTempFileName
 */
public static String createTempFileName() {

    String timeStamp = getTimeStamp();
    String fileName = FILE_PRIFIX + timeStamp + FILE_EXT;
    return fileName;

}


/**
  * getTimeStamp
 */
public static String getTimeStamp() {

    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String timeStamp = dateFormat.format(now);
    return timeStamp;

}


 /**
  * getBitmapFromUri
  */
public static Bitmap getBitmapFromUri(Context context, Uri uri) {

        ContentResolver contentResolver = context.getContentResolver();

            Bitmap bitmap = null;
          try {
              bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
          }catch (IOException e) {
                    e.printStackTrace();
            }
            return bitmap;
}



/**
  * saveImage
 */
public static boolean saveImage(Bitmap bitmap, File file) {

        boolean is_error = false;

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
            out.flush();
            out.close();
        } catch(IOException e) {
            is_error = true;
            e.printStackTrace();
        }

        return ! is_error;
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    Log.d( TAG, msg );
}


} // class CropFile