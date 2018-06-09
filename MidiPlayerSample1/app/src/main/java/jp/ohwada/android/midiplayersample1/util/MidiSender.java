/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

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
 * MIDIメッセージを送信するクラス
 */
public class MidiSender {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MidiSender";

 	private final static String COMMA = ", ";

    private static final int BUF_SIZE  = 3;

    private static final int SEND_OFFSET  = 0;

    // Channel voice messages.
    private static final byte STATUS_NOTE_OFF = (byte) 0x80;
    private static final byte STATUS_NOTE_ON = (byte) 0x90;
    private static final byte STATUS_PROGRAM_CHANGE = (byte) 0xC0;


private static final byte SOUND_OFF_1 = (byte) 0xB0;

private static final byte SOUND_OFF_2 =  (byte) 120;

private static final byte  SOUND_OFF_3 =  (byte) 0;

    private static final int SOUND_OFF_CHANNEL = 1;

    private MidiInputPort mMidiInputPort;


 	/**
	 * constractor
	 */ 
 public MidiSender() {
} // MidiSender

 	/**
	 * setInputPort
	 */ 
public void setInputPort(MidiInputPort port) {
    log_d("setInputPort");
    if (port != null ) {
        log_d(port.toString());
        mMidiInputPort = port;
    }
} // setInputPort


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
	 * PitchBend Change
	 * Code Index Number : 0xe
	 * @param channel 0-15
	 * @param amount 0(low)-8192(center)-16383(high)
	 */
	public void sendPitchWheel(int channel, int amount) {
     byte byte1 = (byte)( MidiMessage.STATUS_PITCH_WHEEL | (channel & 0xf) );
    byte byte2 = (byte) (amount & 0x7f);
    byte byte3 =  (byte)( (amount >> 7) & 0x7f);
    sendCommand( (int)byte1, (int)byte2,  (int)byte3 );
	} // sendMidiPitchWheel


 	/**
	 * sendSoundOff
	 */ 
public void sendSoundOff() {
        sendSoundOff(SOUND_OFF_CHANNEL);
}


 	/**
	 * sendSoundOff
	 */ 
private void sendSoundOff( int channel ) {
    log_d("sendSoundOff");
byte[] buf = new byte[3];
buf[0] = (byte)(SOUND_OFF_1 + ( 0x0f & (channel - 1)));
buf[1] = (byte)SOUND_OFF_2; 
buf[2] = (byte)SOUND_OFF_2;
        long now = System.nanoTime();
        sendMidi(buf, SEND_OFFSET,  3, now);
} // sendSoundOff


 	/**
	 * sendCommand
	 */ 
    public void sendCommand(int status, int data1, int data2) {
        log_d("sendCommand3");
        midiCommand( status, data1, data2);
    } // sendCommand


 	/**
	 * sendCommand
	 */ 
    public void sendCommand(int status, int data1) {
        log_d("sendCommand2");
        midiCommand( status, data1);
    } // sendCommand


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
        sendMidi(buf, SEND_OFFSET, 3, now);
    } // midiCommand


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
        sendMidi(buf, SEND_OFFSET, 2, now);
    }


 	/**
	 * sendMidi
	 */ 
    private void sendMidi(byte[] buffer, int offset, int count, long timestamp) {
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
                   mMidiInputPort.onSend( buffer, offset, count, timestamp );
            } catch (IOException e) {
                log_d( "¥ReceiverSelector send failed ");
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
