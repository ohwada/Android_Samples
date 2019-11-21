/**
 * RenderScript Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/android/renderscript-samples/tree/master/BasicRenderScript
 */
package jp.ohwada.android.renderscript1;


/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;


/**
  * class MainActivity 
  */
//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    // debug
    private final static String TAG = "RenderScript";


    private static final float   SEEKBAR_MAX = 100.0f;
    private static final int  SEEKBAR_DEFAULT = (int)(SEEKBAR_MAX / 2);


    private static final float  SATURATION_MAX = 2.0f;
    private static final float SATURATION_MIN = 0.0f;
    private static final float SATURATION_DEFAULT = (SATURATION_MAX - SATURATION_MIN)/2 ;

    /**
     * Number of bitmaps that is used for RenderScript thread and UI thread synchronization.
     */
    private final int NUM_BITMAPS = 2;

    private int mCurrentBitmap = 0;
    private Bitmap mBitmapIn;
    private Bitmap[] mBitmapsOut;
    private ImageView mImageView;

    private Allocation mInAllocation;
    private Allocation[] mOutAllocations;
    private ScriptC_saturation mScript;
    private RenderScriptTask mCurrentTask;


/**
  * onCreate
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize UI
        mBitmapIn = loadBitmap(R.drawable.data);
        mBitmapsOut = new Bitmap[NUM_BITMAPS];
        for (int i = 0; i < NUM_BITMAPS; ++i) {
            mBitmapsOut[i] = Bitmap.createBitmap(mBitmapIn.getWidth(),
                    mBitmapIn.getHeight(), mBitmapIn.getConfig());
        }

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageBitmap(mBitmapsOut[mCurrentBitmap]);
        mCurrentBitmap += (mCurrentBitmap + 1) % NUM_BITMAPS;

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);
        seekbar.setProgress(SEEKBAR_DEFAULT);
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

/**
  * onProgressChanged
  * adjust the imagesaturation
  */
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                log_d( "onProgressChanged: " + progress);

                float f = (float) ((SATURATION_MAX - SATURATION_MIN) * (progress / SEEKBAR_MAX) + SATURATION_MIN);
                updateImage(f);
            }

/**
  * onStartTrackingTouch
  */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

/**
  * onStopTrackingTouch
  */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        }); // OnSeekBarChangeListener

        // Create renderScript
        createScript();


        // Invoke renderScript kernel and update imageView
        updateImage(SATURATION_DEFAULT);
    }


    /**
     * Initialize RenderScript.
     *
     * <p>In the sample, it creates RenderScript kernel that performs saturation manipulation.</p>
     */
    private void createScript() {
        // Initialize RS
        RenderScript rs = RenderScript.create(this);

        // Allocate buffers
        mInAllocation = Allocation.createFromBitmap(rs, mBitmapIn);
        mOutAllocations = new Allocation[NUM_BITMAPS];
        for (int i = 0; i < NUM_BITMAPS; ++i) {
            mOutAllocations[i] = Allocation.createFromBitmap(rs, mBitmapsOut[i]);
        }

        // Load script
        mScript = new ScriptC_saturation(rs);
    }

    /*
     * In the AsyncTask, it invokes RenderScript intrinsics to do a filtering.
     * After the filtering is done, an operation blocks at Allocation.copyTo() in AsyncTask thread.
     * Once all operation is finished at onPostExecute() in UI thread, it can invalidate and update
     * ImageView UI.
     */
    private class RenderScriptTask extends AsyncTask<Float, Void, Integer> {
        Boolean issued = false;

/**
  * doInBackground
  */
        protected Integer doInBackground(Float... values) {
            float value = values[0];
            log_d("doInBackground: " + value);
            int index = -1;
            if (!isCancelled()) {
                issued = true;
                index = mCurrentBitmap;

                // Set global variable in RS
                mScript.set_saturationValue(value);

                // Invoke saturation filter kernel
                mScript.forEach_saturation(mInAllocation, mOutAllocations[index]);

                // Copy to bitmap and invalidate image view
                mOutAllocations[index].copyTo(mBitmapsOut[index]);
                mCurrentBitmap = (mCurrentBitmap + 1) % NUM_BITMAPS;
            }
            return index;
        }

/**
  * updateView
  */
        void updateView(Integer result) {
            log_d("updateView: " + result);
            if (result != -1) {
                // Request UI update
                mImageView.setImageBitmap(mBitmapsOut[result]);
                mImageView.invalidate();
            }
        }

/**
  * onPostExecute 
  */
        protected void onPostExecute(Integer result) {
            log_d("onPostExecute: " + result);
            updateView(result);
            showToast("updateView");
        }

/**
  * onCancelled 
  */
        protected void onCancelled(Integer result) {
            if (issued) {
                updateView(result);
            }
        }

    } // class RenderScriptTask


    /**
     * Invoke AsyncTask and cancel previous task. When AsyncTasks are piled up (typically in slow
     * device with heavy kernel), Only the latest (and already started) task invokes RenderScript
     * operation.
     */
    private void updateImage(final float f) {
        log_d("updateImage: " + f);
        if (mCurrentTask != null) {
            mCurrentTask.cancel(false);
        }
        mCurrentTask = new RenderScriptTask();
        mCurrentTask.execute(f);
    }


    /**
     * Helper to load Bitmap from resource
     */
    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }


/**
 * showToast
 */
private void showToast( String msg ) {
		Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    Log.d( TAG, msg );
} 


} // class MainActivity
