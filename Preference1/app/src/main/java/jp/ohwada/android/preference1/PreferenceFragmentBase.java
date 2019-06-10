/** 
 *  Preference Sample
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.preference1;


import android.annotation.TargetApi;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;


/**
  *  class PreferenceFragmentBase
  * This fragment shows general preferences only. It is used when the
  * activity is showing a two-pane settings UI.
  */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public  class PreferenceFragmentBase extends PreferenceFragment {

    // debug
	protected final static boolean D = true;
    protected final static String TAG = "Preference";
    protected final static String TAG_SUB = "PreferenceFragmentBase";

    /**
     * bindPreferenceSummaryToValue
     * Binds a preference's summary to its value. More specifically, when the
     */
    protected static void bindPreferenceSummaryToValue(Preference preference) {
            if(preference == null) return;

            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately 
            // with the preference's current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


/**
 * write into logcat
 */ 
 protected static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


    /**
     * sBindPreferenceSummaryToValueListener
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    protected static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringPreference = preference.toString();
            String stringValue = value.toString();
            log_d("onPreferenceChange: " + stringPreference + " , " + stringValue);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
     
                // Set the summary to reflect the new value.
                CharSequence summary =  (index >= 0)?
                                 listPreference.getEntries()[index]
                                : null;
                preference.setSummary(summary);
                log_d("ListPreference: setSummary: " +  summary);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Context context = preference.getContext();
                    Uri uri = Uri.parse(stringValue);
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            context, uri);

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                        log_d("RingtonePreference: setSummary: " + name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
                log_d("others: setSummary: " + stringValue);
            }
            return true;

        } // onPreferenceChange

    }; // sBindPreferenceSummaryToValueListener



} // class PreferenceFragmentBase 

