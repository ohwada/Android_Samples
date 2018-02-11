/**
 * Line Chart with Time Axis
 * 2018-01-01 K.OHWADA
 */

package jp.ohwada.android.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;


/**
 * class TimeXAxisRendere
 */
public class TimeXAxisRenderer extends XAxisRenderer {

  // debug
	private final static boolean D = true;
    	private final static String TAG = "chart";
    	private final static String TAG_SUB = "TimeXAxisRenderer";

    private final static String DATE_FORMAT = "dd MMM HH:mm:ss";

    private long[] mTimes = new long[]{};

  private SimpleDateFormat mSimpleDateFormat;


/**
 * === constractor ===
 */
    public TimeXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
        setAxisDateFortmat( DATE_FORMAT ); 
    } //TimeXAxisRendere


    /**
 * setAxisDateFortmat
 */
public void setAxisDateFortmat( String  format ) {
    log_d("setAxisDateFortmat");
    mSimpleDateFormat = new SimpleDateFormat(format);
} // setXAxisValueFormat


    /**
 * computeTimeAxis
 */
    public void computeTimeAxis(float xmin, float xmax,   long tmin, long tmax, boolean inverted ) {

        log_d( "computeTimeAxis" );
        computeAxis( xmin,  xmax,  inverted);
      int labelCount = mXAxis.getLabelCount();
           mTimes = new long[labelCount];
        computeTimeAxisValues( tmin, tmax, labelCount );

    } // computeTimeAxis



    /**
 * computeTimeAxisValues
 */
    private void computeTimeAxisValues( long tmin, long tmax, int labelCount ) {

    String msg = "computeTimeAxisValues: tmin = " + tmin +", tmax = " + tmax + ", labelCount = " + labelCount ;
    log_d( msg );

             long interval = 0;

        if ( labelCount != 1 ) {
             interval = (long) ( Math.abs(tmax - tmin) / (labelCount - 1) );
        } // if

        msg =  "interval = " +  interval;
        log_d( msg );

            long v = tmin;

            for (int i = 0; i < labelCount; i++) {
                mTimes[i] = v;
                v += interval;
                msg = "i = " + i + ", v = " + v;
             log_d( msg );
            } // for

    } // computeTimeAxisValues


    /**
 * computeTimeAxisValues
 */
     public void renderTimeAxisLabels(Canvas c) {
        log_d( "renderTimeAxisLabels" );
        super.renderAxisLabels( c);
    } //renderAxisLabels


    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
     @Override
     protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        log_d( "drawLabels" );

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        // drawLabel
        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            } // if
        }

        mTrans.pointValuesToPixel(positions);

        // drawLabel
        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (mViewPortHandler.isInBoundsX(x)) {

                // String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);
               String label = getFormattedLabel(  i );

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);


                        if (width > mViewPortHandler.offsetRight() * 2
                                && x + width > mViewPortHandler.getChartWidth())
                            x -= width / 2;
                            // if width

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        x += width / 2;
                    } // if avoid clipping of the last

                } // if avoid clipping of the last

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);

            }
        } // fordrawLabel

    } // drawLabels


    /**
 * computeTimeAxisValues
 */
  private String getFormattedLabel( int i ) {
    String msg  = " getFormattedLabel( int: i = " + i;
        log_d( msg );
    String label = "";
    int index = i / 2;
    int len = mTimes.length;

    if (( index >= 0)&&( index < len )) {
    long time = mTimes[index];
    label = mSimpleDateFormat.format(new Date(time));
    msg = " index = " + index + " , time = " + time + " , label = " + label;
    log_d( msg );
    } //if

 return  label;

    } // getFormattedLabel

 
 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


}  // class TimeXAxisRenderer
