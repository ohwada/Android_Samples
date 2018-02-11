/**
 * Line Chart with Time Axis
 * 2018-01-01 K.OHWADA
 */

package jp.ohwada.android.charting.data;

import android.util.Log;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;


/**
 * class LineTimeData
 */
public class LineTimeData extends LineData {

      // debug
	private final static boolean D = true;
    	private final static String TAG = "chart";
    	private final static String TAG_SUB = "LineTimeData";

     private List<LineTimeDataSet> mLineTimeDataSets = new ArrayList<LineTimeDataSet>();

    private long mTMax = -Long.MAX_VALUE;
    private long mTMin = Long.MAX_VALUE;


/**
 * === constractor ===
 */
    public LineTimeData() {
        super();
    } // LineTimeData


/**
 * === constractor ===
 */
    public LineTimeData(LineTimeDataSet dataSet) {
        super();
    mLineTimeDataSets.add(dataSet);
    } // LineTimeData


/**
 * toLineData
 */
    public LineData toLineData() {

    log_d( "toLineData" );

    LineTimeDataSet tset = getLineTimeDataSetByIndex(0);
    if (tset == null) {
        return null;
    }

    LineDataSet set = tset.toLineDataSet();
        mTMin = tset.getTimeMin();
        mTMax = tset.getTimeMax();
    LineData data = new LineData(set);
    // data do not have this eproperty, each dataset have
    // data.setValueTextColor( getValueColor() );
    // data.setValueTextSize( getValueTextSize() );
    return data;

} // toLineData


/**
 * getLineTimeDataSetByIndex
 */
    public LineTimeDataSet getLineTimeDataSetByIndex(int index) {

log_d( "getLineTimeDataSetByIndex" );
        if (mLineTimeDataSets == null || index < 0 || index >= mLineTimeDataSets.size()) {
            return null;
        }

        return mLineTimeDataSets.get(index);

    } // getLineTimeDataSetByIndex


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

} // class LineTimeData
