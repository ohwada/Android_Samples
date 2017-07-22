/**
 * service list sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.servicelistsample1;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    
     	// debug
     	    private final static boolean D = Constant.DEBUG ;
     	     	private final static String TAG = Constant.TAG ;
    	private final static String TAG_SUB = "MainActivity"; 
    	
    	
    	private final static String SERVICE_NAME = "jp.ohwada.android.servicelistsample1.SkeltonService";
    	
 private 	ServiceList mServiceList;

  
		private TextView mTextView1;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
            	        mTextView1 = (TextView) findViewById( R.id.TextView_1 );
            	            
        	        Button btnStart = (Button) findViewById( R.id.Button_start );
				btnStart.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonStart();
			}
		});	
		
	        	        Button btnStop = (Button) findViewById( R.id.Button_stop );
				btnStop.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonStop();
			}
		});	
		
	        	        Button btnList = (Button) findViewById( R.id.Button_list );
				btnList.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonList();
			}
		});	
		
		    mServiceList  = new ServiceList( this );
		    			
    } // onCreate
    
    
       	/**
	 * procButtonStart
	 */	
    private void procButtonStart() {
        Intent intent = new Intent( this, SkeltonService.class );
        startService( intent );
        boolean ret = mServiceList.isRunning( SERVICE_NAME );
        log_d( "isRunning " + ret );
} // 	procButtonStart
   
   
        	/**
	 * procButtonStop
	 */	
    private void procButtonStop() {
        Intent intent = new Intent( this, SkeltonService.class );
		stopService( intent );
		        boolean ret = mServiceList.isRunning( SERVICE_NAME );
        log_d( "isRunning " + ret );
} // 	procButtonStop  
   
   
   
         	/**
	 * procButtonList
	 */	
    private void procButtonList() {
    String msg = mServiceList.getString();
    	mTextView1.setText( msg );
    			        boolean ret = mServiceList.isRunning( SERVICE_NAME );
        log_d( "isRunning " + ret );
} // 	procButtonList   


 
	/**
	 * write log
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	}  // 	log_d  
   
   
} // class MainActivity
