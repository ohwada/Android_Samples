/**
 * Line Chart with Time Axis
 * 2018-01-01 K.OHWADA
 */

package jp.ohwada.android.charting.data;


import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.data.Entry;


/**
 * class TimeEntry
 */
public class TimeEntry extends Entry  {


    private long time = 0;


/**
 * === constractor ===
 */
    public TimeEntry() {
        super();
    } // TimeEntry


/**
 * === constractor ===
 */
    public TimeEntry(long t, float y) {
        super();
        setY(y);
        this.time = t;
    } // TimeEntry


/**
 * getTime
 */
     public long getTime() {
        return time;
    }


/**
 * setTime
 */
    public void setTime(long t) {
        this.time = t;
    }


} // class TimeEntry
