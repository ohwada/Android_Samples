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

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * PrivateFile
 */
public class PrivateFile {
	// dubug
	protected  final static boolean D = Constant.DEBUG; 
	protected String TAG_SUB = "MyFileUtility";
	
	// mode
	public final static int MODE_BASE = 0;
	public final static int MODE_APP = 1;
			
	// return code
	public final static int C_FAIL =0;
	public final static int C_EXISTS =1;
		public final static int C_SUCCESS =2;
			
	// all files in asset		 
	protected final static String ASSET_PATH = "";
	
	// copy stream
	protected final static int EOF = -1;
	protected final static int BUFFER_SIZE = 1024 * 4;

 
	// junk in assets	
	protected final static String[] IGNORE_FILES 
	 	= new String[] { "images", "sounds", "webkit" };
		 
	protected AssetManager mAssetManager;

	protected String mBaseDir = "";
	
	protected String mAppDir = "";
		
	/**
     * === constractor ===
	 * @param Context context  
	 */	    
	 public PrivateFile( Context context  ) {
		mAssetManager = context.getAssets();
		
		File  dir = context.getFilesDir();	
		setBaseDir( dir );

	} 
	
		/**
     * setBaseDir
	 */	
	protected void setBaseDir( File dir ) {
		if ( dir != null ) {	
		mBaseDir = dir.getPath();
		log_d( "BaseDir "  + mBaseDir );
		mAppDir = mBaseDir;
	} // if dir
}// setBaseDir


	/**
     * setDir
	 * @param String dir 
	 */	
	public void setDir( String dir ) {
		
		mAppDir = mBaseDir + File.separator + dir ;	
		log_d("AppDir " + mAppDir);

	} // 	 setDir
		
	/**
     * mkdirs
	 * @param String dir 
	  * @return  int
	 */	
	public int mkdirs( String dir ) {

	setDir( dir );
		
		File file = new File( mAppDir );
		// nothng to do if exists
		if ( file.exists() ) {
			log_d("exists " + mAppDir);
			return C_EXISTS;
		}

		boolean ret = file.mkdirs();
		if (ret) {
			log_d("mkdir " + mAppDir);
			return C_SUCCESS ;
		}
			log_d("mkdir NG" + mAppDir );		
		return C_FAIL ;

} // mkdirs

	    
	/**
	 * copyAssetsFiles
	 * @param String ext
	 * @return boolean
	 */  
	public boolean copyAssetsFiles( String ext ) {
		
	boolean is_error = false;
		
		List<String> list = getAssetList( ASSET_PATH, ext );
		
		// no result
		if  ( (list == null) ||( list.size() == 0 ) ) {
			return false;
		}
	
		// copy files
		for ( String name: list ) {
			
			log_d( "copy " + name );

				String path_dst =  getPath( name, MODE_APP );
												
			 int ret = copyAssetsToStorage( name, path_dst );
			 if(ret == C_FAIL) {
			 	is_error = true;
			 }
	
		} // for

		return ! is_error ;

	} //copyAssetsFiles
	

	/**
	 * get AssetsList
	 */  
	protected List<String> getAssetList( String path, String ext ) {
		
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

		// all files
		for ( int i=0; i < length; i ++ ) {
						
			String name = files[i];
			log_d( "assets " + name );
			
			// skip if ignore
		 if ( checkIgnore( name ) ) continue;
			
			// skip if munmach ext
			if ( !FileUtility.matchExt( name, ext ) ) continue;

	list.add( name );
	
	} // for
	
	return list;
} // getAssetList



