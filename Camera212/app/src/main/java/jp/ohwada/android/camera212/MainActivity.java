/**
 * Camera2 Sample
 *  explore  supported camera2 features
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera212;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * class MainActivity
 * original : https://github.com/TobiasWeis/android-camera2probe
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";

    private final static String DATA = "Probing...";
    private final static String MIME_TYPE = "text/html";
    private final static String ENCODING = "utf-8";

    private WebView mWebView;


/**
 * onCreate
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.textview_probe);
        mWebView.loadData(DATA, MIME_TYPE, ENCODING);

    }


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
       String data = Camera2Probe.probe(this);
        mWebView.loadData(data, MIME_TYPE, ENCODING);
} 


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

}
