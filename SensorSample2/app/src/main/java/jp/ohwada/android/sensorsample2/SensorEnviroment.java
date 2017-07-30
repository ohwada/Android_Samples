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
import android.widget.Toast;

import java.util.List;


/**
 * SensorEnviroment
 */	 
public class SensorEnviroment {
	
	     	// debug
    	private final static String TAG_SUB = "SensorEnviroment";   
    	
	private final static String LF = "\n";
	
  	private Context mContext;
  	
  		private SensorCommon 	mSensorTemperature;     
  	  private SensorCommon mSensorHumidity;      
       private SensorCommon mSensorPressure; 
       
 /**
 * === constractor ====
 */	       
public SensorEnviroment( Context context ) {
	mContext = context;
mSensorTemperature = new SensorCommon( context, Sensor.TYPE_AMBIENT_TEMPERATURE,  "Temperature" );
mSensorHumidity = new SensorCommon( context, Sensor.TYPE_RELATIVE_HUMIDITY, "Humidity" );
mSensorPressure = new SensorCommon( context,  Sensor.TYPE_PRESSURE, "Pressure" );
} // 	constractor

 
 
 
 
     /**
     * registerAll
     */   
       public String registerAll() {
          log_d("registerAll"); 
        boolean ret1 = mSensorTemperature.registerListener();
                 boolean ret2 = mSensorHumidity.registerListener();
                 boolean ret3 = mSensorPressure.registerListener();
                 String msg = "";
                                  String str = "";
                 if( ret1 ) {
                       msg += "Temperature: " + mSensorTemperature.getSensorInfo() + LF;                    
                 } else {
                   str =  " No Temperature Sensor";
                                          msg += str + LF;
                    log_d( str );
                    toast_short( str );
                 } // if
                if( ret2 ) {
                       msg += "Humidity: " + mSensorHumidity.getSensorInfo() + LF;                    
                 } else {
                                       str =  " No Humidity Sensor";
                                          msg += str + LF;
                                 log_d( str );
                    toast_short( str );
                 } // if
               if( ret3 ) {
                       msg += "Pressure: " + mSensorPressure.getSensorInfo() + LF;                    
                 } else {
                         str =  " No Pressure Sensor";
                                          msg += str + LF;
                                 log_d( str );
                    toast_short( str );
                 } // if
           return msg;
    } // registerAll




              

              



     /**
     * unregisterAll
     */
public void unregisterAll() {
                          	log_d("unregisterAll"); 
        mSensorTemperature.unregisterListener();
                mSensorHumidity.unregisterListener();
                mSensorPressure.unregisterListener();

} // unregisterAll


    /**
     * getValues
     */
    public String getValues() {
         boolean isTemperatureListening = 	 mSensorTemperature.isListening();
          float temperature = mSensorTemperature.getValue() ;
                   boolean isHumidityListening = 	 mSensorHumidity.isListening();
  float humidity =  mSensorHumidity.getValue() ;
                     boolean isPressureListening = 	 mSensorPressure.isListening();
  	float pressure = mSensorPressure.getValue() ;
  	String msg = "";  	
  	if( isTemperatureListening ) {
  	msg += "Temperature: " + temperature  + LF;
  	} else {
  	  msg += "Temperature: ---"  + LF;
  	 } // if
  	   	if( isHumidityListening ) {
  	msg += "Humidity: " + humidity + LF;
  	} else {
  	msg += "Humidity: ---" + LF;  	  
  	} // if
  	  	   	if(  isPressureListening ) {
  	  	msg += "Pressure: " + pressure + LF;
  	  	} else {
   	  	msg += "Pressure: ---"  + LF; 	  	    
  	  	} // if
        return msg;
  	  	} // 	 getValues 	
  	  	
  	  	
	/**
	 * toast short
	 */ 
	private void toast_short( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

  	
  	
  	  	
    /**
     * log_d
     */
    private void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d


} // class SensorEnviroment
