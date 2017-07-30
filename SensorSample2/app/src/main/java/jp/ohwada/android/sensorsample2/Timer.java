/**
 * tsensor sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.sensorsample2;


import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/** 
 * Timer
 */	
public class Timer  {
	
	// debug
			private final static String TAG_SUB = "Timer";
			
	// 1sec
    private final static int TIMER_INTERVAL = 1000;

       private Context mContext;
       
    /** timer */
    private Runnable timerRunnable;
    private Handler timerHandler;
    private boolean isTimerRunning = false;
  
  
        // callback 
    private OnChangedListener mListener;  

    /*
     * callback interface
     */    
    public interface OnChangedListener {
        public void  onChangeTimer();
    }   
    
    
        /*
     * callback
     */ 
    public void setOnChangedListener( OnChangedListener listener ) {
        mListener = listener;
    }
       
          
    /** 
      * === constractor ===
      * @ param Context contex
      */	        
	public Timer( Context context ) {
		mContext = context;
	} // constractor
		


				

// --- Timer ---	
	/**
	 * start Timer
	 */
	protected void start() {
		log_d( "start" );
		isTimerRunning = true;
		 initTimer();
	}



	/**
	 * stopTimer
	 */
	public void stop() {
		log_d( "stop" );
		isTimerRunning = false;
	}



	/**
	 *  initTimer
	 */   
    private void initTimer() {
    	// nothing , if aleardy
    	if ( timerHandler != null ) return;
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
                public void run() {
                	// nothing , if timer not running
                    if ( ! isTimerRunning ) return;
                    updateTimer();
                    // call myself, after TIMER_INTERVAL
                	long now = SystemClock.uptimeMillis();
					long next = now + ( TIMER_INTERVAL - now % TIMER_INTERVAL );
                    timerHandler.postAtTime( timerRunnable, next );
                }
            };
        timerRunnable.run();
    } //  initTimer
   
   
   
   


	/**
	 * updateTimer
	 */ 
	private synchronized void updateTimer() {
		    log_d("updateTimer");
		notifyChangeTimer();
    }
	
	
	
	
	
    /**
     * notifyChangeTimer
     */
    private void notifyChangeTimer() {
        if ( mListener != null ) {
            mListener.onChangeTimer();
        }
    } // notifyChangeTimer
    
    
    /**
     * log_d
     */
    protected void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d	

		
} // class Timer