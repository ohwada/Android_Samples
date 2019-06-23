/**
 * File Provider Sample
 * FileUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.fileprovider1;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * class FileUtil
 */
public class FileUtil {

	// dubug
	private final static boolean D = true; 
    private final static String TAG = "FileProvider";
	private final static String TAG_SUB = "FileUtil";
	

/**
 * File Name
 */ 
    public static final String FILE_PREFIX = "camera_";
    public static final String FILE_EXT_JPG =  ".jpg";


/**
 * Path Name in Assets folder
 * "" : root path
 */ 
    private final static String ASSETS_PATH = "";


/**
 * DateTime Format for File Name
 */ 
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";


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
 *  copyFilesAssetsToExternalFilesDir
 */  
public boolean copyFilesAssetsToExternalFilesDir( String ext ) {

	boolean is_error = false;
		
		List<String> list = getFileNameListInAssets(ASSETS_PATH, ext);
		
        File dir = getExternalFilesDir(mContext, null);

		// no result
		if  ( (list == null) ||( list.size() == 0 ) ) {
            log_d("no result");
			return false;
		}
	
	// copy files
	for ( String name: list ) {
			
		log_d( "copy " + name );
		InputStream is = getAssetsInputStream( name );
		File file = new File(dir, name);
		OutputStream os = getOutputStream( file );
		boolean ret = copyStream( is,  os );
		if(!ret) {
			is_error = true;
		}
	
	} // for

	return ! is_error ;

} // copyFilesAssetsToExternalFilesDir


/**
 * getFileListInExternalFilesDir
 */  
public List<File> getFileListInExternalFilesDir() {

        File dir = getExternalFilesDir(mContext, null);

        List<File> list =	new ArrayList<File>();

        File[]	files = dir.listFiles();


		// nothing if no files
		if ( files == null ) return list;
					 
		int length = files.length;
		// nothing if no files
		if ( length == 0 ) return list;

		for ( int i=0; i < length; i ++ ) {	
			File file = files[i];						
			list.add(file);
	} // for

	return list;
} // getFileListInExternalFilesDir


/**
 * getExternalFilesDir
 */ 	
public static File getExternalFilesDir(Context context, String type) {
    return context.getExternalFilesDir(type);
}


/**
 * getFileNameListInAssets
 */  
public List<String> getFileNameListInAssets(String path, String ext ) {

	List<String> list =	new ArrayList<String>();
		String[] files = null;
		try {
			files = mAssetManager.list(path);
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
			    // skip if not mach ext	
			    if (!name.endsWith(ext))  continue;
			    list.add(name);
	    } // for	
	    return list;

} // getFileNameListInAssets


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
public OutputStream getOutputStream( File file) {	
	
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
public boolean copyStream( InputStream is, OutputStream os ) {
			
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
 * createOutputFileInExternalFilesDir
 *  create File to save Picture for Camera
 */ 
public File createOutputFileInExternalFilesDir() {
        File dir = getExternalFilesDir(mContext, null);
        String filename = createOutputFileName(FILE_PREFIX, FILE_EXT_JPG);
        File file = new File(dir, filename);
    return file;
} // createOutputFileInExternalFilesDir



/**
 * createOutputFileName
 */
private static String createOutputFileName(String prefix, String ext) {
    Date date = new Date();
   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            String currentDateTime =  sdf.format(date);
            String filename = prefix + currentDateTime + ext;
    return filename;
} // createOutputFileName


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class FileUtil