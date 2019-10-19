/**
 * Camera2 Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera223;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.List;


import jp.ohwada.android.camera223.util.Permission;


/**
 *  class SettingsActivity
 */
public class SettingsActivity extends PreferenceActivity 
implements
        SettingsFragment.FragmentListener {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";


    /**
     * Request code for Permissions
     */
    private static final int REQUEST_CODE_AUDIO = MainActivity.REQUEST_CODE_AUDIO;


/**
 *  Key for Preferences 
 */
    private final static String KEY_AUDIO = SettingsFragment.KEY_AUDIO;


    /**
     * Requesting Permission class for RECORD_AUDIO
     */
    private Permission mAudioPerm;


/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    mAudioPerm = new Permission(this);
    mAudioPerm.setPermission(Manifest.permission.RECORD_AUDIO);
    mAudioPerm.setRequestCode(REQUEST_CODE_AUDIO);

    }


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
} // onResume


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        log_d("onRequestPermissionsResult: " + requestCode);
        switch(requestCode) {
            case REQUEST_CODE_AUDIO:
                    mAudioPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults);
                    // nothing to do
                    break;
        }
} // onRequestPermissionsResult



/**
 * onSwitchPreferenceChange
 */ 
    @Override
    public void onSwitchPreferenceChange(String key, boolean value) {
        log_d("onSwitchPreferenceChange: " + key + " , " + value);
        if (KEY_AUDIO.equals(key) && value) {
                // request Permission,  when enable the feature
                mAudioPerm.requestPermissions();
        }
    }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class SettingsActivity
