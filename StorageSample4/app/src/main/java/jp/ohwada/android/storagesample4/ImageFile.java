/**
 * write Storage in Android4.4 earlier style
 * 2017-06-01 K.OHWADA 
 */
 
 package jp.ohwada.android.storagesample4;

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
public class ImageFile  {

// dubug
	private  final static boolean D = Constant.DEBUG; 
		private String TAG_SUB = "ImageFile";

	 ExternalFile  mExternalFile;
	
	/**
     * === constractor ===
	 * @param Context context  
	 */	    
	 public ImageFile( Context context  ) {
 mExternalFile = new ExternalFile(context);
	} // ImageFile
	
		   
	
	/**
	 * mkdirsExternal
	  * @parm String dir
	  	 	 	 * @return int
	 */  
	public int mkdirsExternal( String dir ) {
			return mExternalFile.mkdirs( dir );	

	} // mkdirsPrivate
	

	/**
	 * copyAssetsFiles
	 * @param String ext
	 * @return boolean
	 */  
	public boolean copyAssetsFilesToExternal( String ext ) {
 	return mExternalFile.copyAssetsFiles( ext );
} // copyAssetsFilesToExternal


 	/**
	 * getExternalBitmap
	 * @param String name
	  * @param nt mode
	 * @return Bitmap
	 */	
	public Bitmap getExternalBitmap( String fileName ) {
		
				Bitmap bitmap = null;
				
				String path = mExternalFile.getPath( fileName, ExternalFile.MODE_APP );
				
		try {
		bitmap = BitmapFactory.decodeFile( path );
				} catch (Exception e) {
			if (D) e.printStackTrace();
		} // try
		
		if ( bitmap != null ) {
			bitmap.setDensity( DisplayMetrics.DENSITY_MEDIUM );
		} // if
		
		return bitmap;
	} // getExternalBitmap	
	
	





	



} // class ImageFile