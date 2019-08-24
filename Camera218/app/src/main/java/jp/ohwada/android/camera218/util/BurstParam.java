/**
 * Camera2 Sample
 * BurstParam
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218.util;


import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.util.Log;
import android.util.Range;
import android.util.SparseIntArray;
import android.view.Surface;

import java.io.File;


/**
  *  class BurstParam
  */
public class BurstParam {


/**
   * Image Format
  */
    public final static int FORMAT_JPEG = 1;
    public final static int FORMAT_YUV = 2;
    public final static int FORMAT_RAW = 3;


/**
   * Sensor sensitivity
  */
    private final static int SENSOR_SENSITIVITY = 400;
    private final static int[] SENSOR_SENSITIVITY_ARRAY 
    = {200, 400, 800};


/**
   * Exposure Compensation
  */
    private final static int EXPOSURE_COMPENSATION = 0;
    private final static int[] EXPOSURE_COMPENSATION_ARRAY = {-3, 0, 6};


/**
   * Exposure Time
   * 1 / 100 sec
  */
    private final static long SENSOR_EXPOSURE_TIME = 1000000000l / 100;


/**
  * Flag whether Manual Mode or not
  */
    private boolean isManualMode = false;


/**
  * Flag whether to use external storage
 */
    private boolean isStorage = false;


/**
  * Flag whether to save Images together when captureBurst is completed
 */
    private boolean isSaveTogether = false;


/**
   * Image Format
  */
    private int mImageFormat = FORMAT_JPEG;


/**
   * Number of Shots
  */
    private int mNumberOfShots = 3;


/**
  *  constractor
  */
    public BurstParam() {
        // no action
    }


/**
  *  setManualMode
  */
    public void setManualMode(boolean mode) {
        isManualMode = mode;
    }

/**
  *  getManualMode
  */
    public boolean getManualMode() {
        return isManualMode ;
    }


/**
  *  setUseStorage
  */
    public void setUseStorage(boolean mode) {
        isStorage = mode;
    }


/**
  *  getUseStorage
  */
    public boolean getUseStorage() {
        return isStorage ;
    }

/**
  *  setSaveTogether
  */
    public void setSaveTogether(boolean mode) {
        isSaveTogether = mode;
    }


/**
  *  getSaveTogether
  */
    public boolean getSaveTogether() {
        return isSaveTogether ;
    }


/**
  *  setImageFormat
  */
    public void setImageFormat(int format) {
        mImageFormat = format;
    }

/**
  *  getImageFormat
  */
    public int getImageFormat() {
        return mImageFormat ;
    }

/**
  *  setNumberOfShots
  */
    public void setNumberOfShots(int number) {
        mNumberOfShots = number;
    }

/**
  *  getNumberOfShots
  */
    public int getNumberOfShots() {
        return mNumberOfShots ;
    }


/**
  *  getExposureTime
  */
public static long getExposureTime() {
    return SENSOR_EXPOSURE_TIME;
}


/**
  *  getSensorSensitivity
  */
public static int getSensorSensitivity(int index) {
    int value = SENSOR_SENSITIVITY;
    if((index >= 0 )&&(index < SENSOR_SENSITIVITY_ARRAY.length)) {
    value = SENSOR_SENSITIVITY_ARRAY[index];
    }
return value;
}


/**
  *  getExposureCompensation
  */
public static int getExposureCompensation(int index) {
    int value = 0;
    if((index >= 0 )&&(index < EXPOSURE_COMPENSATION_ARRAY.length)) {
    value = EXPOSURE_COMPENSATION_ARRAY[index];
    }
return value;
}


/**
  * toString
  */
    public String  toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ManualMode: ");
        sb.append(isManualMode);
        sb.append(" use Storage: ");
        sb.append(isStorage);
        sb.append(" ImageFormat: ");
        sb.append(mImageFormat);
        sb.append(" NumberOfShots: ");
        sb.append(mNumberOfShots);
        return sb.toString();
    }


} // class BurstParam

