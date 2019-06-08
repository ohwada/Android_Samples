/**
 * Camera2 Sample
 *  Camera2Probe
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera212;


import android.content.Context;


import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;


import android.hardware.camera2.CameraMetadata;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.util.Range;


import java.util.ArrayList;
import java.util.List;


/**
 * class Camera2Probe
 * probe supported camera2 features
 * original : https://github.com/TobiasWeis/android-camera2probe
 */
public class Camera2Probe {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "Camera2Probe";


        private static final String  HTML_HEADER = "<html><header><title>Camera2Probe</title></header><body>";

        private static final String  HTML_FOOTER =  "</body></html>";

        private static final String HTML_FPOS = "<font style=\"color:#00aa00;\">";
    
        private static final String HTML_FNEG = "<font style=\"color:#990000;\">";
    
        private static final String HTML_CHECK = "<div style=\"float:left;width:20px;color:#00aa00;\">&#x2713;</div>";
    
        private static final String HTML_CROSS = "<div style=\"float:left;width:20px;color:#990000;\">&#x2717;</div>";


/**
 * probe
 */
public static String probe(Context context) {
        CameraManager manager = getManager(context);
        String cameraId = getCameraId(manager, CameraCharacteristics.LENS_FACING_BACK);
        CameraCharacteristics characteristics = getCharacteristics(manager, cameraId);
        List<String> list = probeList(characteristics);
        String result = "";
        for(String str: list) {
                result += str;
                log_d(str);
        } // for
        return result;
}


/**
 * probeList
 */
private static List<String> probeList(CameraCharacteristics characteristics) {

        List<String> list = new ArrayList<String>();


        list.add(HTML_HEADER);
        list.add(probeModel());
        list.add(probeHardwareLevel(characteristics));
        list.add(probeCapability(characteristics));
        list.add(probeFocus(characteristics));
        list.add(probeExposure(characteristics));
        list.add(probeWhiteBalance(characteristics));
        list.add(probeZoom(characteristics));
        list.add(probeFaceDetect(characteristics));
        list.add(HTML_FOOTER);
        return list;
}


/**
 * getManager
 */
public static CameraManager getManager(Context context) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        return manager;
}

/**
 * getCameraId
 */
public static  String getCameraId(CameraManager manager, int cameraFacing) {
        String cameraId = null;
        try {
            String[] ids = manager.getCameraIdList();
            for (int i=0; i<ids.length; i++ ) {
                String id = ids[i];
                CameraCharacteristics c
                        = manager.getCameraCharacteristics(id);
                int facing = c.get(CameraCharacteristics.LENS_FACING);
                if (facing == cameraFacing) {
                    cameraId = id;
                    break;
                }
            } // for
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return cameraId;
} // getCameraId


/**
 * getCharacteristics
 */
public static  CameraCharacteristics getCharacteristics(CameraManager manager, String id) {
    CameraCharacteristics characteristics = null;
        try {
            characteristics = manager.getCameraCharacteristics(id);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return  characteristics;
}


/**
 * probeModel
 */
public static  String probeModel() {
        String result = "<b>Model</b><br>";
        result += "Model: " + Build.MODEL + "<br>";
        result += "Manufacturer: " + Build.MANUFACTURER + "<br>";
        result += "Build version: " + android.os.Build.VERSION.RELEASE + "<br>";
        result += "SDK version: " + android.os.Build.VERSION.SDK_INT + "<br>";
        return result;
}




/**
 * probeHardwareLevel
 */
public static  String probeHardwareLevel(CameraCharacteristics characteristics) {
        String result = "<br><b>Hardware Level</b><br>";
        Integer mylevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        List<Pair> levels = new ArrayList<>();
        // 0:
        levels.add(new Pair<>(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED, "Limited"));
        // 1:
        levels.add(new Pair<>(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL, "Full"));
        // 2:
        levels.add(new Pair<>(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY, "Legacy"));
        // 3:
        levels.add(new Pair<>(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3, "Level_3"));
        // 4: Added in API level 28
        //levels.add(new Pair<>(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL, "External"));

        for (Pair<Integer, String> l : levels) {
            if (l.first == mylevel) {
                result += HTML_CHECK + HTML_FPOS + l.second + "</font><br style=\"clear:both;\">";
            } else {
                result += HTML_CROSS + HTML_FNEG + l.second + "</font><br style=\"clear:both;\">";
            }
        } // for
    return result;
}


/**
 * probeCapability
 */
public static  String probeCapability(CameraCharacteristics characteristics) {
        String result = "<br><b>Capabilities</b><br>";
        List<Pair> ml = new ArrayList<>();
        // 0:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE, "Backward Compatible"));
        // 1:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR, "Manual Sensor"));
        // 2:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING, "Manual Post Processing"));
        // 3:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW, "Raw"));
        // 4:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING, "Private Reprocessing"));
        // 5:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS, "Sensor Settings"));
        // 6:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE, "Burst Capture"));
        // 7:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING, "YUV Reprocessing"));
        // 8:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT, "Depth Output"));
        // 9:
        ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO, "High Speed Video"));

        // 10: Added in API level 28
        //ml.add(new Pair<>(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING, "Motion Tracking"));

        int[] tmp = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        List<Integer> aelist = new ArrayList<Integer>();
        for (int index = 0; index < tmp.length; index++) {
            aelist.add(tmp[index]);
        } // for

        for (Pair<Integer, String> kv : ml) {
            if (aelist.contains(kv.first)) {
                result += HTML_CHECK + HTML_FPOS + kv.second + "</font><br style=\"clear:both;\">";
            } else {
                result += HTML_CROSS + HTML_FNEG + kv.second + "</font><br style=\"clear:both;\">";
            }
        } // for
    return result;
}


