/**
 *  Video Player Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.videoplayer1;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;


/**
  * VideoPlayer
  */
public class VideoPlayer implements OnPreparedListener, OnCompletionListener, OnErrorListener {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Video";
    private final static String TAG_SUB = "VideoPlayer";


// Value: -1004
    private final static int ERROR_IO = MediaPlayer.MEDIA_ERROR_IO;
// Value: -1007
    private final static int ERROR_MALFORMED = MediaPlayer.MEDIA_ERROR_MALFORMED;
// Value: 200
    private final static int ERROR_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
// Value: 100
    private final static int ERROR_SERVER_DIED = MediaPlayer.MEDIA_ERROR_SERVER_DIED;
// Value: -110
    private final static int ERROR_TIMED_OUT = MediaPlayer.MEDIA_ERROR_TIMED_OUT;
// Value: 1
    private final static int ERROR_UNKNOWN = MediaPlayer.MEDIA_ERROR_UNKNOWN;
// Value: -1010
    private final static int ERROR_UNSUPPORTED = MediaPlayer.MEDIA_ERROR_UNSUPPORTED;


/**
  *   Constant for Timer Interval
  *   1 sec
  */
    private static final int TIMER_INTERVAL = 1000;


/**
  * Constant for state of the Video Player
  */
    private static final int STATE_STOP = 0;
    private static final int STATE_PREPARE = 1;
    private static final int STATE_START = 2;
    private static final int STATE_PAUSE = 3;

/**
  *  state of this Video Player
  */
    private int mState = STATE_STOP;


    /**
     * A lock protecting state.
     */
    protected final Object mStateLock = new Object();


/**
  * interface PlayerCallback
  */
public interface PlayerCallback {
    void onPrepared(int duration);
    void onCompletion();
    void onUpdatePosition(int position);
}


/**
  * PlayerCallback
  */
    private PlayerCallback mPlayerCallback;


/**
  * MediaRecorder
  */
    private MediaPlayer mMediaPlayer ;

/**
  * ProgressTask
  * read and notify CurrentPosition periodically
  */
  private Runnable mProgressTask;


/**
  * TimerHandler
  */
  private Handler mTimerHandler = new Handler();


/**
  * Flag to excute ProgressTask
  * set to true at initialization
  * Ithe task ends, if set false
  */
    private boolean isTimerRunning = false;

/**
  * Flag to excute to updste ProgressTask
  */
    private boolean isUpdataProgressRunning = false;


/**
  * View Holder for display Video
  */
    private SurfaceHolder mSurfaceHolder;

/**
  * constractor
  */
public VideoPlayer() {
    // create and strat ProgressTask
    mProgressTask = createProgressTask();
    isTimerRunning = true;
    mTimerHandler.post( mProgressTask);
}


/**
  * setCallback
  */
public void setCallback(PlayerCallback cb) {
    mPlayerCallback = cb;
}


/**
 * setSurfaceHolder
 */ 
public void setSurfaceHolder(SurfaceHolder holder) {
    mSurfaceHolder = holder;
} // setSurfaceHolder


/**
 * seekTo
 */ 
public void close() {
    stop();
    isTimerRunning = false;
}

/**
 *  onPrepared
 *  called when the media file is ready for playback.
 */
@Override
public void onPrepared(MediaPlayer mp) {
    log_d("onPrepared" );
    int duration = mp.getDuration();
    if(mState == STATE_START) {
        // start to display Video, if start method  is called
        mp.start();
    }
    if(mPlayerCallback != null) {
        mPlayerCallback.onPrepared(duration);
    }
}

/**
 *  onCompletion
 *  called when the end of a media source is reached during playback.
 */
@Override
public void onCompletion(MediaPlayer mp) {
    log_d("onCompletion" );

    if(mState == STATE_START) {
        // pause video, if idisplaying
        pause();
        seekTo(0);
    }

    if(mPlayerCallback != null) {
        mPlayerCallback.onCompletion();
    }

} // onCompletion


/**
 *  called to indicate an error
 * TODO : contains a value not defined in API
 */
@Override
public boolean onError(MediaPlayer mp, int what, int extra) {
    String str_what = getError(what);
    String str_extra = getError(extra);
    String msg = "onError: what= " + what + " " + str_what + " , extra= " + extra + " " + str_extra;
    log_d(msg);
    // True if the method handled the error, 
    // false if it didn't. Returning false, 
    // or not having an OnErrorListener at all, will cause the OnCompletionListener to be called.
    return true;
} // onError


/**
 *  getError
 */
private String getError(int code) {
    String error = "";
    switch (code) {
        case ERROR_IO:
            error = "MEDIA_ERROR_IO";
            break;
        case ERROR_MALFORMED:
            error = "MEDIA_ERROR_MALFORMED";
            break;
        case ERROR_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
            error = "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
            break;
        case ERROR_SERVER_DIED:
            error  = "MEDIA_ERROR_SERVER_DIED";
            break;
    case ERROR_TIMED_OUT:
        error  = "MEDIA_ERROR_TIMED_OUT";
        break;
    case ERROR_UNKNOWN:
        error = "MEDIA_ERROR_UNKNOWN";
        break;
    case ERROR_UNSUPPORTED:
        error= "MEDIA_ERROR_UNSUPPORTED";
        break;
    }
    return error;
}



/**
 *  prepare with filePath
 */
public boolean prepare(String filePath) {
        log_d("prepare" );
        synchronized (mStateLock) {
            mState = STATE_PREPARE;
        }
        boolean is_error = false;
        mMediaPlayer = new MediaPlayer();
    try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.prepare();
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
        is_error = true;
    } catch (IllegalStateException e) {
        e.printStackTrace();
        is_error = true;
    } catch (IOException e) {
        e.printStackTrace();
        is_error = true;
    }

