/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.opencl6;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *  class AssetFile
 */
public class AssetFile {

    private final static int COPY_BUF_SIZE = 4096;

 	private final static int COPY_EOF = -1;


/** 
 *  Constractor
 */
public AssetFile() {
    // nop
}


/**
 * getFilePath
 */
public static String getFilePath(Context context, String fileName) {
    File file = getFileInExternalFilesDir(context, fileName);
    String ret = (file == null)? "": file.toString();
    return ret;
}


/** 
 *  getFileInExternalFilesDir
 */
public static File getFileInExternalFilesDir(Context context, String fileName) {

    File dir = context.getExternalFilesDir(null);
    File outFile = new File(dir, fileName);

    AssetManager assetManager = context.getAssets();

    boolean is_error = false;
    try {
            InputStream is = assetManager.open(fileName);
            FileOutputStream os = new FileOutputStream(outFile);
            boolean ret = copyStream(is, os);
            if(!ret) is_error = true;
            if(is != null) is.close();
            if(os != null) os.close();
    } catch (IOException ex) {
            is_error = true;
            ex.printStackTrace();
    }

    File ret = is_error ? null: outFile;
    return ret;
}


/** 
 *  copyStream
 */
private static boolean copyStream(InputStream is, OutputStream os) {

        boolean is_error = false;
        try {
                byte[] buffer = new byte[COPY_BUF_SIZE];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != COPY_EOF) {
                            os.write(buffer, 0, bytesRead);
                } // while

        } catch (IOException e) {
                is_error =true;
                e.printStackTrace();
        }

        return !is_error;
}


/**
 * readImage
 */
public static Bitmap readImage(Context context, String fileName) {

    AssetManager assetManager = context.getAssets();

    Bitmap bitmap = null;
    try {
            InputStream is = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            if(is != null) is.close();
    } catch (IOException ex) {
            ex.printStackTrace();
    }

    return bitmap;
}


} // class AssetFile
