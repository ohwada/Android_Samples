/**
 * Android 7.0 Nougat
 * ScopedDirectoryAccess
 * 2018-02-01 K.OHWADA 
 */
 
package jp.ohwada.android.nougatsample1;

import java.io.BufferedInputStream;
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

 	/**
	 * decodeFile
	 * @param String file_path
	 * @return Bitmap
	 */	
	public static Bitmap decodeFile( String file_path ) {
		
				Bitmap bitmap = null;
				
		try {
		bitmap = BitmapFactory.decodeFile(  file_path );
				} catch (Exception e) {
			if (D) e.printStackTrace();
		} // try
		
		if ( bitmap != null ) {
			bitmap.setDensity( DisplayMetrics.DENSITY_MEDIUM );
		} // if
		
		return bitmap;
	} // getExternalBitmap	
	
	
	/**
 	 * decodeStream
	 * @param InputStream is
	 * @return Bitmap
 	 */
    public static  Bitmap decodeStream( InputStream is ) {

		Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(is));
		if ( bitmap != null ) {
			bitmap.setDensity( DisplayMetrics.DENSITY_MEDIUM );
		} // if
    	return bitmap;
    } //getBitmap



} // class ImageFile