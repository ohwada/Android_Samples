/**
 * tsensor sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.sensorsample2;






import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;


/**
 * SensorCommon
 */	 
public class SensorCommon 
    implements SensorEventListener {
	
	     	// debug
    	private String TAG_SUB = "SensorCommon";   
    	
	private final static String LF = "\n";
	
	private Context mContext;
	
        private SensorManager mSensorManager;
        
     private Sensor mSensor;
      private boolean  isListening = false;
         private int mType = 0;     
         private float mValue = 0f;
                      

 
        
 /**
 * === constractor ====
 */	       
public SensorCommon( Context context, int type, String tag_sub ) {
	mContext = context;
	mType = type;
	TAG_SUB = tag_sub;
	            mSensorManager=(SensorManager) context.getSystemService( Context.SENSOR_SERVICE );
} // 	constractor


 /**
 * === onAccuracyChanged ====
 */	
    @Override
    public void onAccuracyChanged( Sensor sensor, int i ) {
       //  log_d("onAccuracyChanged");
    } // onAccuracyChanged
    
   
    
  /**
 * === onSensorChanged ====
 */	   
           @Override
        public void onSensorChanged( SensorEvent event ) {
              	
              	// log_d("onSensorChanged");  
              	procSensorChanged( event ) ;       

  } // onSensorChanged  
 
 
   /**
 * procSensorChanged
 */	    
          private void procSensorChanged( SensorEvent event ) {
      
            mValue = event.values[0];

  } // procSensorChanged    
   
   
 
 
      /**
     * getSensor
     */         
   public Sensor  getSensor() {
             return mSensor;
 } // getSensor
 
 
       /**
     * getSensor
     */         
   public String  getSensorInfo() {
    String msg = "";
             if ( mSensor != null ) {
                msg = mSensor.toString();
        } else {
            msg = "No Sensor";
    } // if
    return msg;   
 } // getSensor
 
 
 
     /**
     * isListening
     */         
   public boolean  isListening() {
             return isListening;
 } // isListening
 
     
     
     /**
     * getValue
     */         
   public float  getValue() {
             return mValue;
 } // getValue
 

 




     /**
     *registerListener
     */   
     public  boolean registerListener() {
                      	log_d("registerListener");    
Sensor sensor =  getDefaultSensor( mType );
    if ( sensor == null ) return false;
                mSensor = sensor;
                boolean ret = register( sensor,   SensorManager.SENSOR_DELAY_FASTEST );
              if ( ret ) {
                isListening = true;
              }
         return ret;
} //registerListener







     /**
     * getDefaultSensor
     */   
       public Sensor getDefaultSensor( int type ) {
                      	log_d("getDefaultSensor"); 
Sensor sensor = mSensorManager.getDefaultSensor( type );
            if ( sensor == null ) {
                log_d( "can not get sensor" ); 
                return null;
            }
                      	log_d( sensor.toString() );  
    
    return sensor;
} // getDefaultSensor



     /**
     * register
     */
private boolean register( Sensor sensor, int samplingPeriod ) {
    return mSensorManager.registerListener(
                    this,
                    sensor,
                    samplingPeriod );          
} // register







     /**
     * unregisterListener
     */
public void unregisterListener() {
                          	log_d("unregisterListener"); 
    if ( mSensor == null ) return;
mSensorManager.unregisterListener( this, mSensor );
isListening = false;
} // unregisterListener






    /**
     * log_d
     */
    private void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d


} // class SensorUtility
