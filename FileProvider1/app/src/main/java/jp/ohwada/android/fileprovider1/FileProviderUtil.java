/**
 * File Provider Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.fileprovider1;

import android.content.Context;
import android.net.Uri;

import android.support.v4.content.FileProvider;

import android.util.Log;

import java.io.File;


/**
 *  class FileProviderUtil
 */
public class FileProviderUtil  {
 
    // debug
	private final static boolean D = true;
    private final static String TAG = "FileProvider";
    private final static String TAG_SUB = "FileProviderUtil";


/**
 * Constant for authority of FileProvider
 * refer : android:authorities
 */ 
    private static final String AUTHORITY_PROVIDER = ".provider";


/**
  * Context 
 */	
	private Context mContext;


/**
  * Authority for  FileProvider
 * refer : android:authorities
 */	
    String mAuthority;


/**
  * constractor 
 */	    
public FileProviderUtil( Context context  ) {
		mContext = context;
        String packageName = context.getPackageName();
        // refer : android:authorities
        mAuthority = packageName + AUTHORITY_PROVIDER;
} 


/**
 * getUri
 * get URI from File
 */ 
public Uri getUri(File file) {
    return getUriForFile(mContext, mAuthority, file);
} // getUri


/**
 * getUriForFile
 * get URI from File
 */ 
public static Uri getUriForFile(Context context, String authority, File file) {
    return FileProvider.getUriForFile(context, authority, file);
} // getUriForFile


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FileProviderUtil
