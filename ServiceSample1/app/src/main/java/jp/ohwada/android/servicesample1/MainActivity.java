/**
 * Service Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.servicesample1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
	 * class MainActivity
	 */ 
public class MainActivity extends AppCompatActivity  {
  
// debug
    private final static boolean D = true;
	private final static String TAG = "Service";
	private final static String TAG_SUB = "MainActivity";



 	private TimerReceiver mTimerReceiver;

 	private TextView mTextViewTimer;

         	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");

 	/**
	 * onCreate
	 */ 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewTimer = (TextView)findViewById(R.id.TextView_timer);

        Button btnStart = (Button)findViewById(R.id.Button_start);        
         btnStart.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
            startTimerService();
            startTimerReceiver();
            toast_short("start Service");
      }
        }); //  btnStart

        Button btnStop = (Button)findViewById(R.id.Button_stop);
         btnStop.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
            stopTimerService();
            stopTimerReceiver();
            toast_short("stop Service");
      }
        }); //  btnStop

    } // onCreate

 	/**
	 * onDestroy
	 */ 
  @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimerService();
        stopTimerReceiver();
    } // onDestroy



 	/**
	 * startTimerService
	 */ 
private void startTimerService() {
    Intent intent = new Intent(this, TimerService.class);
         startService(intent);
} // startTimerService

 	/**
	 * stopTimerService
	 */ 
private void stopTimerService() {
    Intent intent = new Intent(this, TimerService.class);
        stopService(intent);
} // stopTimerService



 	/**
	 * startTimerReceiver
	 */ 
private void startTimerReceiver() {
 mTimerReceiver = new TimerReceiver();
IntentFilter intentFilter = new IntentFilter();
intentFilter.addAction(TimerService.ACTION);
registerReceiver( mTimerReceiver, intentFilter);
} // startTimerReceiver

 	/**
	 *  stopTimerReceiver
	 */ 
private void stopTimerReceiver() {

    // exception (not registered) occurs
    // already unregisterReceiver
        try {
            if ( mTimerReceiver != null ) {
                unregisterReceiver(mTimerReceiver);
                mTimerReceiver = null;
            }
		} catch (Exception e) {
            // e.printStackTrace();
		}
} //  stopTimerReceiver


 	/**
	 * stopTimerService
	 */ 
private void updateTimer(Date date) {
    String text = mSimpleDateFormat.format(date);
        mTextViewTimer.setText(text);
} // updateTimer

/**
	 * toast_short
	 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

 
 	/**
	 * class TimerReceiver
	 */ 
    private class TimerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // log_d("onReceive");
            Date now = new Date();
            updateTimer(now);
        } // onReceive

    } // class TimerReceiver

} // class MainActivity