/**
 * probeFocus
 */
public static  String probeFocus(CameraCharacteristics characteristics) {
        String result = "<br><b>Focus</b><br>";
        // not able to get the enum/key names from the ints,
        // so I am doing it myself
        List<Pair> ml = new ArrayList<>();
        // 0:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AF_MODE_OFF, "AF: Off"));
        // 1:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AF_MODE_AUTO, "AF: Auto"));
        // 2:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AF_MODE_MACRO, "AF: Macro"));
        // 3:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO, "AF: Continuous Video"));
        // 4:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE, "AF: Continuous Picture"));
        // 5:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AF_MODE_EDOF, "AF: EDOF"));

        int[] tmp = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        List<Integer> aelist = new ArrayList<Integer>();
        for (int index = 0; index < tmp.length; index++) {
            aelist.add(tmp[index]);
        } // for

        for (Pair<Integer, String> kv : ml) {
            if (aelist.contains(kv.first)) {
                result += HTML_CHECK + HTML_FPOS + kv.second + "</font><br style=\"clear:both;\">";
            } else {
                result += HTML_CROSS + HTML_FNEG + kv.second + "</font><br style=\"clear:both;\">";
            }
        } // for

        result +=  "<br style=\"clear:both;\">";
        result += probeMinfoFocusDist(characteristics);
        result += probeMaxRegionsAf(characteristics);
        result +=  "<br style=\"clear:both;\">";
    return result;
}



/**
 *  probeMaxRegionsAf
 */
public static String probeMaxRegionsAf(CameraCharacteristics characteristics) {
            Integer regions = 0;
        try {
            regions = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
        }catch(Exception e){
        }
    String result = HTML_FPOS + "Max Regions AF:  " + "</font>";
    result += regions;
    if(regions == 0) {
            result += HTML_FNEG + " (not support Auto Focus) </font></br>";
    }
    return result;
}



/**
 *  probeMinfoFocusDist
 */
public static String probeMinfoFocusDist(CameraCharacteristics characteristics) {
            float minFocusDist = 0;
        try {
            minFocusDist = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        }catch(Exception e){
        }

        String result = HTML_FPOS + "Minimum Focus Distance: "   + "</font>";
        result += minFocusDist + "</br>";
        if(minFocusDist == 0) {
            result += HTML_FNEG + " (Fixed Focus) </font></br>";
        }
    return result;
}


/**
 *  probeExposure
 */
