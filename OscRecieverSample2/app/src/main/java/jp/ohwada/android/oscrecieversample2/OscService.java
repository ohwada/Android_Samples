/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.oscrecieversample2;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.ohwada.android.oscrecieversample2.util.*;

/**
	 * class OscService
	 */ 
public class OscService extends Service {

// debug
    private final static boolean D = true;
	private final static String TAG = "OSC";
	private final static String TAG_SUB = "OscService";
  
    // Broadcast
    public static final String ACTION = "jp.ohwada.android.oscportoutsample.OscService";

     public static final String KEY_MODE = "mode";

     public static final int MODE_NONE = 0;

 public static final int MODE_STATUS = 1;

     public static final int MODE_MESSAGE = 2;

     public static final int MODE_ERROR = 3;

     public static final String KEY_STATUS =  "status";

     public static final int STATUS_NONE =  0;

     public static final int STATUS_NOT_OPEN =  1;

     public static final int STATUS_LISTENING =  2;

     public static final int STATUS_RECIEVE =  3;

     public static final int STATUS_INVALID_OSC_ADDR =  4;

     public static final int STATUS_NOT_CONVERT =  5;

     public static final int STATUS_UNMATCH =  6;

     public static final String KEY_MESSAGE =  "message";

     public static final String KEY_ERROR =  "error";

    	private final static String LF = "\n";

    private final static String  DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

        // OSC message
    	private final static String ADDRESS_ON = "/s_new";

    // debug
    private static final int NUM_LOG_CHARS = 10;

    private OSCPortInCustom mOSCPortIn;




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
  }

 	/**
	 * onStartCommand
	 */ 
@Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    log_d("onStartCommand");
    setup();
    return START_STICKY;
  } // onStartCommand
  
 	/**
	 * onDestroy
	 */ 
  @Override
  public void onDestroy() {
    super.onDestroy();
    log_d("onDestroy");
    closeInputPort();
  } // onDestroy

 	/**
	 * setup
	 */ 
private void setup() {
    log_d("setup");
	OSCPortInCustom port =  opentInputPort();
    if (port == null) {
        notifyStatus(STATUS_NOT_OPEN);
        return;
    }

        port.setOnEventListener(new OSCPortInCustom.OnEventListener() {
            @Override
            public void onRecieve(byte[] bytes) {
                    notifyRecieve(bytes);
            }
            @Override
            public void onError(int error) {
                    notifyError(error);
            }
            @Override
		    public void onException(Exception ex) {
                notifyException(ex);
            }
        }); // setOnEventListener

    OSCListener listener = createListener();
// todo : Set address from activity
    port.addListener(ADDRESS_ON, listener);
    port.startListening();

	if ( port.isListening() ){
            notifyStatus(STATUS_LISTENING);
    }
	mOSCPortIn = port;

} // setup

 	/**
	 * opentInputPort
	 */ 
private OSCPortInCustom opentInputPort() {
    log_d("opentInputPort");
	OSCPortInCustom port = null;
    try {
	   port = new OSCPortInCustom(OSCPort.defaultSCOSCPort());
		} catch (Exception e) {
                // already use ?
            e.printStackTrace();
                notifyException(e);
		}
        return port;
} // opentInputPort

 	/**
	 * closeInputPort
	 */ 
public void closeInputPort() {
    log_d("closeInputPort");
	if (mOSCPortIn != null) {
	    mOSCPortIn.stopListening();
		mOSCPortIn.close();
    }
} // closeInputPort


 	/**
	 * createListener
	 */ 
private OSCListener createListener() {

	OSCListener listener = new OSCListener() {
		public void acceptMessage(java.util.Date time, OSCMessage message) {
			log_d("listener acceptMessage");
            notifyMessage(time, message);
		}
	}; // OSCListener

    return listener;
} // createListener


 	/**
	 * notifyRecieve
	 */ 
private void notifyRecieve(byte[] bytes) {
    log_d("notifyRecieve");
    //　Log the first 10 characters
        ByteUtil util = new ByteUtil();
        String msg = util.toDebugSub(bytes, NUM_LOG_CHARS);
        log_d(msg);
    // Send message
    // because insufficient memorySending when send  byte array
    notifyStatus(STATUS_RECIEVE);
        } //  notifyRecieve

  	/**
	 * notifyStatus
	 */ 
private void notifyStatus(int status) {
    log_d("notifyStatus");
    Intent intent = new Intent(ACTION);
    intent.putExtra( KEY_MODE, MODE_STATUS );
    intent.putExtra( KEY_STATUS, status );
    sendBroadcast(intent);
} // notifyStatus

  	/**
	 * notifyMessage
	 */ 
private void notifyMessage(java.util.Date time, OSCMessage message) {

    // TODO : send an osc message
    // as interim send formated message for debugging
    OscRecievMessageFormater formater = new OscRecievMessageFormater();
    String msg = formater.toMessageString(time, message);

    Intent intent = new Intent(ACTION);
    intent.putExtra( KEY_MODE, MODE_MESSAGE );
    intent.putExtra( KEY_MESSAGE, msg );
    sendBroadcast(intent);
} // notifyMessage



  	/**
	 * notifyException
	 */ 
private void notifyException(Exception ex) {
    log_d("notifyException");

    // send error message
    String msg = ex.getMessage();
    Intent intent = new Intent(ACTION);
    intent.putExtra( KEY_MODE, MODE_ERROR );
    intent.putExtra( KEY_ERROR, msg);
    sendBroadcast(intent);
} // notifyException

  	/**
	 * notifyError
	 */ 
private void notifyError(int error) {
    log_d("notifyError");
        int status = STATUS_NONE;
    if (error == OSCPortInCustom.ERROR_INVALID_ADDR) {
        status = STATUS_INVALID_OSC_ADDR;
    } else if (error == OSCPortInCustom.ERROR_NOT_CONVERT) {
        status = STATUS_NOT_CONVERT;
    } else if (error == OSCPortInCustom.ERROR_UNMATCH) {
        status = STATUS_UNMATCH;
    }
    notifyStatus(status);
} // notifyError

 	/**
	 * write into logcat
	 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class OscService
