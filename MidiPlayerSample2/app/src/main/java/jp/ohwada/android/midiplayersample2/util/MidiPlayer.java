/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample2.util;

import android.media.midi.MidiInputPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import com.leff.midi.MidiFile;
import com.leff.midi.event.ChannelAftertouch;
import com.leff.midi.event.Controller;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteAftertouch;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.PitchBend;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.util.MidiEventListener;
import com.leff.midi.util.MidiProcessor;

/**
 *  class MidiPlayer
	 * 
	 * with android-midi-lib
	 * https://github.com/LeffelMania/android-midi-lib
 */
public class MidiPlayer {
// debug
    public final static boolean D = true; 
	public final static String TAG = "MIDI";
	private String TAG_SUB = "MidiPlayer";

	private MidiProcessor mProcessor;

 private MidiSender mMidiSender;

  // callback 
    private OnEventListener mListener;

/*
  * callback interface
 */    
    public interface OnEventListener {
        public void onStart(boolean fromBeginning);
        public void onStop(boolean finished);
        public void onEvent(MidiEvent event, long ms);
    } // interface


/**
 *  constractor
 */
    public MidiPlayer()
    {
        mMidiSender = new MidiSender();
    } // MidiPlayer


    /*
     * setOnEventClickListener
     */ 
    public void setOnEventListener( OnEventListener listener ) {
        log_d("setOnEventListener");
        mListener = listener;
    } // setOnEventListener


 	/**
	 * setInputPort
	 */ 
public void setInputPort(MidiInputPort port) {
    mMidiSender.setInputPort(port);
} // setInputPort

 	/**
	 * start
	 */ 
    public void start(File file) {
    log_d("start");

        MidiFile midi_file = null;
        try {
            // read MIDI file
             midi_file = new MidiFile(file);
        } catch(IOException e) {
            if (D) e.printStackTrace();
            return;
        }

        // create a MidiProcessor
            mProcessor = new MidiProcessor( midi_file);

        // register listeners for the events 
        ProcessorEventListener el = new ProcessorEventListener();
        mProcessor.registerEventListener(el, MidiEvent.class);

        // start the Processor
        mProcessor.start();

} // start


 	/**
	 * stop
	 */ 
public void stop() {
    log_d("stop");
    if ( mProcessor != null ) {
        mProcessor.stop();
    }
    sendsoundOff();
} // stop


 	/**
	 * isRunning
	 */ 
public boolean isRunning() {
    if ( mProcessor != null ) {
        return mProcessor.isRunning();
    }
    return false;
} // isRunning


 	/**
	 * sendsoundOff
	 */ 
private void sendsoundOff() {
        mMidiSender.sendSoundOff();
} // sendsoundOff


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


/**
 *  === class ProcessorEventListener ===
 */
public class ProcessorEventListener implements MidiEventListener {

/**
 *  constractor
 */
    public ProcessorEventListener() {
        // dummy
    } // ProcessorEventListener

/**
 *   onStart
 */
    @Override
    public void onStart(boolean fromBeginning) {
        log_d("onStart: "+fromBeginning);
        if ( mListener != null ) {
            mListener.onStart(fromBeginning);
        }
    } // onStart

/**
 *   onStop
 */
    @Override
    public void onStop(boolean finished) {
            log_d("onStop: " + finished);
        if ( mListener != null ) {
            mListener.onStop(finished);
        }
    } // onStop

/**
 *   onEvent
 */
    @Override
    public void onEvent(MidiEvent event, long ms) {
        // log_d("onEvent: " + event);
        procEvent(event, ms);
        if ( mListener != null ) {
            // TODO : interface only
            // mListener.onEvent(event, ms);
        }
    } // onEvent



/**
 *   procEvent
 */
    private void procEvent(MidiEvent event, long ms) {
        // log_d(" received event: " + event);
            Class cls = event.getClass();
            if ( cls.equals(NoteOn.class)) {
                sendNoteOn(event);
            }  else if ( cls.equals(NoteOff.class)) {
                sendNoteOff(event);
            }  else if ( cls.equals(ProgramChange.class)) {
                sendProgramChange(event);
            }  else if ( cls.equals(PitchBend.class)) {
                sendPitchBend(event);
            }  else if ( cls.equals(NoteAftertouch.class)) {
                sendNoteAftertouch(event);
            }  else if ( cls.equals(ChannelAftertouch.class)) {
                sendChannelAftertouch(event);
            }  else if ( cls.equals(Controller.class)) {
                sendController(event);
            }  else if ( cls.equals(Tempo.class)) {
                sendTempo(event);
            } else {
       // log_d("event: " + event);
            }
    } // procEvent


/**
 *  sendNoteOn
 * status : 0x80
 */
    private void sendNoteOn(MidiEvent event) {
// log_d("sendNoteOn");
        NoteOn cls = (NoteOn)event;
        int channel = cls.getChannel();
        int note = cls.getNoteValue();
        int velocity = cls.getVelocity();
        mMidiSender.sendNoteOn(channel,  note, velocity);
    } // sendNoteOn

/**
 * sendNoteOff
 * status : 0x90
 */
    private void sendNoteOff(MidiEvent event) {
// log_d("sendNoteOff");
        NoteOff cls = (NoteOff)event;
        int channel = cls.getChannel();
        int note = cls.getNoteValue();
        int velocity = cls.getVelocity();
        mMidiSender.sendNoteOff(channel,  note, velocity);
    } // sendNoteOff

/**
 * sendNoteAftertouch
 * status : 0xA0
 */
 private void sendNoteAftertouch(MidiEvent event) {
        NoteAftertouch cls = (NoteAftertouch)event;
        int channel = cls.getChannel();
        int note = cls.getNoteValue();
        int amount = cls.getAmount();
        mMidiSender.sendPolyphonicAftertouch(channel, note, amount);
} // sendNoteAftertouch

/**
 * sendController
 * status : 0xB0
 */
                    private void sendController(MidiEvent event) {
        Controller cls = (Controller)event;
        int channel = cls.getChannel();
        int type = cls.getControllerType();
        int value = cls.getValue();
        mMidiSender.sendControlChange(channel, type, value);
} // sendController
/**
 * sendProgramChange
 * status : 0xC0
 */
    private void sendProgramChange(MidiEvent event) {
// log_d("sendProgramChange");
        ProgramChange cls = (ProgramChange)event;
        int channel = cls.getChannel();
        int program = cls.getProgramNumber();
        mMidiSender.sendProgramChange(channel,  program);
} // sendProgramChange

/**
 *  sendChannelAftertouch
 * status : 0xD0
 */
 private void sendChannelAftertouch(MidiEvent event) {
        ChannelAftertouch cls = (ChannelAftertouch)event;
        int channel = cls.getChannel();
        int amount = cls.getAmount();
        mMidiSender.sendChannelAftertouch(channel, amount);
} // sendChannelAftertouch

/**
 * sendPitchBend
 * status : 0xE0
 */
    private void sendPitchBend(MidiEvent event) {
// log_d("sendPitchBend");
        PitchBend cls = (PitchBend)event;
        int channel = cls.getChannel();
        int amount = cls.getBendAmount();
        mMidiSender.sendPitchBendChange(channel, amount);
} // sendPitchBend

/**
 * sendTempo
 */
                    private void sendTempo(MidiEvent event) {
    // TODO : nothing to do
} // sendTempo

} //  class ProcessorEventListener

} //  class MidiPlayer
