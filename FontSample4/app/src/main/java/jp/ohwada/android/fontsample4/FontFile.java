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
import android.graphics.Typeface;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;


/**
 * FontFile
 */
public class FontFile  {

// dubug
	private  final static boolean D = Constant.DEBUG; 
		private String TAG_SUB = "FontFile";
		
		AssetManager 	 	mAssetManager;
	 UnzipFile  mUnzipFile;
	
	/**
     * === constractor ===
	 * @param Context context  
	 */	    
	 public FontFile( Context context  ) {
	 	mAssetManager = context.getAssets();
 mUnzipFile = new UnzipFile(context);
	} // FontFile
	
		   
	
	/**
	 * mkdirsPrivate
	  * @parm String dir
	  	 	 	 * @return int
	 */  
	public int mkdirsPrivate( String dir ) {
			return mUnzipFile.mkdirs( dir );	

	} // mkdirsPrivate
	
	
	/**
	 * unzipPrivate
	 	  * @parm String zipFileName
	  	 	 	 * @return boolean
	 */  
	public boolean unzipPrivate( String zipFileName ) {
	 return mUnzipFile.unzipAssetsToStorage( zipFileName );

	} // unzipPrivate
			

 
 
 		
	/**
	 * getPrivateTypeface
	 	 *@param String fontName
	 	 	 *@return Typeface
	 */  
	public Typeface getPrivateTypeface( String fontName ) {
			
File file = mUnzipFile.getFile( fontName, UnzipFile.MODE_APP );

        Typeface typeface = null;
	try {
    	typeface = Typeface.createFromFile( file );
				} catch (Exception e) {
			if (D) e.printStackTrace();
} /// try

  	return typeface;
	} // getPrivateTypeface






		/**
	 * sgetAssetsTypeface
	 *@param String fontName
	 *@return Typeface 
	 */ 
    public Typeface getAssetsTypeface( String fontName ) {
        Typeface typeface = null;
        try {
        typeface = Typeface.createFromAsset( mAssetManager, fontName );
					} catch (Exception e) {
			if (D) e.printStackTrace();
		
    } // try

return typeface;
} // setAssetsTypeface
	



} // class FontFile