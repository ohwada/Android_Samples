/**
 * sensor sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.sensorsample1;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;


/**
 * SensorUtility
 */	 
public class SensorUtility 
    implements SensorEventListener {
	
	     	// debug
    	private final static String TAG_SUB = "SensorUtility";   
    	
	public final static String LF = "\n";
	
	private Context mContext;
	
        private SensorManager mSensorManager;
        
       private Sensor mSensorLight;
     private Sensor mSensorTemperature;
     private Sensor mSensorHumidity;
          private Sensor mSensorPressure;
                 
        private boolean mIsSensor;
 
 // callback 
    private OnChangedListener mListener;  
    
    
            /*
     * callback interface
     */    
    public interface OnChangedListener {
        public void  onSensorChanged(  String msg );
    } // interface

    /*
     * callback
     */ 
    public void setOnChangedListener( OnChangedListener listener ) {
        mListener = listener;
    } // setOnChangedListener
    
    
        
 /**
 * === constractor ====
 */	       
public SensorUtility( Context context ) {
	mContext = context;
	            mSensorManager=(SensorManager) context.getSystemService( Context.SENSOR_SERVICE );
} // 	SensorUtility


 /**
 * === onAccuracyChanged ====
 */	
    @Override
    public void onAccuracyChanged( Sensor sensor, int i ) {
        log_d("onAccuracyChanged");
    } // onAccuracyChanged
    
   
    
  /**
 * === onSensorChanged ====
 */	   
           @Override
        public void onSensorChanged( SensorEvent event ) {
              	
              	log_d("onSensorChanged");          
       int accuracy = event.accuracy;
     Sensor sensor = event.sensor ; 
     String name = sensor.getName();
       long timestamp = event.timestamp ; 
             float[] values = event.values ;         
             float value = values[0];
         
     String msg = "";           
    msg += "sensor=" + sensor.toString() + LF;          	            
     msg += "timestamp=" + timestamp + LF;            
      msg += "accuracy=" + accuracy + LF; 
     msg += "value=" + value + LF;
                      	                      	
        	notifySensorChanged( msg );
  } // onSensorChanged  
    
 
     /**
     * getStringList
     */   
       public String getStringList() {
                        	log_d("getStringList");
                        	String msg = ""; 
                        	List<Sensor> list = mSensorManager.getSensorList( Sensor.TYPE_ALL );
            if (( list == null )||( list.size() == 0 )) return msg;
            for (Sensor sensor: list ) {
             msg += sensor.toString() + LF;   
            } // for
        return msg;      
    }  // getStringList
    
     /**
     * registerLight
     */   
       public void registerLight() {
                      	log_d("registerLight");    
Sensor sensor =  getSensor( Sensor.TYPE_LIGHT );
    if ( sensor != null ) {
                mSensorLight = sensor;
                registerListener( sensor );
    } // if 
              
} // registerLight



     /**
     * registerTemperature
     */   
       public void registerTemperature() {
                      	log_d("registerTemperature");    
Sensor sensor =  getSensor( Sensor.TYPE_AMBIENT_TEMPERATURE );
    if ( sensor != null ) {
                mSensorTemperature = sensor;
                registerListener( sensor );
    } // if 
              
} // registerTemperature



     /**
     * registerHumidity
     */   
       public void registerHumidity() {
                      	log_d("registerHumidity");    
Sensor sensor =  getSensor( Sensor.TYPE_RELATIVE_HUMIDITY );
    if ( sensor != null ) {
                mSensorHumidity = sensor;
                registerListener( sensor );
    } // if 
              
} // registerHumidity



     /**
     * registerPressure
     */   
       public void registerPressure() {
                      	log_d("registerPressure");    
Sensor sensor =  getSensor( Sensor.TYPE_PRESSURE );
    if ( sensor != null ) {
                mSensorPressure = sensor;
                registerListener( sensor );
    } // if 
              
} // registerPressure



     /**
     * getSensor
     */   
       private Sensor getSensor( int type ) {
                      	log_d("getSensor"); 
                      	Sensor sensor = null;   
            List<Sensor> list = mSensorManager.getSensorList( type);
            if (( list != null )&&( list.size() > 0 )) {
                sensor = list.get(0);
    } // if 
      
    return sensor;
} // getSensor



     /**
     * registerListener
     */
private void registerListener( Sensor sensor ) {
                mIsSensor = mSensorManager.registerListener( 
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_FASTEST );          
} // registerListener



     /**
     * unregisterAll
     */
public void unregisterAll() {
                          	log_d("unregisterAll"); 
                          	unregister( mSensorLight );
                          	unregister( mSensorTemperature );
                          	unregister( mSensorHumidity );
                          	unregister( mSensorPressure );

} // unregisterAll



     /**
     * unregister
     */
private void unregister( Sensor sensor ) {
                          	log_d("unregister"); 
    if ( sensor == null ) return;
mSensorManager.unregisterListener( this, sensor );
} // unregister



    /**
     * notifySensorChanged
     */
    private void notifySensorChanged( String msg ) {
           if ( mListener != null ) {
            mListener.onSensorChanged( msg );
        } 
}	// notifySensorChanged



    /**
     * log_d
     */
    private void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d


} // class SensorUtility
