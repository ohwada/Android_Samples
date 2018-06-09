/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * MidiParserAsyncTask
 * MIDIファイルを解析をする非同期タスク
 */
public class MidiParserAsyncTask extends AsyncTask<Void, Void, Boolean> {

	// debug
	private static final String TAG = "MidiParserAsyncTask";
	private static final boolean D = true;
	
	// excute
 	private MidiParser mParser; 	
 	private MidiMultiToSingle mConverter;			
 	private byte[] mBytes = null;
	private int mRetCode = -1;

 	// result
	private long mTimebase = 0;	
	private List<MidiMessage> mMessageList = null;
		
	/**
	 * === constructor ===
	 */			 
    public MidiParserAsyncTask( Context context ) {
        super();
        mParser = new MidiParser( context );
        mConverter = new MidiMultiToSingle();
    }
	 
	/**
	 * === onPreExecute ===
	 */	
    @Override
    protected void onPreExecute(){
		// dummy	
    }

	/**
	 * === doInBackground ===
	 * @return Boolean
	 */	
    @Override
    protected Boolean doInBackground( Void... param ) {
		mRetCode = mParser.parse( mBytes );
		if ( mRetCode == MidiParser.RET_SUCCESS ) {
			// mutitl to single
			mConverter.convert( mParser.getList() ); 
		}
		return true;
    };
	
	/**
	 * === onProgressUpdate ===
	 */	
    @Override
    protected void onProgressUpdate( Void... params ) {
		// dummy
    }

	/**
	 * === onPostExecute ===
	 */	 
    @Override
    protected void onPostExecute( Boolean result ) {
		mMessageList = mConverter.getList();
		mTimebase = mParser.getTimebase();
    }

	/**
	 * set Bytes
	 * @param byte[] bytes
	 */  	
	public void setBytes( byte[] bytes ) {
		mBytes = bytes;
	}

	/**
	 * get RetCode
	 * @return int
	 */
	public int getRetCode() {
		return mRetCode;
	}
	    		
	/**
	 * get List
	 * @return List<MidiMessage>
	 */  	
	public List<MidiMessage> getList() { 
		return mMessageList;
	}

	/**
	 * get Timebase
	 * @return long
	 */ 
	public long getTimebase() {
		return mTimebase;
	}

	/**
	 * write log
	 * @param String msg
	 */ 
	protected void log_d( String msg ) {
	    if (D) Log.d( TAG,  msg );
	}
		    		   
}