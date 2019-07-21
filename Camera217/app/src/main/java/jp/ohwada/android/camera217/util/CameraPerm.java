/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera217.util;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import jp.ohwada.android.camera217.R;


/**
 * class CameraPerm
 */
public class CameraPerm  extends Permission{

        // debug
    	private final static String TAG_SUB = "CameraPerm";

    /**
     * Request code for camera permissions.
     */
    private static final int REQUEST_CODE = 100;



    /**
     * Permission to use Camera
     */
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;


    /**
     * Message for request permission Dialog
     */
    private String mRequestMessage;



/**
 * constractor
 */
public CameraPerm(Activity activity)  {
    super(activity);
    setPermission( PERMISSION_CAMERA );
    setRequestCode(REQUEST_CODE);
    mRequestMessage = 
    activity.getString(R.string.request_permission);
}


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
    return isPermGranted(context, PERMISSION_CAMERA);
} // isCaremaGranted


/**
 * onRequestPermissionsResult
 * return to Activity,  if granted
 *  finish Activity, if not granted 
 */
@Override
public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    boolean ret = isGrantRequestPermissionsResult( requestCode, permissions, grantResults);
    if (!ret) {
                // finish Activity, if not granted
                toast_long( mRequestMessage );
                  mActivity. finish();
    }
    return ret;
} // onRequestPermissionsResul



 /**
 * requestCameraPermissions
 */
public boolean requestCameraPermissions() {
    if ( !checkSelfPermissionsCompat() ) {
        // show dialog, if not granted
        showPermissionConfirmationDialog();
        return true;
    }
    return false;
} // requestCameraPermissions


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
