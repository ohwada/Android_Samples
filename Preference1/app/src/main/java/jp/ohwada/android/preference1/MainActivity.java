/** 
 *  Preference Sample
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.preference1;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v7.app.AppCompatActivity;


/**
 *  class MainActivity
 */
public class MainActivity extends AppCompatActivity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Preference";
    private final static String TAG_SUB = "MainActivity";


/**
 *  Key for Preferences 
 */
    private final static String KEY_SOCIAL = GeneralPreferenceFragment.KEY_SOCIAL;

    private final static String KEY_NAME = GeneralPreferenceFragment.KEY_NAME;

    private final static String KEY_FRIEND_MESSAGE = GeneralPreferenceFragment.KEY_FRIEND_MESSAGE;


/**
 *  String Constant
 */
    private final static String LF = "\n";
     private final static String COLON = " : ";
     private final static String UNKOWN = "unkown";


/**
 *  TextViewGeneral
 */
    private TextView mTextViewGeneral;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewGeneral = (TextView) findViewById(R.id.TextView_general);

    } // onCreate


/**
 *  onResume
 */
    @Override
    protected void onResume() {
        super.onResume();
        showTextView() ;
}


/**
 * onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // onCreateOptionsMenu


/**
 * onOptionsItemSelected 
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
                startSettingsActivity();
        } else if (id == R.id.action_about) {
                showToast("About");
        }
        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected


/**
 * showTextView
 */
private void showTextView() {

   SharedPreferences  pref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = pref.getString(KEY_NAME, null);
        String message_value = pref.getString(KEY_FRIEND_MESSAGE, null);
        boolean social = pref.getBoolean(KEY_SOCIAL, false);

        String label_name = getString(R.string.pref_title_display_name);
        String label_message = getString(R.string.pref_title_add_friends_to_messages);
        String label_social = getString(R.string.pref_title_social_recommendations);

        String message = getFriendMessageTitle(message_value);

        String text = "";
        text += label_name + COLON + name + LF + LF;
        text += label_message + COLON + message + LF + LF;
        text += label_social + COLON + social + LF + LF;
        mTextViewGeneral.setText(text);
}


/**
 * getFriendMessageTitle
 */
private String getFriendMessageTitle(String pref_value) {
        if(pref_value == null)  return null;

        Resources r = getResources();
        String[] titles =  r.getStringArray(R.array.pref_example_list_titles);
        String[] values = r.getStringArray(R.array.pref_example_list_values);
        int index = 0;
        for(int i=0;i<values.length; i++) {
            String value = values[i];
            if(pref_value.equals(value)) {
                index = i;
                break;
            }
        } // for

        String title =  UNKOWN;
        if ((index >= 0)&&(index < titles.length)) {
                title = titles[index];
        }
    log_d(" title=" +  title + " , index=" + index + " , value=" + pref_value );
        return title;
}



/**
 * startSettingsActivity
 */
private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
}


/**
 * showToast
 */
	private void showToast( String msg ) {
		Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // showToast


/**
 * write into logcat
 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class MainActivity