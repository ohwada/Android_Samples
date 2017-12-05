 	/**
	 * CallerLog
	 * 2017-11-01 K.OHWADA    
	 */

package jp.ohwada.android.callersample;

import android.util.Log;

 	/**
	 * class CallerLog
	 */
public class CallerLog {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "caller";
    	private final static String TAG_SUB = "CallerLog";

    	private final static String LF = "\n";

 	/**
	 * search
	 */ 
public static void search(){
  	StackTraceElement stack[] = (new Throwable()).getStackTrace();
    String msg = "";
    for ( int i=0; i < stack.length; i++ ) {
        StackTraceElement frame = stack[i];
        String className = frame.getClassName();
        String methodName =  frame.getMethodName(); 
        msg = className + "#" +  methodName  +LF; 
        log_d(msg);    
} // for
 } // search

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class end
