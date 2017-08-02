/**
 * analog clock sample
 * 2017-07-01 K.OHWADA 
 */
 
 package jp.ohwada.android.analogclocksample;



import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import java.util.TimeZone;


/**
 * MyBroadcastReceiver
 */ 
public class MyBroadcastReceiver  {
    
  	// debug
			private final static String TAG_SUB = "MyBroadcastReceiver";
			
			  
    private Context mContext;
    










    private final Handler mHandler = new Handler();
    
    
    
        // callback 
    private OnChangedListener mListener;  

    /*
     * callback interface
     */    
    public interface OnChangedListener {
        public void  onTimeTick();
         public void  onTimeChanged();
        public void  onTimezoneChanged( String tz );
    }  // nterface
    
 
           
        /*
     * callback
     */ 
    public void setOnChangedListener( OnChangedListener listener ) {
        mListener = listener;
    } // etOnChangedListener
    
    
    
/**
 * ==== constractor ====
 */     
    public  MyBroadcastReceiver(Context context) {
        mContext = context;
    } // constractor



/**
 * registerReceiver
 */ 
     public void registerReceiver() {

            IntentFilter filter = new IntentFilter();
            // Standard Broadcast Actions
             // The current time has changed. Sent every minute. 
            filter.addAction(Intent.ACTION_TIME_TICK);
            // The time was set
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            // The timezone has changed
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            mContext.registerReceiver(mBroadcastReceiver, filter, null, mHandler);
        } // registerReceiver
        
        
/**
 * unregisterReceiver
 */ 
    public void unregisterReceiver() {
            mContext.unregisterReceiver(mBroadcastReceiver);
    } // unregisterReceiver
    
    






                




    
    

        
        











     


    /*
     * BroadcastReceiver
     */ 
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            String action = intent.getAction();
            log_d(action);

            if (Intent.ACTION_TIME_TICK.equals(action)) {
                notifyTimeTick();
            } else if (Intent.ACTION_TIME_CHANGED.equals(action)) {
                notifyTimeChanged();
            } else if (Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                String tz = intent.getStringExtra("time-zone");
                notifyTimezoneChanged(tz);
            } //        if
        } // onReceive
    }; // BroadcastReceiver
    




    /**
     * notifyTimeTick
     */
    private void notifyTimeTick() {
        if ( mListener != null ) {
            mListener.onTimeTick();
        } // if
    } // notifyTimeTick
    
    
    
    /**
     * notifyTimeChanged
     */
    private void notifyTimeChanged() {
        if ( mListener != null ) {
            mListener.onTimeChanged();
        } // if
    } // notifyTimeChanged   
 
 
    
     /**
     * notifyTimezoneChanged
     */
    private void notifyTimezoneChanged( String tz ) {
        if ( mListener != null ) {
            mListener.onTimezoneChanged( tz );
        } // if
    } // notifyTimezoneChanged    
    

    
        /**
     * log_d
     */
    private void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d	
    
    
  } // class MyBroadcastReceiver  