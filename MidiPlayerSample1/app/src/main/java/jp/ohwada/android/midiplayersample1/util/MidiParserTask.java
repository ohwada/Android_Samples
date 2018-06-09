/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

import android.content.Context;
import android.media.midi.MidiInputPort;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * MidiParserTask
 * MIDIファイルを解析をするタスク
 */
public class MidiParserTask {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MidiParserTask";

	// Parse Timer
	private static final int MSG_WHAT_PARSE = 1;
    private static final int PARSE_DELAY = 500;  // 0.5 sec

    private Context mContext;

	// MIDIファイルを解析をするクラス    
    private MidiParserAsyncTask mAsync;

	private FileBinary mFileBinary;

	// Parse Timer
    private boolean isStart = false;
    private boolean isRunning = false;
   

    private Callback mCallback;


   /*
     * callback interface
     */ 
    public interface Callback {
        public void onSuccess( List<MidiMessage> list, long timebase ) ;
        public void onError(int ret ) ;
    } // interface


/**
	 * === constructor ===
	 */		
public MidiParserTask  ( Context context ) {
        mContext = context;
		mFileBinary = new FileBinary( context );
} // ParserTask


/**
	 * setCallback
	 */		
public void setCallback( Callback callback ) {
        mCallback = callback;
} // setCallback


    /**
	 * parse
	 */ 
public void parse( String file_name ) {
		 byte[] bytes = mFileBinary.readAssetBinaryFile( file_name );
		startParse( bytes );
} // parse


    /**
	 * stop
	 */     
public void stop() {
    	log_d( "stop" );   
        stopParse();
} //  stop


    /**
	 * startParse
	 */ 
private void startParse(byte[] bytes) {

    	log_d( "startParse" );  

		// 実行中であれば、中断する
		if ( mAsync != null ) {
            stopParse();
        }

		mAsync = new MidiParserAsyncTask( mContext );
		mAsync.setBytes( bytes );
		mAsync.execute();
		startTimer();
	} // startParse


    /**
	 * stopParse
	 */     
    private void stopParse() {
    	log_d( "stopParse" );    	

    	stopTimer();
    	if ( mAsync != null ) {
        	mAsync.cancel( true );
        	mAsync = null;
        }

    } // stopParse




// -- Parse Timer Handler ---
	/**
	 * startTimer
	 */    
	 private void startTimer() {
		isStart = true; 
		updateParseRunning();
	} // startTimer
	

	/**
	 * stopTimer
	 */ 
	 private void stopTimer() {	    
		isStart = false;
		updateParseRunning();
	} // stopTimer


	/**
	 * updateParseRunning 
	 */		
    private void updateParseRunning() {
        boolean running = isStart;
        if ( running != isRunning ) {
            if ( running ) {
            	// start
                timerHandler.sendMessageDelayed( 
                	Message.obtain( timerHandler, MSG_WHAT_PARSE ), 
                	PARSE_DELAY );                         
             } else {
                // stop 
                timerHandler.removeMessages( MSG_WHAT_PARSE );
            }
			isRunning = running;
        }
    }


	/**
	 * === Timer Handler ===
	 */	    
    private Handler timerHandler = new Handler() {

        public void handleMessage( Message m ) {
            if ( isRunning ) {
				checkParseStatus();
                sendMessageDelayed( 
                	Message.obtain( this, MSG_WHAT_PARSE ), 
                	PARSE_DELAY );
            }
        }
    }; //  timerHandler
 
   	
	/**
	 * 周期的に解析の終了をチェックする
	 */	
    private synchronized void checkParseStatus() { 
    	if ( mAsync == null ) return;
		if ( mAsync.getStatus() != AsyncTask.Status.FINISHED ) return;

		stopTimer();

		int ret = mAsync.getRetCode();
		if ( ret == MidiParser.RET_SUCCESS ) {
			notifySuccess( mAsync.getList(), mAsync.getTimebase() );
		} else {
			notifyError( ret );
		}

	}  // checkParseStatus


 	/**
	 * notifySuccess
	 */ 
private void notifySuccess( List<MidiMessage> list, long timebase  ) {
    if (mCallback != null) {
        mCallback.onSuccess( list, timebase );
    }
} // notifySuccess


 	/**
	 * notifyError
	 */ 
private void notifyError(int ret ) {
    if (mCallback != null) {
        mCallback.onError(ret);
    }
} // notifyError


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class ParserTask
