/**
 * MIDI Synth Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midisynthsample;

import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;

import com.example.android.common.midi.synth.SynthEngine;

/**
 * class MidiSynthDeviceService
 * original : https://github.com/googlesamples/android-MidiSynth
 */
public class MidiSynthDeviceService extends MidiDeviceService {

    private SynthEngine mSynthEngine = new SynthEngine();
    private boolean mSynthStarted = false;

/**
 * onCreate
 */
    @Override
    public void onCreate() {
        super.onCreate();
    }

/**
 * onDestroy
 */
    @Override
    public void onDestroy() {
        mSynthEngine.stop();
        super.onDestroy();
    }

/**
 * onGetInputPortReceivers
 */
    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[]{mSynthEngine};
    }

    /**
     * onDeviceStatusChanged
     * This will get called when clients connect or disconnect.
     */
    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        if (status.isInputPortOpen(0) && !mSynthStarted) {
            mSynthEngine.start();
            mSynthStarted = true;
        } else if (!status.isInputPortOpen(0) && mSynthStarted) {
            mSynthEngine.stop();
            mSynthStarted = false;
        }
    }

} // class MidiSynthDeviceService
