/**
 * Cloud Vision Sample
 * FileUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision4.util;


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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;


/**
 * File Utility
 */
public class FileUtil {

	// dubug
	private final static boolean D = true; 
    private final static String TAG = "CloudVision";
	private final static String TAG_SUB = "FileUtil";
	

	/**
 * Dir Name
 */ 
   public static final String DIR_NAME =  "CloudVision";


/**
  * Constant for copyStream
 */	
	private final static int EOF = -1;
	private final static int BUFFER_SIZE = 1024 * 4;

/**
  * Context 
 */	
	private Context mContext;

/**
  * AssetManager 
 */	
	private AssetManager mAssetManager;


		
/**
  * constractor 
 */	    
public FileUtil( Context context  ) {
		mContext = context;
		mAssetManager = context.getAssets();
} 
	

/**
 * make Directory in ExternalStoragePublicPictures
 */ 
public static void mkDirInExternalStoragePublicPictures() {
        File dir = getDirInExternalStoragePublicPictures();
        log_d("mkDir:" +dir.toString());
        if(!dir.exists()) {
            // make Directory if not exist
           dir.mkdir();
        }
} // mkDirInExternalStoragePublicPictures

	    
/**
 *  copyFilesAssetToExternalStoragePublicPictures
 */  
public boolean copyFilesAssetToExternalStoragePublicPictures( String ext ) {
		
	boolean is_error = false;
		
		List<String> list = getFileListInAsset(ext);
		
        File dir = getDirInExternalStoragePublicPictures();

		// no result
		if  ( (list == null) ||( list.size() == 0 ) ) {
			return false;
		}
	
	// copy files
	for ( String name: list ) {
			
		log_d( "copy " + name );
		InputStream is = getAssetsInputStream( name );
		File file = new File(dir, name);
		OutputStream os = getOutputStream( file );
		boolean ret = copyStream( is,  os );
		if(ret) {
            scanFile(file);
		} else {
			is_error = true;
		}
	
	} // for

	return ! is_error ;

} // copyFilesAssetToExternalStoragePublicPictures
	


/**
 * get Directory in ExternalStoragePublicPictures
 */ 
private static File getDirInExternalStoragePublicPictures() {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File dir = new File(root, DIR_NAME);
        return dir;
} // etDirInExternalStoragePublicPictures


/**
 * getFileListInAsset
 */  
public List<String> getFileListInAsset( String ext ) {

		List<String> list =	new ArrayList<String>();
		String[] files = null;
		try {
			files = mAssetManager.list("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// nothing if no files
		if ( files == null ) return list;
					 
		int length = files.length;
		// nothing if no files
		if ( length == 0 ) return list;

		for ( int i=0; i < length; i ++ ) {						
			String name = files[i];	
//log_d(name);
			// skip if not mach ext	
			if (!name.endsWith(ext))  continue;
			list.add(name);
	} // for
	
	return list;

}

	
/**
 * getAssetsInputStream
 */ 	
public InputStream getAssetsInputStream( String fileName ) {
		
		InputStream is = null;
		try {
			is = mAssetManager.open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	return is;
} //getAssetsInputStream


/**
 * getOutputStream
 */ 
	private OutputStream getOutputStream( File file ) {
	
        if (file == null ) return null;

		OutputStream os = null;
		try {
			os = new FileOutputStream(file); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return os;

} // getOutputStream


/**
 * copyStream
 */
private boolean copyStream( InputStream is, OutputStream os ) {

        if (is == null ) return false;			
        if (os == null ) return false;		

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
 * scanFile
 * egister into Content Provider
 */ 
private  void scanFile(File file) {
        String[] paths = new String[]{ file.getPath() };
        MediaScannerConnection.scanFile(mContext, paths,
                /*mimeTypes*/null, mMediaScannerConnectionClient );
} // scanFile


/**
 * write into logcat
 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


/**
 * MediaScannerConnection.MediaScannerConnectionClient
 */ 
private static MediaScannerConnection.MediaScannerConnectionClient mMediaScannerConnectionClient =
new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        // Do nothing
                    }
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        String msg = "Scanned " + path + " -> uri=" + uri;
                        log_d(msg);
                    }
}; // MediaScannerConnectionClient


} // class FileUtil