/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.javaoscsample;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortOut;


/**
 *  class MainActivity
 *  reference : https://github.com/hoijui/JavaOSC
 */
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSC";
    	private final static String TAG_SUB = "MainActivity";

    	private final static String LF = "\n";

    	private final static float FREQ_ON = 440;

    	private final static int NODE = 1000;

	// SharedPreferences 
    private static final String PREF_IP_ADDR  = "ip_addr";
    private static final String DEFAULT_IP_ADDR = "";

    private static final String IP_ADDR_HINT = "127.0.0.1";

private TextView mTextViewPort;

private TextView mTextViewFreq;

private EditText  mEditTextAddress;

private Button mButtonOn1;

private Button mButtonOff1;

private OscSender mOscSender;

    private SharedPreferences mPreferences;


/**
 *  == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

mTextViewPort = (TextView) findViewById(R.id.TextView_port);
    String port = Integer.toString( OSCPort.defaultSCOSCPort() );
    mTextViewPort.setText(port);

mTextViewFreq = (TextView) findViewById(R.id.TextView_freq);
        String freq = Float.toString(FREQ_ON);
		mTextViewFreq.setText(freq);

 mEditTextAddress = (EditText) findViewById(R.id.EditText_address);

        Button btnSet = (Button) findViewById(R.id.Button_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procSet();
            }
        }); // btnSet


mButtonOn1 = (Button) findViewById(R.id.Button_on);
        mButtonOn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procOn();
            }
        }); // mButtonOn1


mButtonOff1 = (Button) findViewById(R.id.Button_off);
        mButtonOff1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procOff();
            }
        }); // mButtonOff1



    mOscSender = new OscSender();
    mOscSender.setOnEventListener(new OscSender.OnEventListener() {
            @Override
            public void onFinish() {
                toast_short(R.string.send_ok);
            }

            @Override
            public void onError(String err) {
                String msg = getString(R.string.send_not) + LF + err; 
                log_d(msg);
                toast_short(msg);
            }
        });

	mPreferences = PreferenceManager.getDefaultSharedPreferences( this );


    } // onCreate


	/*
	 * === onResume ==
	 */
	@Override
	protected void onResume() {
		super.onResume();
        String addr = mPreferences.getString( PREF_IP_ADDR,  DEFAULT_IP_ADDR );
        mEditTextAddress.setText(addr);
        if (TextUtils.isEmpty(addr)) {
            mEditTextAddress.setHint(IP_ADDR_HINT);
        }
} // onResume


	/*
	 * == onDestroy ====
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
        } else if (id == R.id.action_clear) {
            clearAddr();
            toast_short(R.string.action_clear);
        } else if (id == R.id.action_close) {
            closePort();
            toast_short(R.string.action_close);
        } 
        return true;
    } // onOptionsItemSelected

/**
 *  procSet
 */
private void procSet() {
	String addr = mEditTextAddress.getText().toString();
    log_d("procSet: " + addr);

    OSCPortOut port = mOscSender.openPortOut(addr);
    if (port == null) {
			toast_short(R.string.set_not);
    } else {
            setPrefAddr( addr );
			toast_short(R.string.set_ok);
    }

} // procSet


	/**
	 * setPrefAddr
	 */
    private void setPrefAddr( String addr ) {
		mPreferences.edit().putString( PREF_IP_ADDR, addr ).commit();
	} // setPrefAddr


/**
 *  procOn
 */
private void procOn() {
        log_d("procOn");
		if ( ! mOscSender.hasPortOut()) {
			toast_short(R.string.please_set);
            return;
		}

				mButtonOn1.setEnabled(false);
                mButtonOff1.setEnabled(true);

				mOscSender.sendOn(NODE, FREQ_ON);

} // procOn


/**
 *  procOff
 */
private void procOff() {
        log_d("procOff");
		if ( ! mOscSender.hasPortOut()) {
			toast_short(R.string.please_set);
            return;
		}

                mButtonOn1.setEnabled(true);
                mButtonOff1.setEnabled(false);

				mOscSender.sendOff(NODE);

} // procOff


/**
	 * clearAddr
	 */
private void clearAddr() {
            mEditTextAddress.setText("");
            mEditTextAddress.setHint(IP_ADDR_HINT);
            setPrefAddr("");
} // clearAddr


/**
	 * closePort
	 */
private void closePort() {
	if (mOscSender != null) {
		mOscSender.cancel();
    }
	if (mOscSender != null) {
        mOscSender.closePortOut();
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
