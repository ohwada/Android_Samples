/**
 * MIDI KeyBoard Sample
 * 2018-05-01
 */


package jp.ohwada.android.midikeyboardsample;

import android.app.Activity;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.util.Log;

import com.mobileer.miditools.MidiPortSelector;
import com.mobileer.miditools.MidiPortWrapper;

import java.io.IOException;

/**
 * class KeyboardInputPortSelector
 * original : https://github.com/philburk/android-midisuite/tree/master/MidiKeyboard
 */
public class KeyboardInputPortSelector extends MidiPortSelector {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "KeyboardInputPortSelector";

    private MidiInputPort mInputPort;
    private MidiDevice mOpenDevice;

    private Callback mCallback;

   /*
     * callback interface
     */ 
    public interface Callback {
        public void onPortOpened(MidiInputPort port);
    } // interface

    /**
     * constractor
     * 
     * @param midiManager
     * @param activity
     * @param spinnerId ID from the layout resource
     */
    public KeyboardInputPortSelector(MidiManager midiManager, Activity activity,
            int spinnerId) {
        super(midiManager, activity, spinnerId, MidiDeviceInfo.PortInfo.TYPE_INPUT);
    } // MidiInputPortSelector


 	/**
	 * setCallback
	 */ 
public void setCallback(Callback callback) {
    mCallback = callback;
} // setCallback


 	/**
	 * == onPortSelected ==
	 */ 
    @Override
    public void onPortSelected(final MidiPortWrapper wrapper) {
        close();
        final MidiDeviceInfo info = wrapper.getDeviceInfo();

        if (info != null) {
                log_d(info.toString());

            mMidiManager.openDevice(info, new MidiManager.OnDeviceOpenedListener() {

                    @Override
                public void onDeviceOpened(MidiDevice device) {
                log_d("onDeviceOpened");

                    if (device == null) {
                        log_d( "evice could not open ");
                    } else {
                        log_d( "device could open ok");
                        mOpenDevice = device;
                        mInputPort = mOpenDevice.openInputPort(
                                wrapper.getPortIndex());
                            notifyPortOpened(mInputPort);
                    } // if

                } // onDeviceOpened

            }, null); // OnDeviceOpenedListener

            // Don't run the callback on the UI thread because openInputPort might take a while.
        } 

    } // onPortSelected

 	/**
	 * == onClose ==
	 */ 
    @Override
    public void onClose() {

        try {
            if (mInputPort != null) {
                log_d("close port");
                mInputPort.close();
            }
            mInputPort = null;
            if (mOpenDevice != null) {
                mOpenDevice.close();
            }
            mOpenDevice = null;
        } catch (IOException e) {
            log_d("cleanup failed");
            e.printStackTrace();
        }

        super.onClose();

    } // onClose


 	/**
	 * getReceiver
	 */ 
    public MidiReceiver getReceiver() {
        return mInputPort;
    } // getReceiver


 	/**
	 * notifyPortOpened
	 */ 
private void notifyPortOpened(MidiInputPort port) {
    if (mCallback != null ) {
        mCallback.onPortOpened(port);
    }
} // notifyPortOpened


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class KeyboardInputPortSelector
