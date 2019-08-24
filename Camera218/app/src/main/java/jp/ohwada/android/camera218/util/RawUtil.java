/**
 * Camera2 Sample
 * RawUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218.util;


import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;


import android.media.Image;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * class RawUtil
 * save Image with  DngCreator
 */ 
public class RawUtil {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "RawUtil";


/**
 * for File Name
 */ 
    private static final String FILE_PREFIX = FileUtil.FILE_PREFIX;
    private static final String FILE_EXT_DNG =  FileUtil.FILE_EXT_DNG;


/**
 * saveImage with DNG format
 */ 
public static boolean saveImageAsDng(Image image, CameraCharacteristics characteristics, CaptureResult captureResult, File file) {

            log_d("saveImageAsJpeg");
            int format = image.getFormat();
            if(format != ImageFormat.RAW_SENSOR) {
                    // nop, if not RAW format
                    return false;
            }

            DngCreator dngCreator  = new DngCreator(characteristics, captureResult);

            boolean is_error = false;
            FileOutputStream output = null;
            try {
                        output = new FileOutputStream(file);
                        dngCreator.writeImage(output, image);
            } catch (IOException e) {
                            e.printStackTrace();
            }

            try {
                    if (output != null) output.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            return !is_error;

} // saveImage


/**
  * getBurstOutputFile
 */
public static File getBurstOutputFile(Context context, String dateTime, int index, boolean use_storage ) {

    String fileName = FileUtil.getBurstOutputFileName(FILE_PREFIX, FILE_EXT_DNG, dateTime, (index+1) );

    File file = FileUtil.getFileInDir(context, fileName, use_storage );
    return file;

} // getBurstOutputFile


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class RawUtil
