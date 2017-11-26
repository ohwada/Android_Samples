/**
 * line chart 
 * 2017-11-01 K.OHWADA
 */

package jp.ohwada.android.linecharttimesample3;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Calendar;
import java.util.Date;

/**
 * class MainActivity
 */    
public class MainActivity extends AppCompatActivity  {
    
 MyLineChartTime mLineChartTime;
 
/**
 * === onCreate ===
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LineChart chart = (LineChart) findViewById(R.id.chart);
       
         mLineChartTime = new MyLineChartTime( chart );
         
mLineChartTime.setLeftAxisMinimum(-110f);
 mLineChartTime.setLeftAxisMaximum(110f);
 mLineChartTime.setXAxisLabelsToSkip( 240 );
  mLineChartTime.setDateFormat( "dd HH" );  
      
        setData();
        
    } // onCreate

/**
 * setData
 */
    private void readData() {
filename = "sakura_api_2016-0708.json";
}

/**
 * setData
 */
    private void setData() {
    
    int N = 1000;
    
          
          // now
          Date date_now = new Date();          
           Calendar cal = Calendar.getInstance();
        cal.setTime(date_now); 
        
        double yydiv = (2* Math.PI) /  N;
double yy = 0;
float y = 0f;

    for( int i=0; i<N; i++ ) {
        
            // one minute 
        cal.add(Calendar.MINUTE, 1);
        Date date = cal.getTime();

            yy += yydiv;
            y = (float) ( 100*Math.sin(yy) );

        mLineChartTime.addData( date, y );
    } // for
      
    } // setData


   
} // MainActivity
