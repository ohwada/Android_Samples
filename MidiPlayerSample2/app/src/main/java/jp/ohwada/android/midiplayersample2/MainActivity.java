/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample2;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.support.v7.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leff.midi.event.MidiEvent;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.ohwada.android.midiplayersample2.util.*;


/*
 * class MainActivity
 */
public class MainActivity extends AppCompatActivity {

// debug
    public final static boolean D = true; 
	public final static String TAG = "MIDI";
	private String TAG_SUB = "MainActivity";

    	private Permission mPermission;

    private FileSelector mFileSelector ;

    private MidiPlayer mMidiPlayer;

    private MidiManager mMidiManager;

    private MidiInputPort mMidiInputPort;

    private MidiRecieverPortSelector  mRecieverPortSelector;

    private TextView mTextViewDescription;

private ProgressBar mProgressBar;


/**
 * == onCreate ==
 */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_main );

		mTextViewDescription = (TextView) findViewById(R.id.TextView_description);

       mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

		Button btnShow = (Button) findViewById(R.id.Button_show);
		btnShow.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				prepareShowList( );
			}
		}); // btnShow

		Button btnStop = (Button) findViewById(R.id.Button_stop);
		btnStop.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				stopPlay( );
			}
		}); // btnStop

		ListView listView = (ListView) findViewById( R.id.ListView_file);

        mFileSelector = new  FileSelector(this);
        mFileSelector.setListView(listView);
        mFileSelector.setup();
        mFileSelector.setOnClickListener(new FileSelector.OnClickListener() {
            @Override
            public void onItemClick(File file) {
                startPlay(file);
            }
        }); // setOnClickListener

        mPermission = new Permission( this );
        mPermission.setPermReadExternalStorage();

        mMidiManager = (MidiManager) getSystemService(MIDI_SERVICE);

        mMidiPlayer = new MidiPlayer();
        mMidiPlayer.setOnEventListener(new MidiPlayer.OnEventListener() {
            @Override
            public void onStart(boolean fromBeginning) {
                onPlayerStart(fromBeginning);
            }
            @Override
            public void onStop(boolean finished) {
                onPlayerStop(finished);
            }
            @Override
            public void onEvent(MidiEvent event, long ms) {
                // TODO : interface only
                // nothing to do
            }
        }); // setOnEventListener


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
 * == onCreateOptionsMenu ==
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    } // onCreateOptionsMenu

    /**
     * === onOptionsItemSelected ===
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            toast_short("Settings");
        } else if (id == R.id.action_close) {
            closeInputPort();
        } 
        return true;
    } // onOptionsItemSelected

    /**
     * === onRequestPermissionsResult ===
     */
    @Override
    public void onRequestPermissionsResult( int request, String[] permissions, int[] results ) {

log_d("onRequestPermissionsResult");
    	boolean ret = mPermission.procRequestPermissionsResult( request, permissions, results );
        if ( ret ) {
        	 showList();
        }

        super.onRequestPermissionsResult(request, permissions, results);
        
    } // onRequestPermissionsResult


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
 * prepareShowList
 */
private void prepareShowList() {
    log_d("prepareShowList");
    mTextViewDescription.setVisibility(View.GONE);
    if ( mPermission.hasPerm(true) )  {
		showList();
    }
} // prepareShowList


/**
 * ShowList
 */
private void showList() {
    boolean ret = mFileSelector.showList();
    if (!ret) {
        mTextViewDescription.setVisibility(View.VISIBLE);
        toast_short(R.string.description);
    }
} // ShowList


/**
 * startPlay
 */
private void startPlay( File file ) {
    log_d("startPlay");
    if ( mMidiInputPort == null ) {
        toast_short(R.string.please_select);
        log_d(R.string.please_select);
        return;
    }

    if (isRunning() ) {
            toast_short(R.string.please_stop);
            log_d(R.string.please_stop);
            return;
    }

    mMidiPlayer.start(file);
    String path = file.getAbsolutePath();
    log_d("path: " + path);
    String name = file.getName();
    toast_short(name);
} // startPlay


/**
 * stopPlay
 */
private void stopPlay() {
    log_d("stopPlay");
    if (mMidiPlayer != null ) {
        mMidiPlayer.stop();
    }
    hideProgressBar();
    toast_short("stop");
} // stopPlay

/**
 * isRunning
 */
private boolean isRunning() {
    if (mMidiPlayer != null ) {
        return mMidiPlayer.isRunning();
    }
    return false;
} // isRunning

/**
 * onPlayerStart
 */
    private void onPlayerStart(boolean fromBeginning) {
        log_d("procPlayerStart: " + fromBeginning);

    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast_short("start");
                showProgressBar();
            }
    }); // runOnUiThread

    } // onPlayerStart

/**
 * onPlayerStop
 */
private void onPlayerStop(boolean  finished) {
    log_d("onPlayerStop: " + finished);

    runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast_short("finished");
                hideProgressBar();
            }
    }); // runOnUiThread

} // onPlayerStop

/**
 * showProgressBar
 */
private void showProgressBar() {
        if (mProgressBar != null ) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
} // showProgressBar

/**
 * hideProgressBar
 */
private void hideProgressBar() {
        if (mProgressBar != null ) {
            mProgressBar.setVisibility(View.GONE);
        }
} // hideProgressBar


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
        toast_short("close InputPort");
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

