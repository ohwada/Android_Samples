/**
 * Line Chart Time of One Second Interval Data
* MPAndroidChart:v3.0.3  
 * 2017-12-01 K.OHWADA
 */

package jp.ohwada.android.linecharttimesample5;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.util.Log;

/**
 * class MyLineChartTime
 */
public class MyLineChartTime {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "chart";
    	private final static String TAG_SUB = "MyLineChartTime";

    // Coefficient to conver long type to float type
    private final static double X_RANGE = 1000L;

    private LineChart mChart;
private XAxis mXAxis;
private YAxis mLeftAxis;


  private ArrayList<Long> mXValues;
  private ArrayList<Float> mYValues;

  private SimpleDateFormat mXAxisDateFormat;
  private long mTtimeStart = 0;
    private double mTimeScale = 0;
 private String mXAxisValueFormat = "dd MMM HH:mm:ss";


    /**
 * === constructor ===
 */
public  MyLineChartTime( LineChart chart ) {
                
          mChart = chart;              

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

// XAxis
        mXAxis = mChart.getXAxis();
        
        mXAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

        mXAxis.setTextSize(10f);
        mXAxis.setTextColor(Color.WHITE);
        mXAxis.setDrawAxisLine(false);
        mXAxis.setDrawGridLines(true);
        mXAxis.setTextColor(Color.rgb(255, 192, 56));
        mXAxis.setCenterAxisLabels(true);
        mXAxis.setGranularity(1f); // one hour
        
// AxisLeft
        mLeftAxis = mChart.getAxisLeft();
        
        mLeftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);


        mLeftAxis.setTextColor(ColorTemplate.getHoloBlue());
        mLeftAxis.setDrawGridLines(true);
        mLeftAxis.setGranularityEnabled(true);        
        mLeftAxis.setTextColor(Color.rgb(255, 192, 56));

    // AxisRight
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

    // mValues = new ArrayList<Entry>();
     mXValues = new ArrayList<Long>();
     mYValues = new ArrayList<Float>();
    }

    /**
 * setXAxisValueFormat
 */
public void setXAxisValueFormat( String  datetime_format ) {
    mXAxisValueFormat = datetime_format ;
} // setXAxisValueFormat

    /**
 * setXAxisValueFormatter
 */
// public void setXAxisValueFormatter( long time_start, long time_scale, String  datetime_format ) {
public void setXAxisValueFormatter( long time_start, double time_scale, String  datetime_format ) {

    mTtimeStart = time_start;
    mTimeScale = time_scale;
    mXAxisDateFormat = new SimpleDateFormat( datetime_format );

    mXAxis.setValueFormatter( new IAxisValueFormatter() {


    
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                // long millis = mTtimeStart +  mTimeScale * (long)value;               
                long millis = mTtimeStart +  (long)( mTimeScale * (double)value );               
 
               return mXAxisDateFormat.format(new Date(millis));
            
    } // getFormattedValue

        }); // setXAxisValueFormatter

    } // setXAxisValueFormatter


    /**
 * setLeftAxisMinimum
 */