	/**
	 * checkIgnore
	 */  
	protected boolean checkIgnore( String name ) {

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
	protected int copyAssetsToStorage( String name_src, String path_dst ) {
		
			File file_dst = new File( path_dst );
			// file exsts
			if ( file_dst.exists() ) {
				log_d( "exitsts " + path_dst );
					return C_EXISTS;
	}		
	
		 // file open
		 InputStream is = getAssetsInputStream( name_src );
		 if ( is == null ) return C_FAIL;	
	
		FileOutputStream fos = getFileOutputStream( file_dst );
if ( fos == null ) return C_FAIL;

boolean ret = copyStream( is,  fos );

		 // file close
		try {
		 is.close();
			 fos.close();
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} // try 
					
				if ( ret ) {
				log_d( "copy OK " + path_dst );
							return C_SUCCESS ;
			} 
				log_d( "copy NG " + path_dst );
		return C_FAIL ;

		 	
	} // copyAssetsToStorage
	



	/**
	 * getAssetsInputStream
	 */ 	
	protected InputStream getAssetsInputStream( String fileName ) {
		
		InputStream is = null;
		try {
			is = mAssetManager.open( fileName );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
	return is;
} //getAssetsInputStream



	/**
	 * getFileOutputStream
	 */ 
	protected FileOutputStream getFileOutputStream( File file ) {	
	
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file); 
					} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
		return fos;

} // getFileOutputStream



	/**
	 * copyStream
	 */
protected boolean copyStream( InputStream is, FileOutputStream fos ) {
			
		byte[] buffer = new byte[BUFFER_SIZE];
		int n = 0;	
			boolean is_error = false;
			
		try {
			// copy input to output		
			while (EOF != (n = is.read(buffer))) {
				fos.write(buffer, 0, n);
			} // while

		} catch (IOException e) {
			is_error = true;
			if (D) e.printStackTrace();
	}

	return ! is_error;
}	// copyFileStream



	/**
	 * getBitmap
	 * @param String name
	  * @param nt mode
	 * @return Bitmap
	 */	
	public Bitmap getBitmap( String name, int mode ) {
		
				Bitmap bitmap = null;
		try {
		bitmap = BitmapFactory.decodeFile( getPath( name, mode ) );
				} catch (Exception e) {
			if (D) e.printStackTrace();
		} // try
		
		if ( bitmap != null ) {
			bitmap.setDensity( DisplayMetrics.DENSITY_MEDIUM );
		} // if
		
		return bitmap;
	} // getBitmap



	/**
	 * getFile
	 */		
		protected File getFile( String name, int mode ) {
		File file = new File( getPath( name, mode ) );
		return file;		
	} // getFile



	/**
	 * getPath
	 */			
	protected String getPath( String name, int mode ) {
				
		 String path = getDir( mode )  + File.separator + name ;
		 return path;		 
	} // getPath
	
 	/**
	 * getDir
	 */	
	protected String getDir( int mode ) {

		String dir = "";
		if (mode == MODE_BASE) {
			dir = mBaseDir;
		} else if (mode == MODE_APP) {
			dir = mAppDir;
		} // if

		return dir;
	} // getDir
				

 	/**
	 * getFileList
	 	 * @param int mode
	 * @return List<String>
	 */	
public List<String> getFileList( int mode ) {
List<String> list = new ArrayList<String>();
File file_base = new File( getDir( mode ) );
String[] base_names = file_base.list();
int base_length = base_names.length;
		// nothing if no files
		if ( base_length == 0 ) return list;

		// all files
		for ( int i=0; i < base_length; i ++ ) {
						
			String base_name = base_names[i];
			String base_path = mBaseDir   + File.separator + base_name;
			File base_file = new File(base_path);
			
			// get child if directory
			if( base_file.isDirectory() ) {
				List<String> list_child = getChildFileList( base_name );
				for ( String name: list_child ) {
					list.add( name );		
			} // for	list_child
			} // if isDir	
			} // for i	
			
			return list;
					
} // getBaseFileList



 	/**
	 * etChildFileList
	 */
protected List<String> getChildFileList( String dir ) {
	
 List<String> list = new ArrayList<String>();
		list.add( dir + File.separator );
	String dir_path = mBaseDir   + File.separator + dir;
	File file_dir = new File(dir_path);
			String[] names = file_dir.list();
			int length = names.length;
				for ( int j=0; j < length; j ++ ) {
								String name = names[j];		
								String path = dir + File.separator + name;				list.add( path );		
			} // for j
			
			return list;
			} // 	getChildFileList	 

	

 	/**
	 * write into logcat
	 */ 
	protected void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


} // class PrivateFile