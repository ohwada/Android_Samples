/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.simplecropview1;



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


	private final static String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private final static String FILE_PREFIX = "scv_";


/**
  * constractor 
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
  * createNewUri
 */
  public static Uri createNewUri(Context context, Bitmap.CompressFormat format) {

    File dir = ExternalStorageFile.getDirInPictures();
   if (!dir.canWrite()) {
        log_d("createNewUri: not write permission");
        return null;
    }

    ContentValues values = getContentValues(dir, format);

    ContentResolver resolver = context.getContentResolver();
    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    return uri;
  }


/**
  * getContentValues
 */ 
private static ContentValues getContentValues(File dir, Bitmap.CompressFormat format) {

    long currentTimeMillis = System.currentTimeMillis();
    long time = currentTimeMillis / 1000;

    Date today = new Date(currentTimeMillis);
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String title = dateFormat.format(today);

    String imageType = getImageType(format);
    String mimeType = "image/" + imageType;

    String ext = getImageExt(format);
    String fileName = FILE_PREFIX + title + ext;

    File file = new File(dir, fileName);
    String path = file.toString();

    ContentValues values = new ContentValues();
    values.put(MediaStore.Images.Media.TITLE, title);
    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
    values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
    values.put(MediaStore.Images.Media.DATA, path);
    values.put(MediaStore.MediaColumns.DATE_ADDED, time);
    values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);

    if (file.exists()) {
      values.put(MediaStore.Images.Media.SIZE, file.length());
    }
    return values;
}


/**
  * getPath
 */ 
public static String getPath(Context context, Uri uri) {
    ContentResolver contentResolver = context.getContentResolver();
    String[] columns = { MediaStore.Images.Media.DATA };
    Cursor cursor = contentResolver.query(uri, columns, null, null, null);
    cursor.moveToFirst();
    String path = cursor.getString(0);
    cursor.close();
    return path;
}


/**
  * getImageType
 */ 
  public static String getImageType(Bitmap.CompressFormat format) {
    switch (format) {
      case JPEG:
        return "jpeg";
      case PNG:
        return "png";
    }
    return "png";
  }

/**
  * getImageType
 */ 
  public static String getImageExt(Bitmap.CompressFormat format) {
    switch (format) {
      case JPEG:
        return ".jpg";
      case PNG:
        return ".png";
    }
    return ".png";
  }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    Log.d( TAG, msg );
}


} // class CropFile