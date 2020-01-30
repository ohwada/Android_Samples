/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.simplecropview1;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
//import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *  class Permission
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
// public class ActivityPermission {
public class FragmentPermission extends ContextPermission {

    // debug
    private final static String TAG = "ActivityPermission";


/**
  * Activity, Context
 */ 
        //private Activity mActivity;
        //private Context mContext;

    private Fragment  mFragment;


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
public FragmentPermission(Fragment fragment) {
    super();
    setContext(fragment.getContext());
    mFragment = fragment;
    //mActivity = activity;
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
             mFragment, perms, mRequestCode);

        return true;
} // requestPermissions

 
/**
  * requestPermissions
 */ 
public void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
            fragment.requestPermissions(permissions, requestCode);
} // requestPermissions



} // class Permission
