/**
 * Line Chart with Time Axis
 * 2018-01-01 K.OHWADA
 */

package jp.ohwada.android.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;

import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.renderer.LineChartRenderer;


import  jp.ohwada.android.charting.data.TimeEntry;
import  jp.ohwada.android.charting.data.LineTimeDataSet;
import  jp.ohwada.android.charting.data.LineTimeData;
import jp.ohwada.android.charting.renderer.TimeXAxisRenderer;


/**
 * class LineTimeChart
 */
public class LineTimeChart extends LineChart {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "chart";
    	private final static String TAG_SUB = "LineTimeChart";

    private TimeXAxisRenderer mTimeXAxisRenderer;


    // procDraw
    // for performance tracking
    private long totalTime = 0;
    private long drawCycles = 0;


/**
 * === constrctor ===
 */
    public LineTimeChart(Context context) {
        super(context);
        initTimeChart();
    } // ineTimeChart


/**
 * === constrctor ===
 */
    public LineTimeChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTimeChart();
    } // ineTimeChart


/**
 * === constrctor ===
 */
    public LineTimeChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTimeChart();
    } // ineTimeChart

/**
 * initTimeChart
 */
    private void initTimeChart() {

        mTimeXAxisRenderer = new TimeXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);

    } // initTimeChart


/**
 * initTimeChart
 */
    public void setXAxisDateFortmat ( String format ) {
        mTimeXAxisRenderer.setAxisDateFortmat (  format );
    } // setTimeAxisValueFortmat


/**
 * setLineTimeDataset
 */
    public void setLineTimeDataSet ( LineTimeDataSet tset ) {

    LineDataSet set = tset.toLineDataSet();
          LineData data = new LineData(set);
    setData( data );

    long tmin = tset.getTimeMin();
    long tmax = tset.getTimeMax();
    boolean  inverted = false;
            mTimeXAxisRenderer.computeTimeAxis( mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, tmin, tmax,  inverted );

    } // setLineTimeDataSet


/**
 * setLineTimeData
 */
    public void setLineTimeData ( LineTimeData tdata ) {

    LineData data = tdata.toLineData();
    setData( data );

    long tmin = tdata.getTimeMin();
    long tmax = tdata.getTimeMax();
    boolean  inverted = false;
            mTimeXAxisRenderer.computeTimeAxis( mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, tmin, tmax,  inverted );

    } // setLineTimeData


/**
 * onDraw
 */
    @Override
    protected void onDraw(Canvas canvas) {
        log_d("onDraw");
        // inhibit BarLineChartBase#onDraw
        // supper.onDraw(canvas);
        procDraw(canvas);
    } // onDraw


/**
 * procDraw
 * original BarLineChartBase#onDraw
 */
    private void procDraw(Canvas canvas) {

        log_d("procDraw");

        if (mData == null)
            return;

        long starttime = System.currentTimeMillis();

        // execute all drawing commands
        drawGridBackground(canvas);

      if (mAutoScaleMinMaxEnabled) {
            autoScale();
        } // if

        if (mAxisLeft.isEnabled()) {
            mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum, mAxisLeft.isInverted());
        } // if

        if (mAxisRight.isEnabled()) {
            mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum, mAxisRight.isInverted());
        } // if

        if (mXAxis.isEnabled()) {
            mTimeXAxisRenderer.computeAxis(mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, false);
    } // if

  mTimeXAxisRenderer.renderAxisLine(canvas);
        mAxisRendererLeft.renderAxisLine(canvas);
        mAxisRendererRight.renderAxisLine(canvas);

        mTimeXAxisRenderer.renderGridLines(canvas);
        mAxisRendererLeft.renderGridLines(canvas);
        mAxisRendererRight.renderGridLines(canvas);

        if (mXAxis.isEnabled() && mXAxis.isDrawLimitLinesBehindDataEnabled()) {
            mTimeXAxisRenderer.renderLimitLines(canvas);
        } // if

        if (mAxisLeft.isEnabled() && mAxisLeft.isDrawLimitLinesBehindDataEnabled()) {
            mAxisRendererLeft.renderLimitLines(canvas);
        } // if

        if (mAxisRight.isEnabled() && mAxisRight.isDrawLimitLinesBehindDataEnabled()) {
            mAxisRendererRight.renderLimitLines(canvas);
        } // if

        // make sure the data cannot be drawn outside the content-rect
        int clipRestoreCount = canvas.save();
        canvas.clipRect(mViewPortHandler.getContentRect());

        mRenderer.drawData(canvas);

        // if highlighting is enabled
        if (valuesToHighlight()) {
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);
        } //if

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        mRenderer.drawExtras(canvas);

        if (mXAxis.isEnabled() && !mXAxis.isDrawLimitLinesBehindDataEnabled()) {
            mTimeXAxisRenderer.renderLimitLines(canvas);
        } // if

        if (mAxisLeft.isEnabled() && !mAxisLeft.isDrawLimitLinesBehindDataEnabled()) {
            mAxisRendererLeft.renderLimitLines(canvas);
        } // if

        if (mAxisRight.isEnabled() && !mAxisRight.isDrawLimitLinesBehindDataEnabled()) {
            mAxisRendererRight.renderLimitLines(canvas);
        } // if

       mTimeXAxisRenderer.renderTimeAxisLabels(canvas);
       // mTimeXAxisRenderer.renderAxisLabels(canvas);

        mAxisRendererLeft.renderAxisLabels(canvas);
        mAxisRendererRight.renderAxisLabels(canvas);

        if (isClipValuesToContentEnabled()) {
            clipRestoreCount = canvas.save();
            canvas.clipRect(mViewPortHandler.getContentRect());

            mRenderer.drawValues(canvas);

            canvas.restoreToCount(clipRestoreCount);
        } else {
            mRenderer.drawValues(canvas);
        } // if

        mLegendRenderer.renderLegend(canvas);

        drawDescription(canvas);

        drawMarkers(canvas);

        if (mLogEnabled) {
            long drawtime = (System.currentTimeMillis() - starttime);
            totalTime += drawtime;
            drawCycles += 1;
            long average = totalTime / drawCycles;
            Log.i(LOG_TAG, "Drawtime: " + drawtime + " ms, average: " + average + " ms, cycles: "
                    + drawCycles);
        } // if

    } // procDraw



 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d




} // class LineTimeChart
