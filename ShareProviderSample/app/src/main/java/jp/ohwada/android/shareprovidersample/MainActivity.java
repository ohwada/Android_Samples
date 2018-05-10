/**
* Share Provider sample
 * with ShareCompat
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.shareprovidersample;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import android.support.v7.widget.Toolbar;

import android.support.v4.app.ShareCompat;


/**
 *  class MainActivity
 *  reference : https://dev.classmethod.jp/smartphone/android/android-tips-35-sharecompat/
 */

public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Share";
    	private final static String TAG_SUB = "MainActivity";

    	private final static String CHOOSER_TITLE = "Choose Send App";
    	private final static String SUBJECT = "Share Test"; 
    	private final static String TEXT = "This is Share Test Text";
    	private final static String TYPE = "text/plain";              
    	private final static String[] EMAIL_TO = {"user@example.com"};   

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    } // onCreate


/**
 * == onCreateOptionsMenu == 
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // onCreateOptionsMenu


/**
 * == onOptionsItemSelected == 
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share) {
                toast_short("Share");
                createBuilder();
        } else if (id == R.id.action_settings) {
                toast_short("Settings");
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected


/**
 * == onPrepareOptionsMenu == 
 */
@Override
public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    // ShareCompat.IntentBuilder builder = createBuilder();

// TO DO : UnsupportedOperationException: This is not supported, use MenuItemCompat.getActionProvider()
    // ShareCompat.configureMenuItem(menu, R.id.action_share, builder);
     
    return true;
} // onPrepareOptionsMenu



/**
 *  createBuilder
 */
private  ShareCompat.IntentBuilder createBuilder() {

            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this);
           

            builder.setChooserTitle( CHOOSER_TITLE);
            builder.setSubject(SUBJECT);
            builder.setText(TEXT);
            builder.setType(TYPE);
            builder.setEmailTo(EMAIL_TO);
            builder.startChooser();

return builder;
} // createBuilder


   /**
 * toast_short
 */
	private void toast_short( String msg ) {
		Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class MainActivity