/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample2.util;

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
 */

public class MidiSender {

// debug
    private final static boolean D = true;
	private final static String TAG = "MIDI";
	private final static String TAG_SUB = "MidiSender";

	// status byte code
    // https://www.midi.org/specifications-old/item/table-1-summary-of-midi-message
	private final static int STATUS_NOTE_OFF = 0x0080;
	private final static int STATUS_NOTE_ON = 0x0090;
	private final static int STATUS_POLYPHONIC_AFTERTOUCH = 0x00A0;
	private final static int STATUS_CONTROL_CHANGE = 0x00B0;
	private final static int STATUS_PROGRAM_CHANGE = 0x00C0;
	private final static int STATUS_CHANNEL_AFTERTOUCH = 0x00D0;
	private final static int STATUS_PITCH_BEND_CHANGE = 0x00E0;

	private final static int STATUS_SYSTEM_EXCLUSIVE = 0x00F0;	

    // sound off 
    private static final byte SOUND_OFF_1 = (byte) 0xB0;

    private static final byte SOUND_OFF_2 =  (byte) 120;

    private static final byte  SOUND_OFF_3 =  (byte) 0;

    private static final int SOUND_OFF_CHANNEL = 1;

    // MIDI message
    private static final int SEND_OFFSET  = 0;

    private static final int BUF_SIZE  = 3;

    // debug
 	private final static String COMMA = ", ";


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
	 * sendNoteOn
	 * status : 0x80
 	 * key (note) number
	 * the velocity.
	 */ 
    public void sendNoteOn(int channel, int note, int velocity) {
        // log_d("sendNoteOn");
        midiCommand(STATUS_NOTE_ON + channel, note, velocity);
    } // sendNoteOn


 	/**
	 * sendNoteOff
	 * status : 0x90
 	 * key (note) number
	 * the velocity.
	 */ 
    public void sendNoteOff(int channel, int note, int velocity) {
        // log_d("sendNoteOff");
        midiCommand(STATUS_NOTE_OFF + channel, note, velocity);
    } // sendNoteOff

	/**
	 * PolyphonicAftertouch
	 * status :  : 0xA0
     * the key (note) number. 
     * the pressure value
	 */
	public void sendPolyphonicAftertouch(int channel, int note, int pressure ) {
     byte byte1 = (byte)( STATUS_POLYPHONIC_AFTERTOUCH  | (channel & 0xf) );
    byte byte2 = (byte) (note & 0x7f);
    byte byte3 =  (byte)(pressure & 0x7f);
    sendCommand( (int)byte1, (int)byte2,  (int)byte3 );
} // sendPolyphonicAftertouch

	/**
	 * Control Change
	 * status :  : 0xB0
	 * the controller number (0-119).  
     *  the controller value (0-127).
	 */
	public void sendControlChange(int channel, int number, int value) {
     byte byte1 = (byte)( STATUS_CONTROL_CHANGE | (channel & 0xf) );
    byte byte2 = (byte) (number & 0x7f);
    byte byte3 =  (byte)(value & 0x7f);
    sendCommand( (int)byte1, (int)byte2,  (int)byte3 );
	} // sendControlChange

 	/**
	 *  sendProgramChange
	 * status : 0xC0
 	 * new program number
	 */ 
    public void  sendProgramChange(int channel, int program) {
        // log_d("sendChangeProgramChange");
        midiCommand( STATUS_PROGRAM_CHANGE + channel, program);
    } //  sendProgramChange 

 	/**
	 *  sendChannelAftertouch
	 * status : 0xD0
	 * the single greatest pressure value (of all the current depressed keys)
	 */ 
    public void  sendChannelAftertouch(int channel, int pressure ) {
        // log_d(" sendChannelAftertouch");
        midiCommand( STATUS_CHANNEL_AFTERTOUCH + channel, pressure);
    } //  sendChannelAftertouch 

	/**
	 * PitchBend Change
	 * status : 0xE0
	 * amount 0(low)-8192(center)-16383(high)
	 */
	public void sendPitchBendChange(int channel, int amount) {
     byte byte1 = (byte)( STATUS_PITCH_BEND_CHANGE | (channel & 0xf) );
    byte byte2 = (byte) (amount & 0x7f);
    byte byte3 =  (byte)( (amount >> 7) & 0x7f);
    sendCommand( (int)byte1, (int)byte2,  (int)byte3 );
	} // sendPitchBendChange




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
        // log_d("sendCommand3");
        midiCommand( status, data1, data2);
    } // sendCommand


 	/**
	 * sendCommand
	 */ 
    public void sendCommand(int status, int data1) {
        // log_d("sendCommand2");
        midiCommand( status, data1);
    } // sendCommand


 	/**
	 * midiCommand
	 */ 
    private void midiCommand(int status, int data1, int data2) {
        // log_d("midiCommand3");
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
        // log_d("midiCommand2");
        byte[] buf = new byte[ BUF_SIZE];
        buf[0] = (byte) status;
        buf[1] = (byte) data1;
        buf[2] = 0;
        long now = System.nanoTime();
        sendMidi(buf, SEND_OFFSET, 2, now);
    } // midiCommand


 	/**
	 * sendMidi
	 */ 
    private void sendMidi(byte[] buffer, int offset, int count, long timestamp) {
        String hex = toHex(buffer[0]) + COMMA;
        hex  += toHex(buffer[1]) + COMMA;
        hex  += toHex(buffer[2]);
        // log_d("sendMidi: " + hex );
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
