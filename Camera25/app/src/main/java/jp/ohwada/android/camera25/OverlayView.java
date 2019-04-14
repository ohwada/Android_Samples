/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera25;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * class OverlayView
 * show score like Dragon Ball Scouter
 * overlay on camera preview
 */
public class OverlayView extends View
	implements SensorEventListener {

	// debug
	private final static boolean D = true;
	private final static String TAG = "Camera2";
	private final static String TAG_SUB = "OverlayView";

	// text
	private static final int TEXT_SIZE = 30;

	// text posision
	private static final int TEXT_X = TEXT_SIZE;
	private static final int POWER_Y = TEXT_SIZE + TEXT_SIZE;
	private static final int SPEED_Y = POWER_Y + TEXT_SIZE;
	private static final int LIFE_Y = SPEED_Y + TEXT_SIZE;


	// paint
	private Paint mPointText;
	private Paint mPaintBitmap = new Paint();

	// score	// score
	private String mLabelPower, mLabelSpeed, mLabelLife;
	private float mPowerValue, mSpeedValue, mLifeValue;

	// view size
	private int mViewWidth;
	private int mViewHeight;

	private Bitmap mBitmap;

	// bitmap size
	private int mBitmapWidth;
private int mBitmapHeight;

	// sensor matrix
	private static final int MATRIX_SIZE = 16;
	float[] inR = new float[MATRIX_SIZE];
	float[] outR = new float[MATRIX_SIZE];

	// sensor value
	private static final int SENSOR_SIZE = 3;
	float[] mOrientation  = new float[SENSOR_SIZE];
	float[] mMagnetic      = new float[SENSOR_SIZE];
	float[] mAccelerometer = new float[SENSOR_SIZE];


/**
 * consoractor
 */
public OverlayView(Context context) {
		super(context);
		initView(context);
}

	/**
	 * consoractor
	 */
	public OverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	/**
	 * consoractor
	 */
	public OverlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * initView
	 */
	private void initView(Context context) {
		// paint
		mPointText = new Paint();
		mPointText.setAntiAlias(true);
		mPointText.setTextSize(TEXT_SIZE);
		mPointText.setColor(Color.WHITE);

		// label
		Resources r = context.getResources();
		mLabelPower = r.getString(R.string.label_power);
		mLabelSpeed = r.getString(R.string.label_speed);
		mLabelLife = r.getString(R.string.label_life);
		
		// bitmap
		mBitmap = BitmapFactory.decodeResource(r, R.drawable.cursor);
		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();
	}

	/**
	 * onDraw
	 */
@Override
public void onDraw(Canvas canvas) {
		calcScore();
		//draw Text
		canvas.drawText(mLabelPower + mPowerValue, TEXT_X, POWER_Y, mPointText);
		canvas.drawText(mLabelSpeed + mSpeedValue, TEXT_X, SPEED_Y, mPointText);
		canvas.drawText(mLabelLife + mLifeValue, TEXT_X, LIFE_Y, mPointText);

		// draw bitmap at center of view
		float x = (mViewWidth - mBitmapWidth) / 2;
		float y = (mViewHeight - mBitmapHeight) / 2;
		canvas.drawBitmap(mBitmap, x, y, mPaintBitmap);
}


/**
 * calcScore
 */
private void calcScore() {
		// calculate score using sensor values ​​instead of random numbers
		// Power using azimuth
		mPowerValue = radToDeg(mOrientation[0]);
		// Speed using pitch 
		mSpeedValue = radToDeg(mOrientation[1]);
		// Life using roll			
		mLifeValue = radToDeg(mOrientation[2]);
}

		
/**
 * onSizeChanged
 */ 
@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh){
			log_d("w=" + w + "h=" + h + " , " + "oldw=" + oldw + "oldh" + oldh);
		mViewWidth = w;
		mViewHeight = h;
}
	

/**
 * onTouchEvent
 */ 
@Override
public boolean onTouchEvent( MotionEvent event ){
	    	super.onTouchEvent(event);
		int action = event.getAction();
		float x = event.getAxisValue(MotionEvent.AXIS_X);
		float y = event.getAxisValue(MotionEvent.AXIS_Y);
		log_d("action= " +action + " x= " +x + " y= " +y);
	    return true;
}


/**
 * onAccuracyChanged
 */ 
@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// nothing to do
}

/**
 *  onSensorChanged
 */ 
@Override
public void onSensorChanged(SensorEvent event) {
			synchronized (this) {
				 switch (event.sensor.getType()) { 
		    		case Sensor.TYPE_ACCELEROMETER: 
		    			mAccelerometer = event.values.clone();
		    			break;
		    		case Sensor.TYPE_MAGNETIC_FIELD: 
		    			mMagnetic = event.values.clone();
		    			break;
		    	}
				if(mAccelerometer != null && mMagnetic != null) {
					SensorManager.getRotationMatrix( inR, null, mAccelerometer, mMagnetic );
					SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Z,SensorManager.AXIS_MINUS_X, outR);
					SensorManager.getOrientation( outR, mOrientation );
				}
				invalidate();
			 }
}


/**
 *  radToDeg
 */
private float radToDeg( float rad ){
	    	return (float)(rad * 180 / Math.PI);
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class OverlayView
