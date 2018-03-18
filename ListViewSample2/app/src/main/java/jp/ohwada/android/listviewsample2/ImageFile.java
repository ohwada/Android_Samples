/**
 * ListView sample
 * 2018-03-01 K.OHWADA 
 */
 
package jp.ohwada.android.listviewsample2;

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
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * class ImageFile
 */
public class ImageFile {

	// dubug
	private  final static boolean D = Constant.DEBUG; 
	private String TAG_SUB = "ImageFile";
					 
	private AssetManager mAssetManager;

		
	/**
     * === constractor ===
	 * @param Context context  
	 */	    
	 public ImageFile( Context context  ) {
		mAssetManager = context.getAssets();
	} // ImageFile
	

	/**
	 * getAssetBitmap
     * @ param String codename
     * @return Bitmap
	 */
public Bitmap getAssetBitmap( String fileName ) {
	log_d("getBitmap");
InputStream is =  getAssetInputStream( fileName );
 if ( is == null ) return null;
 Bitmap bitmap = BitmapFactory.decodeStream(is);
if ( bitmap == null )  return null;
	bitmap.setDensity( DisplayMetrics.DENSITY_MEDIUM );
	return bitmap;
	} // getAssetBitmap



	/**
	 * getAssetInputStream
     * @param String fileName
     * @return InputStream
	 */ 	
	public InputStream getAssetInputStream( String fileName ) {
		log_d("getAssetInputStream");	
		InputStream is = null;
		try {
			is = mAssetManager.open( fileName );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
	return is;
} //getAssetInputStream


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


} //class ImageFile