 /**
 *  Video Player Sample
 * 2019-02-01 K.OHWADA
 */
 
package jp.ohwada.android.videoplayer1;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * File Utility
 */
public class FileUtil {

	// dubug
	private final static boolean D = true; 
    private final static String TAG = "Video";
	private final static String TAG_SUB = "FileUtil";
	
	
	// copy stream
	private final static int EOF = -1;
	private final static int BUFFER_SIZE = 1024 * 4;

	private Context mContext;
	private AssetManager mAssetManager;


		
/**
  * constractor 
 */	    
public FileUtil( Context context  ) {
		mContext = context;
		mAssetManager = context.getAssets();
} 
	

	    
/**
 *  copyFilesAssetToExternalFilesDir
 */  
public boolean copyFilesAssetToExternalFilesDir() {
		
	boolean is_error = false;
		
		String[] files = getFilesInAsset();
		
        File dir = mContext.getExternalFilesDir(null);

	int length = files.length;
		// no result
		if  ( length == 0 ) {
			return false;
		}


	// copy files
		for ( int i=0; i < length; i ++ ) {	
					
		String name = files[i];				
		log_d( "copy " + name );
		InputStream is = getAssetsInputStream( name );
		if(is == null) continue;
		OutputStream os = getOutputStream( dir, name );
		boolean ret = copyStream( is,  os );
		if(!ret) {
			is_error = true;
		}
	
	} // for

	return ! is_error ;

} // copyFilesAssetToExternalFilesDir
	

/**
 * getFilesInAsset
 */  
public String[] getFilesInAsset() {

		List<String> list =	new ArrayList<String>();
		String[] files = new String[0];
		try {
			files = mAssetManager.list("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
} // getFilesInAsset


/**
 * getFileNameListInExternalFilesDir
 */  
public List<String> getFileNameListInExternalFilesDir() {
	 File dir = mContext.getExternalFilesDir(null);

		List<String> list =	new ArrayList<String>();

	File[]	files = dir.listFiles();


		// nothing if no files
		if ( files == null ) return list;
					 
		int length = files.length;
		// nothing if no files
		if ( length == 0 ) return list;

		for ( int i=0; i < length; i ++ ) {	
			File file = files[i];						
			String name = file.getName();	
			//log_d(name);
			list.add(name);
	} // for

	return list;
} // getFileNameListInExternalFilesDir


/**
 * getPathInExternalFilesDir
 */ 
public String getPathInExternalFilesDir(String fileName) {
	 File dir = mContext.getExternalFilesDir(null);
	File file = new File(dir, fileName);
	return file.toString();
} // getFilePathInExternalFilesDir

	
/**
 * getAssetsInputStream
 */ 	
	private InputStream getAssetsInputStream( String fileName ) {
		
		InputStream is = null;
		try {
			is = mAssetManager.open(fileName);
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
	return is;
} //getAssetsInputStream


/**
 * getOutputStream
 */ 
	private OutputStream getOutputStream( File dir, String name ) {
	
		OutputStream os = null;

		try {
			File file = new File(dir, name);
			os = new FileOutputStream(file); 
					} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
		return os;

} // getOutputStream


/**
 * copyStream
 */
private boolean copyStream( InputStream is, OutputStream os ) {
			
		byte[] buffer = new byte[BUFFER_SIZE];
		int n = 0;	
			boolean is_error = false;
			
		try {
			// copy input to output		
			while (EOF != (n = is.read(buffer))) {
				os.write(buffer, 0, n);
			}
		} catch (IOException e) {
			is_error = true;
			e.printStackTrace();
		}

				try {
					is.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 

	return ! is_error;
}	// copyStream



/**
 * write into logcat
 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class FileUtil