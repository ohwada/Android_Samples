/**
 * tsensor sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.sensorsample2;




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
    		
    	    	private Timer mTimer;
    	    	
    	 private SensorEnviroment  mSensorEnviroment;
    	 
		private TextView mTextView1;
			private TextView mTextView2;	
	
		
       	/**
	 * === onCreate ===
	 */			
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            	        	log_d("onCreate");    
            	        	
                    mTimer  = new Timer( this );
         mTimer.setOnChangedListener(
            new Timer.OnChangedListener() {
            @Override
            public void onChangeTimer() {
				procChangeTimer();
            }
            
          } );
          
          
            mSensorEnviroment   = new SensorEnviroment( this );

                           
        setContentView(R.layout.activity_main);
    
            	        mTextView1 = (TextView) findViewById( R.id.TextView_1 );

           	        mTextView2 = (TextView) findViewById( R.id.TextView_2 );           	            

			
    } // onCreate
    
  
  
    
    
       	/**
	 * === onResume ===
	 */	    
         @Override
        protected void onResume() {
        	super.onResume();
        	log_d("onResume");
        	String msg = mSensorEnviroment.registerAll();
        	mTimer.start();
        	    log_d( msg );
        	mTextView1.setText( msg ); 
  } //  onResume  	

        
        	   
        	/**
	 * === onPause ===
	 */	    
         @Override
        protected void onPause() {
        	super.onPause();
        	        	log_d("onPause");
        	mSensorEnviroment.unregisterAll();
        	        	mTimer.stop();
  } //  onPause  
  
     
        
        	   
     	   

        

  
  
 
   

  
        	/**
	 * procChangeTimer
	 */	
  private void procChangeTimer() {
    log_d("procChangeTimer");
    String msg = mSensorEnviroment.getValues();
    log_d( msg );
        	mTextView2.setText( msg );  	  	  	
  } // procChangeTimer
  
  
  
     
  
  
  
    /**
     * log_d
     */
    protected void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d
    
        
} // class MainActivity

