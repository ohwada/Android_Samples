/**
 * Vision Sample
 * FileUtil
 * 2019-02-01 K.OHWADA 
 */
package jp.ohwada.android.vision8;

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
 * class FileUtil
 */
public class FileUtil {

	// dubug
	private final static boolean D = true; 
	private final static  String TAG = "Vision";
	private final static String TAG_SUB = "FileUtil";
				
	
	// copy stream
	private final static int EOF = -1;
	private final static int BUFFER_SIZE = 1024 * 4;

	// junk in assets	
	private final static String[] IGNORE_FILES 
	 	= new String[] { "images", "sounds", "webkit" };
		 
	private AssetManager mAssetManager;
		
	private Context mContext;

	/**
     * constractor 
	 */	    
public FileUtil( Context context  ) {

		mContext = context;
		mAssetManager = context.getAssets();
		
} // FileUtility
	

public List<File> getAppListFile(String ext) {

	File dir = mContext.getExternalFilesDir(null);

	File[] files  = dir.listFiles();

	List<File> list = new ArrayList<File>();

	for(int i=0; i < files.length; i++ ){
		File file = files[i];
		// match ext
		if(file.isFile() && file.getName().endsWith(ext)) {
				list.add(file);
		}
	}
    return list;
} //getAppListFile


/**
 * copyAssetToAppDir
 * @return boolean
 */  
public boolean copyAssetToAppDir( String ext ) {
boolean is_overwrite = false;
return copyAssetToAppDir( ext, is_overwrite );
} // copyAssetToAppDir

  
/**
 * copyAssetToAppDir
 * @return boolean
 */  
private boolean copyAssetToAppDir( String ext, boolean is_overwrite ) {
		
	boolean is_error = false;
		
		List<String> list = getAssetList( ext );
		
		// no result
		if  ( (list == null) ||( list.size() == 0 ) ) {
			return false;
		}
	
		File dir = mContext.getExternalFilesDir(null);

	// copy files
	for ( String name: list ) {

        File file = new File(dir, name);
		if (file.exists() && !is_overwrite)	continue;											
		boolean ret = copyAssetsToStorage( name, file );
		if(!ret) {
			 	is_error = true;
		}
	
	} // for

	return ! is_error ;

} // copyAssetToAppDir
	
/**
 * get AssetsList
 */  
public List<String> getAssetList( String ext ) {
	String path = "";
	return getAssetList( path, ext );
}

/**
 * get AssetsList
 */  
private List<String> getAssetList( String path, String ext ) {

	List<String> list =	new ArrayList<String>();
		String[] files = null;
		try {
			files = mAssetManager.list( path );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		}

		// nothing if no files
		if ( files == null ) return list;
					 
		int length = files.length;
		// nothing if no files
		if ( length == 0 ) return list;


		for ( int i=0; i < length; i ++ ) {
						
			String name = files[i];
			
			// skip if ignore
		 	if ( checkIgnore( name ) ) continue;
		
			// skip if not mach ext	
			if (!name.endsWith(ext))  continue;

			list.add(name);
	
	} // for
	
	return list;

} // getAssetList


/**
 * checkIgnore
 */  
private boolean checkIgnore( String name ) {

	if ( name == null ) return false;
		
	for ( int i=0; i < IGNORE_FILES.length; i ++ ) {
		
			// check ignore
			if ( name.equals( IGNORE_FILES[ i ] ) ) return true;

	} // for
	return false;

} // checkIgnore
	
	
/**
	 * copyAssetsToStorage
 */  	
private boolean copyAssetsToStorage( String name_src, File file_dst ) {
	
		InputStream is = getAssetsInputStream( name_src );
		OutputStream os = getOutputStream( file_dst );

		return copyStream( is,  os );			

	} // copyAssetsToStorage
	
/**
 * getAssetsBitmap
 */ 	
public Bitmap getAssetsBitmap( String fileName ) {
	InputStream is = getAssetsInputStream(fileName );
if (is == null) return null;
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
} // getAssetsBitmap

/**
 * getAssetsResizedBitmap
 */ 	
public Bitmap getAssetsResizedBitmap( String fileName, int max_width, int max_height ) {
	InputStream is = getAssetsInputStream(fileName );
if (is == null) return null;
        Bitmap bitmap_orig = BitmapFactory.decodeStream(is);
 	double scale = calcResizeScale( bitmap_orig, max_width,  max_height );
   int desired_width = (int) (bitmap_orig.getWidth() * scale);
   int desired_height = (int) (bitmap_orig.getHeight() * scale);
	boolean filter = true;
	Bitmap bitmap_resized = Bitmap.createScaledBitmap(bitmap_orig,
        desired_width, desired_height, filter );

        return bitmap_resized;
} // getAssetsBitmap


/**
 * calcResizeScale
 */ 
private double calcResizeScale( Bitmap bitmap, int max_width, int max_height ) {

	double scale = 1;
	int bitmap_width = bitmap.getWidth();
	int bitmap_height  = bitmap.getHeight();


if ( bitmap_width >= bitmap_height ) {
	// horizontal image
    scale = (double) max_width / bitmap_width;
} else {
	// portrait image
    scale = (double) max_height / bitmap_height;
}
return scale;

} // calcResizeScale


/**
 * getAssetsInputStream
 */ 	
public InputStream getAssetsInputStream( String fileName ) {
		
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
private OutputStream getOutputStream( File file ) {
	boolean append = false;
	return getOutputStream( file, append );
} // getOutputStream


/**
 * getOutputStream
 */ 
private OutputStream getOutputStream( File file, boolean append ) {	
	
		OutputStream os = null;
		try {
			os = new FileOutputStream(file, append); 
					} catch (IOException e) {
			if (D) e.printStackTrace();
		} 		
		return os;

} // getOutputStream


/**
 * copyStream
 */
private boolean copyStream( InputStream is, OutputStream os ) {
			
	if(is == null) return false;
	if(os == null) return false;

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

		try {
			if(is != null) is.close();
			if(os != null) os.close();
		} catch (IOException e) {
			if (D) e.printStackTrace();
		}

		return ! is_error;

}	// copyStream


/**
 * getBitmap
 * @return Bitmap
 */	
public Bitmap getBitmap( File file ) {
	return getBitmap( file.toString() );
} // getBitmap

/**
 * getBitmap
 * @return Bitmap
 */	
private Bitmap getBitmap( String path ) {
	return getBitmap( path, DisplayMetrics.DENSITY_MEDIUM );
} // getBitmap

/**
 * getBitmap
 */	
private Bitmap getBitmap( String path, int density ) {

		Bitmap bitmap = getBitmapDecodeFile( path );		
		if ( bitmap != null ) {
			bitmap.setDensity( density );
		}		
		return bitmap;

} // getBitmap

/**
 * getBitmap
 */	
private Bitmap getBitmapDecodeFile( String path ) {

		Bitmap bitmap = null;
		try {
		bitmap = BitmapFactory.decodeFile( path );
				} catch (Exception e) {
			if (D) e.printStackTrace();
		}
		return bitmap;

} // getBitmap

/**
 * getScaledBitmap
 */	
public Bitmap getAssetScaledBitmap( String fileName, int max_width, int max_height ) {

    InputStream is = getAssetsInputStream( fileName );
    if (is == null) {
        return null;
    }

	// get image size
	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeStream(is, null, options);
    try {
        if( is != null ) is.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

	int scale = calcScale(options.outWidth, options.outHeight, max_width, max_height);

	BitmapFactory.Options options_2 = new BitmapFactory.Options();
	options_2.inJustDecodeBounds = false;
	options_2.inSampleSize =  scale;
	Bitmap bitmap = 	BitmapFactory.decodeStream(is, null, options_2);

	return bitmap;
}


/**
 * getScaledBitmap
 */	
public Bitmap getScaledBitmap( File file, int max_width, int max_height ) {

    FileInputStream fis = null;
    try {
        fis = new FileInputStream(file);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    if (fis == null) {
        return null;
    }

	// get image size
	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeStream(fis, null, options);
    try {
        if( fis != null ) fis.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

	int scale = calcScale(options.outWidth, options.outHeight, max_width, max_height);
	String path = file.toString();
	if(scale > 1) {
		return getScaledBitmap( path, scale );
	}
	return getBitmap( path );

} // getScaledBitmap

/**
 * getScaledBitmap
 */	
private Bitmap getScaledBitmap( String path, int scale ) {

	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = false;
	options.inSampleSize =  scale;
	Bitmap bitmap = BitmapFactory.decodeFile(path, options);

	return bitmap;

} // getScaledBitmap


/** 
 * calcScale
 */
private int calcScale(int image_width, int image_height, int max_width, int max_height) {

    float scaleX = image_width / max_width;
    float scaleY = image_height / max_height;
    int scale = (int) Math.floor(Float.valueOf(Math.max(scaleX, scaleY)).doubleValue());

    if (scale > 0 && (scale & (scale - 1)) == 0) {	
    // nop if Power of 2
    } else {	
		// Round to power of 2
		scale = (int) Math.pow(2.0,
			(Math.floor(Math.log(scale - 1) / Math.log(2.0))));
    }
    return scale;

} // calcScall


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class FileUtility