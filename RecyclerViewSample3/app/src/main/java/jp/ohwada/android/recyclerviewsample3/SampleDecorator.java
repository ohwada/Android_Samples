/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * class SampleDecorator
 */
public class SampleDecorator extends RecyclerView.ItemDecoration {

    	// debug
    	private final static String TAG_SUB = "SampleAdapter";

    private static final int DIVIDER_HEIGHT = 1;
    private static final int DIVIDER_COLOR = Color.BLUE;

    // divider
    private Paint mPaint;

/**
 * constractor
 */
    public SampleDecorator() {
    log_d("SampleDecorator");
        mPaint = new Paint();
        mPaint.setColor(DIVIDER_COLOR);
    } // constractor


/**
 * == onDrawOver ==
 * Draw any appropriate decorations into the Canvas supplied to the RecyclerView. Any content drawn by this method will be drawn after the item views are drawn and will thus appear over the views.
 */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    log_d(" onDrawOver");
        int count = parent.getChildCount();
        int width = parent.getWidth();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            int bottom = child.getBottom();
            c.drawRect(0, bottom, width, bottom + DIVIDER_HEIGHT, mPaint);
        }
    } // onDrawOver

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class SampleDecorator

