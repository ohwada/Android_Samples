/**
 *  WebView sample
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.webview2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * FileUtil
 */
public class FileUtil {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "WebView";
    	private final static String TAG_SUB = "FileUtil";

	// copy stream
	private final static int BUF_SIZE = 1024 * 4;
	private final static int EOF = -1;

    	private final static String LF = "\n";

  private Context mContext;

	 private AssetManager mAssetManager;

/**
 * constractor
 */
public FileUtil(Context context) {
    mContext = context;
	mAssetManager = context.getAssets();
} // FileUti

	/**
	 * copyAssetToSTorage
	 */ 
public boolean copyAssetToSTorage( String dirName, String fileName) {

        File root_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
    File sub_dir = new File(root_dir, dirName);
        if ( !sub_dir.exists() ) {
            sub_dir.mkdir();
        }
    File file = new File(sub_dir, fileName );
    // append false
    FileOutputStream fos  = getFileOutputStream( file, false ) ;
    InputStream is = getAssetInputStream( fileName );
    return copyStream(  is,  fos );

} // copyAssetToSTorage

/**
 * getAssetInputStream
 */
private InputStream getAssetInputStream( String fileName ) {
		
		InputStream is = null;
		try {
			is = mAssetManager.open( fileName );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 		
	return is;

} //getAssetInputStream

/**
 * getFileOutputStream
 */
private FileOutputStream getFileOutputStream(File file, boolean append ) {

    FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(file, append);
          } catch (Exception e) {
            e.printStackTrace();
            }
return fos;

} // getFileOutputStream

/**
 *  readAssetTextFile
 */ 
public  String readAssetTextFile( String fileName ) {
    InputStream is = getAssetInputStream( fileName );
    return readTextFile( is );
} // readAssetTextFile

/**
 *  readTextFile
 */ 
public  String readTextFile( InputStream is ) {

    if( is == null ) return null;

    StringBuilder builder = new StringBuilder();
    BufferedReader br = null;
    try {

        br = new BufferedReader(new InputStreamReader(is));
        String str;
        while ((str = br.readLine()) != null) {
         builder.append(str);
         builder.append(LF);
        } // while

    } catch (Exception e){
			if (D) e.printStackTrace();
    } finally {
        try {
            if (br != null) br.close();
        } catch (Exception e){
			if (D) e.printStackTrace();
        }
    }

    try {
            if (is != null) is.close();
    } catch (Exception e){
			if (D) e.printStackTrace();
    }

    return builder.toString();

} // readTextFile


 /**
  * writeTextFile
 */ 
public boolean writeTextFile( File file, String text, boolean append ) {

        if( file == null ) return false;
        boolean is_error = false;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream( file, append );
            os.write( text.getBytes() );
        } catch ( Exception e ) {
            is_error = true;
            if (D) e.printStackTrace();
        } finally {
            try {
                if ( os != null )  os.close();
            } catch ( IOException e ) {
                if (D) e.printStackTrace();
            }
        }
        return !is_error;

} // writeTextFile


/**
 * copyStream
 */
private boolean copyStream( InputStream is, OutputStream os ) {

    if (is == null) return false;
    if (os == null) return false;	
			
	byte[] buffer = new byte[BUF_SIZE];
	int n = 0;	
	boolean is_error = false;			
    try {
			// copy input to output		
			while (EOF != (n = is.read(buffer))) {
				os.write(buffer, 0, n);
			} // while

    } catch (IOException e) {
			is_error = true;
			if (D) e.printStackTrace();
    }

	try {
        if (is != null) is.close();
        if (os != null) os.close();
	} catch (IOException e) {
			if (D) e.printStackTrace();
	}

	return ! is_error;

}	// copyStream


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} //  public class FileUti
