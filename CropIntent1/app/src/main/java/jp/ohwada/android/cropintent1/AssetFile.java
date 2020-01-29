/**
 * Crop Intent Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.cropintent1;


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
 * class AssetFile
 */
public class AssetFile {

    // debug
    private final static String TAG = "AssetFile";


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
public AssetFile( Context context  ) {
		mContext = context;
		mAssetManager = context.getAssets();
} 
	

/**
 * make Directory in ExternalStoragePublicPictures
 */ 
public static void mkDirInExternalStoragePublicPictures() {
        ExternalStorageFile.mkDirInPictures();
}

	    
/**
 *  copyFilesAssetToExternalStoragePublicPictures
 */  
public boolean copyFilesAssetToExternalStoragePublicPictures( String ext ) {
		
	boolean is_error = false;
		
		List<String> list = getFileListInAsset(ext);
		
        File dir = ExternalStorageFile.getDirInPictures();

		// no result
		if  ( (list == null) ||( list.size() == 0 ) ) {
			return false;
		}
	
	// copy files
	for ( String name: list ) {

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

} 
	

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
	private InputStream getAssetsInputStream( String fileName ) {
		
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
}


/**
 * MediaScannerConnection.MediaScannerConnectionClient
 */ 
private static MediaScannerConnection.MediaScannerConnectionClient mMediaScannerConnectionClient =
new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        // nop
                    }
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        // nop
                    }
}; // MediaScannerConnectionClient


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    Log.d( TAG, msg );
}


} // class AssetFile