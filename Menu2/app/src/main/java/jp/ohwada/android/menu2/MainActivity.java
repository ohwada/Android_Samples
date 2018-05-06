/**
* Menu Sample
*  with Support Libraly
 * 2018-05-01 K.OHWADA 
 */

package jp.ohwada.android.menu2;


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

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.ShareCompat;


/**
 *  class MainActivity
 */
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Menu";
    	private final static String TAG_SUB = "MainActivity";



/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        if (id == R.id.action_settings) {
                toast_short("Settings");
        } else if (id == R.id.action_share) {
                toast_short("Share");
        } else if (id == R.id.action_about) {
                toast_short("About");
        }
        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected


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