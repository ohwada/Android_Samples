/**
 * uncaught exception sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.uncaughtexceptionsample1;

import android.app.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.graphics.Bitmap;
import android.util.Log;
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

		private TextView mTextView;
	

	
	/**
 	 * === onCreate === 
 	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        		Thread.setDefaultUncaughtExceptionHandler( new BugHandler( this ) );
        		        
            			log_d( "onCreate" );
        setContentView(R.layout.activity_main);
                				
	        Button btnException = (Button) findViewById( R.id.Button_exception );
				btnException.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonException();
			}
		});	
		
		mkdir();
    }

      	/**
	 * mkdir
	 */	
    private void mkdir() {
    	            			log_d( "mkdir" );
    	   	     	        File base_dir  = Environment.getExternalStorageDirectory();

    		String path = base_dir.getPath() + File.separator +  Constant.SUB_DIR;
	File dir = new File( path );
	// mkdir if not exists
	if ( ! dir.exists() ) {
		dir.mkdirs();
	} // if
} // mkdir
   
    
    
      	/**
	 * procButtonException
	 */	
    private void procButtonException() {
    	    	            			log_d( "procButtonException" );
    	// raise  exception
    	mTextView.setText( "hoge" );
} // 	procButtonException
   


    	



 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d
	
 } // class MainActivity
