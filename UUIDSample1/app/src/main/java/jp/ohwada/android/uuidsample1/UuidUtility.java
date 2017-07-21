/**
 *uuid sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.uuidsample1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * UuidUtility
 */
public class UuidUtility  {

    private final static boolean D = Constant.DEBUG;

    private final static String PREF_NAME_UUID =  "uuid";   
    private final static String PREF_DEFAULT_UUID =  "";
    
private SharedPreferences mSharedPreferences;

    		
	/**
	 * === constractor ===
	 */     
    public UuidUtility(Context context) {
	 	mSharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

    } // UuidUtility
    
    
	/**
	 * getUuid
	 * @ return String
	 */ 
	public String getUuid( ) {
	String uuid1 =getPref();
	if (( uuid1 != null)&&( uuid1.length() > 0 )) {
		return uuid1;
		}		
  	String uuid2 =generate();
  	setPref(uuid2);
  	return uuid2;
} // getUuid



	     
	/**
	 * generate
	 */	 
	 private String generate()  {
	  String uuid =  UUID.randomUUID().toString();
	  return uuid;
	  }// generate
	
	

	 	/**
	 * getPref
	 */
	private String getPref() {
	 	String value = mSharedPreferences.getString( PREF_NAME_UUID, PREF_DEFAULT_UUID );
	 		return value;
	 	} // getPref
	
	
	 	/**
	 * setPref
	 */	 	
private void setPref( String value ) {
	  	mSharedPreferences.edit().putString( PREF_NAME_UUID, value ).commit(); 
	 } // setPref
	 

	
} // class UuidUtility
