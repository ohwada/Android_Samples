/**
 * MIDI KeyBoard Sample
 * 2018-05-01
 */


package jp.ohwada.android.midikeyboardsample;

import android.app.Activity;
import android.media.midi.MidiInputPort;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobileer.miditools.MidiConstants;
import com.mobileer.miditools.MidiInputPortSelector;
import com.mobileer.miditools.MusicKeyboardView;

import java.io.IOException;


/**
 * class MainActivity
 * original : https://github.com/philburk/android-midisuite/tree/master/MidiKeyboard
 */
public class MainActivity extends AppCompatActivity {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MainActivity";

    private static final int DEFAULT_VELOCITY = 64;

   
     private MidiSender mMidiSender;

    private KeyboardInputPortSelector  mKeyboardInputportSelector;

    private MusicKeyboardView mKeyboard;

    private MidiManager mMidiManager;

    private ChannelViewController mChannelViewController;


 	/**
	 * onCreate
	 */ 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnProgram = (Button) findViewById(R.id.button_program);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_channels);

        mMidiManager = (MidiManager) getSystemService(MIDI_SERVICE);

        mMidiSender = new MidiSender();

    mChannelViewController = new ChannelViewController();

     mChannelViewController.setProgramButton(btnProgram);

     mChannelViewController.setChannelSpinner( spinner );

        setupMidi();

    } // onCreate


 	/**
	 * onDestroy
	 */ 
    @Override
    public void onDestroy() {
        closeSynthResources();
        super.onDestroy();
    } // onDestroy

 	/**
	 * onProgramSend
	 */
    public void onProgramSend(View view) {
        log_d("onProgramSend");
        sendChangeProgram();
    } // onProgramSend


 	/**
	 * onProgramDelta
	 */
    public void onProgramDelta(View view) {
        log_d("onProgramDelta");
        Button button = (Button) view;
        int delta = Integer.parseInt(button.getText().toString());
        log_d("onProgramDelta: " + delta);
        changeProgram(delta);
    } // onProgramDelta


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

        // Setup Spinner that selects a MIDI input port.
        mKeyboardInputportSelector = new KeyboardInputPortSelector(mMidiManager,
                this, R.id.spinner_receivers);

mKeyboardInputportSelector.setCallback( new KeyboardInputPortSelector.Callback() {

            @Override
        public void onPortOpened(MidiInputPort port) {
                log_d("onPortOpened");
                procPortOpened(port);
            }

        }); // setCallback


        mKeyboard = (MusicKeyboardView) findViewById(R.id.musicKeyboardView);

        mKeyboard.addMusicKeyListener(new MusicKeyboardView.MusicKeyListener() {

            @Override
            public void onKeyDown(int keyIndex) {
                log_d("onKeyDown: " + keyIndex);
                sendNoteOn(keyIndex);
            }

            @Override
            public void onKeyUp(int keyIndex) {
                log_d(" onKeyUp: " + keyIndex);
                sendNoteOff(keyIndex);
            }
        });

    } // setupMidi

/**
 *  procPortOpened
 */
private void procPortOpened(final MidiInputPort port) {
    log_d("procPortOpened");

     if (port != null) {
         mMidiSender.setInputPort(port);
    }

    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showUiThread(port);
            }
    }); // runOnUiThread

} // procPortOpened


/**
 *  showUiThread
 */
private void showUiThread(MidiInputPort port) {

            log_d("showUiThread");

            if (port == null) {
                log_d(R.string.error_port_busy);
                toast_short(R.string.error_port_busy);

            } else {
                log_d(R.string.port_open_ok);
                toast_short(R.string.port_open_ok);

            }

} // showUiThread


/**
 *  sendNoteOn
 */
private void sendNoteOn(int keyIndex) {
                log_d("sendNoteOn: " + keyIndex);
                mMidiSender.sendNoteOn(getChannel(), keyIndex, DEFAULT_VELOCITY);
            } // sendNoteOn

/**
 * endNoteOff
 */
private void sendNoteOff(int keyIndex) {
                log_d("endNoteOff: " + keyIndex);
                mMidiSender.sendNoteOff(getChannel(), keyIndex, DEFAULT_VELOCITY);
            } // endNoteOff


 	/**
	 * sendChangeProgram
	 */
    public void sendChangeProgram() {
        log_d("sendChangeProgram");
         mMidiSender.sendChangeProgram( getChannel(), getProgram() );
    } // sendChangeProgram


/**
 * changeProgram
 */
    private void changeProgram(int delta) {
        log_d("changeProgram");
        mChannelViewController.changeProgram(delta);
        sendChangeProgram();
    } // changeProgram



/**
 * getChannel
 */
private int getChannel() {
    return mChannelViewController.getChannel();
} // getChannel


/**
 * getProgram
 */
private int getProgram() {
    return mChannelViewController.getProgram();
} // getProgram


/**
 * closeSynthResources
 */
    private void closeSynthResources() {
        if (mKeyboardInputportSelector != null) {
            mKeyboardInputportSelector.close();
            mKeyboardInputportSelector.onDestroy();
        }
    } // closeSynthResources


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
	} // log_d

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
