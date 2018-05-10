/**
* Share Reciever Sample
 * with ShareCompat
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.sharerecieversample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


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

    	private final static String LF = "\n";

private TextView mTextView1;
private ImageView mImageView1;

/**
 *  == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

mTextView1 = (TextView) findViewById(R.id.TextView_1);
mImageView1 = (ImageView) findViewById(R.id.ImageView_1); 

procReader();

    } // onCreate

/**
 *  procReader
 */
private void procReader() {

ShareCompat.IntentReader reader = ShareCompat.IntentReader.from(this);

if (reader == null) return;

Drawable drawableIcon = reader.getCallingApplicationIcon();

 String label =  (String)reader.getCallingApplicationLabel();
 String callingPackage = reader.getCallingPackage();
 String text = (String)reader.getText();
 String type = reader.getType();
 String subject = reader.getSubject();

 String callingActivity = "null"; 
ComponentName componentName = reader.getCallingActivity(); if (componentName != null) {
    callingActivity = componentName.getClassName(); 
}

String detail = "ShareCompat.IntentReader"+ LF;
detail += "ApplicationLabel=" +  label + LF;
detail += "CallingPackage=" + callingPackage + LF;
detail += "CallingActivity=" + callingActivity + LF;
detail += "Subject="  + subject + LF;
 detail += "Text=" + text + LF;
detail += "Type="  + type + LF;

log_d(detail);

mImageView1.setImageDrawable(drawableIcon); 
mTextView1.setText(detail);

    } // onCreate


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