/**
 * logcat sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.logcatsample1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

/**
 * MainActivity
 */
public class MainActivity extends Activity {
    
        	// debug
    	private final static String TAG_SUB = "MainActivity";
    	
    	private final static String SUB_DIR = "LogcatSample/logs";

	private final static String LF = "\n";


	private TextView mTextView1;
	
  private String mText;
  
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_d("onCreate" );
        setContentView(R.layout.activity_main);
        
                        		mTextView1 = (TextView) findViewById( R.id.TextView_1 ) ;


        		Button btnView = (Button) findViewById( R.id.Button_view ) ;
       btnView.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
            	procView( );
            }
        });
        
        		Button btnWrite = (Button) findViewById( R.id.Button_write ) ;
       btnWrite.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
            	procWrite( );
            }
        });  

			
    } // onCreate
    
    
     private void procView( ) {
     	        log_d("procView" );
     	        mText = "";
     	        LogcatViewThread thread = new LogcatViewThread( mHandler ) ;
     	        thread.start();
}	// procView   


    private void procWrite( ) {
    	     	        log_d("procWrite" );
    	     	        File base_dir  = Environment.getExternalStorageDirectory();
    	
    	     	             	        LogcatWriteThread thread = new LogcatWriteThread( base_dir, SUB_DIR  ) ;
     	        thread.start();
 
}	// procWrite


 	/**
	 * Handler
	 */ 
Handler mHandler = new Handler() {

    public void handleMessage( Message msg ) {
    log_d( "handleMessage" );
        switch(msg.what) {
	    case LogcatViewThread.WHAT_LINE:
	        String line = (String) msg.obj;
	        setText( line );
	        // log_d( "line" + line );
	        break;
	        
     	    default : 
    	        break;
        } //  switch

    } // handleMessage

}; // mHandler


private void setText( String str ) {
    mText += str + LF;
    mTextView1.setText( mText );
} // setText


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

	
} // MainActivity
