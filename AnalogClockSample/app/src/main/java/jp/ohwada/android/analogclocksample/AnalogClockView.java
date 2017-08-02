/**
 * analog clock sample
 * 2017-07-01 K.OHWADA 
 */
 
 package jp.ohwada.android.analogclocksample;


// ======
// base android.widget.AnalogClock
// ======

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
 * class AnalogClockView
 */
@RemoteView
public class AnalogClockView extends View {
    
    private Time mCalendar;
    
    private MyBroadcastReceiver mBroadcastReceiver;
    
private ClockTimer mClockTimer;

    private ClockDial mClockDial;
    private ClockHour mClockHour;
        private ClockMinute mClockMinute;
         private ClockSecond mClockSecond;
         
                

// onMeasure
    private int mDialWidth = 0;
    private int mDialHeight = 0;



    private boolean isAttached = false;



    
    private boolean isChanged = false;
    
    
/**
 * ==== constractor ====
 */
    public AnalogClockView( Context context ) {
        this(context, null);
    } // constractor


/**
 * ==== constractor ====
 */
    public AnalogClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    } // constractor



/**
 * ==== constractor ====
 */
    public AnalogClockView(Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        
    mBroadcastReceiver  = new MyBroadcastReceiver( context );
         mBroadcastReceiver.setOnChangedListener(
            new MyBroadcastReceiver.OnChangedListener() {
            @Override
           public void  onTimeTick() {
				updateTime();
            }
             @Override
           public void  onTimeChanged() {
				updateTime();
            }
                        @Override
           public void  onTimezoneChanged( String tz ) {
				procTimezoneChanged( tz );
            }
                       
          } ); // setOnChangedListener
                 
                    mClockTimer  = new ClockTimer( context );
         mClockTimer.setOnChangedListener(
            new ClockTimer.OnChangedListener() {
            @Override
            public void onChangeTimer() {
				updateTime();
            }
            
          } );  // ClockTimer
          
                  mClockDial = new ClockDial(context, attrs, defStyle);
        mClockHour = new ClockHour(context, attrs, defStyle);
        mClockMinute = new ClockMinute(context, attrs, defStyle);
            mClockSecond = new ClockSecond(context, attrs, defStyle);
   mCalendar = new Time();
    mDialWidth = mClockDial.getDialWidth();
        mDialHeight = mClockDial.getDialHeight();
    } // constractor
    
 
 
 
/**
 * nitClock
 */
    private void initClock( Context context ) {
          mClockDial = new ClockDial( context );
        mClockHour = new ClockHour( context );
        mClockMinute = new ClockMinute( context );
    } // nitClock
    
    
    
     
        

        

/**
 * === onAttachedToWindow === 
 */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCalendar = new Time();
        updateTime();
        if (!isAttached) {
            isAttached = true;
            mBroadcastReceiver.registerReceiver();
            mClockTimer.start(); 
    } // if           
    } // onAttachedToWindow




/**
 * === onDetachedFromWindow === 
 */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if ( isAttached ) {
            mBroadcastReceiver.unregisterReceiver();
              mClockTimer.stop();
            isAttached = false;
        } // if
        
    } // onDetachedFromWindow
    
    


/**
 * === onMeasure === 
 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        } // if

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float )heightSize / (float) mDialHeight;
        } // if

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
                resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
    } // onMeasure




/**
 * === onSizeChanged === 
 */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isChanged = true;
    } // onSizeChanged


/**
 * === onDraw === 
 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean is_changed = isChanged;
        if (is_changed) {
            isChanged = false;
        } // if

        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();
        
        int x = availableWidth / 2;
        int y = availableHeight / 2;


    mClockDial.draw( canvas, x, y, 
  availableWidth,  availableHeight,  is_changed );

    mClockHour.draw( canvas, x, y, is_changed );

    mClockMinute.draw( canvas, x,  y, is_changed );
    
    mClockSecond.draw( canvas, x,  y, is_changed );

    } // onDraw


    
    
    
/**
 * updateTime
 */
    private void updateTime() {
        mCalendar.setToNow();

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        int Minutes = (int)(minute + second / 60.0f);
        int Hour = (int)(hour + Minutes / 60.0f);
        
        mClockHour.setTime( Hour );
        mClockMinute.setTime( Minutes );
        mClockSecond.setTime( second );
                        
        isChanged = true;
        invalidate();
        
    } // updateTime



/**
 * procTimezoneChanged
 */
private void procTimezoneChanged( String tz ) {
          mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
          updateTime();   
} // procTimezoneChanged
   
    
} // class AnalogClockView
