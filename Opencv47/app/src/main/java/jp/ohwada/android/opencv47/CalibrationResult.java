/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/camera-calibration
 */
package jp.ohwada.android.opencv47;


import org.opencv.core.Mat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/** 
 *  abstract class CalibrationResult
 */
public abstract class CalibrationResult {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "CalibrationResult";


    private static final int CAMERA_MATRIX_ROWS = 3;
    private static final int CAMERA_MATRIX_COLS = 3;
    private static final int DISTORTION_COEFFICIENTS_SIZE = 5;


/** 
 *  save
 */
    public static void save(Activity activity, Mat cameraMatrix, Mat distortionCoefficients) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        double[] cameraMatrixArray = new double[CAMERA_MATRIX_ROWS * CAMERA_MATRIX_COLS];
        cameraMatrix.get(0,  0, cameraMatrixArray);
        for (int i = 0; i < CAMERA_MATRIX_ROWS; i++) {
            for (int j = 0; j < CAMERA_MATRIX_COLS; j++) {
                Integer id = i * CAMERA_MATRIX_ROWS + j;
                editor.putFloat(id.toString(), (float)cameraMatrixArray[id]);
            }
        }

        double[] distortionCoefficientsArray = new double[DISTORTION_COEFFICIENTS_SIZE];
        distortionCoefficients.get(0, 0, distortionCoefficientsArray);
        int shift = CAMERA_MATRIX_ROWS * CAMERA_MATRIX_COLS;
        for (Integer i = shift; i < DISTORTION_COEFFICIENTS_SIZE + shift; i++) {
            editor.putFloat(i.toString(), (float)distortionCoefficientsArray[i-shift]);
        }

        editor.commit();
        log_d("Saved camera matrix: " + cameraMatrix.dump());
        log_d("Saved distortion coefficients: " + distortionCoefficients.dump());
    }


/** 
 *  tryLoad
 */
    public static boolean tryLoad(Activity activity, Mat cameraMatrix, Mat distortionCoefficients) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getFloat("0", -1) == -1) {
            log_d( "No previous calibration results found");
            return false;
        }

        double[] cameraMatrixArray = new double[CAMERA_MATRIX_ROWS * CAMERA_MATRIX_COLS];
        for (int i = 0; i < CAMERA_MATRIX_ROWS; i++) {
            for (int j = 0; j < CAMERA_MATRIX_COLS; j++) {
                Integer id = i * CAMERA_MATRIX_ROWS + j;
                cameraMatrixArray[id] = sharedPref.getFloat(id.toString(), -1);
            }
        }
        cameraMatrix.put(0, 0, cameraMatrixArray);
        log_d( "Loaded camera matrix: " + cameraMatrix.dump());

        double[] distortionCoefficientsArray = new double[DISTORTION_COEFFICIENTS_SIZE];
        int shift = CAMERA_MATRIX_ROWS * CAMERA_MATRIX_COLS;
        for (Integer i = shift; i < DISTORTION_COEFFICIENTS_SIZE + shift; i++) {
            distortionCoefficientsArray[i - shift] = sharedPref.getFloat(i.toString(), -1);
        }
        distortionCoefficients.put(0, 0, distortionCoefficientsArray);
        log_d("Loaded distortion coefficients: " + distortionCoefficients.dump());

        return true;
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class CalibrationResult
