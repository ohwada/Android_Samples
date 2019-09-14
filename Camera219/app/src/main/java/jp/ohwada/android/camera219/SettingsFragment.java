/**
 * Camera2 Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera219;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.UUID;


/**
  * class SettingsFragment
  */
public class SettingsFragment extends PreferenceFragment {


    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "SettingsFragment";


/**
 *  Key for Preferences 
 */
    public final static String KEY_PORT = "port";
   public final static String KEY_RESOLUTION = "resolution";
    public final static String KEY_ROTATION = "rotation";
    public final static String KEY_ABOVE_LOCK_SCREEN = "above_lock_screen";
   public final static String KEY_ALLOW_ALL_IPS = "allow_all_ips";
   public final static String KEY_SSDP_ID = "ssdp_id";
    public final static String KEY_DISCOVERABLE = "discoverable";
   public final static String KEY_RUN_ON_BOOT = "run_on_boot";
public final static String KEY_APP_VERSION = "app_version";


/** 
 *  onCreate
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_settings);

            // bind the Listener to each preference
            bindListChangeListener(KEY_RESOLUTION);
            bindListChangeListener(KEY_ROTATION);


            // processing for each preference
            bindPortChangeListener();
            populateDiscoverableId();
            enableIpWarning();
            updateVersionSummary();

    }

/**
 * bindPortChangeListener
 */ 
    private void  bindPortChangeListener() {
        EditTextPreference portPref = (EditTextPreference) findPreference(KEY_PORT);
        portPref.setOnPreferenceChangeListener(mPortChangeListener);
        // show default value
        String value = portPref.getText();
        mPortChangeListener. onPreferenceChange(portPref, value);
}


/**
 * mPortChangeListener
 */ 
private Preference.OnPreferenceChangeListener mPortChangeListener =
new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                String ipAddress = NetworkUtil.getIPAddress();
                String summary = stringValue + " (on " + ipAddress + ")";
                preference.setSummary(summary);
                return true;
            }
}; // OnPreferenceChangeListener


/**
 * populateDiscoverableId
 */ 
    private void populateDiscoverableId() {
        EditTextPreference idPref = (EditTextPreference) findPreference(KEY_SSDP_ID);
        String id = idPref.getText();
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            idPref.setText(id);
        }
        idPref.setSummary(id);
    }


/**
 * enableIpWarning
 */ 
private void enableIpWarning() {
        SwitchPreference ipPref = (SwitchPreference) findPreference(KEY_ALLOW_ALL_IPS);
        ipPref.setOnPreferenceChangeListener(mIpsChangeListener);
}


/**
 * mIpsChangeListener
 */ 
private Preference.OnPreferenceChangeListener mIpsChangeListener =
new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SwitchPreference ipPref  = (SwitchPreference) preference;
                Boolean val = (Boolean) newValue;
                if (val) {
                    showConfirmDialog(ipPref);
                    return false;
                }
                return true;
            }
}; // OnPreferenceChangeListener


/**
 * showConfirmDialog
 */ 
private void showConfirmDialog( final SwitchPreference ipPref) {
    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.dialog_confirm_title)
                            .setMessage(R.string.dialog_confirm_message)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(R.string.dialog_confirm_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int button) {
                                    // preference wasn't saved anyway; nothing to do...
                                }
                            })
                            .setPositiveButton(R.string.dialog_confirm_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int button) {
                                    ipPref.setChecked(true);
                                }
                            })
                            .create().show();
}

/**
 * updateVersionSummary
 */ 
    private void updateVersionSummary() {

        EditTextPreference versionPref = (EditTextPreference) findPreference(KEY_APP_VERSION);
        versionPref.setSummary(BuildConfig.VERSION_NAME);
}


/** 
 *  bindSwitchChangeListener
 */
private void bindSwitchChangeListener(String key) {
            Preference preference_storage = findPreference(key);
            preference_storage.setOnPreferenceChangeListener(mPreferenceChangeListener);
}


/** 
 *  bindListChangeListener
 */
private void bindListChangeListener(String key) {
           Preference preference = findPreference(key);
            String value = getValue(preference);
            preference.setOnPreferenceChangeListener(mPreferenceChangeListener);
            // show default value
            mPreferenceChangeListener. onPreferenceChange(preference, value);
} 


/** 
 *  onViewCreated
 */
@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set White the background color
        // show the camera preview, when  transparent
        view.setBackgroundColor(Color.WHITE);
}

 
/** 
 *  getValue
 */
private String getValue(Preference preference) {
        Context context = preference.getContext();
        String key = preference.getKey();
        log_d("getValue: key=" + key);
            SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(context);
            String value = pref.getString(key, "");
            return value;
}


/**
 * write into logcat
 */ 
private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
  * PreferenceChangeListener
  * A preference value change mListener 
  * that updates the preference's summary
   * to reflect its new value.
 */
private Preference.OnPreferenceChangeListener mPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringPreference = preference.toString();
            String stringValue = value.toString();
            log_d("onPreferenceChange: " + stringPreference + " , " + stringValue);

            if (preference instanceof SwitchPreference) {
                    procSwitchPreferenceChange( preference, value);

            } else if (preference instanceof ListPreference) {
                    procListPreferenceChange(preference, value);

            } else {
                    // For all other preferences
                    setSimpleSummary(preference, stringValue);

            }
            return true;

        } // onPreferenceChange


/**
 * procSwitchPreferenceChange
 */ 
private void procSwitchPreferenceChange(Preference preference, Object value) {
                    String key = preference.getKey();
                    boolean b =  (boolean) value;
                    String stringValue = value.toString();
                    setSimpleSummary(preference, stringValue);
}


/**
 * procListPreferenceChange
 * For list preferences, look up the correct display value 
 *  in the preference's 'entries' list.
 */ 
private void procListPreferenceChange(Preference preference, Object value) {
            ListPreference listPreference = (ListPreference) preference;
            String stringValue = value.toString();
            int index = listPreference.findIndexOfValue(stringValue);
     
            // Set the summary to reflect the new value.
            CharSequence summary =  (index >= 0)?
                                 listPreference.getEntries()[index]
                                : null;
            preference.setSummary(summary);
            log_d("ListPreference: setSummary: " +  summary);
}


/**
 * setSimpleSummary
 * simple string representation.
 */ 
private void setSimpleSummary(Preference preference, String stringValue) {
        preference.setSummary(stringValue);
        log_d("setSummary: " + stringValue);
}


    }; // PreferenceChangeListener



} // class SettingsFragment
