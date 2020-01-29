/**
 * Crop Intent Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.cropintent1;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * class ExternalStoragePermission
 */
public class ExternalStoragePermission {

    // debug
    private static final String TAG = "ExternalStoragePermission";


    /**
     * Permissions required to read and write external storage.
     */
    private static String[] EXTERNAL_STORAGE_PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE, 
        Manifest.permission.WRITE_EXTERNAL_STORAGE };


    public ExternalStoragePermission() {
        // nop
    }

    /**
     * requestPermissions
     */
    public static void requestPermissions(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, EXTERNAL_STORAGE_PERMISSIONS, requestCode);
    }


    /**
     * checkePermissions
     */
public static boolean checkPermissions(Activity activity) {
        for (String permission : EXTERNAL_STORAGE_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
}


    /**
     * verifyPermissions
     */
    public static boolean verifyPermissions(int[] grantResults) {

        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        } // for

        return true;
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    Log.d( TAG, msg );
} 


} // class ExternalStoragePermission
