/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.oscrecieversample2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.illposed.osc.OSCPort;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.ohwada.android.oscrecieversample2.util.*;


 	/**
	 * class MainActivity
	 */ 
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSC";
    	private final static String TAG_SUB = "MainActivity";

    	private final static String LF = "\n";

        // OSC message
    	private final static String ADDRESS_ON = "/s_new";

        private TextView mTextViewAddr;

        private TextView mTextViewPort;

        private TextView mTextViewMessage;

           private String mDebugMessage = "";


 	private OscReceiver mOscReceiver;


 	/**
	 * onCreate
	 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

mTextViewAddr = (TextView) findViewById(R.id.TextView_addr);
mTextViewPort = (TextView) findViewById(R.id.TextView_port);
mTextViewMessage = (TextView) findViewById(R.id.TextView_message);

        Button btnSetup = (Button) findViewById(R.id.Button_setup);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startOscService();
            }
        }); // btnSetup

            startOscReceiver();

    } // onCreate


	/*
	 * == onResume ==
	 */
	@Override
	protected void onResume() {
		super.onResume();
        log_d("onResume");
        String addr = NetworkUtil.getIPAddress();
        int port = OSCPort.defaultSCOSCPort();
        mTextViewAddr.setText(addr);
        mTextViewPort.setText(Integer.toString(port));
	} // onResume


	/*
	 * == onDestroy ==
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
        stopOscService();
        stopOscReceiver();
} // onDestroy


/**
 * == onCreateOptionsMenu ==
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    } // onCreateOptionsMenu

    /**
     * === onOptionsItemSelected ===
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            toast_short(R.string.action_settings);
        } else if (id == R.id.action_clear) {
            mDebugMessage = "";
            mTextViewMessage.setText("");
            toast_short(R.string.action_clear);
        } else if (id == R.id.action_stop) {
            stopOscService();
            toast_short(R.string.action_stop);
        } 
        return true;
    } // onOptionsItemSelected


 	/**
	 * procStatus
	 */ 
private void procStatus(int status) {
        log_d("procStatus");

    String msg = "";
    if (status == OscService.STATUS_NOT_OPEN) {
        msg = getString(R.string.status_not_open);
    } else if (status == OscService.STATUS_LISTENING) {
        msg = getString(R.string.status_listening);
    } else if (status == OscService.STATUS_RECIEVE) {
        msg = getString(R.string.status_recieve);
    } else if (status == OscService.STATUS_INVALID_OSC_ADDR) {
        msg = getString(R.string.status_invalid_osc_addr);
    } else if (status == OscService.STATUS_NOT_CONVERT) {
        msg = getString(R.string.status_not_convert);
    } else if (status == OscService.STATUS_UNMATCH) {
        msg = getString(R.string.status_unmatch);
    }

    log_d(msg);
    final String log_msg = msg;
                addDebugMessage(msg);
    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewMessage.setText(mDebugMessage);
                toast_short(log_msg);
            }
    }); // runOnUiThread

} //  procStatus


 	/**
	 * procError
	 */ 
private void procError(final String error) {
        log_d("procError");
                log_d(error);
                addDebugMessage(error);
    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewMessage.setText(mDebugMessage);
                toast_short(error);
            }
    }); // runOnUiThread

} //  procError


 	/**
	 * procMessage
	 */ 
private void procMessage(final String msg) {
        log_d("procMessage");
                log_d(msg);
                addDebugMessage(msg);
    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewMessage.setText(mDebugMessage);
                toast_short("message");
            }
    }); // runOnUiThread

} //  procMessage



 	/**
	 * startOscService
	 */ 
private void startOscService() {
    log_d("startOscService");
    Intent intent = new Intent(this, OscService.class);
        startService(intent);
} // startOscService

 	/**
	 * stopOscService
	 */ 
private void stopOscService() {
    log_d("stopOscService");
    Intent intent = new Intent(this, OscService.class);
        stopService(intent);
} // stopOscService

 	/**
	 * startOscReceiver
	 */ 
private void startOscReceiver() {
    log_d("startOscReceiver");
    mOscReceiver = new OscReceiver();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(OscService.ACTION);
    registerReceiver( mOscReceiver, intentFilter);
} // startOscReceiver


 	/**
	 *  stopOscReceiver
	 */ 
private void stopOscReceiver() {
    log_d("stopOscReceiver");
        try {
            if ( mOscReceiver != null ) {
                unregisterReceiver(mOscReceiver);
            }
            mOscReceiver = null;
		} catch (Exception e) {
            // e.printStackTrace();
		}
} //  stopOscReceiver


 	/**
	 *  addDebugMessage
	 */ 
private void addDebugMessage(String msg) {
    mDebugMessage += msg + LF;
} //  addDebugMessage

 	/**
	 *  showDebugMessage
	 */ 
private void showDebugMessage() {
                mTextViewMessage.setText(mDebugMessage);
} // showDebugMessage

/**
	 * toast_short
	 */
	private void toast_short( int res_id) {
        toast_short( getString(res_id) );
	} // toast_short

/**
	 * toast_short
	 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

 	/**
	 * class OscReceiver
	 */ 
    private class OscReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            log_d("onReceive");
            int mode = intent.getIntExtra(OscService.KEY_MODE, 0);
    if (mode == OscService.MODE_STATUS) {
            int status = intent.getIntExtra(OscService.KEY_STATUS, 0);
            procStatus(status);
    } else if (mode == OscService.MODE_MESSAGE) {
            String msg = intent.getStringExtra(OscService.KEY_MESSAGE);
            procMessage(msg);
    } else if (mode == OscService.MODE_ERROR) {
            String error = intent.getStringExtra(OscService.KEY_ERROR);
            procError(error);
    }
        } // onReceive

    } // class OscReceiver

} // class MainActivity
