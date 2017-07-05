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
public class Unzip {
	
	// dubug
	private  final static boolean D = Constant.DEBUG; 
	private final static String TAG_SUB = "Unzip";
	
		// copy stream
	private final static int EOF = -1;
	private final static int BUFFER_SIZE = 1024;
	
	/**
     * === constractor ===
	 */	    
	 public Unzip() {
		// dummy
	} // Unzip
	
    /**
     * unzip
     * @param ZipInputStream zis
     * @param String outputDir
     * @throws IOException
     */
    public static boolean unzip( ZipInputStream zis, String outputDir, boolean is_overwrite )  throws IOException {

	// if input is null
    	if ( zis == null ) return false;
    	
    	// if not exists or not directory
File out = new File( outputDir );
if ( ( out == null ) || !out.exists() || ! out.isDirectory()  ) return false;

    		boolean is_error = false;
            ZipEntry entry   = null;

		// *** while entry ***
	 // restore entries  to directory and file one by one
			while ( (entry = zis.getNextEntry()) != null ) {
				
				// create  file or directory
				String name = entry.getName();
				log_d("entry name " + name);
				String path_dst = outputDir + File.separator + name;
				File file_dst = new File( path_dst );
				if ( file_dst.exists() ) {
					log_d("exitsts " + path_dst);
					// skip if exists
					if ( !is_overwrite ) continue;
				} // file_dst

				FileOutputStream fos = new FileOutputStream( file_dst );
				if ( fos == null ) return false;

				// mkdirs and skip if entry is directory
				if ( entry.isDirectory() ) {
					boolean ret = file_dst.mkdirs();
					if (ret) {
						log_d("unzip mkdirs OK " + path_dst);
					} else {
						is_error = true;
						log_d("unzip mkdirs NG " + path_dst);
					} // if ret
					continue;

				} // if entry

		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		
		// output  file
					while ((size = zis.read(buf, 0, buf.length)) > EOF ) {
						fos.write(buf, 0, size);
					} // while read

				fos.close();

			} // *** while  entry end ***

	return !is_error;

   } // unzip



 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d		

	




} // class Unzip