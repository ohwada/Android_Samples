/**
 * broadcast receiver sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.broadcastreceiversample1;


import android.app.Activity;

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
     	   	
    	private MyBroadcastReceiver mBroadcastReceiver;
    	
    	private DigitalClock mDigitalClock;
    	
		private TextView mTextView1;
		private TextView mTextView2;
		private TextView mTextView3;
						
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
            mBroadcastReceiver  = new MyBroadcastReceiver( this );
         mBroadcastReceiver.setOnChangedListener(
            new MyBroadcastReceiver.OnChangedListener() {
            @Override
           public void  onTimeTick() {
				procTimeTick();
            }
             @Override
           public void  onTimeChanged() {
				procTimeChanged();
            }
                        @Override
           public void  onTimezoneChanged( String tz ) {
				procTimezoneChanged( tz );
            }
                       
          } ); // setOnChangedListener
          
         mDigitalClock = new DigitalClock( this ); 
         
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
    mBroadcastReceiver.registerReceiver();
} // 	procButtonStart


        	/**
	 * procButtonStop
	 */	
    private void procButtonStop() {
    mBroadcastReceiver.unregisterReceiver();
} // 	procButtonStop

  
       	/**
	 * procTimeTick
	 */	
    private void procTimeTick() {
    	log_d("procTimeTick");
    	String msg = "TIME_TICK" + LF;
msg  += mDigitalClock.getFormatTime() + LF;
    	mTextView1.setText( msg );
} // 	procTimeTick

       	/**
	 * procTimeChanged
	 */	
    private void procTimeChanged() {
            	log_d("procTimeChanged");
    	    	String msg = "TIME_CHANGED" + LF;
msg  += mDigitalClock.getFormatTime() + LF;
    	mTextView2.setText( msg );
} // 	procTimeChanged


       	/**
	 * procTimezoneChanged
	 */	
    private void procTimezoneChanged( String tz ) {
                    	log_d("procTimezoneChanged");
    	    	    	String msg = "TIMEZONE_CHANGED" + LF;
msg  += tz + LF;
    	mTextView3.setText( msg );
} // 	procTimezoneChanged


        /**
     * log_d
     */
    protected void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d	
    
   
} // class MainActivity
