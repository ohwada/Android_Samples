/**
 * MIDI Synth Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midisynthsample;

import android.content.pm.PackageManager;
import android.media.midi.MidiDevice.MidiConnection;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android.common.midi.MidiOutputPortConnectionSelector;
import com.example.android.common.midi.MidiPortConnector;
import com.example.android.common.midi.MidiTools;

/**
 * class MainActivity
 * original : https://github.com/googlesamples/android-MidiSynth
 */
public class MainActivity extends AppCompatActivity {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MainActivity";


	private final static String DEVICE_MANUFACTURER = "AndroidTest";

	private final static String DEVICE_PRODUCT =  "SynthExample";

      private final static  int PORT_INDEX = 0;

    private MidiOutputPortConnectionSelector mPortSelector;

/**
 * onCreate
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMidi();

    } // onCreate

/**
 * onDestroy
 */
    @Override
    public void onDestroy() {
        closeSynthResources();
        super.onDestroy();
    }

/**
 * onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        setKeepScreenOn(menu.findItem(R.id.action_keep_screen_on).isChecked());
        return true;
    }

/**
 * onOptionsItemSelected
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_keep_screen_on:
                boolean checked = !item.isChecked();
                setKeepScreenOn(checked);
                item.setChecked(checked);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

/**
 * setKeepScreenOn
 */
    private void setKeepScreenOn(boolean keepScreenOn) {
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

/**
 * setupMidi
 */
    private void setupMidi() {
    log_d("setupMidi");
boolean hasMidi = getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI);
        if (!hasMidi) {
            log_d(R.string.not_support_midi);
            toast_short(R.string.not_support_midi);
            return;
        }

    SynthOutputPortConnectionSelector selector = new SynthOutputPortConnectionSelector(this);
    selector.findDevice( DEVICE_MANUFACTURER, DEVICE_PRODUCT );

    mPortSelector =  
    selector.setupSelector( R.id.spinner_synth_sender,  PORT_INDEX, new SynthOutputPortConnectionSelector.Callback() {

            @Override
        public void onPortsConnected(MidiConnection connection) {
                procPortsConnected(connection);
            }

        }); // setupSelector

    } // setupMidi


/**
 * closeSynthResources
 */
    private void closeSynthResources() {
        if (mPortSelector != null) {
            mPortSelector.close();
        }
    }

private void procPortsConnected(final MidiConnection connection) {

            log_d("procPortsConnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showUiThread(connection);
                }
            });

    } // procPortsConnected

private void showUiThread(MidiConnection connection) {
                    if (connection == null) {
                        log_d(R.string.error_port_busy);
                        toast_short(
                                R.string.error_port_busy);
                        mPortSelector.clearSelection();
                    } else {
                        log_d(R.string.port_open_ok);
                        toast_short(
                                R.string.port_open_ok);
                    }
} // showUiThread

/**
 * toast_short
 */
	private void toast_short( int res_id ) {
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
	private void log_d( int res_id ) {
        log_d( getString(res_id) );
	} // log

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class MainActivity
