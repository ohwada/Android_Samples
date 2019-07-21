/**
 * Camera2 Sample
 * CameraParam
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217.util;


import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.util.Log;
import android.util.Range;
import android.util.SparseIntArray;
import android.view.Surface;

import java.io.File;

/**
  *  class CameraParam
  */
public class CameraParam {


/**
   * Control Mode
  */
 //0: 
 public final static int  CONTROL_MODE_OFF 
= CaptureRequest.CONTROL_MODE_OFF ;
//1:
 public final static int  CONTROL_MODE_AUTO
= CaptureRequest.CONTROL_MODE_AUTO;
//2:
 public final static int  CONTROL_MODE_USE_SCENE_MODE
= CaptureRequest.CONTROL_MODE_USE_SCENE_MODE;
//3:
 public final static int  CONTROL_MODE_OFF_KEEP_STATE
= CaptureRequest.CONTROL_MODE_OFF_KEEP_STATE;


/**
   * Control Capture Intent
  */
// 0:
 public final static int  CAPTURE_INTENT_CUSTOM
= CaptureRequest.CONTROL_CAPTURE_INTENT_CUSTOM;
// 1:
 public final static int CAPTURE_INTENT_PREVIEW
= CaptureRequest.CONTROL_CAPTURE_INTENT_PREVIEW;
// 2:
 public final static int CAPTURE_INTENT_STILL_CAPTURE
= CaptureRequest.CONTROL_CAPTURE_INTENT_STILL_CAPTURE;
// 3:
 public final static int CAPTURE_INTENT_VIDEO_RECORD
= CaptureRequest.CONTROL_CAPTURE_INTENT_VIDEO_RECORD;
// 4:
 public final static int CAPTURE_INTENT_VIDEO_SNAPSHOT
= CaptureRequest.CONTROL_CAPTURE_INTENT_VIDEO_SNAPSHOT;
// 5:
 public final static int CAPTURE_INTENT_ZERO_SHUTTER_LAG
= CaptureRequest.CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG;
// 6:
 public final static int CAPTURE_INTENT_MANUAL
= CaptureRequest.CONTROL_CAPTURE_INTENT_MANUAL;
// 7:
// Added in API level 28
 //public final static int CAPTURE_INTENT_MOTION_TRACKING
// = CaptureRequest.CONTROL_CAPTURE_INTENT_MOTION_TRACKING;


/**
   * Auto Focus Mode
  */
// 0:
 public final static int  AF_MODE_OFF = CaptureRequest.CONTROL_AF_MODE_OFF;
//  1:
public final static int AF_MODE_AUTO = CaptureRequest.CONTROL_AF_MODE_AUTO;
//  2:
public final static int AF_MODE_MACRO = CaptureRequest.CONTROL_AF_MODE_MACRO;
//  3:
public final static int AF_MODE_CONTINUOUS_VIDEO = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO;
//  4:
public final static int AF_MODE_CONTINUOUS_PICTURE = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
//  5:
public final static int AF_MODE_EDOF = CaptureRequest.CONTROL_AF_MODE_EDOF;


/**
   * Auto Exposure Mode
  */
// 0:
public final static int  AE_MODE_OFF = CaptureRequest.CONTROL_AE_MODE_OFF;
//  1:
public final static int  AE_MODE_ON = CaptureRequest.CONTROL_AE_MODE_ON;
//  2:
public final static int  AE_MODE_ON_AUTO_FLASH = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
//  3:
public final static int  AE_MODE_ON_ALWAYS_FLASH = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
//  4:
public final static int  AE_MODE_ON_AUTO_FLASH_REDEYE = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE;


/**
   * Auto White Balance Mode
  */
//Value: 0:
public final static int AWB_MODE_OFF = CaptureRequest.CONTROL_AWB_MODE_OFF;
//  Value: 1:
public final static int AWB_MODE_AUTO = CaptureRequest.CONTROL_AWB_MODE_AUTO;
//  Value: 2:
public final static int AWB_MODE_INCANDESCENT = CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT;
//  Value: 3:
public final static int AWB_MODE_FLUORESCENT = CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT;
//  Value: 4:
public final static int AWB_MODE_WARM_FLUORESCENT = CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT;
//  Value: 5:
public final static int AWB_MODE_DAYLIGHT = CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT;
//Value: 6:
public final static int AWB_MODE_CLOUDY_DAYLIGHT = CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT;
//Value: 7:
public final static int AWB_MODE_TWILIGHT = CaptureRequest.CONTROL_AWB_MODE_TWILIGHT;
//Value: 8:
public final static int AWB_MODE_SHADE = CaptureRequest.CONTROL_AWB_MODE_SHADE;


/**
   * LENS_OPTICAL_STABILIZATION_MODE
  */
//Value: 0:
public final static int STABILIZATION_MODE_OFF = 
CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF;
//Value: 1:
public final static int STABILIZATION_MODE_ON = 
CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON;

/**
   * STATISTICS_LENS_SHADING_MAP_MODE
  */
//Value: 0:
public final static int SHADING_MAP_MODE_OFF = 
CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE_OFF;
//Value: 1:
public final static int SHADING_MAP_MODE_ON = 
CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE_ON;


/**
   * Control Mode
  */
    private int mControlMode = CONTROL_MODE_OFF;


/**
   * Control Capture Intent
  */
    private int mControlCaptureIntent = CAPTURE_INTENT_CUSTOM;


/**
   * Auto Focus Mode
  */
    private int mAfMode = AF_MODE_OFF;


/**
   * Auto Exposure Mode
  */
    private int mAeMode = AE_MODE_OFF;


/**
   * Auto White Balance Mode
  */
    private int mAwbMode = AWB_MODE_OFF;


/**
   * LENS_OPTICAL_STABILIZATION_MODE
  */
    private int mOpticalStabilizationMode = STABILIZATION_MODE_OFF;

/**
   * STATISTICS_LENS_SHADING_MAP_MODE
  */
    private int mShadingMapMode = SHADING_MAP_MODE_OFF;


/**
   * Manual Mode
  */
    private boolean isManualMode = false;


/**
   * SENSOR_INFO_SENSITIVITY_RANGE
   * values, as defined in ISO 12232:2006
  */
    private Range<Integer> mSensitivityRange;


/**
   * SENSOR_INFO_EXPOSURE_TIME_RANGE
   * Units: Nanoseconds
  */
    private Range<Long> mExposureTimeRange;


/**
  *  SENSOR_SENSITIVITY
  */
    private int mSensitivity = 0;


/**
  *  SENSOR_EXPOSURE_TIME
  *  Units: Nanoseconds
  */
    private long mExposureTime = 0;


/**
   * LENS_APERTURE 
   * Units: The aperture f-number
  */
    private float mAperture = 0;


/**
  * SENSOR_FRAME_DURATION 
 */
    private long mFrameDuration = 0;


/**
  * Flag whether to use external storage
 */
    private boolean isStorage = false;


/**
  * Flag whether to useRaw format
 */
    private boolean isRaw = false;


/**
  *  constractor
  */
    public CameraParam() {
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
  *  setSensitivityRange
  */
    public void setSensitivityRange(Range<Integer> range) {
        mSensitivityRange = range;
    }


/**
  *  setExposureTimeRange
  */
    public void setExposureTimeRange(Range<Long> range) {
        mExposureTimeRange = range;
    }

/**
  *  setControlMode
  */
    public void setControlMode(int mode) {
            mControlMode = mode;
    }


/**
  *  getControlMode
  */
    public int getControlMode() {
            return mControlMode;
    }


/**
  *  setControlCaptureIntent
  */
    public void setControlCaptureIntent(int mode) {
            mControlCaptureIntent = mode;
    }


/**
  *  getControlCaptureIntent
  */
    public int getControlCaptureIntent() {
            return mControlCaptureIntent;
    }


/**
  *  setAfMode
  */
    public void setAfMode(int mode) {
            mAfMode = mode;
    }


/**
  *  getAfMode
  */
    public int getAfMode() {
            return mAfMode;
    }


/**
  *  setAeMode
  */
    public void setAeMode(int mode) {
            mAeMode = mode;
    }


/**
  *  getAeMode
  */
    public int getAeMode() {
            return mAeMode;
    }


/**
  *  setAwbMode
  */
    public void setAwbMode(int mode) {
            mAwbMode = mode;
    }


/**
  *  getAwbMode
  */
    public int getAwbMode() {
            return mAwbMode;
    }



/**
  *  setSensitivity
  */
    public boolean setSensitivity(int sensitivity) {

        // check range
        boolean ret = mSensitivityRange.contains(sensitivity);
        if(ret) {
            mSensitivity = sensitivity;
        }
        return ret;
    }


/**
  *  getSensitivity
  */
    public int getSensitivity() {
        int sensitivity = mSensitivity;

        // check range
        int lower = mSensitivityRange.getLower();
        int upper = mSensitivityRange.getUpper();
        if(sensitivity < lower) {
               sensitivity = lower;
        }
        if(sensitivity > upper) {
               sensitivity = upper;
        }
        return sensitivity;
    }

/**
  *  setExposureTime
  */
    public boolean setExposureTime(long time) {

        if( mExposureTimeRange == null) return false;

        // check range
        boolean ret = mExposureTimeRange.contains(time);
        if(ret) {
            mExposureTime = time;
        }
        return ret;
    }


/**
  *  getExposureTime
  */
    public long getExposureTime() {

        long time = mExposureTime;

        // check range
        long lower = mExposureTimeRange.getLower();
        long upper = mExposureTimeRange.getUpper();
        if(time < lower) {
               time = lower;
        }
        if(time > upper) {
               time = upper;
        }
        return time;
    }


/**
  *  setAperture
  */
    public void setAperture(float aperture) {
            mAperture = aperture;
    }


/**
  *  getAperture
  */
    public float getAperture() {
            return mAperture;
    }

/**
  *  setFrameDuration
  */
    public void setFrameDuration(long duration) {
            mFrameDuration = duration;
    }


/**
  *  getFrameDuration
  */
    public long getFrameDuration() {
            return mFrameDuration;
    }


/**
  *  setOpticalStabilizationMode
  */
    public void setOpticalStabilizationMode(int mode) {
            mOpticalStabilizationMode = mode;
    }


/**
  *  getOpticalStabilizationMode
  */
    public int getOpticalStabilizationMode() {
            return mOpticalStabilizationMode;
    }

/**
  *  setShadingMapMode
  */
    public void setShadingMapMode(int mode) {
            mShadingMapMode = mode;
    }


/**
  *  getShadingMapMode
  */
    public int getShadingMapMode() {
            return mShadingMapMode;
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
            return  isStorage;
    }


/**
  *  setUseRaw
  */
    public void setUseRaw(boolean mode) {
            isRaw = mode;
    }


/**
  *  getUseRaw
  */
    public boolean getUseRaw() {
            return  isRaw;
    }


/**
  * toString
  */
    public String  toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ManualMode: ");
        sb.append(isManualMode);
        sb.append(" ControlMode: ");
        sb.append(mControlMode);
        sb.append(" ControlCaptureIntent: ");
        sb.append(mControlCaptureIntent);
        sb.append(" AfMode: ");
        sb.append(mAfMode);
        sb.append(" AeMode: ");
        sb.append(mAeMode);
        sb.append(" AwbMode: ");
        sb.append(mAwbMode);
        sb.append(" Sensitivity: ");
        sb.append(mSensitivity);
        sb.append(" ExposureTime: ");
        sb.append(mExposureTime);
        sb.append(" Aperture: ");
        sb.append(mAperture);
        sb.append(" FrameDuration: ");
        sb.append(mFrameDuration);
        sb.append(" OpticalStabilizationMode: ");
        sb.append(mOpticalStabilizationMode);
        sb.append(" ShadingMapMode: ");
        sb.append(mShadingMapMode);
        sb.append(" use Storage: ");
        sb.append(isStorage);
        sb.append(" use Raw: ");
        sb.append(isRaw);
        return sb.toString();
    }



} // class CameraParam

