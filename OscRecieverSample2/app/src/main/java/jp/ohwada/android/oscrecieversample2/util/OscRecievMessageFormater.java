/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.oscrecieversample2.util;

import android.util.Log;


import com.illposed.osc.AddressSelector;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPort;
import com.illposed.osc.utility.OSCByteArrayToJavaConverter;
import com.illposed.osc.utility.OSCPatternAddressSelector;


import java.text.SimpleDateFormat;
import java.util.List;


 	/**
	 * OscRecievMessageFormater
	 */ 
public class OscRecievMessageFormater {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSC";
    	private final static String TAG_SUB = "OscRecievMessageFormater";

    	private final static String LF = "\n";

    private final static String  DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


 	/**
	 * constractor
	 */ 
public OscRecievMessageFormater() {
        // nothing to do
} // constractor



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

} // class OscRecievMessageFormater
