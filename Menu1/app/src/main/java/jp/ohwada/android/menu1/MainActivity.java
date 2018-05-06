/**
 * Menu Sample
*  without Support Libraly
 * 2018-03-01 K.OHWADA
 */

package jp.ohwada.android.menu1;

import android.app.Activity;
import android.content.Context;


import android.os.Bundle;
import android.util.Log;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
/* MainActivity
*/
public class MainActivity extends Activity {
   
   // debug
    private static final boolean D = true;
    private static final String TAG = "Menu";
    private static final String TAG_SUB = "MainActivity";
   
   
/**
/* == onCreate ==
*/
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);      
         setContentView( R.layout.activity_main );                        
           } // onCreate
           

    /**
     * === onCreateOptionsMenu ===
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        log_d( "onCreateOptionsMenu" );
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
 
 
  
    /**
     * === onOptionsItemSelected ===
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        log_d("onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            toast_short("Settings");
        } else if (id == R.id.action_about) {
            toast_short("About");
        } 
        return true;
    } // onOptionsItemSelected


      /**
     * toast short
     */       
    private void toast_short( String msg ) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    

    /**
     * log_d
     */
    private  void log_d( String msg ) {
        
        if ( D ) Log.d( TAG, TAG_SUB + " " + msg );
    } // log_d end


} // end of MainActivity