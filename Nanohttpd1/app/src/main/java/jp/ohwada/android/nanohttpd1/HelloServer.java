/**
 * NanoHttpd Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.nanohttpd1;


import android.util.Log;

import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.util.ServerRunner;

/**
 *  class HelloServer
 original : https://github.com/NanoHttpd/nanohttpd/blob/master/samples/src/main/java/org/nanohttpd/samples/http/HelloServer.java
 */
public class HelloServer extends NanoHTTPD {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "HTTPD";
    	private final static String TAG_SUB = "HelloServer";

/**
 * constractor
 */
public HelloServer(int port) {
        super(port);
} // HelloServer

/**
 * serve
 */
@Override
public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        log_d(method + " '" + uri + "' ");

        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }

        msg += "</body></html>\n";

        return Response.newFixedLengthResponse(msg);
} // serve

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class HelloServer
