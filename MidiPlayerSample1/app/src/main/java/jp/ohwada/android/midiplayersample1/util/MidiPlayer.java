/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

import java.util.List;

import android.media.midi.MidiInputPort;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * MidiPlayer
 * MIDIメッセージを演奏する
 */
public class MidiPlayer {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MidiPlayer";


    // Play Timer
    private static final int MSG_WHAT_PLAY = 100;
    private static final int PLAY_DELAY = 100; // 0.1 sec
	
     private MidiSender mMidiSender;

	// MIDIのリスト
	private List<MidiMessage> mList = null;

    // リストの処理中の位置を示すポインタ
	private int mPointer = 0;
	
	// ベース時間
	private long mTimebase = 0;		

	// 演奏を開始したシステム時間
	private long mStartTime = 0;

	// MIDIメッセージを実行するシステム時間	
	private long mPlayTime = 0;
		
    // 演奏を行うかどうかのフラグ
    private boolean isPlayLoop = false;  
    
    // Play Timer
    private boolean isPlayStart = false;
    private boolean isPlayRunning = false;
	
	// callback 
    private OnChangedListener mListener;  

	/*
	 * callback interface
	 */    
    public interface OnChangedListener {
    	// MIDI メッセージを通知する
        void onChanged( MidiMessage mes );
    	// 終了を通知する
        void onFinished();
    }

	/*
	 * callback
	 */ 
    public void setOnChangedListener( OnChangedListener listener ) {
        mListener = listener;
    }
       		
	/*
	 * コンストラクタ
	 */
	public MidiPlayer( ) {
        mMidiSender = new MidiSender();
	} // MidiPlayer


	/*
	 * setMessageList
	 * @param List<MidiList> list 
	 */
	public void setMessageList( List<MidiMessage> list ) {
		mList = list;
	} // setMessageList


	/*
	 * setTimebase
	 * @param long time	 
	 */
	public void setTimebase( long time ) {
		mTimebase = time;
	} // setTimebase


 	/**
	 * setInputPort
	 */ 
public void setInputPort(MidiInputPort port) {
    mMidiSender.setInputPort(port);
} // setInputPort

	/*
	 * isPlayRunning
	 */    
public boolean isPlayRunning() {
    return isPlayRunning;
} // isPlayRunning

	/*
	 * 開始する
	 */    
	public void start() {
		log_d( "start" );
		startPlayTimer();
		isPlayLoop = true;
		mStartTime = SystemClock.elapsedRealtime();
		mPlayTime = mStartTime; 
	}

	/*
	 * 終了する
	 */ 
	public void stop() {
		log_d( "stop" );
		stopPlayTimer();
        if ( mMidiSender != null ) {
            mMidiSender.sendSoundOff();
        }
	} // stop
		

// --- Play Timer Handler ---
	/*
	 * startPlayTimer
	 */ 
	private void startPlayTimer() {
        isPlayStart = true;
        updatePlayRunning();
    }

	/*
	 * stopPlayTimer
	 */     
    private void stopPlayTimer() {
        isPlayStart = false;
        updatePlayRunning();
    }

	/*
	 * updatePlayRunning
	 */ 
    private void updatePlayRunning() {
        boolean running = isPlayStart;
        if ( running != isPlayRunning ) {
			if ( running ) {
				if ( isPlayLoop ) {
					procMessageLoop();
				}
				// start
				playHandler.sendMessageDelayed(
					Message.obtain( playHandler, MSG_WHAT_PLAY ), 
					PLAY_DELAY );               
			} else {
				// stop
				playHandler.removeMessages( MSG_WHAT_PLAY );
			}
			isPlayRunning = running;
        }
    }

	/*
	 * Play Timer Handler
	 */ 
    private Handler playHandler = new Handler() {
        public void handleMessage( Message m ) {
            if ( isPlayRunning ) {
            	if ( isPlayLoop ) {
            		procMessageLoop();
            	}
                sendMessageDelayed(
                	Message.obtain( this, MSG_WHAT_PLAY ), 
                	PLAY_DELAY );
            }
        }
    };

