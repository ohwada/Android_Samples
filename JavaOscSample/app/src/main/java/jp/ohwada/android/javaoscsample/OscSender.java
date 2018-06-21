/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.javaoscsample;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortOut;


 /**
 *   class OscSender
 *  reference : https://github.com/hoijui/JavaOSC
 */
public class OscSender {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSC";
    	private final static String TAG_SUB = "OscSender";

        // OSC message
    	private final static String ADDRESS_ON = "/s_new";

    	private final static String ADDRESS_OFF = "/n_free";

    	private final static String LABEL_NODE ="javaosc-example";

    	private final static String LABEL_FREQ = "freq";


    private AsyncSender mAsyncSender;

     private OSCPortOut mOSCPortOut;

  // callback 
    private OnEventListener mListener;

/*
  * callback interface
 */    
    public interface OnEventListener {
        public void onFinish();
        public void onError(String msg);
    } // interface

/**
 *  constractor
 */
    public OscSender() {
        // nothing to do
    } // OscSender


/**
 *  setOnEventListener
 */
    public void setOnEventListener(OnEventListener listener ) {
        mListener = listener ;
    } // setOnEventListener


/**
 *  openPortOut
 */
public OSCPortOut openPortOut(String addr) {
    log_d("openPortOut: " + addr);
        OSCPortOut port = null;
		try {
            InetAddress inet_sddr = InetAddress.getByName(addr);
			 port =
				new OSCPortOut(inet_sddr);
		} catch (Exception e) {
            e.printStackTrace();
		}
        if (port !=null ) {
            setPortOut(port);
        }
        return port;
} // openPortOut

/**
 *   closePortOut
 */
    public void closePortOut() {
        if ( mOSCPortOut != null) {
            mOSCPortOut.close();
        }
    } //  closePortOut

/**
 *  setPortOut
 */
    public void setPortOut(OSCPortOut port) {
        mOSCPortOut = port;
    } // setPortOut

/**
 *   hasPortOut
 */
    public boolean hasPortOut() {
        if ( mOSCPortOut != null) {
            return true;
        }
        return false;
    } //  hasPortOut

/**
 *  sendOn
 */
public void sendOn(int node, float freq) {
        log_d("sendOn");

		List<Object> args = new ArrayList<Object>(6);
		args.add(LABEL_NODE);
		args.add(new Integer(node));
		args.add(new Integer(1));
		args.add(new Integer(0));
		args.add(LABEL_FREQ);
		args.add(new Float(freq));

		OSCMessage msg = new OSCMessage(ADDRESS_ON, args);
        start(msg);

} // sendOn

/**
	 * sendOff
	 */
public void sendOff(int node) {
        log_d("sendOff");

		List<Object> args = new ArrayList<Object>(1);
		args.add(new Integer(node));
		OSCMessage msg = new OSCMessage(ADDRESS_OFF, args);

        start(msg);

} // sendOff

/**
	 * start
	 */
public void start(OSCMessage msg) {
        log_d("start");
        logMessage(msg);
        mAsyncSender = new AsyncSender(msg);
		mAsyncSender.execute();
} // start


/**
	 * logMessage
	 */
private void logMessage(OSCMessage msg) {
		byte[] bytes = msg.getByteArray();
        ByteUtil util = new ByteUtil();
        String text = util.toDebug(bytes);
        // log_d(text);
} // logMessage


/**
	 * cancel
	 */
public void cancel() {
	if (mAsyncSender != null) {
		mAsyncSender.cancel(true);
    }
} // cancel


/**
	 * notifyfinish
	 */
private void notifyFinish() {
	if (mListener != null) {
		mListener.onFinish();
    }
} // notifyfinish


/**
	 * notifyError
	 */
private void notifyError(String msg) {
	if (mListener != null) {
		mListener.onError(msg);
    }
} // otifyError

/**
 * toHex
 */ 
private String toHex(byte b) {
    return String.format("%02x", b);
} // toHex

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


/**
 *   === class AsyncSender ===
 */
public class AsyncSender extends AsyncTask<Void, Void, Boolean> {
 
		private OSCMessage mOSCMessage;

		private Exception mException;

    public AsyncSender(OSCMessage msg ) {
        super();
        log_d("AsyncSender");
        mOSCMessage = msg;
    } // AsyncSender
 
    @Override
    protected Boolean doInBackground( Void... param ) {
        log_d("doInBackground");
		try {
			if ( mOSCPortOut != null ) {
			    mOSCPortOut.send(mOSCMessage);
            }
		} catch (Exception e) {
            mException = e;
            e.printStackTrace();
            return false;
		}
       return true;
    }

    @Override
    protected void onPreExecute(){
		// nothing to do
    }


    @Override
    protected void onPostExecute( Boolean result ) {
        log_d("onPostExecute");
        if( mException != null ) {
            String msg = mException.getMessage();
            notifyError(msg);
        } else {
            notifyFinish();
        }
    }

} // class AsyncSender


} // class OscSender
