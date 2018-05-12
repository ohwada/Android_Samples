
package jp.ohwada.android.mailshareprovidersample;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
 * class AssetsFile
 */
public class AssetFile {

	// dubug
	protected  final static boolean D = true; 
	protected String TAG = "Share";
	protected String TAG_SUB = "AssetFile";
			
	// all files in assets		 
	protected final static String ASSET_PATH = "";
	
	// copy stream
	protected final static int EOF = -1;
	protected final static int BUFFER_SIZE = 1024 * 4;

	// junk in assets	
	protected final static String[] ASSET_IGNORE_DIRS 
	 	= new String[] { "images", "sounds", "webkit" };
		 
// indexof
 private final static int INDEX_NOT_FOUND = -1;
 private final static int INDEX_OFFSET = 1;

	protected AssetManager mAssetManager;

		
	/**
     * === constractor ===
	 * @ param Context context
	 */	    
	 public AssetFile( Context context  ) {
		mAssetManager = context.getAssets();
	} // AssetFile
	


	/**
	 * readTextFile
     * @ param String fileName
     * @ param boolean exists
     * @return String
	 */  
public String readTextFile( String fileName, boolean exists ) {
	if (exists) {
		boolean ret =existsFile( fileName );
		if (!ret) return null;
	}
	return readTextFile( fileName );
} // readTextFile


	/**
	 * readTextFile
     * @ param String fileName
     * @return String
	 */  
public String readTextFile( String fileName ) {
	InputStream is = getAssetInputStream(  fileName );
 	if (is == null ) return null;
 	String str = readFromStream( is);
	try {
				if (is != null ) is.close();
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
	return str;
} // readTextFile


	/**
	 * readImageFile
     * @ param String fileName
     * @ param boolean exists
     * @return Bitmap
	 */  
public Bitmap readImageFile( String fileName, boolean exists ) {
	if (exists) {
		boolean ret =existsFile( fileName );
		if (!ret) return null;
	}
	return readImageFile( fileName );
} // readImageFile


	/**
	 * readImageFile
     * @ param String fileName
     * @return Bitmap
	 */  
public Bitmap readImageFile( String fileName ) {
	InputStream is = getAssetInputStream(  fileName );
 	if (is == null ) return null;
		Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(is));
		if ( bitmap != null ) {
			bitmap.setDensity(DisplayMetrics.DENSITY_MEDIUM);
		}
	try {
				if (is != null ) is.close();
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		return bitmap;
} // readImageFile


	/**
	 * existsFile
     * @ param String fileName
     * @return boolean
	 */  
public boolean existsFile( String fileName ) {
	List<String> list = getFileList();
	if (list == null) return false;
	boolean ret = list.contains(fileName);
return ret;
} // existsFile


	/**
	 * getValidFileList
     * @ param String ext
     * @return List<String>
	 */  
	public List<String> getValidFileList( String ext ) {
		
	List<String> list =	new ArrayList<String>();

	List<String> list_raw = getFileList();


		// nothing if no files
		if ( list_raw == null ) return list;
		
		int size = list_raw.size();

		// nothing if no files
		if ( size == 0 ) return list;	

		// all files
		for ( String name: list_raw ) {
						
			log_d( "getValidFileList " + name );
			
			// skip if ignore
		 if ( checkAssetsIgnoreDir( name ) ) continue;
			
			// skip if munmach ext
			if ( !matchExt( name, ext ) ) continue;

	list.add( name );
	
	} // for
	
	return list;
} // getValidFileList


	/**
	 * getFileList
     * @return  List<String>
	 */  
public List<String> getFileList() {
	String[] files = getFiles( ASSET_PATH );
	if (files == null) return null;
	List<String> list = Arrays.asList(files);
	return list;
} // getFileList


	/**
	 * getFiles
     * @param String path
     * @return String[]
	 */  
	public String[] getFiles( String path ) {
		
	String[] files = null;
	
		try {
			files = mAssetManager.list( path );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		}

		return files;

} // getFiles


	/**
	 * checkAssetsIgnoreDir
	 */  
	private boolean checkAssetsIgnoreDir( String name ) {

		if ( name == null ) return false;
		
	for ( int i=0; i < ASSET_IGNORE_DIRS.length; i ++ ) {
		
			// check ignore
			if ( name.equals( ASSET_IGNORE_DIRS[ i ] ) ) return true;

		} // for

		return false;
} // checkAssetsIgnoreDir

	
	

	/**
	 * getAssetInputStream
     * @param String fileName
     * @return InputStream
	 */ 	
	public InputStream getAssetInputStream( String fileName ) {
		
		InputStream is = null;
		try {
			is = mAssetManager.open( fileName );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
	return is;
} //getAssetInputStream


	/**
	 * copyStream
     * @ param InputStream is
     * @ param OutputStream os
     * @return boolean
	 */
public boolean copyStream( InputStream is, OutputStream os ) {
			
		byte[] buffer = new byte[BUFFER_SIZE];
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

	return ! is_error;
}	// copyStream


	/**
	 * readFromStream
     * @param InputStream is
     * @return String
	 */
public String readFromStream( InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();   
        try{  
            try {  
                br = new BufferedReader(new InputStreamReader(is));
       
                String str;     
                while((str = br.readLine()) != null){     
                    sb.append(str +"\n");    
                }      
            } finally {  
                if (br != null) br.close();  
            }  
        } catch (IOException e) {  
  			e.printStackTrace();
        }  
	return sb.toString();
} // readFromStream

	/**
	 * matchExt
	 * @param String fileName
	 	 * @param String ext
	 	 * @return boolean
	 */
public static boolean matchExt( String fileName, String ext ) {
	
	String name_ext = parseExt( fileName );

if ( ( ext != null )&&( ext.equals( name_ext )) ) {
		return true;
}		
		return false;		
} // matchExt


	/**
	 * parseExt
	 * @param String fileName
	 	 * @return String
	 */
public static String parseExt( String fileName ) {
	String ext = "";


        if (fileName == null) return ext;

	// find point index
    int point = fileName.lastIndexOf(".");
    if ( point != INDEX_NOT_FOUND ) {
        ext = fileName.substring( point + INDEX_OFFSET );
    } // if point

    return ext;
} // parseExt


 	/**
	 * write into logcat
	 */ 
	protected void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} //class AssetsFile