public static  String  probeExposure(CameraCharacteristics characteristics) {
        String result = "<br><b>Exposure</b><br>";
        // not able to get the enum/key names from the ints,
        // so I am doing it myself
        List<Pair> ml = new ArrayList<>();
        // 0:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AE_MODE_OFF, "AE: Off"));
        // 1:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AE_MODE_ON, "AE: Auto"));
        // 2:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH, "AE: Auto Flash"));
        // 3:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH, "AE: Always Flash"));
        // 4:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE, "AE: Auto Flush Redeye"));

        // 5: Added in API level 28
        //ml.add(new Pair<>(CameraMetadata.CONTROL_AE_MODE_ON_EXTERNAL_FLASH, "AE: External Flash"));

        int[] tmp = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        List<Integer> aelist = new ArrayList<Integer>();
        for (int index = 0; index < tmp.length; index++) {
            aelist.add(tmp[index]);
        }

        for (Pair<Integer, String> kv : ml) {
            if (aelist.contains(kv.first)) {
                result += HTML_CHECK + HTML_FPOS + kv.second + "</font><br style=\"clear:both;\">";
            } else {
                result += HTML_CROSS + HTML_FNEG + kv.second + "</font><br style=\"clear:both;\">";
            }
        } // for
        result += "<br/>";
        result += probeAeLock(characteristics);
        result += probeMaxRegionsAe(characteristics);
        result += probeExposureTimeRange(characteristics);
        result += probeSensitivityRange(characteristics);
        result += probeApertures(characteristics);
    return result;
}


/**
 *  probeAeLock
 */
public static  String  probeAeLock(CameraCharacteristics characteristics) {
        boolean lock = false;
        try {
            lock = characteristics.get(CameraCharacteristics.CONTROL_AE_LOCK_AVAILABLE);
        }catch(Exception e){
        }
        String result = "";
        if (lock) { 
                result += HTML_CHECK + HTML_FPOS + "AE Lock" + "</font><br style=\"clear:both;\">";
        } else {
                result += HTML_CROSS + HTML_FNEG + "AE Lock" + "</font><br style=\"clear:both;\">";
        }
    return result;
}


/**
 *  probeMaxRegionsAe
 */
public static  String  probeMaxRegionsAe(CameraCharacteristics characteristics) {
Integer regions = 0;
        try {
            regions = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
        }catch(Exception e){
        }
    String result = HTML_FPOS + "Max Regions AE:  " + "</font>";
    result += regions + "</br>";
    return result;
}


/**
 *  probeExposureTimeRange
 */
public static  String  probeExposureTimeRange(CameraCharacteristics characteristics) {
        Range<Long> range = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        String result = "";
        if( range != null) {
            result += HTML_CHECK + HTML_FPOS + "ExposureTimeRange: " + "</font>";
            result += range.toString() + 
            "<br style=\"clear:both;\">";
        } else {
            result += HTML_CROSS + HTML_FNEG + "ExposureTimeRange" +
            "</font><br style=\"clear:both;\">";
        }
        return result;
}



/**
 *  probeSensitivityRange
 */
public static  String  probeSensitivityRange(CameraCharacteristics characteristics) {
        Range<Integer> range = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        String result = "";
        if( range != null) {
            result += HTML_CHECK + HTML_FPOS + "SensitivityRange: " + "</font>";
            result += range.toString() + 
            "<br style=\"clear:both;\">";
        } else {
            result += HTML_CROSS + HTML_FNEG + "SensitivityRange" +
            "</font><br style=\"clear:both;\">";
        }
        return result;
}


/**
 * probeApertures
 */
public static  String  probeApertures(CameraCharacteristics characteristics) {
         float[] apertures = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
        String result = "";
        if(apertures != null) {
            result += HTML_CHECK + HTML_FPOS + "Apertures: " + "</font>";
            for(int i=0; i<apertures.length; i++) {
                    result += apertures[i] + ", ";
            }
            result += "<br style=\"clear:both;\">";
        } else {
            result += HTML_CROSS + HTML_FNEG + "Apertures" +
            "</font><br style=\"clear:both;\">";
        }
        return result;
}


/**
 *  probeWhiteBalance
 */
