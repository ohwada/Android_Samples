/**
 * Service Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.servicesample1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


/**
	 * class TimerService
	 * send Broadcast Message every second
	 */ 
public class TimerService extends Service {

// debug
    private final static boolean D = true;
	private final static String TAG = "Service";
	private final static String TAG_SUB = "TimerService";
  
    public static final String ACTION = "jp.ohwada.android.servicesample1.TimerService";


    // timer 1 sec
	private final static long TIMER_DELAY = 1000;
	private final static long TIMER_PERIOD = 1000;

    private Timer mTimer;

    private Handler mHandler;


 	/**
	 * onBind
	 */ 
  @Override
  public IBinder onBind(Intent intent) {
        log_d("onBind");
     return null;
  }

 	/**
	 * onCreate
	 */ 
  @Override
  public void onCreate() {
    super.onCreate();
        log_d("onCreate");
  } // onCreate

 	/**
	 * onStartCommand
	 */ 
@Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    log_d("onStartCommand");
    startTimer();
    return START_STICKY;
  } // onStartCommand
  
 	/**
	 * onDestroy
	 */ 
  @Override
  public void onDestroy() {
    super.onDestroy();
    log_d("onDestroy");
    stopTimer();
  } // onDestroy


 	/**
	 * startTimer
	 * send Broadcast Message every second
	 */ 	 
private void startTimer() {
    mHandler = new Handler();
    mTimer = new Timer(true);
    mTimer.schedule( new TimerTask() {

      @Override
      public void run(){
        mHandler.post( new Runnable(){
          public void run(){
            // log_d("Timer run" );
            sendBroadcast(new Intent(ACTION));
          }
        }); // Runnable
      }
    }, TIMER_DELAY, TIMER_PERIOD);
} // startTimer

 	/**
	 * stopTimer
	 */ 
private void stopTimer() {
    if( mTimer != null ) {
        mTimer.cancel();
        mTimer = null;
    }
} // stopTimer

 	/**
	 * write into logcat
	 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class TimerService
