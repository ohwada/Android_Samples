/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.simplecropview1;


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
// public class ActivityPermission {
public class ActivityPermission extends ContextPermission {

    // debug
    private final static String TAG = "ActivityPermission";


/**
  * Activity, Context
 */ 
        private Activity mActivity;
        //private Context mContext;


/**
  * Request Code
 */ 
    //private int mRequestCode;


/**
  * Permissions
 */ 
    //private String[] mPermissions = new String[1];


/**
  * constractor
 */ 
public ActivityPermission(Activity activity) {
    super();
    setContext(activity);
    mActivity = activity;
    //mContext  = activity;
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
  * requestPermissions
 */ 
public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
            ActivityCompat.requestPermissions(activity, permissions,
requestCode);
} // requestPermissions



} // class Permission