public static  String  probeWhiteBalance(CameraCharacteristics characteristics) {
        String result = "<br><b>White Balance</b><br>";
        List<Pair> ml = new ArrayList<>();
        // 0:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_OFF, "WB: Off"));
        // 1:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_AUTO, "WB: Auto"));      
        // 2:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT, "WB: Incandescent"));
        // 3:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT, "WB: Fluorescent"));
        // 4:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT, "WB: Warm Fluorescent"));
        // 5:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT, "WB: Daylight"));
        // 6:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT, "WB: cloudy Daylight"));
        // 7:
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_TWILIGHT, "WB: Twilight"));
        // 8
        ml.add(new Pair<>(CameraMetadata.CONTROL_AWB_MODE_SHADE, "WB: Shade"));

        int[] tmp = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
        List<Integer> aelist = new ArrayList<Integer>();

        for (int index = 0; index < tmp.length; index++) {
            aelist.add(tmp[index]);
        } // for

        for (Pair<Integer, String> kv : ml) {
            if (aelist.contains(kv.first)) {
                result += HTML_CHECK + HTML_FPOS + kv.second + "</font><br style=\"clear:both;\">";
            } else {
                result += HTML_CROSS + HTML_FNEG + kv.second + "</font><br style=\"clear:both;\">";
            }
        } // for

    result += probeAwbLock(characteristics);
    result += probeMaxRegionsAwb(characteristics);
    return result;
}


/**
 *  probeAwbLock
 */
public static  String  probeAwbLock(CameraCharacteristics characteristics) {
        boolean lock = false;
        try {
            lock = characteristics.get(CameraCharacteristics.CONTROL_AWB_LOCK_AVAILABLE);
        }catch(Exception e){
        }
        String result = "";
        if (lock) {
                result += HTML_CHECK + HTML_FPOS + "AWB Lock" + "</font><br style=\"clear:both;\">";
        } else {
                result += HTML_CROSS + HTML_FNEG + "AWB Lock" + "</font><br style=\"clear:both;\">";
        }

    return result;
}


/**
 *  probeMaxRegionsAwb
 */
public static  String  probeMaxRegionsAwb(CameraCharacteristics characteristics) {
Integer regions = 0;
        try {
            regions = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB);
        }catch(Exception e){
        }
    String result = HTML_FPOS + "Max Regions AWB:  " + "</font>";
    result += regions + "</br>";
    return result;
}


/**
 *  probeZoom
 */
public static  String  probeZoom(CameraCharacteristics characteristics) {
        String result = "<br><b>Zoom</b><br>";
        int type = 0;
        float zoom = 0;
        try {
            type = characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE);
            zoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        }catch(Exception e){
        }

    if(type == CameraCharacteristics.SCALER_CROPPING_TYPE_CENTER_ONLY) {
                    result += HTML_FPOS + "Zoom Type: Center Only" + "</font></br>";
    } else if(type == CameraCharacteristics.SCALER_CROPPING_TYPE_FREEFORM) {
                    result += HTML_FPOS + "Zoom Type: Free Form " + "</font></br>";
    }
    result += HTML_FPOS + "Max Digital Zoom: " + "</font>";
    result += zoom;
    if(zoom < 1) {
        result += HTML_FNEG + "(not support Zoom)</font></br>";
    }
    result += "</br>";
    return result;
}


/**
 *  probeFaceDetect
 */
public static  String  probeFaceDetect(CameraCharacteristics characteristics) {
        String result = "<br><b>Face Detect</b><br>";
        List<Pair> ml = new ArrayList<>();
        // 0:
        ml.add(new Pair<>(CameraMetadata.STATISTICS_FACE_DETECT_MODE_OFF, "Face Detect: Off"));
        // 1:
        ml.add(new Pair<>(CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE, "Face Detect: Simple"));
        // 2:
        ml.add(new Pair<>(CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL, "Face Detect: Full"));

        int[] tmp = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
        List<Integer> list = new ArrayList<Integer>();
        for (int index = 0; index < tmp.length; index++) {
            list.add(tmp[index]);
        } // for

        for (Pair<Integer, String> kv : ml) {
            if (list.contains(kv.first)) {
                result += HTML_CHECK + HTML_FPOS + kv.second + "</font><br style=\"clear:both;\">";
            } else {
                result += HTML_CROSS + HTML_FNEG + kv.second + "</font><br style=\"clear:both;\">";
            }
        } // for
    return result;
}


/**
 * contains
 */
public static  boolean contains(int[] modes, int mode) {
        if (modes == null) {
            return false;
        }
        for (int i=0; i<modes.length; i ++) {
            if (mode == modes[i]) {
                return true;
            }
        } // for
        return false;
}


/**
 * write into logcat
 */ 
private  static  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class Camera2Probe
