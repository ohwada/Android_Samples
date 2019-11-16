/**
 * Camera2 Sample
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.camera224;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.params.StreamConfigurationMap;

import android.util.Log;
import android.util.Pair;
import android.util.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * class Camera2Feature
 */
public class Camera2Feature {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "Camera2Feature";

    // camera face
    public static final int CAMERA_FACING_BACK = CameraCharacteristics.LENS_FACING_BACK;
    public static final int CAMERA_FACING_FRONT = CameraCharacteristics.LENS_FACING_FRONT;

    protected int mFacing = CAMERA_FACING_BACK;


    private Context mContext;

    private static List<Pair> EFFECT_LIST = new ArrayList<>();

static {
        EFFECT_LIST.add( new Pair<>(CameraMetadata.CONTROL_EFFECT_MODE_OFF, "off" ));

        EFFECT_LIST.add( new Pair<>(CameraMetadata.CONTROL_EFFECT_MODE_MONO, "mono" ));

                EFFECT_LIST.add( new Pair<>( CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE, "negative" ));
                EFFECT_LIST.add( new Pair<>( CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE, "solarize" ));
                EFFECT_LIST.add( new Pair<>( CameraMetadata.CONTROL_EFFECT_MODE_SEPIA, "sepia" ));
                EFFECT_LIST.add( new Pair<>( CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE, "posterize" ));
                EFFECT_LIST.add( new Pair<>( CameraMetadata.CONTROL_EFFECT_MODE_AQUA, "aqua" ));
                EFFECT_LIST.add( new Pair<>( CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD, "whiteboard" ));
                EFFECT_LIST.add( new Pair<>( CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD, "blackboard" ));
}


/** 
 *  constractor
 */
public Camera2Feature(Context context) {
        mContext = context;
}


/**
   * setFacing
  */
public void setFacing(int facing) {
            mFacing = facing;
}

/** 
 *  getEffectList
 */
public List<String> getEffectList() {
        List<String> list = new ArrayList<String>();
        int[]  array = getEffectArray() ;
        int length = array.length;
        log_d("length= " + length);
        for( int i=0; i<length; i++ ) {
            int key = array[i];
            String effect = findValueInEffectList(key);
            list.add(effect);
        }
        return list;
}


/** 
 *  getEffectId
 */
public int getEffectId(String effect) {
    return findKeyInEffectList(effect);
}


/** 
 *  findValueInEffectList
 */
private String findValueInEffectList(int key) {
        String val = null;
        for (Pair<Integer, String> pair : EFFECT_LIST) {
                Integer first = pair.first;
                String second = pair.second;
                if (first == key) {
                    val = second;
                    break;
                }
        } // for
        return val;
}


/** 
 * findKeyInEffectList
 */
private int findKeyInEffectList( String effect) {
        int key = 0;
        for (Pair<Integer, String> pair : EFFECT_LIST) {
                Integer first = pair.first;
                String second = pair.second;
                if (effect.equals(second)) {
                        key = first;
                    break;
                }
        } // for
        return key;
}


/** 
 *  getEffectArray
 */
private int[] getEffectArray() {
        CameraManager manager = getCameraManager();
        String cameraId = getCameraId(manager, mFacing);
        CameraCharacteristics characteristics = getCameraCharacteristics(manager, cameraId);
        int[] array = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        return array;
}


/** 
 *  getResolutionList
 */
public List<Size> getResolutionList() {
    Size[] array = getJpegResolutionArray();
    List<Size> list = Arrays.asList(array);
    return list;
}


/** 
 *  getJpegResolutionArray
 */
private Size [] getJpegResolutionArray() {
        CameraManager manager = getCameraManager();
        String cameraId = getCameraId(manager, mFacing);
        CameraCharacteristics characteristics = getCameraCharacteristics(manager, cameraId);
        StreamConfigurationMap map = getStreamConfigurationMap(characteristics);

// fot TextureVew
// map.getOutputSizes(SurfaceTexture.class);
// fot SurfaceVew   
// map.getOutputSizes(SurfaceHolder.class);

        Size[] outputSizes = map.getOutputSizes(ImageFormat.JPEG);

        return outputSizes;
}


/**
 * getCameraManager
 */
protected CameraManager getCameraManager() {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        return  manager;
}


/**
 * getCameraId
 */
protected String getCameraId(CameraManager manager, int cameraFacing) {
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
 * getCameraCharacteristics
 */ 
private CameraCharacteristics getCameraCharacteristics(CameraManager manager, String cameraId) {
    CameraCharacteristics characteristics = null;
    try {
        characteristics
        = manager.getCameraCharacteristics(cameraId);
} catch (CameraAccessException e) {
        e.printStackTrace();
    }
        return characteristics;
}


/**
 * getStreamConfigurationMap
 */ 
private StreamConfigurationMap getStreamConfigurationMap(CameraCharacteristics characteristics) {
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        return map;
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} //  class Camera2Feature
