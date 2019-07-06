/**
 * Camera2 Sample
 * SettingPreferenceFragment
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera216;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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


/**
  * class SettingPreferenceFragment
  */
public class SettingPreferenceFragment extends PreferenceFragment {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "SettingPreferenceFragment";


/**
 *  Key for Preferences 
 */
    public final static String KEY_LOCATION = "switch_location";
    public final static String KEY_STORAGE = "switch_storage";


/**
 *  interface FragmentListener
 */
public interface FragmentListener {
    void onSwitchPreferenceChange(String key, boolean value);
}


/**
 *  FragmentListener
 */
    private FragmentListener mListener;


/** 
 *  onCreate
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_setting);

            Preference preference_location = findPreference(KEY_LOCATION);
            preference_location.setOnPreferenceChangeListener(mPreferenceChangeListener);

            Preference preference_storage = findPreference(KEY_STORAGE);
            preference_storage.setOnPreferenceChangeListener(mPreferenceChangeListener);

    } // onCreate


/** 
 *  onViewCreated
 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
    } // onViewCreated


/**
 * onAttach
 */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        log_d("onAttach");
        if ( activity instanceof FragmentListener) {
                // set up a mListener, if Activity implement FragmentListener
                mListener = (FragmentListener) activity;
        } else {
                // raise an exception, if not implement
                throw new UnsupportedOperationException(
                 "Listener is not Implementation.");
        }

    } // onAttach


/**
 * write into logcat
 */ 
private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d



    /**
     * PreferenceChangeListener
     * A preference value change mListener that updates the preference's summary
     * to reflect its new value.
     */
private Preference.OnPreferenceChangeListener mPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {


/**
 * onPreferenceChange
 */ 
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringPreference = preference.toString();
            String stringValue = value.toString();
            log_d("onPreferenceChange: " + stringPreference + " , " + stringValue);

            if (preference instanceof SwitchPreference) {
                    procSwitchPreferenceChange( preference, value);
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
                    if( mListener != null) {
                            // call back to Listener
                            mListener.onSwitchPreferenceChange(key, b);
                            log_d("call onSwitchPreferenceChange");
                    }
                    String stringValue = value.toString();
                    setSimpleSummary(preference, stringValue);

} // procSwitchPreferenceChange


/**
 * setSimpleSummary
 * simple string representation.
 */ 
private void setSimpleSummary(Preference preference, String stringValue) {
        preference.setSummary(stringValue);
        log_d("setSummary: " + stringValue);
} // setSimpleSummary


    }; // mPreferenceChangeListener


} // class SettingPreferenceFragment
