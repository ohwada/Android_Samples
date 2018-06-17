/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.oscrecieversample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import java.text.SimpleDateFormat;
import java.util.List;

// TODO : IllegalArgumentException occurs and abnormally ends
// if there is an illegal character in the address part of the received OSC Message

 	/**
	 * class MainActivity
	 */ 
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSC";
    	private final static String TAG_SUB = "MainActivity";

        // OSC message
    	private final static String ADDRESS_ON = "/s_new";

        private TextView mTextViewAddr;

        private TextView mTextViewPort;

        private TextView mTextViewMessage;

        private OscReciever mOscReciever;

        //private OSCPortIn mOSCPortIn;


 	/**
	 * onCreate
	 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
mTextViewAddr = (TextView) findViewById(R.id.TextView_addr);
mTextViewPort = (TextView) findViewById(R.id.TextView_port);
mTextViewMessage = (TextView) findViewById(R.id.TextView_message);

        Button btnSetup = (Button) findViewById(R.id.Button_setup);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    setup();
            }
        }); // btnSetup

        mOscReciever = new OscReciever();
        mOscReciever.setOnEventListener(new OscReciever.OnEventListener() {
            @Override
            public void onMessage(java.util.Date time, OSCMessage message) {
                    showMessage(time, message);
            }
        }); // setOnEventListener

    } // onCreate


	/*
	 * == onResume ==
	 */
	@Override
	protected void onResume() {
		super.onResume();

        String addr = NetworkUtil.getIPAddress();
        int port = mOscReciever.getDefaultPort();
        mTextViewAddr.setText(addr);
        mTextViewPort.setText(Integer.toString(port));
	} // onResume


	/*
	 * == onDestroy ==
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
        closePort();
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
        } else if (id == R.id.action_close) {
            closePort();
            toast_short(R.string.action_close);
        } 
        return true;
    } // onOptionsItemSelected


 	/**
	 * setup
	 */ 
private void setup() {
    boolean ret = mOscReciever.setup(ADDRESS_ON);

    if (!ret) {
        String msg = getString(R.string.open_not);
        mTextViewMessage.setText(msg);
        log_d(msg);
        toast_short(msg);
        return;
    }

    if ( mOscReciever.isListening() ) {
        String msg = getString(R.string.port_listening);
        mTextViewMessage.setText(msg);
        log_d(msg);
        toast_short(msg);
    }
} // setup



 	/**
	 * showMessage
	 */ 
private void showMessage(java.util.Date time, OSCMessage message) {
    log_d("showMessage");

    String msg = mOscReciever.toMessageString(time, message);
    log_d(msg);
    final String log_msg = msg;

    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewMessage.setText(log_msg);
                toast_short(R.string.msg_recieved);
            }
    }); // runOnUiThread

} // showMessage


 	/**
	 * closePort
	 */ 
private void closePort() {
	if (mOscReciever != null) {
        mOscReciever.stopListening();
		mOscReciever.closeInputPort();
    }
} // closePort


/**
	 * toast_short
	 */
	protected void toast_short( int res_id ) {
toast_short( getString(res_id) );
	} // toast_short

/**
	 * toast_short
	 */
	protected void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
