/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.simplecropview1;


//import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.ContextCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *  class Permission
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class ContextPermission {

    // debug
    private final static String TAG = "ContextPermission";


/**
  * Activity, Context
 */ 
        //protected Activity mActivity;
        protected Context mContext;


/**
  * Request Code
 */ 
    protected int mRequestCode;


/**
  * Permissions
 */ 
    protected String[] mPermissions = new String[1];


/**
  * constractor
 */ 
//public Permission(Activity activity) {
public ContextPermission() {
    // nop
}


/**
  * setContext
 */ 
protected void setContext(Context context) {
    mContext = context;
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

        //requestPermissions(
             //mContext, perms, mRequestCode);

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
  * checkSelfPermission
 */ 
public static boolean checkSelfPermission(Context context, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
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


} // class ContextPermission
