/**
 * MIDI KeyBoard Sample
 * 2018-05-01
 */

package jp.ohwada.android.midikeyboardsample;

import android.app.Activity;
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
 * class ChannelViewController
 * original : https://github.com/philburk/android-midisuite/tree/master/MidiKeyboard
 */
public class ChannelViewController {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "ChannelViewController";

    private final static int MAX_CHANNELS = 16;

    private final static int MAX_PROGRAM = 127;

    private final static int SOUND_PIANO = 0;

// current channel
// ranges from 0 to 15
    private int mChannel = 0; 

// sound
// https://ja.wikipedia.org/wiki/General_MIDI
// ranges from 0 to 127
//   0:Piano
    private int[] mPrograms = new int[MAX_CHANNELS]; 

    private Button mProgramButton;


   /*
     * constractor
     */ 
public ChannelViewController() {
    for (int i=0; i<MAX_CHANNELS; i++ ) {
        mPrograms[i] = SOUND_PIANO;
    }
} // ChannelViewController


/**
 * setProgramButton
 */
public void setProgramButton(Button btn) {
mProgramButton = btn;
} // setProgramButton


/**
 * setChannelSpinner
 */
public void setChannelSpinner(Spinner spinner) {
        spinner.setOnItemSelectedListener(new ChannelSpinnerListener());
    } // setChannelSpinner


/**
 * getChannel
 */
   public int getChannel() {
    return mChannel;
}


/**
 * getProgram
 */
   public int getProgram() {
    return mPrograms[mChannel];
}

/**
 * changeProgram
 */
   public void changeProgram(int delta) {
        int program = mPrograms[mChannel];
        program += delta;
        if (program < 0) {
            program = 0;
        } else if (program > MAX_PROGRAM) {
            program = MAX_PROGRAM;
        }

    mPrograms[mChannel] = program;
    updateProgramText();

    } // changeProgram


/**
 * updateProgramText
 */
    private void updateProgramText() {
        log_d("updateProgramText");
        mProgramButton.setText( "" + getProgram() );
    } // updateProgramText


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


/**
 * === class ChannelSpinnerListener  ===
 */
    public class ChannelSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            int channel = pos & 0x0F;
            log_d("onItemSelected: " + channel);
            mChannel = channel;
            updateProgramText();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // nothing to do
        }

    } // class ChannelSpinnerListener

} // class ChannelViewController