public void setLeftAxisMinimum(float min) {
    if ( mLeftAxis != null ) {
        mLeftAxis.setAxisMinimum( min );
    } // if
    
        } // setLeftAxisMinimum   
   
       /**
 * setLeftAxisMaximum
 */     
 public void setLeftAxisMaximum(float max) {
      mLeftAxis.setAxisMaximum( max );
}

       /**
 * setLeftAxisOffset
 */ 
 public void setLeftAxisOffset(float offset) {
        //mLeftAxis.setYOffset( offset );
}

       /**
 * setXAxiTypeface
 */
    public void setXAxiTypeface( Typeface tf ) {
        mXAxis.setTypeface(tf);
}
 
 
        /**
 * setLeftAxisTypeface
 */
     public void setLeftAxisTypeface( Typeface tf ) {
         mLeftAxis.setTypeface(tf);
    }
 
  
         /**
 * toggleValues
 */   
            public void toggleValues() {   
             List<ILineDataSet> sets = mChart.getData()
                      .getDataSets();

             for (ILineDataSet iSet : sets) {

                 LineDataSet set = (LineDataSet) iSet;
                 set.setDrawValues(!set.isDrawValuesEnabled());
             } // for
    
            mChart.invalidate();
            
           } // toggleValues
           

   
            /**
 * toggleHighlight
 */       
    public void toggleHighlight() {
            if (mChart.getData() != null) {
                 mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                 mChart.invalidate();
              } // for
              
        } // toggleHighlight


            /**
 * toggleFilled
 */            
        public void toggleFilled() {  
               List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

              for (ILineDataSet iSet : sets) {

                   LineDataSet set = (LineDataSet) iSet;
                if (set.isDrawFilledEnabled())
                      set.setDrawFilled(false);
                  else
                       set.setDrawFilled(true);
              
              } // for
              
                mChart.invalidate();
                
        } // oggleFilled
        
        
      
             /**
 * toggleCircles
 */                  
    public void toggleCircles() {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                       set.setDrawCircles(true);
                
                } // for
                mChart.invalidate();
                
    } // toggleCircles
    
    
                /**
 * toggleCubic
 */     
              public void toggleCubic() {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                   LineDataSet set = (LineDataSet) iSet;
                    if (set.getMode() == 
                     LineDataSet.Mode.CUBIC_BEZIER)
                        set.setMode(LineDataSet.Mode.LINEAR);
                    else
                       set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
               
                } // for
                
                mChart.invalidate();
            
            } // toggleCubic
            
            
                /**
 * toggleCubic
 */
    public void toggleStepped() {
               List<ILineDataSet> sets = mChart.getData()
                      .getDataSets();


                for (ILineDataSet iSet : sets) {

                  LineDataSet set = (LineDataSet) iSet;
                  
                   if (set.getMode() == 
                    LineDataSet.Mode.STEPPED)
                         set.setMode(LineDataSet.Mode.LINEAR);
                    else
                         set.setMode(LineDataSet.Mode.STEPPED);
               
               } // for
               
                mChart.invalidate();
    
    } // toggleStepped
    
    

                 /**
 * togglePinch
 */               
    public void togglePinch() {
                
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                 else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                
    } // togglePinch
    
    

                 /**
 * toggleAutoScaleMinMax
 */                 
    public void toggleAutoScaleMinMax() {
                
                mChart.setAutoScaleMinMaxEnabled(!
                 mChart.isAutoScaleMinMaxEnabled());
                 mChart.notifyDataSetChanged();
                 
        } // toggleAutoScaleMinMax
      
        
                   /**
 * animateX
 */       
        public void animateX( int x ) {
                 mChart.animateX(x);
                 
            } // animateX
            
             
                    /**
 * animateY
 */                
        public void animateY( int y ) {
                 mChart.animateY(y);
                 
            } // animateY
            
            
                    /**
 * animateXY
 */  
    public void animateXY( int x, int y ) {
                 mChart.animateXY( x, y );
                 
                 } // animateXY
                 
                 

                     /**
 * saveToPath
 */                
        public boolean saveToPath() {
            return mChart.saveToPath( ("title" +
                  System.currentTimeMillis() ), "" );
                
                } // saveToPath
                

                      /**
 * saveToGallery
 */               
                  public boolean saveToGallery() {           
                    return mChart.saveToGallery( ("title"+System.currentTimeMillis ()), 0 );
                
                } // saveToGallery
                

                      /**
 * procProgressChanged
 */   
    public void procProgressChanged(SeekBar seekBar, int progress, boolean fromUser ) {

    //        tvX.setText("" + (mSeekBarX.getProgress()));

        // setData(mSeekBarX.getProgress(), 50);

        // redraw
        // mChart.invalidate();

    } // procProgressChanged
    

 
                         /**
 * addData
 */    

    public void addData( long x, float y ) {
          mXValues.add(x);
          mYValues.add(y);
    
    } // addData
        
                                /**
 * setData
 */   
            public void setData() {
        
            // create a dataset and give it a type
                 LineDataSet set1 = new LineDataSet( getValues(), "DataSet 1");


                 set1.setAxisDependency(AxisDependency.LEFT);
         set1.setColor(ColorTemplate.getHoloBlue());
         set1.setValueTextColor(ColorTemplate.getHoloBlue());
         set1.setLineWidth(1.5f);
         set1.setDrawCircles(false);
         set1.setDrawValues(false);
         set1.setFillAlpha(65);
         set1.setFillColor(ColorTemplate.getHoloBlue());
         set1.setHighLightColor(Color.rgb(244, 117, 117));
         set1.setDrawCircleHole(false);

        // create a data object with the datasets
         LineData data = new LineData(set1);
         data.setValueTextColor(Color.WHITE);
         data.setValueTextSize(9f);

        // set data
         mChart.setData(data);
        
        } // setData

    private ArrayList<Entry> getValues() {

        ArrayList<Entry> values = new ArrayList<Entry>();

        long xmin = Long.MAX_VALUE;
        long xmax = -Long.MAX_VALUE;
        int xlen = mXValues.size();
        int ylen = mYValues.size();

        // calc min max
        for (int i=0; i<xlen; i++ ) {
            long x = mXValues.get(i);
            if (x < xmin ) {
                xmin = x;
            }

            if (x > xmax ) {
                xmax = x;
            }

        } // for

        double xscale = (double)( X_RANGE / (xmax - xmin ) ) ;
       
 // convert long type to float type
    for (int i=0; i<xlen; i++ ) {
        long xx = mXValues.get(i);
        float x = (float)( xscale * ( xx - xmin ) );
        if ( i<ylen) {
            float y = mYValues.get(i);
    log_d( xx + " " + x );
            values.add( new Entry(x,y) );
        } // if

    } // for

    setXAxisValueFormatter( xmin, 1/xscale, mXAxisValueFormat );

        return values;
    } // getValues

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // MyLineChartTime