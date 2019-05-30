/**
 *  Video Player Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.videoplayer1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
 * class VideoActivity 
 */
public class VideoActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Audio";
    private final static String TAG_SUB = "VideoActivity";


/**
 * key for Intent extra
 */ 
    public final static String EXTRA_KEY_FILE_NAME = "file_name";


/**
 * max progress value of SeekBar
 */ 
    public static final int MAX_PROGRESS = 100;

/**
 * VideoPlayer
 */ 
    private VideoPlayer mVideoPlayer;

/**
 * FileUtil
 */ 
    private FileUtil mFileUtil;

/**
 * SeekBar for display progress
 */ 
    private SeekBar mSeekbar;

/**
 * flag whether the user operate SeekBar
 */ 
    private boolean isUserSeeking;

/**
 * duration of the playing file
 */ 
    private int mDuration = 0;


/**
 * Flag whether SurfaceView is available
 * since SurfaceView works asynchronously
 */ 
    private boolean isSurfaceViewAvailable = false;

/**
 * File Name to play
 */ 
    private String mFileName;

/**
 * SurfaceView to display the Video
 */ 
    private SurfaceView mSurfaceView;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Button btnPlay = (Button) findViewById(R.id.button_play);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlayer();
                }
            }); // btnPlay


        Button btnStop = (Button) findViewById(R.id.button_stop);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopPlayer();
                }
            }); // btnStop


        Button btnPause = (Button) findViewById(R.id.button_pause);
            btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   pausePlayer();
                }
            }); // btnPause

        mVideoPlayer = createVideoPlayer();

        mSurfaceView = (SurfaceView)findViewById(R.id.surface);
        mSurfaceView.getHolder().addCallback(mSurfaceViewListener);


        mSeekbar = (SeekBar)findViewById(R.id.seekbar);
        mSeekbar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

    mFileUtil = new FileUtil(this);

    } // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        Intent intent = getIntent();
        mFileName = intent.getStringExtra(EXTRA_KEY_FILE_NAME);
        preparePlayer();
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
 * onDestroy
 */ 
@Override
public void onDestroy() {
    super.onDestroy();
    if(mVideoPlayer != null) {
        mVideoPlayer.close();
    }
    mVideoPlayer = null;
}


/**
 * createVideoPlayer
 */ 
private VideoPlayer createVideoPlayer() {
            VideoPlayer videoPlayer = new VideoPlayer();
            videoPlayer.setCallback(new VideoPlayer.PlayerCallback() {
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
}); // VideoPlayer.PlayerCallback
    return videoPlayer;
} // createVideoPlayer


 /**
 *  preparePlayer
 *  controll with flags
 *  since SurfaceView works asynchronously
 */
private void preparePlayer() {
    log_d("preparePlayer");

    if( !isSurfaceViewAvailable) {
        log_d("not SurfaceViewAvailable");
        return;
    } 
    if( mFileName == null) {
        log_d("not set FileName");
        return;
    } 

    isUserSeeking = false;
    mSeekbar.setMax(MAX_PROGRESS);
    setSeekbarProgress(0);

    mVideoPlayer.setSurfaceHolder(mSurfaceView.getHolder());

        String path = mFileUtil.getPathInExternalFilesDir(mFileName);
    boolean ret = mVideoPlayer.prepare(path);

    if(ret) {
            int duration = mVideoPlayer.getDuration();
            if(duration > 0 ) {
                    mDuration = duration;
            }
            // play the beginning a little bit
            // to display the image
            mVideoPlayer.start();
            mVideoPlayer.pause();
    }

    showToast(mFileName);
} // preparePlayer


 /**
 *  startPlayer
 */
private void startPlayer() {
log_d("startPlayer");
    mVideoPlayer.start();
} // startPlayer


 /**
 *  pausePlayer
 */
private void pausePlayer() {
log_d("pausePlayer");
    mVideoPlayer.pause();
} // pausePlayer


 /**
 *  stopPlayer
 */
private void stopPlayer() {
    log_d("stopPlayer");
    mVideoPlayer.stop();
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
                mVideoPlayer.seekTo(position);
} // procStopTrackingTouch



/**
 * SurfaceHolder.Callback
 */ 
    private final SurfaceHolder.Callback mSurfaceViewListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            log_d( "surfaceCreated" );
            isSurfaceViewAvailable = true;
            preparePlayer();
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            log_d( " surfaceDestroyed" );
            isSurfaceViewAvailable = false;
            stopPlayer();
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // log_d( "surfaceChanged" );
            // nop
    }

}; // SurfaceHolder.Callback


} // class VideoActivity
