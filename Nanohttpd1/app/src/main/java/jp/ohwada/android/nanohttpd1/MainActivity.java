/**
 * NanoHttpd Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.nanohttpd1;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

/**
 * class MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "HTTPD";
    	private final static String TAG_SUB = "MainActivity";

    	private final static int PORT = 8080;

    private HelloServer mServer;

/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = (Button) findViewById(R.id.Button_start);
         btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServer();
            }
        }); // btnStart

        Button btnStop = (Button) findViewById(R.id.Button_stop);
         btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopServer();
            }
        }); // btnStop

        TextView tvIpaddr = (TextView) findViewById(R.id.TextView_ipaddr);

        TextView tvPort = (TextView) findViewById(R.id.TextView_port);

        String addr = NetworkUtil.getMyIPAddress();
        tvIpaddr.setText(addr);

        tvPort.setText( Integer.toString(PORT) );

} // onCreate


/**
 * onDestroy
 */
@Override
public void onDestroy() {
    super.onDestroy();
    if (mServer != null) {
            mServer.stop();
    }
} // onDestroy

/**
 * startServer
 */
private void startServer() {
    boolean wasStarted = false;
    boolean isAlive = false;
        try {
            mServer = new HelloServer(PORT);
            mServer.start();
            wasStarted = mServer.wasStarted();
            isAlive = mServer.isAlive();
        } catch (Exception ex) {
            if(D) ex.printStackTrace();
        }
    log_d("wasStarted: " + wasStarted);
    log_d("isAlive: " + isAlive);
    if( wasStarted && isAlive ) {
        toast_long("Started");
    } else {
        toast_long("Failed");
    }
} // startServer

/**
 * stopServer
 */
private void stopServer() {
    if (mServer != null) {
            mServer.stop();
        toast_long("stop");
    }
} // stopServer


   /**
 * toast_long
 */
private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // toast_long

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class MainActivity
