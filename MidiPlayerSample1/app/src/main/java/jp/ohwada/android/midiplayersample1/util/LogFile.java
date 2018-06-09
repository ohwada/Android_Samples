/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;


/**
 * LogFile
 * TODO : runtime permission
 */
public final class LogFile  { 
	// debug
    private final static boolean D = true; 

    private final static String DIR = "midi";
    private final static String EXT = ".txt";				
    private final static String LF = "\n";	

	// filename
    private static SimpleDateFormat mDateFormatLog = 
    	new SimpleDateFormat("yyyyMMdd");

	/**
	 * constractor
	 */
    private LogFile() {
		// duumy
    }

	/**
	 * make directory
	 */ 
	public static void mkdir() {
		File dir = new File( getPath() );
		if ( !dir.exists() ) { 
			dir.mkdir();
		}
	} // mkdir

	/**
	 * --- write log  ---
	 * @param Context context 
	 * @param String msg
	 */ 
	public static void write( Context context, String msg ) {
		Date now = new Date( System.currentTimeMillis() );
		String name = mDateFormatLog.format( now );
		String filename = getPath() + File.separator + name + EXT;
		String data = msg + LF;

		// write log file
		File file = new File( filename );
		OutputStream os = null;
		try {
			os = new FileOutputStream( file, true );
			os.write( data.getBytes() );
		} catch ( FileNotFoundException e ) {
			if (D) e.printStackTrace();
		} catch ( IOException e ) {
			if (D) e.printStackTrace();
		}
		if ( os != null ) {
			try {
				os.close();
			} catch ( IOException e ) {
				if (D) e.printStackTrace();
			}
		}
	} // write

	/**
	 * getPath
	 * @return String msg
	 */ 
	private static String getPath() {
		String sd = Environment.getExternalStorageDirectory().getPath();
		String path = sd + File.separator + DIR ;
		return path;
	} // getPath
				
} // class LogFile