	/*
	 * タイマーから起動して、MIDIのリストを処理する
	 */ 
    private synchronized void procMessageLoop() {
		// 全てのMIDIメッセージを処理したときは、終了する
    	if ( isEnd() ) {
			log_d( "Stop" );	
			stopPlayTimer();
			notifyFinished();
			return;
		}
		// タイマーからの起動を停止する
	    isPlayLoop = false;
	    while( true ) {
	    	// 全てのMIDIメッセージを処理したときは、終了する
	        if ( isEnd() ) break;
	        // 演奏時間にならないときは、いったん終了する
			if ( isWaiting() ) break;
    		MidiMessage mes = mList.get( mPointer ); 
			mPointer ++;
			if ( mes != null ) {
	        	// 演奏時間を設定する
				long playtime = mes.playtime / ( 1000 * mTimebase );
				mPlayTime = mStartTime + playtime;
				// 機器に送信する
sendMidiMessage( mes );
				nofityMessage( mes );
			}

		}					
		// タイマーからの起動を有効にする
		isPlayLoop = true;	
    }
	
	/*
	 * 全てのMIDIメッセージを処理したかの判定
	 * @return boolean
	 */ 
	private boolean isEnd() {
		if ( mPointer >= mList.size() ) {
			return true;
		}
		return false;
    }	
    	
	/*
	 * 演奏時間になったかの判定
	 * @return boolean
	 */ 
	private boolean isWaiting() {
		if ( SystemClock.elapsedRealtime() < mPlayTime ) {
			return true;
		}
		return false;
	}
	

	/*
	 * MIDI メッセージを送信する
	 * @param MidiMessage mes
	 */ 
    private void sendMidiMessage( MidiMessage mes ) {
    	if ( mes == null ) return;
    	int track = mes.track ;
    	int status = mes.status ;
    	byte[] bytes = mes.bytes ;
    	
		// MidiOutputDevice の引数が byte でなく int であるため、変換する
    	int len = bytes.length;
    	int[] int_bytes = new int[ 256 ];
    	for ( int i=0; i<256; i++ ){
			int_bytes[ i ] = 0;
		} 

        for ( int i=0; i<len; i++ ){
			int_bytes[ i ] = bytes[ i ] & 0x00ff;
		} 

		int cable = 0;
    	int byte0 = int_bytes[0];
    	int byte1 = int_bytes[1];
    	int byte2 = int_bytes[2];

    	switch ( status ) {
			case MidiMessage.STATUS_NOTE_OFF:
				mMidiSender.sendCommand( byte0, byte1, byte2 );
				break;
		
			case MidiMessage.STATUS_NOTE_ON:
				mMidiSender.sendCommand( byte0, byte1, byte2 );
				break;

			case MidiMessage.STATUS_POLYPHONIC_AFTERTOUCH:
				// midiOutputDevice.sendMidiPolyphonicAftertouch( cable, byte0, byte1, byte2 ) ;
				mMidiSender.sendCommand( byte0, byte1, byte2 );
				break;

			case MidiMessage.STATUS_CONTROL_CHANGE:
				// midiOutputDevice.sendMidiControlChange( cable, byte0, byte1, byte2 );
				mMidiSender.sendCommand( byte0, byte1, byte2 );
				break;

			case MidiMessage.STATUS_PROGRAM_CHANGE:
				// midiOutputDevice.sendMidiProgramChange( cable, byte0, byte1 );
				mMidiSender.sendCommand( byte0, byte1 );
				break;

			case MidiMessage.STATUS_CHANNEL_AFTERTOUCH:
				mMidiSender.sendCommand( byte0, byte1 );
				break;

			case MidiMessage.STATUS_PITCH_WHEEL:
				int amount = (byte1 << 7) + byte2;
				mMidiSender.sendPitchWheel( byte0, amount );
				break;

    		case MidiMessage.STATUS_SYSTEM_EXCLUSIVE:
    			// 無視する
    			break;
    	}

    } // sendMidiMessage


	/*
	 * MIDI メッセージを通知する
	 * @param MidiMessage m
	 */ 
    private void nofityMessage( MidiMessage m ) {
 		if ( mListener != null ) {
 			mListener.onChanged( m );
		}
    }

	/*
	 * 終了を通知する
	 */ 
    private void notifyFinished() {
 		if ( mListener != null ) {
 			mListener.onFinished();
		}
    }

 		   	
	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	}

} // class MidiPlayer
