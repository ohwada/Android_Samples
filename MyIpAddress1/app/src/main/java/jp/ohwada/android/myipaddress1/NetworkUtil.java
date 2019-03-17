/*
 * My IP Address Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.myipaddress1;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
	 * class NetworkUtil
    * refeence : https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code
	 */ 
public class NetworkUtil {

        // debug
	private final static boolean D = true;
	public final static String TAG = "MyIPAddr";
	private final static String TAG_SUB = "NetworkUtil";

    private static final String IP_ADDR_DEFAULT = "0.0.0.0";

 	/**
	 * getIPAddress
	 */ 
public static String getMyIPAddress() {
    String addr = null;
    try {
            addr = getIpAddress();
	} catch (Exception e) {
        if(D) e.printStackTrace();
	}
    log_d( "MyIPAddress: " + addr );
    return addr;
} //  getIPAddress

 	/**
	 * getIpAddress
	 */ 
public static String getIpAddress() throws Exception{

    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    
  while( interfaces.hasMoreElements() ) {
        NetworkInterface intf = interfaces.nextElement();
        Enumeration<InetAddress> addresses = intf.getInetAddresses();


    while( addresses.hasMoreElements() ) {
        InetAddress addr = addresses.nextElement();

        if (!addr.isLoopbackAddress() ) {
            String h_addr = addr.getHostAddress();
            log_d("HostAddress: " + h_addr);
            if ( isIPv4(h_addr) &&  ! IP_ADDR_DEFAULT.equals(h_addr) ) {
                return h_addr;
            } // if

        } // if
      } // while
    } // while
    return null;
} // etIPAddress

 	/**
	 * isIPv4
	 */ 
public static boolean isIPv4(String addr) {
    // v4 192.168.1.1
    // v6 0:0:0:0:0:0:0:1
           if( addr.indexOf(':') < 0) {
                return true;
        }
        return false;
} // isIPv4

 	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class NetworkUtil