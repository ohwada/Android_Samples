/**
 * device id sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.deviceidsample1;

import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;


/**
 * DeviceId
 */	 
public class DeviceId {
	
	public final static String LF = "\n";
	
	private Context mContext;

public DeviceId( Context context ) {
	mContext = context;
} // 	DeviceId


/**
 * getDeviceId
 * @ return String
 */	 
public String getDeviceId() {
String msg = "";	
msg += "IMEI: " + getImei() + LF;
msg += "IMSI: " + getMsi() + LF;
msg += "ICCID: " + getIccid() + LF;
msg += "Mac Address: " + getMacAddress() + LF;
msg += "Android ID: " + getAndroidId() + LF;
return msg;
} // getDeviceId

/**
 * getImei
 */	 
private String getImei() {
TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
String imei = tm.getDeviceId();
return imei;
} // getImei


/**
 * getMsi
 */	
private String getMsi() {
TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
String str = tm.getSubscriberId();
return str;
} // getMsi


/**
 * getIccid
 */	
private String getIccid() {
	TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
 String str = tm.getSimSerialNumber();
return str;
} // getIccid



/**
 * getMacAddress
 */	
	 private String getMacAddress( ) {
	 	WifiManager wm = (WifiManager) mContext.getSystemService( Context.WIFI_SERVICE );
		WifiInfo wi = wm.getConnectionInfo();
		String str = wi.getMacAddress();
		return str;
		} // getMacAddress
	
		
/**
 * getAndroidId
 */			
private String getAndroidId() {
		ContentResolver cr = mContext.getContentResolver();
	  	String str = android.provider.Settings.Secure.getString( 
	  		cr, 
	  		android.provider.Settings.Secure.ANDROID_ID );
		return str;
	 } // getAndroidId


} // DeviceId
