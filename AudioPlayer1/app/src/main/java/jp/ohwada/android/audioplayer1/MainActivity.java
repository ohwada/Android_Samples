/**
 *  Audio Player Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.audioplayer1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;


/**
 * class MainActivity 
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Audio";
    private final static String TAG_SUB = "MainActivity";


/**
 * NOTE
 * this app can use the Files in Assets forlder, 
 * or Files in external files dir as DataSource.
 * in the latter modo, you copy Files from Assets folder to 
external files dir, 
 * and then you can use.
 * TODO 
 * in the former mode,
 it is not stable in operation.
 * is it useless to specify the DataSource by FileDescriptor
 */ 
    private final static boolean USE_EXTERNAL_FILES_DIR = true;


/**
 * File extension of File in Asset folder
 */ 
    private final static String FILE_EXT = ".mp3";

    private final static String LF = "\n";

/**
 * max progress value of SeekBar
 */ 
    public static final int MAX_PROGRESS = 100;


    private AudioPlayer mAudioPlayer;

    private FileUtil mFileUtil;

    private ListView mListView ;

    private SeekBar mSeekbar;

   	private ListAdapter mAdapter;

	private List<String> mList;

/**
 * flag whether the user operate SeekBar
 */ 
    private boolean isUserSeeking;

/**
 * duration of the playing file
 */ 
    private int mDuration = 0;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            mListView = (ListView) findViewById(R.id.list);

     
        Button btnCopy = (Button) findViewById(R.id.button_copy);
            btnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyFiles();
                }
            }); // btnCopy

    if(USE_EXTERNAL_FILES_DIR) {
        btnCopy.setVisibility(View.VISIBLE);
    }

        Button btnPlay = (Button) findViewById(R.id.button_play);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlayer();
                }
            }); // btnPlay


        Button btnPause = (Button) findViewById(R.id.button_pause);
            btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   pausePlayer();
                }
            }); // btnPause


        Button btnStop = (Button) findViewById(R.id.button_stop);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopPlayer();
                }
            }); // btnStop




        mSeekbar = findViewById(R.id.seekbar);
        mSeekbar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mList = new ArrayList<String>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener


            mAudioPlayer = new AudioPlayer();
            mAudioPlayer.setCallback(new AudioPlayer.PlayerCallback() {
                @Override
                public  void onPrepared(int duration) {
                    procPrepared(duration);
                }
                @Override
                public void onCompletion() {
                    procCompletion();
                }
                @Override
                public void onUpdatePosition(int position) {
                    procUpdatePosition(position);
                }
}); // AudioPlayer.PlayerCallback


mFileUtil = new FileUtil(this);

    } // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        showList();
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        log_d("onPause");
        super.onPause();
        stopPlayer();
} // onPause


/**
 * onPause
 */ 
@Override
public void onDestroy() {
    super.onDestroy();
    if(mAudioPlayer != null) {
        mAudioPlayer.close();
    }
    mAudioPlayer = null;
}


/** 
 *   copyFiles from Asset folder to ExternalFilesDir
 */
private void copyFiles() {
    if( !USE_EXTERNAL_FILES_DIR ) return;
    boolean ret = mFileUtil.copyFilesAssetToExternalFilesDir( FILE_EXT );
    if(ret) {
        showList();
        showToast("copy successful");
    } else {
        showToast("copy faild");
    }
} // copyFiles


/** 
 *   showList
 *   show File Name in ExternalFilesDir
 */
private void showList() {

    if(USE_EXTERNAL_FILES_DIR) {
        mList = mFileUtil.getFileNameListInExternalFilesDir();
    } else {
        mList = mFileUtil.getFileListInAsset(FILE_EXT);
    }
    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();
} // showList


/** 
 *  procItemClick
 */
private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		//log_d(msg );

		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        String item = mList.get( position );
        preparePlayer(item);

} // procItemClick


 /**
 *  preparePlayer
 */
private void preparePlayer(String fileName) {
    log_d("preparePlayer:" + fileName);

    if( mAudioPlayer.isPlaying() ) {
        showToast("please STOP");
        return;
    }

    boolean ret = false;
    if(USE_EXTERNAL_FILES_DIR) {
        String path = mFileUtil.getPathInExternalFilesDir(fileName);
        ret = mAudioPlayer.prepare(path);
    } else {
        FileDescriptor fd = mFileUtil.getFileDescriptorInAsset(fileName);
        ret = mAudioPlayer.prepare(fd);
    }
    if(ret) {
            int duration = mAudioPlayer.getDuration();
            if(duration > 0 ) {
                    mDuration = duration;
            }
    }
    isUserSeeking = false;
    mSeekbar.setMax(MAX_PROGRESS);
    setSeekbarProgress(0);
    showToast(fileName);
} // preparePlayer


 /**
 *  startPlayer
 */
private void startPlayer() {
log_d("startPlayer");
    mAudioPlayer.start();
} // startPlayer


 /**
 *  pausePlayer
 */
private void pausePlayer() {
log_d("pausePlayer");
    mAudioPlayer.pause();
} // pausePlayer


 /**
 *  stopPlayer
 */
private void stopPlayer() {
    log_d("stopPlayer");
    mAudioPlayer.stop();
    setSeekbarProgress(0);
} // stopPlayer



 /**
 *  procPrepared
 */
private void procPrepared(int duration) {
        log_d("procPrepared; " + duration);
        if(duration > 0 ) {
            mDuration = duration;
        }
} // procPrepared

 /**
 *  procCompletion
 */
private void procCompletion() {
log_d("procCompletion");
    setSeekbarProgress(0);
} // procCompletion


/** 
 *   procUpdatePosition
 *   convert the position to the progress value of SeekBar
 *   and set the progress value
 */
private void procUpdatePosition(int position) {
    //log_d("procUpdatePosition: " + position);

     float ratio = 0;
    if( mDuration > 0 ) {
        ratio = (float)position / (float)mDuration;
    }

    int progress = (int)(MAX_PROGRESS * ratio);
    setSeekbarProgress(progress);

} // procUpdatePosition


/** 
 *   setSeekbarProgress
 */
private void setSeekbarProgress(int progress) {
        if (!isUserSeeking) {
            mSeekbar.setProgress(progress);
        }
} // setSeekbarProgress


/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * SeekBar.OnSeekBarChangeListener 
 */ 
private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            // This holds the progress value for onStopTrackingTouch.
            int userSelectedPosition = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        userSelectedPosition = progress;
                        isUserSeeking = true;
                    }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nop
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                procStopTrackingTouch(userSelectedPosition);
            }

}; // SeekBar.OnSeekBarChangeListener


/**
 * procStopTrackingTouch
 * convert the progess value of SeekBar to the position of the playing file 
 * and seek to the position
 */ 
private void procStopTrackingTouch(int userSelectedPosition) {
log_d("procStopTrackingTouch: " + userSelectedPosition);
                float ratio = (float)userSelectedPosition/(float)MAX_PROGRESS;
                int position = (int)(ratio * mDuration);
                mAudioPlayer.seekTo(position);
} // procStopTrackingTouch


} // class MainActivity
