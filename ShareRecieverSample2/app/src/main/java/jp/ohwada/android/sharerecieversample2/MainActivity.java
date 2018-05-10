/**
* Share Reciever Sample
 * 2018-05-01 K.OHWADA 
 */

package jp.ohwada.android.sharerecieversample2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Bundle;

 import android.support.v4.app.ShareCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 *  class MainActivity
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

 procImage( getIntent() );

    } // onCreate



/**
 *  procImage
 */
private void procImage(Intent intent) {

    String msg;
    if (intent == null) return;
    msg = "intent : " + intent.toString();
    log_d(msg);

    String action = intent.getAction();
    String type = intent.getType();
    String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
    String text = intent.getStringExtra(Intent.EXTRA_TEXT);
    String[] email_arr =           intent.getStringArrayExtra(Intent.EXTRA_EMAIL);
    Uri uri_stream = intent.getParcelableExtra(Intent.EXTRA_STREAM);

    String msg_email = "";
    if ( email_arr != null ) {
        int len = email_arr.length;
        if ( len > 0 ) {
            for (int i=0; i<len; i++) {
                msg_email += email_arr[i] + LF;
            }
        }
    }

    String msg_stream = "";
    if (uri_stream != null) {
        msg_stream = "stream=" + uri_stream.toString() + LF;
    }

    msg = "Share Intent" + LF;
    msg = "action=" + action + LF;
    msg += "type=" + type + LF;
    msg += " text=" +  text + LF;
    msg += "email: " + msg_email + LF;
    msg += msg_stream + LF;
    log_d(msg);
    mTextView1.setText(msg);

    if (uri_stream == null) return;

        try {
            mImageView1.setImageURI(uri_stream);
        } catch (Exception e) {
            e.printStackTrace();
        }


} // procImage

    // Bitmap bitmap = null;
    // try {
        // bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_stream);
    // }
   //  catch (FileNotFoundException e) {
        // e.printStackTrace();
    // }
    // catch (IOException e) {
        //e.printStackTrace();
    //}
    // if (bitmap != null) {
        // mImageView1.setImageBitmap(bitmap);
    // }

// } // procImage


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