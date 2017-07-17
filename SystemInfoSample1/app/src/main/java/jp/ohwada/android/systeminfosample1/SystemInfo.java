/**
 * system info sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.systeminfosample1;

import android.content.Context;
import android.os.Build;


/**
 * SystemInfo
 */	 
public class SystemInfo {
	
	public final static String LF = "\n";
	
	private Context mContext;

public SystemInfo( Context context ) {
	mContext = context;
} // 	SystemInfo


/**
 * getSystemInfo
 * @ return String
 */	 
public String getSystemInfo() {
String msg = "";	
msg += "BOARD:" + Build.BOARD + LF ;
msg += "BOOTLOADER:" + Build.BOOTLOADER + LF;  
msg += "BRAND:" + Build.BRAND + LF;
msg += "CPU_ABI:" + Build.CPU_ABI + LF;
msg += "CPU_ABI2:" + Build.CPU_ABI2 + LF; 
msg += "DEVICE:" + Build.DEVICE + LF;
msg += "DISPLAY:" + Build.DISPLAY + LF;
msg += "FINGERPRINT:" + Build.FINGERPRINT + LF;
msg += "HARDWARE:" + Build.HARDWARE + LF;  
msg += "HOST:" + Build.HOST + LF;
msg += "ID:" + Build.ID + LF;
msg += "MANUFACTURER:" + Build.MANUFACTURER + LF;
msg += "MODEL:" + Build.MODEL + LF;
msg += "PRODUCT:" + Build.PRODUCT + LF;
msg += "RADIO:" + Build.RADIO + LF; 
msg += "TAGS:" + Build.TAGS + LF;
msg += "TIME:" + Build.TIME + LF;
msg += "TYPE:" + Build.TYPE + LF;
msg += "UNKNOWN:" + Build.UNKNOWN + LF; 
msg += "USER:" + Build.USER + LF;
msg += "VERSION.CODENAME:" + Build.VERSION.CODENAME + LF;
msg += "VERSION.INCREMENTAL:" + Build.VERSION.INCREMENTAL + LF;
msg += "VERSION.RELEASE:" + Build.VERSION.RELEASE + LF;
msg += "VERSION.SDK:" + Build.VERSION.SDK + LF;
msg += "VERSION.SDK_INT:" + Build.VERSION.SDK_INT + LF;
return msg;
} // getSystemInfo



} // class SystemInfo
