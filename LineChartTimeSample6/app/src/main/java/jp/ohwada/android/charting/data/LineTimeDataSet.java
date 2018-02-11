/**
 * Line Chart with Time Axis
 * 2018-01-01 K.OHWADA
 */

package jp.ohwada.android.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;


/**
 * class LineTimeDataSet
 */
public class LineTimeDataSet extends LineDataSet {

      // debug
	private final static boolean D = true;
    	private final static String TAG = "chart";
    	private final static String TAG_SUB = "LineTimeDataSet";

    // Coefficient to conver long type to float type
    private final static double X_RANGE = 1000L;

    /**
     * the entries that this DataSet represents / holds together
     */
     private List<TimeEntry> mTimeEntries = new ArrayList<TimeEntry>();

    private long mTMax = -Long.MAX_VALUE;
    private long mTMin = Long.MAX_VALUE;


/**
 * === constractor ===
 */
    public LineTimeDataSet(List<TimeEntry> values, String label) {
    super(null, label);
    mTimeEntries = values;
    } // LineTimeDataSet


/**
 * toLineDataSet
 */
    public LineDataSet toLineDataSet() {

log_d( "toLineDataSet" );

        calcMinMaxTime();
        long tmin = getTimeMin(); 
        long tmax = getTimeMax(); 
     double xscale = (double)( X_RANGE / (tmax - tmin ) ) ;
    
        List<Entry> values = new ArrayList<Entry>();

        for ( TimeEntry e: mTimeEntries ) {
            
        long t = e.getTime();
        float x = (float)( xscale * ( t - tmin ) );
        float y = e.getY();
            values.add( new Entry(x,y) );

        } // for

        LineDataSet set = new LineDataSet(values, getLabel() );
            set.setAxisDependency( getAxisDependency() );
         set.setColor( getColor() );
         set.setValueTextColor( getValueTextColor() );
         set.setLineWidth( getLineWidth() );
         set.setDrawCircles( isDrawCirclesEnabled() );
         set.setDrawValues( isDrawValuesEnabled() );
         set.setFillAlpha( getFillAlpha() );
         set.setFillColor( getFillColor() );
         set.setHighLightColor( getHighLightColor() );
         set.setDrawCircleHole( isDrawCircleHoleEnabled() );
       set.setValueTextSize( getValueTextSize() );
        return set;

    } // toLineDataSet


/**
 * calcMinMaxTime
 */
    public void calcMinMaxTime() {

log_d( "calcMinMaxTime" );

        if (mTimeEntries == null || mTimeEntries.isEmpty())
            return;

    mTMax = -Long.MAX_VALUE;
    mTMin = Long.MAX_VALUE;

        for ( TimeEntry e : mTimeEntries ) {

            if (e.getTime() < mTMin) {
                mTMin = e.getTime();
            }

            if (e.getTime() > mTMax) {
                mTMax = e.getTime();
            }

        } // for


    } // calcMinMaxTime


/**
 * getTimeMin
 */
     public long getTimeMin() {
        return mTMin;
    }


/**
 * getTimeMax
 */
     public long getTimeMax() {
        return mTMax;
    }


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class LineTimeDataSet
