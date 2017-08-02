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
 * class ClockDial
 */
public class ClockDial extends ClockBase {
  
    private Time mCalendar;

    private Drawable mDial;

    private int mDialWidth;
    private int mDialHeight;

    private boolean isAttached;

    private boolean isChanged;
    
    
/**
 * ==== constractor ====
 */
    public  ClockDial(Context context) {
       super( context );
    } // constractor




/**
 * ==== constractor ====
 */

    public  ClockDial(Context context, AttributeSet attrs,
                       int defStyle) {
                        super( context, attrs,
                      defStyle );
                       initDial( context, attrs,
                       defStyle ) ;
    } // constractor
    
 
        
/**
 * ntDial
 */         
private void initDial(Context context, AttributeSet attrs,
                       int defStyle) {     
        
        TypedArray a = getTypedArray( context, attrs,
                      defStyle );
                           
       mDial = a.getDrawable( R.styleable.AnalogClock_dial );
                 if (mDial == null) {
           mDial = getResDrawable( R.drawable.clock_dial );
        } // if
        
                 
        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
        
    } // intDial
   
    
    
/**
 * getDialWidth
 */ 
public int getDialWidth() {
return mDialWidth;
} // getDialWidth



/**
 * getDialHeight
 */ 
public int getDialHeight() {
return mDialHeight;
} // getDialHeight




/**
 * draw
 */ 
    public void draw( Canvas canvas, int x, int y, 
    float availableWidth, float availableHeight, boolean is_changed ) {

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        if (availableWidth < w || availableHeight < h) {
            float scale = Math.min((float) availableWidth / (float) w,
                                   (float) availableHeight / (float) h);
                                                               
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if ( is_changed ) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

    } // // draw


    

} // class ClockDial

