

/**
 * logcat sample
 * 2017-06-01 K.OHWADA 
 */

package jp.ohwada.android.logcatsample1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


import java.io.File;



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * LogcatViewThread
 */
public class LogcatViewThread extends Thread {

    private final static boolean D = Constant.DEBUG ;
 	private final static String TAG = Constant.TAG ;
 	private final static String TAG_SUB = "LogcatViewThread" ;
 	
public final static int WHAT_LINE = 1;

	private final static String LF = "\n";

// logcat command	
// https://developer.android.com/studio/command-line/logcat.html?hl=ja
// - v time : 日付、起動時刻、優先度 / タグ、メッセージ発行元プロセスの PID
// V : Verbose（最も低い優先度）
	private final static String[] CMD_STRINGS = new String[] { "logcat", "-v", "time", "V" }; 
	
	private final static int BUFFER_SIZE = 1024;
	
	// 0.2 sec
	private final static long SLEEP_TIME = 200;
	
	// 60 sec
	private final static int MAX_SLEEP_COUNT = (int)( 60 * 1000 / SLEEP_TIME );
	
	// 100 lines 
	private final static int MAX_LINE_COUNT = 100;

	private Handler mHandler;

		             		
/**
 * === constractor ===
 */
public LogcatViewThread( Handler handler  ) {
	mHandler = handler;
} // LogcatViewThread


	    /**
	 * run
	 */
	public void run() {
		log_d( "run" );
		procLogcatLoop();
} // run	
	


			
	    /**
	 * procLogcatLoop
	 */  
	private void procLogcatLoop() {
	    log_d( "procLogcatLoop" ); 
		Process proc = null;
		InputStream is = null;
	 	InputStreamReader isr = null;
	 	BufferedReader br = null;
	try {
		proc = Runtime.getRuntime().exec( CMD_STRINGS ); 
		is = proc.getInputStream();
	 	 isr = new InputStreamReader( is );
	 	br = new BufferedReader( isr, BUFFER_SIZE  );	
	 						} catch ( IOException e ) {
		if (D) e.printStackTrace();
	} // try
		
	 int sleep_count = 0;  
	 int line_count = 0;
	 String line = "";
		
	 	// endless loop
		while ( true ) {

		line = "";
		try {
			line = br.readLine();
		} catch ( Exception e ) {
			if (D) e.printStackTrace();
	} // try


			if ((line != null) && (line.length() > 0)) {
							line_count ++;
							sendMessage(line);
				// log_d( "line_count= " + line_count );
				 // log_d( line );			
			} else {
				
					sleep_count ++;	
					log_d( "sleep_count= " + sleep_count );
				try {
					// sleep 0.2 sec, if not get line

					Thread.sleep( SLEEP_TIME  );
						} catch ( Exception e ) {
		if (D) e.printStackTrace();
	} // try

			} // if line
			
				// break, if over 100 lines
				if( line_count >= MAX_LINE_COUNT ) {
											sendMessage( "line over ");
					log_d( "line_count over " + line_count );
  					break;
    			} // line_count
    			
				// over 60 sec
				if ( sleep_count > MAX_SLEEP_COUNT ) {
																sendMessage( "time over ");
					log_d( "sleep_count over " + sleep_count );
  						break;
				} // if  sleep_count
	
		} // while
		
		try {
		if ( is != null ) is.close();
		if ( isr != null ) isr.close();
		if ( br != null ) br.close();
						} catch ( IOException e ) {
		if (D) e.printStackTrace();
	} // try
			
	} //  procLogcat
	
	
	/**
	 * sendmessage
	 */ 	
	private void sendMessage( String str ) {
			Message msg = Message.obtain();
		msg.what = WHAT_LINE;
		msg.obj = str;
		mHandler.sendMessage(msg);
} // sendmessage



	/**
	 * write log
	 * @ param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d
	
		
} // class Logcat

