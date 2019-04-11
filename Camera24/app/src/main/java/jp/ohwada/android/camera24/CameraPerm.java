/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera24;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


/**
 * class CameraPerm
 */
public class CameraPerm  {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Camera2";
    	private final static String TAG_SUB = "CameraPerm";


    /**
     * Request code for camera permissions.
     */
    private static final int REQUEST_mPermissions = 1;

    /**
     * Permissions required to take a picture.
     */
    private String[] mPermissions = { Manifest.permission.CAMERA };

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
 * setPermissions
 */
public void setPermissions( String[] permissions ) {
mPermissions = permissions;
} // setPermissions

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

        if ( requestCode != REQUEST_mPermissions ) {
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
            ActivityCompat.requestPermissions( mActivity, mPermissions, REQUEST_mPermissions );

} // requestCameraPermissions

    /**
     * Tells whether all the necessary permissions are granted to this app.
     *
     * @return True if all the required permissions are granted.
     */
private boolean checkSelfPermission() {
log_d("hasAllPermissionsGranted");
        for (String permission : mPermissions) {
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
        for (String permission : mPermissions) {

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
