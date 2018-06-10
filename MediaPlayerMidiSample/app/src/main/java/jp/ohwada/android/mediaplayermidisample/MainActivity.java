/**
 * Media Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.mediaplayermidisample;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * class MainActivity
 */
public class MainActivity extends AppCompatActivity {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "MainActivity";

    private MediaPlayer mMediaPlayer;

    	private Permission mPermission;

    private FileSelector mFileSelector ;

    private TextView mTextViewDescription;

/**
 * == onCreate ==
 */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_main );

		mTextViewDescription = (TextView) findViewById(R.id.TextView_description);

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

		ListView listView = (ListView) findViewById( R.id.ListView_1 );
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

} // onCreate


	/*
	 * === onDestroy ===
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopPlay();
	} // onDestroy


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
    if (mMediaPlayer != null) {
            toast_short(R.string.please_stop);
            return;
    }

    try {
        String path = file.getAbsolutePath();
        String name = file.getName();
        mMediaPlayer = createPlayer();
	    mMediaPlayer.setDataSource(path);
	    mMediaPlayer.prepare();
		mMediaPlayer.start();
        log_d("startPlay: " + path);
		toast_short(name);
    } catch (Exception e) {
                e.printStackTrace();
	}
} // startPlay


/**
 * stopPlay
 */
private void stopPlay() {
    if ( mMediaPlayer == null ) {
        return;
    }
    try {
        disposePlayer();
        toast_short("stop");
    } catch (Exception e) {
                e.printStackTrace();
	}
} // stopPlay


/**
 * createPlayer
 * 
 * create each time,
 * because exception occurs, when using one instance
 */
private MediaPlayer createPlayer() throws Exception {
        MediaPlayer  player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                log_d("onCompletion");
                stopPlay();
            }
        });
    return player;
} // createPlayer


/**
 * disposePlayer
 * 
 * dispose instance
 * because exception occurs, when using one instance
 */
private void disposePlayer() throws Exception {
    if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.stop();
    }
    mMediaPlayer.reset();
    mMediaPlayer.release();
mMediaPlayer = null;
} // disposePlayer


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
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity

