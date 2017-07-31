/**
 * broadcast receiver sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.broadcastreceiversample1;


import java.util.Calendar;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * DigitalClock
 */
public class DigitalClock  {

   	// debug
			private final static String TAG_SUB = "DigitalClock";
			
    private final static String FORMAT12 = "h:mm:ss aa";
    private final static String FORMAT24 = "k:mm:ss";
 
         private Context        mContext;   
         
      private Calendar mCalendar;  
      
      private boolean is24HourMode = false;
        private String mFormat = "";
               
/**
 * constractor
 */
    public DigitalClock( Context context ) {
        mContext = context;
                if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }   set24HourFormat( DateFormat.is24HourFormat( context ) );
    } // constractor


/**
 * set24HourFormat
 */
    public void set24HourFormat( boolean is_24_hour_mode ) {
        is24HourMode = is_24_hour_mode;
        if ( is24HourMode ) {
            mFormat = FORMAT24;
        } else {
            mFormat = FORMAT12;
        }
    } // set24HourFormat
    
    
/**
 * getFormatTime
 */
public CharSequence getFormatTime() {
                          mCalendar.setTimeInMillis( System.currentTimeMillis() );
    return DateFormat.format( mFormat, mCalendar );
} // getFormatTime



  
                            

                

        /**
     * log_d
     */
    protected void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d	
    
} // class DigitalClock


