/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1;

import android.media.midi.MidiInputPort;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import jp.ohwada.android.midiplayersample1.util.*;


/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MainActivity";

    // file name
	private final static String MIDI_FILE_NAME_1 = "furusato.mid";

	private final static String MIDI_FILE_NAME_2 = "we_wish_you_a_merry_christmas.mid";

	// MIDIファイルを解析をするクラス 
    private MidiParserTask mParserTask;   

   	// MIDIメッセージを演奏するクラス
   	private MidiPlayer mMidiPlayer = null;
   	
    private MidiManager mMidiManager;


    private MidiInputPort mMidiInputPort;

    private MidiRecieverPortSelector  mRecieverPortSelector;

   	// UI
	private ListView mListView;


	private ArrayAdapter<String> mAdapter;
 


   


	/*
	 * === onCreate ===
	 */
	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		Button btnStart1 = (Button) findViewById(R.id.Button_start_1);
		btnStart1.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				startPlay( MIDI_FILE_NAME_1 );
			}
		}); // btnStart1

		Button btnStart2 = (Button) findViewById(R.id.Button_start_2);
		btnStart2.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				startPlay( MIDI_FILE_NAME_2 );
			}
		}); // btnStart2

		Button btnStop = (Button) findViewById(R.id.Button_stop);
		btnStop.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				stopPlay( );
			}
		}); // btnStop
		
	
		mListView = (ListView) findViewById( R.id.ListView_report );
		mAdapter = new ArrayAdapter<String>( this, R.layout.list_report, R.id.TextView_report );
		mListView.setAdapter( mAdapter );


        mParserTask = new MidiParserTask(this);
        mParserTask.setCallback(
            new MidiParserTask.Callback() {

            public void onSuccess( List<MidiMessage> list, long timebase ) {
                playMidi(list, timebase );
            }
            
            public void onError(int ret ) {
                toast_short("parse error");
            }

          } ); // setCallback



		mMidiPlayer = new MidiPlayer();
		mMidiPlayer.setOnChangedListener( new MidiPlayer.OnChangedListener() {

			@Override
			public void onChanged( MidiMessage m ) {
				showMidiMessage( m );				
			}

			@Override
			public void onFinished() {
				addMessage( "Finish" );				
			}

		}); // setOnChangedListener


        mMidiManager = (MidiManager) getSystemService(MIDI_SERVICE);

			// TODO : runtime permission
			// LogFile.mkDir();

        setupMidi();

	} // onCreate



	/*
	 * === onDestroy ===
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopPlay();
        closeInputPort();
	} // onDestroy

    /**
     * === onCreateOptionsMenu ===
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        log_d( "onCreateOptionsMenu" );
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // onCreateOptionsMenu


    /**
     * === onOptionsItemSelected ===
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        log_d("onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            toast_short("Settings");
        } else if (id == R.id.action_close) {
            closeInputPort();
            toast_short("close port");
        } 
        return true;
    } // onOptionsItemSelected


    /**
	 * startPlay
	 */     
    private void startPlay( String file_name ) {
    log_d("startPlay");

    if ( mMidiInputPort == null ) {
        toast_short("please select reciever");
        log_d("please select reciever");
        return;
    }

		mAdapter.clear();

		// ファイルを読み込む
		addMessage( "File Read" );

        mParserTask.parse(file_name);

} //  startPlay



    /**
	 * stopPlay
	 */     
    private void stopPlay() {
    	log_d( "stopPlay" );    	
    	addMessage( "Stop" );

    	if ( mParserTask != null ) {
        	mParserTask.stop();
        }

        if ( mMidiPlayer != null ) {
        	mMidiPlayer.stop();
        }

    } // stopPlay


	/*
	 * メッセージを表示する
	 */     
    private void showMidiMessage( MidiMessage mes ) {
    	if ( mes == null ) return;

    	int track = mes.track ;
    	int status = mes.status ;
    	byte[] bytes = mes.bytes ;
    	String str = track + " : ";
    	for ( byte b: bytes ) {
			str += toHexString( b ) + " ";
		} 
		addMessage( str );
} // showMidiMessage


	/*
	 * HEX文字列に変換する
	 * @param byte b
	 * @return String
	 */ 
	private String toHexString( byte b ) {
		int n = b & 0x00ff;
		String str = Integer.toHexString( n );
		if ( n < 0x0010 ) {
			str = "0" + str;
		}
    	return str;
    } // toHexString


	/**
	 * addMessage
	 * @param String msg
	 */	
	private void addMessage( String msg ) {
		mAdapter.add( msg );
        mAdapter.notifyDataSetChanged();
	} // addMessage

 

	/**
	 * MIDIメッセージを演奏する
	 */		
    private void playMidi( List<MidiMessage> list, long timebase ) { 
    	if ( list == null ) {
			toast_short( "Parser Error null" );
			return;
    	}
    	if ( list.size() == 0 ) {
			toast_short( "Parser Error zero" );
			return;
    	}

// 演奏中なら、停止する
    if ( mMidiPlayer.isPlayRunning() ) {
            mMidiPlayer.stop();
			toast_short( "sop" );
			log_d( "sop, because running " );
    }


		mMidiPlayer.setMessageList( list );
		mMidiPlayer.setTimebase( timebase );
		mMidiPlayer.start();
    	addMessage( "Play" );

	}//  // playMidi



 	/**
	 * setupMidi
	 */
    private void setupMidi() {
log_d("setupMidi");
boolean hasMidi = getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI);
        if (!hasMidi) {
            log_d(R.string.not_support_midi);
            toast_short(R.string.not_support_midi);
            return;
        }

        // Setup Spinner that selects a MIDI input port.
        mRecieverPortSelector = new MidiRecieverPortSelector(mMidiManager,
                this, R.id.spinner_receivers);

    mRecieverPortSelector.setCallback( new MidiRecieverPortSelector.Callback() {

            @Override
        public void onPortOpened(MidiInputPort port) {
                log_d("onPortOpened");
                procPortOpened(port);
            }

        }); // setCallback

    } // setupMidi


/**
 *  procPortOpened
 */
private void procPortOpened(final MidiInputPort port) {
    log_d("procPortOpened");

     if (port != null) {
        mMidiInputPort = port;
         mMidiPlayer.setInputPort(port);
    }

    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showUiThread(port);
            }
    }); // runOnUiThread

} // procPortOpened


/**
 *  showUiThread
 */
private void showUiThread(MidiInputPort port) {

            log_d("showUiThread");

            if (port == null) {
                log_d(R.string.error_port_busy);
                toast_short(R.string.error_port_busy);

            } else {
                log_d(R.string.port_open_ok);
                toast_short(R.string.port_open_ok);

            }

} // showUiThread



/**
 * closeInputPort
 */
    private void closeInputPort() {
        log_d("closeInputPort");
        if (mMidiInputPort != null) {
            try {
                mMidiInputPort.onFlush();
                mMidiInputPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // closeInputPort


/**
 * toast_short
 */
	private void toast_short( int res_id ) {
        toast_short( getString(res_id) );
	} // toast_short


/**
 * toast_short
 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short
 

 	/**
	 * write into logcat
	 */ 
	private void log_d( int res_id ) {
        log_d( getString(res_id) );
	} // log_d


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class MainActivity
