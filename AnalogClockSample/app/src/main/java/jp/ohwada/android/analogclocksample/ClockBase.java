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
 * class ClockBase
 */
public class ClockBase  {
    
protected final static float R360 = 360.0f;
protected final static float S12 = 12.0f;
protected final static float S60 = 60.0f;

 protected Context  mContext;
 
 protected Resources mResources;
 
    protected Drawable mHand;

    protected float mTime = 0.0f;


/**
 * ==== constractor ====
 */
    public ClockBase( Context context ) {
       initClock( context );
    } // constractor
    
    
/**
 * ==== constractor ====
 */

    public  ClockBase(Context context, AttributeSet attrs,
                       int defStyle) {
                     initClock( context );
    } // constractor
    
    
    
    
            
/**
 *  initClock
 */
        protected void initClock( Context context ) {
        mContext = context;
               mResources  = context.getResources();
       } /// initClock    
       
    
/**
 * getTypedArray
 */
    protected TypedArray getTypedArray( Context context, AttributeSet attrs,
                       int defStyle ) {   
                return context.obtainStyledAttributes(
                     attrs, R.styleable.AnalogClock, defStyle, 0 );
            } // getTypedArray
                     
  
  
                                 
/**
 * getResDrawable
 */
    protected Drawable getResDrawable( int res_id ) {
           return mResources.getDrawable( res_id );
} // getResDrawable






/**
 * setTime
 */
public void setTime( int time ) {
    mTime = time;
} // setTime





/**
 * drawHand
 */ 
    protected void drawHand( Canvas canvas,int x, int y,  float step, boolean is_changed ) {

        canvas.save();
        canvas.rotate( R360 * (mTime / step ), x, y );
        final Drawable hand = mHand;
        
        if ( is_changed ) {
            int w = hand.getIntrinsicWidth();
            int h = hand.getIntrinsicHeight();
            hand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        
        hand.draw(canvas);
        canvas.restore();

    } // drawHand





} // class Clockbase