    return ! is_error;
} // prepare


/**
 *  start
 */
public boolean start() {
log_d("start" );
        synchronized (mStateLock) {
            mState = STATE_START;
            isUpdataProgressRunning = true;
        }
    boolean is_error = false;
    if(mMediaPlayer == null) return false;
    try {
        mMediaPlayer.start();
    } catch (IllegalStateException e) {
        e.printStackTrace();
        is_error = true;
    }
    return ! is_error;
} // start


/**
 *  pause
 */
public void pause() {
    log_d("pause" );
    synchronized (mStateLock) {
        mState = STATE_PAUSE;
         isUpdataProgressRunning = false;
    }
    if(mMediaPlayer == null) return;
    try{
        mMediaPlayer.pause();
    } catch (IllegalStateException e) {
        e.printStackTrace();
    }
} // pause


/**
 *  stop
 */
public void stop() {
    log_d("stop" );
    synchronized (mStateLock) {
        mState = STATE_STOP;
    isUpdataProgressRunning = false;
    }
    if(mMediaPlayer == null) return;
    try{
        // release resources
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    } catch (IllegalStateException e) {
        e.printStackTrace();
    }
    mMediaPlayer = null;
} // stop


/**
 *  isPlaying
 */
public boolean isPlaying() {
    if(mMediaPlayer == null) return false;
    boolean ret = false;
    try {
        ret = mMediaPlayer.isPlaying();
    } catch (IllegalStateException e) {
        e.printStackTrace();
    }
    return ret;
} // isPlaying


/**
 *  getDuration
 */
public int getDuration() {
    if(mMediaPlayer == null) return 0;
    int ret = 0;
    try {
        ret = mMediaPlayer.getDuration();
    } catch (IllegalStateException e) {
        e.printStackTrace();
    }
    return ret;
} //  getDuration


/**
 * seekTo
 */ 
public void seekTo(int position) {
    log_d("seekTo: " + position);
    if(mMediaPlayer == null) return;
        mMediaPlayer.seekTo(position);
}


/**
 * createProgressTask
  * excute updateProgress periodically
 */ 
private Runnable createProgressTask() {

Runnable task = new Runnable() {
    @Override
    public void run() {
        if (!isTimerRunning) {
            // stop when flag is unset
            return;
        }
        if (isUpdataProgressRunning) {
            updateProgress(); 
        }
//      // call myself after 1 sec
        mTimerHandler.postDelayed(this,  TIMER_INTERVAL);
    } // run
}; // Runnable
    return task;
} // createProgressTask


/**
 * updateProgress
  * read and notify CurrentPosition periodically
  * call this,  right after playback is complete
 */ 
private void updateProgress() {
    if (mMediaPlayer == null ) return;
    try {
        int position = mMediaPlayer.getCurrentPosition();
        notifyUpdatePosition(position) ;
    } catch (IllegalStateException e) {
        e.printStackTrace();
    }
} // updateProgress

/**
 * notifyUpdatePosition
 */ 
private void notifyUpdatePosition(int position) {
    if( mPlayerCallback != null) {
        mPlayerCallback.onUpdatePosition(position); 
    }
} // notifyUpdatePosition



/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class VideoPlayer

