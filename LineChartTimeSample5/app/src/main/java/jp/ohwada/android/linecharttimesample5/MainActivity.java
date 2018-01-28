/**
 * Line Chart Time of One Second Interval Data
 * 2017-12-01 K.OHWADA
 */

package jp.ohwada.android.linecharttimesample5;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.util.Log;

/**
 * class MainActivity
 */
public class MainActivity extends FragmentActivity {

	private final static boolean D = true;
    private final static String TAG = "chart";
    private final static String TAG_SUB = "MainActivity";

    private MyLineChartTime mChart;

    private final static long ONE_SEC = 1000L;

    private final static String DATETIME_FORMAT = "HH:mm:ss";

/**
 * === onCreate ===
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        LineChart chart = (LineChart) findViewById(R.id.chart1);

        mChart = new MyLineChartTime( chart);
    
         Typeface tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
       Typeface tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
          
        mChart.setLeftAxisMinimum( -110 );
        mChart.setLeftAxisMaximum( 110 );

         mChart.setXAxiTypeface( tfRegular );    
         mChart.setLeftAxisTypeface( tfLight );

        // mChart.setXAxisValueFormatter( time_start, TIME_SCALE, DATETIME_FORMAT );
        mChart.setXAxisValueFormat( DATETIME_FORMAT );

        setData();  
      
        
} // onCreate



/**
 * onCreateOptionsMenu
 */
    @Override
   public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
   }

/**
 * onCreateOptionsMenu
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

            if( id == R.id.actionToggleValues) {
                    mChart.toggleValues();
                
        } else if( id == R.id.actionToggleHighlight) {
            mChart.toggleHighlight();
     
           } else if( id == R.id.actionToggleFilled) {
            mChart.toggleFilled();
           
           } else if( id == R.id.actionToggleCircles) {
                mChart.toggleCircles();
          
           } else if( id == R.id.actionToggleCubic) {
                mChart.toggleCubic();
            
          } else if( id == R.id.actionToggleStepped) {
            mChart.toggleStepped();
            
            } else if( id == R.id.actionTogglePinch) {
                mChart.togglePinch();
            
            } else if( id == R.id.actionToggleAutoScaleMinMax) {
                mChart.toggleAutoScaleMinMax();

            } else if( id == R.id.animateX) {
                mChart.animateX(3000);
            
            } else if( id == R.id.animateY) {
                mChart.animateY(3000);
            
              } else if( id == R.id.animateXY) {
                 mChart.animateXY(3000, 3000);

              } else if( id == R.id.actionSave) {
                 if ( mChart.saveToPath() ) {
                     Toast.makeText(getApplicationContext(), "Saving // SUCCESSFUL!",
                             Toast.LENGTH_SHORT).show();
                 } else {
                     Toast.makeText(getApplicationContext(), "Saving // FAILED!", Toast.LENGTH_SHORT)
                            .show();
                    mChart.saveToGallery();
                } // if saveToPath

             } // if id
                  
         return true;
         
     } // onCreateOptionsMenu
    


/**
 * setData
 */
    private void setData() {     

         int N = 100;

          // now
            long x = System.currentTimeMillis();

        double yydiv = (2* Math.PI) /  N;
        double yy = 0;
        float y = 0f;


        // increment by one seond
         for (int i = 0; i < N; i++) {

        // increment by  one seond
            x += ONE_SEC;

            yy += yydiv;
            y = (float) ( 100*Math.sin(yy) );
  
            mChart.addData( x, y );

        } // for
        
                  mChart.setData( );  
        
        } // -- setData --


         	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // MainActivity