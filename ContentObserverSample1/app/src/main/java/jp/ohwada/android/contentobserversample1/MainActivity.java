/**
 * content observer sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.contentobserversample1;


import android.app.Activity;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Environment;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    
     	// debug
    	private final static String TAG_SUB = "MainActivity";   
    	
     	private final static String LF = "\n";   
     	   	
    	private SystemSettingsContentObserver mContentObserver;
    	
		private TextView mTextView1;
		private TextView mTextView2;
		private TextView mTextView3;
						
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
            mContentObserver  = new SystemSettingsContentObserver( this );
         mContentObserver.setOnChangedListener(
            new SystemSettingsContentObserver.OnChangedListener() {
                        @Override
           public void  onChange( boolean selfChange ) {
				procChange( selfChange );
            }                     
          } ); // setOnChangedListener
          
         
        setContentView(R.layout.activity_main);
    
            	        mTextView1 = (TextView) findViewById( R.id.TextView_1 );
              	        mTextView2 = (TextView) findViewById( R.id.TextView_2 );
              	                    	        mTextView3 = (TextView) findViewById( R.id.TextView_3 );          	            
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
				
    } // onCreate
    
   
    
       	/**
	 * procButtonStart
	 */	
    private void procButtonStart() {
   mContentObserver.registerContentObserver();
} // 	procButtonStart


        	/**
	 * procButtonStop
	 */	
    private void procButtonStop() {
   mContentObserver.unregisterContentObserver();
} // 	procButtonStop

  
       	/**
	 * procChange
	 */	
    private void procChange( boolean selfChange ) {
    	log_d("procChange");
    	String msg = "onChange : " + selfChange;
    	mTextView1.setText( msg );
} // 	procChange
    	
    	

        /**
     * log_d
     */
    protected void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d	
    
   
} // class MainActivity
