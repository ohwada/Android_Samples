/**
 * font sample
 * 2017-06-01 K.OHWADA 
 */
 
 package jp.ohwada.android.fontsample4;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;


/**
 * Unzip
 */
public class UnzipFile extends PrivateFile {

		private String EXT_ZIP ="zip";
	/**
     * === constractor ===
	 * @param Context context  
	 */	    
	 public UnzipFile( Context context  ) {
 super( context  );
		 TAG_SUB = "Unzip";

	} // Unzip

	    
	/**
	 * nzipAssetsFiles
	 * @return boolean
	 */  
	public boolean unzipAssetsFiles() {
		
	boolean is_error = false;
		
		List<String> list = getAssetList( ASSET_PATH, EXT_ZIP );
		
		// no result
		if  ( (list == null) ||( list.size() == 0 ) ) {
			return false;
		}
	
		// unzip files
		for ( String name: list ) {
			
			log_d( "unzip " + name );
												
			  boolean ret = unzipAssetsToStorage( name );
			 if( !ret ) {
			 	is_error = true;
			 }
	
		} // for

		return ! is_error ;

	} // unzipAssetsFiles
	
	
	/**
	 * unzipAssetsToStorage
	 	 	  * @parm String zipFileName
	  	 	 	 * @return boolean
	 */  	
	public boolean unzipAssetsToStorage( String zipFileName ) {
	
				boolean is_error = true;
			ZipInputStream zis = getAssetsZipInputStream( zipFileName );
		if ( zis == null ) return false;	
	
	try {
	Unzip.unzip( zis, mAppDir, false );
			} catch (IOException e) {
			is_error = true;
			if (D) e.printStackTrace();
		} // try


		try {
			zis.close();
		} catch (IOException e) {
			// is_error = true;
			if (D) e.printStackTrace();
		} // try

// closezipInputStream( zis );

return ! is_error;
	} // unzipAssetsToStorage
	
 
 
 		/**
	 * getAssetsZipInputStream
	 */ 	
	private ZipInputStream getAssetsZipInputStream( String fileName ) {
		InputStream is = getAssetsInputStream( fileName );
		if ( is == null ) return null;
		ZipInputStream zis = null;

	try {
			zis = new ZipInputStream(is);
		} catch (Exception e) {
			if (D) e.printStackTrace();
		} 
		
	return zis;
} //getAssetsZipInputStream
  

} // class UnzipFile