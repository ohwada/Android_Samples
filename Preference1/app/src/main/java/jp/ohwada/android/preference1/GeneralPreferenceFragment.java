/** 
 *  Preference Sample
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.preference1;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;


/**
  * class GeneralPreferenceFragment
  * This fragment shows general preferences only. It is used when the
  * activity is showing a two-pane settings UI.
  */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class GeneralPreferenceFragment extends PreferenceFragmentBase {

/**
 *  Key for Preferences 
 */
    public final static String KEY_SOCIAL = "switch_social_recommendations";
   public final static String KEY_NAME = "text_display_name";
    public final static String KEY_FRIEND_MESSAGE = "list_add_friends_to_messages";


/** 
 *  onCreate
 */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(KEY_NAME));
            bindPreferenceSummaryToValue(findPreference(KEY_FRIEND_MESSAGE));
        }


/** 
 *  onOptionsItemSelected
 */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


} // class GeneralPreferenceFragment
