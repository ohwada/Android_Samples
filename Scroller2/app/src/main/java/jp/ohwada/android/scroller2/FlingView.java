/**
 * Scroller Sample
 * 2019-10-01 K.OHWADA
 * reference : http://gpsoft.dip.jp/hiki/?Android%E3%81%A7%E3%83%95%E3%83%AA%E3%83%83%E3%82%AF
 */
package jp.ohwada.android.scroller2;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * class FlingView
 */
public class FlingView extends LinearLayout {


    private static final int PAGES_NUM = 3;

    private int mPageWidth = 320;
    private int mMinFlingMove = 40;

    private GestureDetector mDetector;

    private Scroller mScroller;


/**
 * Constractor
 */
public FlingView(Context context) {
        super(context);
        initView(context);
}


/**
 * Constractor
 */
public FlingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
}


/**
 * Constractor
 */
public FlingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
}


/**
 * Constractor
 */
public FlingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
}


/**
 * initView
 */
private void initView(Context contex) {
        mDetector = new GestureDetector(contex, new GestureListener() );

        mScroller = new Scroller(contex, new DecelerateInterpolator());
}


/**
 * onTouchEvent
 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

// scroll automatically to the nearest page boundary.
// if touch up when scrolling to the middle of the page by dragging

        boolean b = mDetector.onTouchEvent(event);


        if ( event.getAction() == MotionEvent.ACTION_UP && !b ) {
            // touch up and not fling

            // current posision
            int currentX = getScrollX();

            // scroll posision
            int targetX = 0;

            if ( currentX % mPageWidth < mPageWidth / 2 ) {
                // choose Previous page　when curren posision is smaller than half
                targetX = currentX / mPageWidth * mPageWidth;
            } else {
                // choose  Next page when curren posision is larger than half
                targetX = (currentX / mPageWidth + 1) * mPageWidth;
                targetX = Math.min(targetX, mPageWidth * (PAGES_NUM - 1));
            }

            // scroll to the specified page
            mScroller.startScroll(currentX, 0, targetX - currentX, 0);//
            invalidate(); // redraw
            return true;
        }
        return b;

    }


/**
 * resetPosition
 */
    public void resetPosition() {

        // adjust View Metrics
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        mPageWidth = disp.getWidth();
        mMinFlingMove = mPageWidth / 8;

        // change View Size
        // the width of LinearLayout is 3 times the width of Display
        int newWidth = mPageWidth * PAGES_NUM ;
        int newHeight = getLayoutParams().height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(newWidth, newHeight);
        setLayoutParams(params);

        // scroll to Middle page
        scrollTo(mPageWidth, 0);

    }


/**
 * computeScrol
 */
    @Override
    public void computeScroll() {
        if ( mScroller.computeScrollOffset() ) {
            // when the scroller is finished.

            // get the current scroll position
            int currX = mScroller.getCurrX();

            // scroll the current position
            scrollTo(currX, 0);
        }
    }


/**
 * class GestureListener
 */
private class GestureListener extends GestureDetector.SimpleOnGestureListener {


/**
 * onScroll
 */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
            float distanceX, float distanceY) {
// follow the movement and scrol、when dragged

            // scroll the dragged distance
            scrollBy((int) distanceX, 0);

            return true;
    }


/**
 * onFling
 */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
        float velocityX, float velocityY) {

// scroll pages by flicking left and right
// scrolling stops at the page boundary.

        // current posision
        int currentX = getScrollX();

        // scroll posision
        int targetX = 0;

        if ( velocityX > 0 ) { 
            // when Flick to Right

            // when the current position is out of range
            if ( currentX <= 0 ) return false;

            targetX = currentX / mPageWidth * mPageWidth;
        } else { 
            // when Flick to Left

            // when the current position is out of range
            if ( currentX >= mPageWidth * (PAGES_NUM - 1) ) return false;

            targetX = mPageWidth * (currentX / mPageWidth + 1);
        }

        mScroller.startScroll(currentX, 0, targetX - currentX, 0);
        invalidate();// redraw
        return true;
    }


/**
 * onDown
 */
    @Override
    public boolean onDown(MotionEvent e) {

// stop scrolling
// if touch the screen while scrolling

        if ( !mScroller.isFinished() ) { 
            // stop scrolling while scrolling
            mScroller.forceFinished(true);
            return true;
        }
        return true;
    }


/**
 * onContextClick
 */
    @Override
    public boolean onContextClick(MotionEvent e) {
        return false;
    }


/**
 * onContextClick
 */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }


/**
 * onContextClick
 */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


/**
 * onSingleTapConfirmed
 */
    @Override
    public boolean	onSingleTapConfirmed(MotionEvent e) {
        return false;
    }


/**
 * onSingleTapUp
 */
    @Override
    public boolean	onSingleTapUp(MotionEvent e) {
        return false;
    }


/**
 * onLongPress
 */
    @Override
    public void onLongPress(MotionEvent e) {
        // nop
    }


/**
 * onShowPress
 */
    @Override
    public void	onShowPress(MotionEvent e) {
        // nop
    }


} // class GestureListener



} // class FlingView
