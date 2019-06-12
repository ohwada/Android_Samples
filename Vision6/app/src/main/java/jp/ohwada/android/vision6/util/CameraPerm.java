/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision6.util;

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

import jp.ohwada.android.vision6.R;

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
    private static final int REQUEST_PERMISSIONS = 1;


    /**
     * Permissions required to take a picture.
     */
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    private String[] mPermissions = { PERMISSION_CAMERA };


    /**
     * Message of Permissions required to take a picture.
     */
    private String mRequestMessage;


    private Activity  mActivity;
    private Context mContext;


/**
 * constractor
 */
public CameraPerm(Activity activity)  {
    mActivity = activity;
    mContext = activity;
    mRequestMessage = 
    activity.getString(R.string.request_permission);
}


/**
 * setPermissions
 */
public void setPermissions( String[] permissions ) {
mPermissions = permissions;
} // setPermissions


/**
 * setRequestMessage
 */
public void setRequestMessage( int res_id ) {
    mRequestMessage = mContext.getString( res_id );
} // setPermissions


/**
 * setRequestMessage
 */
public void setRequestMessage( String msg ) {
    mRequestMessage = msg;
} // setPermissions


 /**
 * isCaremaGranted
  */
public  static boolean isCameraGranted(Context context) {
    int perm = ActivityCompat.checkSelfPermission( context, PERMISSION_CAMERA );
    if ( perm == PackageManager.PERMISSION_GRANTED ) {
                return true;
    }
    return false;
} // isCaremaGranted


/**
 * onRequestPermissionsResult
 */
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    boolean ret = isGrantRequestPermissionsResult( requestCode, permissions, grantResults);
    if (!ret) {
                    toast_long( mRequestMessage );
                   mActivity. finish();
    }

} // onRequestPermissionsResul

/**
 * procRequestPermissionsResult
 */
private boolean isGrantRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ( requestCode != REQUEST_PERMISSIONS ) {
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
    if ( !checkSelfPermissions() ) {
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
            ActivityCompat.requestPermissions( mActivity, mPermissions, REQUEST_PERMISSIONS );

} // requestCameraPermissions

public boolean checkSelfPermissions() {
        for (String permission : mPermissions) {
            if ( ActivityCompat.checkSelfPermission( mContext, permission )
                    != PackageManager.PERMISSION_GRANTED ) {
                return false;
            }
        }
        return true;
} // checkSelfPermissions



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
                .setMessage(mRequestMessage)
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
                                    toast_long(mRequestMessage);
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
