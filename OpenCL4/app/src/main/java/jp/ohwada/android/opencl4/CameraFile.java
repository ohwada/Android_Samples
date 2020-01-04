/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.opencl4;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * class CameraFile
 */
public class CameraFile {


    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT = ".jpg";


/**
  * Context 
 */
	private Context mContext;


/**
  * constractor 
 */
public CameraFile(Context context) {
		mContext = context;
}


/** 
 *  getFileDir
 */
public File getFileDir() {
        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!dir.exists()) {
            dir.mkdir();
        }
        return dir;
}


/** 
 *  getFileName
 */
public String getFileName() {
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String fileName = FILE_PREFIX + timeStamp + FILE_EXT;
        return fileName;
}


} // class class CameraFile
