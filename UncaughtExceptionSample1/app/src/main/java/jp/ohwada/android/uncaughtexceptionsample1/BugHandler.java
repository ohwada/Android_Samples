/**
 * uncaught exception sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.uncaughtexceptionsample1;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * BugHandler
 */
public class BugHandler implements UncaughtExceptionHandler {

 	// debug
    private final static boolean D = Constant.DEBUG ;
    	private final static String TAG_SUB = "BugHandler";
    	
    	// file name
    	 private final static String PREFIX = "bug_";
    	     	 private final static String EXT_TXT = ".txt";
    private final static String DATE_FORMAT = "yyyyMMdd_kkmmss";
    
    
    private final static String CHAR_SHARP = "#";
    private final static String CHAR_COLON = ":";
    private final static String LF = "\n";	
    
	private Context mContext;

	private UncaughtExceptionHandler mHandler;

	private String mLogDir = "";
	

		        		
    /**
     * === constractor ===
	 * @ param Context context
     */
	public BugHandler( Context context ) {
		mContext = context;
		mHandler = Thread.getDefaultUncaughtExceptionHandler();	
	
		setLogDir();	
	} // BugHandler




		
		



    /**
     * === uncaughtException ===
     */	
	public void uncaughtException( Thread thread, Throwable throwable ) {
		log_d( "uncaughtException" );
		saveLog( throwable );
		mHandler.uncaughtException( thread, throwable );
	} // uncaughtException


    /**
     * saveLog
     * @ param Throwable throwable
     */	
	private void saveLog( Throwable throwable ) {


		File file = getFile();
		
		StackTraceElement[] stacks = throwable.getStackTrace();
        PrintWriter pw = null;
        
        try {
        	pw = new PrintWriter( new FileOutputStream( file ) );
         	      	
        	StringBuilder sb = new StringBuilder();
        	int len = stacks.length;
        	for (int i = 0; i < len; i++) {
            	StackTraceElement stack = stacks[ i ];
            	sb.setLength( 0 );
            	sb.append( stack.getClassName() ).append( CHAR_SHARP );
            	sb.append( stack.getMethodName() ).append( CHAR_COLON );
            	sb.append( stack.getLineNumber() );
           		pw.println( sb.toString() );
        	} // for
        	
		} catch ( FileNotFoundException e ) {	
			if (D) e.printStackTrace();
        } // try
        
        if ( pw != null ) {        	
        	pw.close();
        } // if
        
	} // saveLog


    /**
     * setLogDir
     */	
	private void setLogDir() {
    	   	     	        File base_dir  = Environment.getExternalStorageDirectory();

		mLogDir = base_dir.getPath() + File.separator +  Constant.SUB_DIR;
	} // setLogDir
	
	
	    /**
     * getFile
     */	
	private File getFile() {

		SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
		String time = sdf.format( new Date( System.currentTimeMillis() ) );
		String name = PREFIX + time + EXT_TXT;
				log_d( "name " + name );
		String path = mLogDir + File.separator + name;
		File file = new File( path );
	return file;
} // 	getFile




 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d



} // class MyUncaughtExceptionHandler