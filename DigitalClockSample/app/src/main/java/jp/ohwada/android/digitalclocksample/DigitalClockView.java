/**
 * degital clock sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.digitalclocksample;


import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;
 
 // =====
 // base on
// android.googlesource.com/platform/frameworks/base/+/47fb191/core/java/android/widget/DigitalClock.java
// =====

/**
 * DigitalClockView
 */
public class DigitalClockView extends TextView {

   
    private SystemSettingsContentObserver mContentObserver;
    
     private ClockTimer  mClockTimer; 
    
    private DigitalClockText mDigitalClockText;

    
/**
 * constractor
 */
    public DigitalClockView(Context context) {
        super(context);
        initView(context);
    }


/**
 * constractor
 */
    public DigitalClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


/**
 * initView
 */
    private void initView( Context context ) {

            mContentObserver  = new SystemSettingsContentObserver( context ) ;
  
         mContentObserver.setOnChangedListener(
            new SystemSettingsContentObserver.OnChangedListener() {
                        @Override
           public void  onChange( boolean selfChange ) {
				procSystemSettingsChange();
            }                     
          } ); // setOnChangedListener
          


            mClockTimer  = new ClockTimer( context );
         mClockTimer.setOnChangedListener(
            new ClockTimer.OnChangedListener() {
            @Override
            public void onChangeTimer() {
				updateText();
            }
            
          } );  // ClockTimer
        
        
     mDigitalClockText = new DigitalClockText( context );         
    
    
        
    } // initView
    
    
    
    
/**
 * onAttachedToWindow
 */
    @Override
    protected void onAttachedToWindow() {
                super.onAttachedToWindow();
      mClockTimer.start();  
    } // onAttachedToWindow
    






/**
 * onDetachedFromWindow
 */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
              mClockTimer.stop(); 
    } // onDetachedFromWindow






    /**
     * procSystemSettingsChange
     */
private void procSystemSettingsChange() {
    mDigitalClockText.setSystemToFormat();
} // procSystemSettingsChange





    /**
     * updateText
     */
private void updateText() {
   setText( mDigitalClockText.getFormatTime() ); 
    invalidate();
} // updateText







    
} // class DigitalClock


