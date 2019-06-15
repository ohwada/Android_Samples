/**
 * Camera2 Sample
 * SettingsPreferenceFragment
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.camera213;


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
  * class SettingsPreferenceFragment
  */
public class SettingsPreferenceFragment extends PreferenceFragment {


    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "SettingsPreferenceFragment";


/**
 *  Key for Preferences 
 */
    public final static String KEY_AUDIO = "switch_audio";
    public final static String KEY_STORAGE = "switch_storage";
    public final static String KEY_FORMAT = "list_output_format";


/**
 *  interface FragmentListener
 */
public interface FragmentListener {
    void onSwitchPreferenceChange(String key, boolean value);
}


/**
 *  FragmentListener
 */
    private FragmentListener mListener = null;



/**
 *   Activity, Context
 */
    private Activity mActivity;
    private Context mContext;



/** 
 *  onCreate
 */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_settings);

            // bind the mListener to each preference
            Preference preference_audio = findPreference(KEY_AUDIO);
            preference_audio.setOnPreferenceChangeListener(mPreferenceChangeListener);

            Preference preference_storage = findPreference(KEY_STORAGE);
            preference_storage.setOnPreferenceChangeListener(mPreferenceChangeListener);

            Preference preference_format = findPreference(KEY_FORMAT);
            String value = getValue(preference_format);
            preference_format.setOnPreferenceChangeListener(mPreferenceChangeListener);
            // show default value
            mPreferenceChangeListener. onPreferenceChange(preference_format, value);
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
 * onAttach
 */
@Override
public void onAttach(Activity activity) {
    super.onAttach(activity);
    log_d("onAttach");
    mActivity = activity;
    mContext = activity;
    if ( activity instanceof FragmentListener) {
            // set up the mListener,
            // if Activity implement FragmentListener
            mListener = (FragmentListener) activity;
    } else {
            // raise an exception, if not implement
            throw new UnsupportedOperationException(
                 "Listener is not Implementation.");
    }
}


/** 
 *  getValue
 */
private String getValue(Preference preference) {
        Context context = preference.getContext();
        String key = preference.getKey();
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
                    if( mListener != null) {
                            // call back to Listener
                            mListener.onSwitchPreferenceChange(key, b);
                            log_d("call onSwitchPreferenceChange: " + key);
                    }
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


} // class SettingsPreferenceFragment
