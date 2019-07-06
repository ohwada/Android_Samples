/**
 * Camera2 Sample
 * Permission
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera216.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


/**
 * class Permission
 * base Class for request permission
 */
public class Permission  {

    // debug
	protected final static boolean D = true;
    protected final static String TAG = "Camera2";
    protected final static String TAG_PERM = "Permission";


 /**
  * Request code 
  */
    protected int mRequestCode = 100;


/**
  * Permission to request
  */
   protected String mPermission;


/**
  * Permissions to request
  */
   protected String[] mPermissions = new String[1];


/**
  * ctivity, Context
  */
    protected Activity  mActivity;
    protected Context mContext;


/**
 * constractor
 */
public Permission(Activity activity)  {
    mActivity = activity;
    mContext = activity;
}


 /**
 * isPermGranted
  */
protected static boolean isPermGranted(Context context, String permission) {
    int perm = ActivityCompat.checkSelfPermission( context, permission );
    if ( perm == PackageManager.PERMISSION_GRANTED ) {
                return true;
    }
    return false;
} // isPermGranted


 /**
 * checkSelfPermission
  */
public boolean checkSelfPermission() {
    int perm = ActivityCompat.checkSelfPermission( mContext, mPermission );
    if ( perm == PackageManager.PERMISSION_GRANTED ) {
                return true;
    }
    return false;
} // isCaremaGranted


/**
 * setPermission
 */
public void setPermission(String permission) { 
    mPermission = permission;
    mPermissions[0] = permission;
}


/**
 * setPermissions
 */
public void setPermissions(String[] permissions) { 
    mPermissions = permissions;
}


/**
 * setRequestCode
 */
public void setRequestCode(int code) { 
    mRequestCode = code;
}


/**
 * onRequestPermissionsResult
 */
public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        return isGrantRequestPermissionsResult( requestCode, grantResults);

} // onRequestPermissionsResul


/**
 * isGrantRequestPermissionsResult
 */
public boolean isGrantRequestPermissionsResult(int requestCode, int[] grantResults) {

        if ( requestCode != mRequestCode ) {
                return false;
        }
        return isGrantRequestPermissionsResult(grantResults);

} // isGrantRequestPermissionsResult


/**
 * isGrantRequestPermissionsResult
 */
public boolean isGrantRequestPermissionsResult(int[] grantResults) {

            boolean is_deny = false;
            for (int result : grantResults) {
                    if (result !=   PackageManager.PERMISSION_GRANTED) {
                        is_deny = true;
                    }
            } // for
            return  !is_deny;

} // isGrantRequestPermissionsResult


 /**
 * requestPermissions
 */
public boolean requestPermissions() {
    log_perm("equestPermissions");
    if ( !checkSelfPermissionsCompat() ) {
        // request Permissins, if not granted
        requestPermissionsCompat();
        return true;
    }
    return false;
} // requestPermissionissions


/**
 * Request permissions 
  */
protected void requestPermissionsCompat() {
        log_perm("requestPermissionsCompat");
            ActivityCompat.requestPermissions( mActivity, mPermissions, mRequestCode );
} // requestPermissionissions


/**
 * checkSelfPermissionsCompat
  */
protected boolean checkSelfPermissionsCompat() {
        int length = mPermissions.length;
        log_perm("checkSelfPermissionsCompat: length= " + length);
        for (int i=0; i<length; i++ ) {
            String permission = mPermissions[i];
            log_perm("permission: " + permission);
                if ( ActivityCompat.checkSelfPermission( mContext, permission )
                    != PackageManager.PERMISSION_GRANTED ) {
                        return false;
                }
        } // for
        return true;
} // checkSelfPermissionsCompat


/**
 * write into logcat
 */ 
protected void log_perm( String msg ) {
	    if (D) Log.d( TAG, TAG_PERM + " " + msg );
} // log_d


} // class Permission
