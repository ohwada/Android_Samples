/**
 * MIDI KeyBoard Sample
 * 2018-05-01
 */

package jp.ohwada.android.midikeyboardsample;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.midi.MidiInputPort;
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
 * MidiSender
 * original : https://github.com/philburk/android-midisuite/tree/master/MidiKeyboard
 */

public class MidiSender {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MidiSender";

 	private final static String COMMA = ", ";

    private static final int BUF_SIZE  = 3;

    // Channel voice messages.
    private static final byte STATUS_NOTE_OFF = (byte) 0x80;
    private static final byte STATUS_NOTE_ON = (byte) 0x90;
    private static final byte STATUS_PROGRAM_CHANGE = (byte) 0xC0;


    private MidiInputPort mMidiInputPort;


 	/**
	 * constractor
	 */ 
 public MidiSender() {
} // MidiSender

 	/**
	 * setMidiReceiver
	 */ 
public void setInputPort(MidiInputPort port) {
    log_d("setMidiReceiver");
    if (port != null ) {
        log_d(port.toString());
        mMidiInputPort = port;
    }
} // setMidiReceiver


 	/**
	 *  sendChangeProgram
	 */ 
    public void  sendChangeProgram(int channel, int program) {
        log_d("sendChangeProgram");
        midiCommand(STATUS_PROGRAM_CHANGE + channel, program);
    } //  sendChangeProgram 


 	/**
	 * sendNoteOn
	 */ 
    public void sendNoteOn(int channel, int pitch, int velocity) {
        log_d("sendNoteOn");
        midiCommand(STATUS_NOTE_ON + channel, pitch, velocity);
    } // sendNoteOn


 	/**
	 * sendNoteOff
	 */ 
    public void sendNoteOff(int channel, int pitch, int velocity) {
        log_d("sendNoteOff");
        midiCommand(STATUS_NOTE_OFF + channel, pitch, velocity);
    } // sendNoteOff


 	/**
	 * midiCommand
	 */ 
    private void midiCommand(int status, int data1, int data2) {
        log_d("midiCommand3");
        byte[] buf = new byte[ BUF_SIZE];
        buf[0] = (byte) status;
        buf[1] = (byte) data1;
        buf[2] = (byte) data2;
        long now = System.nanoTime();
        sendMidi(buf, 3, now);
    } // sendNoteOff


 	/**
	 * midiCommand
	 */ 
    private void midiCommand(int status, int data1) {
        log_d("midiCommand2");
        byte[] buf = new byte[ BUF_SIZE];
        buf[0] = (byte) status;
        buf[1] = (byte) data1;
        buf[2] = 0;
        long now = System.nanoTime();
        sendMidi(buf, 2, now);
    }


 	/**
	 * sendMidi
	 */ 
    private void sendMidi(byte[] buffer, int count, long timestamp) {
        String hex = toHex(buffer[0]) + COMMA;
        hex  += toHex(buffer[1]) + COMMA;
        hex  += toHex(buffer[2]);
        log_d("sendMidi: " + hex );
        if (mMidiInputPort == null) {
            log_d("not set InputPort");
            return;
        }

            try {
                // send event immediately
                   mMidiInputPort.send(buffer, 0, count, timestamp);
            } catch (IOException e) {
                log_d( "mKeyboardReceiverSelector.send() failed ");
                e.printStackTrace();
            }

    } // sendMidi

/**
 * toHex
 */ 
private String toHex(byte b) {
    return String.format("%02x", b);
} // toHex

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MidiSender
