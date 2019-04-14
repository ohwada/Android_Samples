/**
 * Camera2 Sample
 * overlay on camera preview
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera25;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


 /**
 *  class MainActivity
  */
public class MainActivity extends PreviewActivity {

        // debug
   	private final static String TAG_SUB = "MainActivity";

    // output file
    private static final String FILE_PREFIX = "screenshot_";
    private static final String FILE_EXT = ".jpg";
    private static final int JPEG_QUALITY = 100;

	//View
	private OverlayView mOverlayView;
	
	private FrameLayout mContentView;

	//Sensor
	private SensorManager mSensorManager;
	
	
/**
 * createExtend
 */
@Override
protected void createExtend() {
        log_d("createExtend");
        mOverlayView = (OverlayView)findViewById(R.id.overlay);      
       mContentView = (FrameLayout) findViewById(android.R.id.content);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);   

} // createExtend


/**
 * resumeExtend
 */
@Override
protected void resumeExtend() {
        log_d("resumeExtend");
        // register sensor listener
    	final Sensor accele = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	final Sensor magnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    	mSensorManager.registerListener(mOverlayView, accele, SensorManager.SENSOR_DELAY_GAME);
    	mSensorManager.registerListener(mOverlayView, magnet, SensorManager.SENSOR_DELAY_GAME);
} //resumeExtend


/**
 * pauseExtend
 */
@Override
protected void pauseExtend() {
        log_d("pauseExtend");
        // release sensor listener
        mSensorManager.unregisterListener(mOverlayView);
} // pauseExtend


/**
 * onCreateOptionsMenu
 */
@Override
public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
}
  
  
/**
 * onOptionsItemSelected
 */
@Override
 public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_switch) {
                switchView();
                return true;
        } else if (id == R.id.action_save) {
        		saveScreenShot();
                return true;
        }
        return super.onOptionsItemSelected(item);
}

/**
 * switchView
 */
private void switchView() {
    if(mOverlayView.getVisibility() == View.VISIBLE){
                // hide view, if show
            	mOverlayView.setVisibility(View.GONE);
    }else{
                //show view, if hidden
            	mOverlayView.setVisibility(View.VISIBLE);
    }
}


/**
 * saveScreenShot
 */  
private void saveScreenShot(){

        File file =   getAppOutputFile(FILE_PREFIX, FILE_EXT);
        Bitmap bitmap = getScreenShot();
        boolean is_error = false;
    	OutputStream os = null;
    	try {
    				os = new FileOutputStream(file);
    				bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, os); 
    		} catch (Exception e){
                    is_error = true;
    				e.printStackTrace();
    		}

    		try {
    					if (os != null ) os.close();
    		} catch (Exception e){
    					e.printStackTrace();
    		}

     if(!is_error) {
        String msg = "saved " + file.toString();
        log_d(msg);
        toast_long(msg);
    }

}   
    


/**
 * getScreenShot
 */  
private Bitmap getScreenShot(){
  		mContentView.setDrawingCacheEnabled(true);
  		Bitmap bitmap = Bitmap.createBitmap(mContentView.getDrawingCache());
  	  return bitmap;
}


/**
 * write into logcat
 */ 
private void log_d( int res_id ) {
   log_base(  TAG_SUB, res_id );
} // log_d

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
   log_base(  TAG_SUB, msg );
} // log_d

} // class MainActivity