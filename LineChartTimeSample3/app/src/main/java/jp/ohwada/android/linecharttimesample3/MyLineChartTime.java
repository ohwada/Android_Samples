/**
 * line chart 
 * 2017-11-01 K.OHWADA
 */

package jp.ohwada.android.linecharttimesample3;

import android.graphics.Color;

import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * class MyLineChartTime
 */
 public class MyLineChartTime {
    
private static final int DATA_SET_INDEX = 0;

    private LineChart mChart;
    private XAxis mXAxis;
    private YAxis mLeftAxis;          
      private SimpleDateFormat mSimpleDateFormat;
               
    /**
 * === constructor ===
 */ 
 public  MyLineChartTime(LineChart chart )  {
        mChart = chart;
            initChart();

} // constructor

   /**
 * setLeftAxisMinimum
 */
public void setLeftAxisMinimum(float min) {
            mLeftAxis.setAxisMinValue(min);    
        } // setLeftAxisMinimum   
   
       /**
 * setLeftAxisMaximum
 */     
 public void setLeftAxisMaximum(float max) {
        mLeftAxis.setAxisMaxValue(max);
} // setLeftAxisMaximum

       /**
 * setXAxisLabelsToSkip
 */     
 public void setXAxisLabelsToSkip( int skip ) {
        mXAxis.setLabelsToSkip(skip);
} // setXAxisLabelsToSkip
     
       /**
 * setDateFormat
 */     
 public void setDateFormat( String format ) {
              mSimpleDateFormat  = new SimpleDateFormat(format);
} // setDateFormat


    /**
 * addData
 */        
          public void addData( Date date, double value ) {
        
         LineData data = mChart.getData();
        if  (data == null) {
             return;
        } // if

        LineDataSet set = data.getDataSetByIndex(DATA_SET_INDEX);
        if (set == null) {
            set = new LineDataSet(null, "Sample Data");
            set.setColor(Color.BLUE);
            set.setDrawValues(false);
            data.addDataSet(set);
        } // if

        // reqire MPAndroidChart:v2.1.6
        data.addXValue( mSimpleDateFormat.format(date) );

        data.addEntry(new Entry( (float) value, set.getEntryCount() ), DATA_SET_INDEX );
        
    // must notify when adding data
        mChart.notifyDataSetChanged();

        // mChart.setVisibleXRangeMaximum(60);

        // mChart.moveViewToX(data.getXValCount() - 61);     
    } // addData


    /**
 *  initChart
 */  
    private void initChart() {
        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

    // Legend
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

// X Axis 
        mXAxis = mChart.getXAxis();
        mXAxis.setTextColor(Color.BLACK);

// Left Axis 
        mLeftAxis = mChart.getAxisLeft();
        mLeftAxis.setTextColor(Color.BLACK);
        mLeftAxis.setStartAtZero(false);
        mLeftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    
    } // initChart


    
   
} // MyLineChartTime
