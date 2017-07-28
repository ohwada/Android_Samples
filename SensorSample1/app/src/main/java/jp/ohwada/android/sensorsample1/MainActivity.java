/**
 * sensor sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.sensorsample1;


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
    	
    	
    	 private SensorUtility  mSensorUtility;
    	 
		private TextView mTextView1;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
            mSensorUtility   = new SensorUtility( this );
             mSensorUtility.setOnChangedListener(
            new SensorUtility.OnChangedListener() {
            	
            public void onSensorChanged(  String msg ) {
                procSensorChanged(   msg ) ;
            }            
            
          } ); // setOnChangedListener
          
         
                 
        setContentView(R.layout.activity_main);
    
            	        mTextView1 = (TextView) findViewById( R.id.TextView_1 );

            	            
	        	        Button btnList = (Button) findViewById( R.id.Button_list );
				btnList.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonList();
			}
		});	
		
		
	        	        Button btnLight = (Button) findViewById( R.id.Button_light );
				btnLight.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonLight();
			}
		});	
		
		
		        	        Button btnTemperature = (Button) findViewById( R.id.Button_temperature );
				btnTemperature.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonTemperature();
			}
		});	
		
		        	        Button btnHumidity = (Button) findViewById( R.id.Button_humidity );
				btnHumidity.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonHumidity();
			}
		});	

        	        Button btnPressure = (Button) findViewById( R.id.Button_pressure );
				btnPressure.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonPressure();
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
	 * === onResume ===
	 */	    
         @Override
        protected void onResume() {
        	super.onResume();
  } //  onResume  	
        	   
  
     
        	   
        	/**
	 * procButtonList
	 */	
    private void procButtonList() {
    	log_d("procButtonList");
         String msg =  mSensorUtility.getStringList();
          	mTextView1.setText( msg ); 
} // 	procButtonList       	   

        
        	   
       	/**
	 * procButtonLight
	 */	
    private void procButtonLight() {
    	log_d("procButtonLight");
                	mSensorUtility.registerLight();
   
} // 	procButtonLight
  
  
   
       	/**
	 * procButtonTemperature
	 */	
    private void procButtonTemperature() {
    	    	log_d("procButtonTemperature");
                	mSensorUtility.registerTemperature();
   
} // 	procButtonTemperature  
   
   
         	/**
	 * procButtonHumidity
	 */	
    private void procButtonHumidity() {
    	    	    	log_d("procButtonHumidity");
                	mSensorUtility.registerHumidity();
   
} // 	procButtonHumidity  
   

    
         	/**
	 * procButtonPressure
	 */	
    private void procButtonPressure() {
    	    	log_d("procButtonPressure");
                	mSensorUtility.registerPressure();
   
} // 	procButtonPressure  
   
   
           	/**
	 * procButtonStop
	 */	
    private void procButtonStop() {
    	    	log_d("procButtonStop");
                	mSensorUtility.unregisterAll();
   
} // 	procButtonStop 
   
   
   
    /**
     * procSensorChanged
     */   
   private void  procSensorChanged(  String msg ) {
    log_d( msg );
        	mTextView1.setText( msg );
  } //  procSensorChanged
  
  
  
    /**
     * log_d
     */
    protected void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d
    
        
} // class MainActivity

