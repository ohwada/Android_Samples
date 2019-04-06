/**
 * TextureView Sample
 * Camera Preview using camera 2 API
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.textureview1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;

import android.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * class CameraPerm
 */
public class CameraPerm  {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "TextureView";
    	private final static String TAG_SUB = "CameraPerm";


    /**
     * Request code for camera permissions.
     */
    private static final int REQUEST_CAMERA_PERMISSIONS = 1;

    /**
     * Permissions required to take a picture.
     */
    private static final String[] CAMERA_PERMISSIONS = { Manifest.permission.CAMERA };

    private Activity  mActivity;
    private Context mContext;

/**
 * constractor
 */
public CameraPerm(Activity activity)  {
    mActivity = activity;
mContext = activity;
}


/**
 * onRequestPermissionsResult
 */
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    boolean ret = isGrantRequestPermissionsResult( requestCode, permissions, grantResults);
    if (!ret) {
                    toast_long( R.string.request_permission );
                   mActivity. finish();
    }

} // onRequestPermissionsResul

/**
 * procRequestPermissionsResult
 */
private boolean isGrantRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ( requestCode != REQUEST_CAMERA_PERMISSIONS ) {
            return false;
    }
boolean is_deny = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    is_deny = true;
                }
            } // for

    return  !is_deny;

} // isGrantRequestPermissionsResult

 /**
 * requestCameraPermissions
 */
public boolean requestCameraPermissions() {

    if ( !checkSelfPermission() ) {
        showPermissionConfirmationDialog();
        return true;
    }
    return false;

} // requestCameraPermissions

    /**
     * Requests permissions necessary to use camera and save pictures.
     */
private void requestPermissions() {
log_d("requestPermissions");
            ActivityCompat.requestPermissions( mActivity, CAMERA_PERMISSIONS, REQUEST_CAMERA_PERMISSIONS );

} // requestCameraPermissions

    /**
     * Tells whether all the necessary permissions are granted to this app.
     *
     * @return True if all the required permissions are granted.
     */
private boolean checkSelfPermission() {
log_d("hasAllPermissionsGranted");
        for (String permission : CAMERA_PERMISSIONS) {
            if ( ActivityCompat.checkSelfPermission( mContext, permission )
                    != PackageManager.PERMISSION_GRANTED ) {
                return false;
            }
        }
        return true;
} // hasAllPermissionsGranted


/**
 * Gets whether you should show UI with rationale for requesting the permissions.
 *
  * @return True if the UI should be shown.
 */
public boolean shouldShowRationale() {
        for (String permission : CAMERA_PERMISSIONS) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                return true;
            } // if

    } // for
        return false;

} // shouldShowRationale



/**
 * A dialog that explains about the necessary permissions.
  */
public void showPermissionConfirmationDialog() {

    new AlertDialog.Builder(mContext)
                .setMessage(R.string.request_permission)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           requestPermissions();
                        }
}) // setPositiveButton
                .setNegativeButton(R.string.button_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mActivity.finish();
                                    toast_long(R.string.request_permission);
                                }
                            }) // setNegativeButton
                    .show();

} // showPermissionConfirmationDialog


/**
 * toast_long
 */
private void toast_long( int res_id ) {
		ToastMaster.makeText( mContext, res_id, Toast.LENGTH_LONG ).show();
} // toast_long


/**
 * toast_long
 */
private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
} // toast_long


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class CameraPerm
