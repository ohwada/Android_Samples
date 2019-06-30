/**
 * Cloud Vision Sample
 * Permission
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.cloudvision1;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *  class Permission
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class Permission {

    // debug
	private final static boolean D = true;
    private final static String TAG = "CloudVision";
    private final static String TAG_SUB = "Permission";


/**
  * Activity, Context
 */ 
        private Activity mActivity;
        private Context mContext;


/**
  * Request Code
 */ 
    private int mRequestCode;


/**
  * Permissions
 */ 
    private String[] mPermissions = new String[1];


/**
  * constractor
 */ 
public Permission(Activity activity) {
    mActivity = activity;
    mContext  = activity;
}

/**
  * setRequestCode
 */ 
public void setRequestCode(int code) {
    mRequestCode = code;
}

/**
  * setPermission
 */ 
public void setPermission(String permission) {
    mPermissions[0] = permission;
}

/**
  * setPermissions
 */ 
public void setPermissions(String... permissions) {
    mPermissions = permissions;
}



/**
  * requestPermission
  * @return boolean
  * true :  requests permissions
  * false :  not requests permissions, all permissions are granted 
 */ 
public boolean requestPermissions() {
        // permissions
        List<String> list = getRequestPermissions(mPermissions);
        int size = list.size();
        if(size == 0) return false;

       String[] perms = list.toArray( new String[size] );
        requestPermissions(
             mActivity, perms, mRequestCode);

        return true;
} // requestPermissions


/**
  * getRequestPermissions
 */ 
    public List<String> getRequestPermissions(String[] permissions) {

        List<String> permissionsNeeded = new ArrayList<>();

        for (int i=0; i<permissions.length; i++) {
                String permission = permissions[i];
                boolean hasPermission = checkSelfPermission(mContext, permission);
                if (!hasPermission) {
                        permissionsNeeded.add(permission);
                }
        } // for
        return permissionsNeeded;
}

 
/**
  * requestPermissions
 */ 
public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
            ActivityCompat.requestPermissions(activity, permissions,
requestCode);
} // requestPermissions


/**
  * checkSelfPermission
 */ 
public static boolean checkSelfPermission(Context context, String permission) {
        int permissionCheck = ActivityCompat.checkSelfPermission(context, permission);
        boolean hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);
        return hasPermission;
} // checkSelfPermission


/** 
 *  onRequestPermissionsResult
 */
public boolean onRequestPermissionsResult(
            int requestCode,  String[] permissions,  int[] grantResults) {
    return isGrantRequestPermissionsResult(requestCode, grantResults);
} // onRequestPermissionsResult


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
public boolean isGrantRequestPermissionsResult( int[] grantResults) {

            boolean is_deny = false;
            for (int i=0; i<grantResults.length; i++ ) {
                    int result = grantResults[i];
                    if (result !=   PackageManager.PERMISSION_GRANTED) {
                        is_deny = true;
                    }
            } // for
        return  !is_deny;

} // isGrantRequestPermissionsResult


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class Permission
