/**
 * content observer sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.contentobserversample1;



import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import java.util.TimeZone;


/**
 * SystemSettingsContentObserver
 */ 
public class SystemSettingsContentObserver  {
    
  	// debug
			private final static String TAG_SUB = "MyContentObserver";
			
			  
    private Context mContext;
    
      private ContentResolver mContentResolver;
     
     private InnerContentObserver mContentObserver;
     
    



  
        // callback 
    private OnChangedListener mListener;  

    /*
     * callback interface
     */    
    public interface OnChangedListener {
        public void  onChange( boolean selfChange );
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
    public  SystemSettingsContentObserver(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    } // constractor



/**
 * registerContentObserver
 */ 
     public void registerContentObserver() {
             log_d("registerContentObserver");   
        mContentObserver = new InnerContentObserver();
        
        mContentResolver.registerContentObserver(
                Settings.System.CONTENT_URI, true, mContentObserver );
                
        } // registerContentObserver
        
   
        
/**
 * unregisterContentObserver
 */ 
    public void unregisterContentObserver() {
                     log_d("unregisterContentObserver");  
        mContentResolver.unregisterContentObserver( mContentObserver );
    } // unregisterContentObserver
    
    





     

    
        /*
     * InnerContentObserver
     */ 
    private class InnerContentObserver extends ContentObserver {
        
        public InnerContentObserver() {
            super(new Handler());
        } // InnerContentObserver

        @Override
        public void onChange( boolean selfChange ) {
            log_d("onChange");
            notifyChange(  selfChange );
        } // onChange
    } // class InnerContentObserver





    
    
    

 
    
     /**
     * notifyChange
     */
    private void notifyChange( boolean selfChange ) {
        if ( mListener != null ) {
            mListener.onChange( selfChange );
        } // if
    } // notifyChange   
    

    
        /**
     * log_d
     */
    private void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d	
    
    
  } // class MyBroadcastReceiver  