/**
 *  Voice Recorder Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.voicerecorder1;

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
 * class AudioPerm
 */
public class AudioPerm  {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Camera2";
    	private final static String TAG_SUB = "AudioPerm";


    /**
     * Request code for camera permissions.
     */
    private static final int REQUEST_PERMISSION = 1;


    /**
     * Permissions required 
     */
    private static final  String PERMISSION_AUDIO = Manifest.permission.RECORD_AUDIO;

    private String[] PERMISSIONS = { PERMISSION_AUDIO };


    private Activity  mActivity;
    private Context mContext;


/**
 * constractor
 */
public AudioPerm(Activity activity)  {
    mActivity = activity;
    mContext = activity;
}




 /**
 * isCaremaGranted
  */
public  static boolean isCaremaGranted(Context context) {
    int perm = ActivityCompat.checkSelfPermission( context, PERMISSION_AUDIO );
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
                    toast_long( R.string.request_permission );
                   mActivity. finish();
    }

} // onRequestPermissionsResul

/**
 * procRequestPermissionsResult
 */
private boolean isGrantRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ( requestCode != REQUEST_PERMISSION ) {
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
 * requestAudioPermissions
 */
public boolean requestAudioPermissions() {
    if ( !checkSelfPermissions() ) {
        showPermissionConfirmationDialog();
        return true;
    }
    return false;
} // requestAudioPermissions

    /**
     * Requests permissions necessary to use camera and save pictures.
     */
private void requestPermissions() {
log_d("requestPermissions");
            ActivityCompat.requestPermissions( mActivity, PERMISSIONS, REQUEST_PERMISSION );

} // requestAudioPermissions

public boolean checkSelfPermissions() {
        for (String permission : PERMISSIONS) {
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
        for (String permission : PERMISSIONS) {

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


} // class AudioPerm
