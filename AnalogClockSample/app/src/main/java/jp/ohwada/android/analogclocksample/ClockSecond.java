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
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import java.util.TimeZone;

/**
 * class ClockSecond
 */
public class ClockSecond  extends ClockBase{
    


    
    /**
 * ==== constractor ====
 */
    public ClockSecond(Context context) {
       super(context);
    } // constractor
  
    
    /**
 * ==== constractor ====
 */    
    public ClockSecond(Context context, AttributeSet attrs,
                       int defStyle) {
                                            super( context, attrs,
                      defStyle );
                       initHand( context, attrs,
                      defStyle ) ;
      } // constractor
      
      
  
 /**
 * initHand
 */  
   private void  initHand( Context context, AttributeSet attrs,
                       int defStyle ) {       
        
        TypedArray a = getTypedArray( context, attrs,
                      defStyle );
                           
        mHand = a.getDrawable( R.styleable.AnalogClock_hand_second );
        
        if (mHand == null) {         
           mHand = getResDrawable( R.drawable.clock_hand_second );
        } // if
                       

    } // initHand
      
         

/**
 * draw
 */ 
    public void draw( Canvas canvas, int x, int y, boolean is_changed ) {
    drawHand( canvas,x, y,  S60, is_changed );

    }// draw


    
} // class ClockSecond

