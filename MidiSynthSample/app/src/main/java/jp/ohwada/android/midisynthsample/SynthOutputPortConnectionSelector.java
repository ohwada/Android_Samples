/**
 * MIDI Synth Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midisynthsample;

import android.app.Activity;
import android.content.Context;
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
 * class SynthOutputPortConnectionSelector
 * original : https://github.com/googlesamples/android-MidiSynth
 */
public class SynthOutputPortConnectionSelector  {


// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "SynthOutputPortConnectionSelector";

    private Activity mActivity;

    private MidiManager  mMidiManager;

    private MidiDeviceInfo mSynthDeviceInfo;

    private Callback mCallback;

   /*
     * callback interface
     */ 
    public interface Callback {
        public void onPortsConnected(MidiConnection connection);
    } // interface

/**
 * constractor
 */
public SynthOutputPortConnectionSelector(Activity activity)  {
        mActivity = activity;
        mMidiManager = (MidiManager) activity.getSystemService(Context.MIDI_SERVICE);
} // SynthOutputPortConnectionSelector

/**
 * findDevice
 */
public MidiDeviceInfo findDevice( String manufacturer, String product ) {
        log_d("findDevice");
    mSynthDeviceInfo = MidiTools.findDevice( mMidiManager, manufacturer,
                product );
        log_d(mSynthDeviceInfo.toString());
        return mSynthDeviceInfo;
} // findDevice



/**
 * setupSelector
 */
public MidiOutputPortConnectionSelector setupSelector( int spinner_id, int port_index, Callback callback ) {

        log_d("setupSelector");
        mCallback = callback;

        MidiOutputPortConnectionSelector selector = new MidiOutputPortConnectionSelector(
        mMidiManager,  mActivity, spinner_id, mSynthDeviceInfo, port_index );

        selector.setConnectedListener(new MyPortsConnectedListener());

        return selector;
    } // setupSelector


private void notifyPortsConnected(MidiConnection connection) {
    if(mCallback != null) {
        mCallback.onPortsConnected(connection);
    }
} // notifyPortsConnected

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


/**
 * class MyPortsConnectedListener
 *
 *  TODO A better way would be to listen to the synth server
 *  for open/close events and then disable/enable the spinner.
 */
    private class MyPortsConnectedListener
            implements MidiPortConnector.OnPortsConnectedListener {

        @Override
        public void onPortsConnected(final MidiConnection connection) {
            log_d("onPortsConnected");
            notifyPortsConnected(connection);
        }

    } // class MyPortsConnectedListener

} // class SynthOutputPortConnectionSelector
