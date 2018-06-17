/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.oscrecieversample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import java.text.SimpleDateFormat;
import java.util.List;

public class OscReciever {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSC";
    	private final static String TAG_SUB = "OscReciever";

    	private final static String LF = "\n";

    private final static String  DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

        private OSCPortIn mOSCPortIn;

  // callback 
    private OnEventListener mListener;

/*
  * callback interface
 */    
    public interface OnEventListener {
		public void onMessage(java.util.Date time, OSCMessage message);
    } // interface

 	/**
	 * constractor
	 */ 
public OscReciever() {
    // nothing to do
} // constractor

/**
 *  setOnEventListener
 */
    public void setOnEventListener(OnEventListener listener ) {
        mListener = listener ;
    } // setOnEventListener

 	/**
	 * setup
	 */ 
public boolean setup(String addr) {
    OSCPortIn port = opentInputPort();
    if ( port == null ) {
        return false;
    }

    setInputPort(port);
    OSCListener listener = createListener(); 
    addListener(addr, listener);
    startListening();
        return true;
} // setup


 	/**
	 * opentInputPort
	 */ 
public OSCPortIn opentInputPort() {

	   OSCPortIn port = null;
    try {
	   port = new OSCPortIn(OSCPort.defaultSCOSCPort());
		} catch (Exception e) {
            e.printStackTrace();
		}

    return port;
} // opentInputPort

 	/**
	 * setInputPort
	 */ 
public void setInputPort(OSCPortIn port) {
	   mOSCPortIn = port;
} // setInputPort


 	/**
	 * createListener
	 */ 
public OSCListener createListener() {
	OSCListener listener = new OSCListener() {
		public void acceptMessage(java.util.Date time, OSCMessage message) {
			log_d("acceptMessage");
            if ( mListener != null ) {
                mListener.onMessage(time, message);
            }
		}
	};

    return listener;
} // createListener

 	/**
	 * addListener
	 */ 
public void addListener(String addr, OSCListener listener) {
	if ( mOSCPortIn != null ) {
	    mOSCPortIn.addListener(addr, listener);
    }
} // addListener


 	/**
	 * startListening
	 */ 
public void startListening() {
	if ( mOSCPortIn != null ) {
	    mOSCPortIn.startListening();
    }
} // startListening


 	/**
	 * stopListening
	 */ 
public void stopListening() {
	if ( mOSCPortIn != null ) {
	    mOSCPortIn.stopListening();
    }
} // startListening

 	/**
	 * isListening
	 */ 
public boolean isListening() {
    boolean ret = false;
	if ( mOSCPortIn != null ) {
	    ret = mOSCPortIn.isListening();
    }
    return ret;
} // isListening


 	/**
	 * getPort
	 */ 
public int getDefaultPort() {
return OSCPort.defaultSCOSCPort();
} // getPort


 	/**
	 * closeInputPort
	 */ 
public void closeInputPort() {
	if (mOSCPortIn != null) {
		mOSCPortIn.close();
    }
} // closeInputPort


 	/**
	 * toMessageString
	 */ 
public String toMessageString(java.util.Date time, OSCMessage message) {

    String msg = "time= " + toDateString(time, DATE_FORMAT) + LF;
    msg += "message= " + toMessageString(message) +LF;
    return msg;

} // toMessageString



 	/**
	 * toDateString
	 */ 
public String toDateString(java.util.Date time, String format) {
    if (time == null) {
        return null;
    }

    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String msg = null;
    if (sdf != null) {
        msg = sdf.format(time);
    }
    return msg;
} //  // toDateString


 	/**
	 * toMessageString
	 */ 
public String  toMessageString(OSCMessage message) {
    if (message == null) {
        return null;
    }

    String msg = "";
    msg += "addr= " + message.getAddress() + LF;
    List<Object> args = message.getArguments();
    msg += "Arguments= " +  toArgumentsString(args) + LF;
    return msg;
} // toMessageString


 	/**
	 * toArgumentsString
	 */ 
public String  toArgumentsString(   List<Object> args) {

    if (( args == null)||(args.size() == 0)) {
        return null;
    }

    String msg = "";
    Object obj = null;
    String str = null;

    for(int i=0; i<args.size(); i++) {
		try {
            obj = args.get(i);
            if (obj != null ) {
                str = obj.toString();
            }
		} catch (Exception e) {
            e.printStackTrace();
		}
        msg += "obj( " + i + " )= " + str +LF;
    } // for

    return msg;
} // toArgumentsString


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class OscReciever
