/**
 * OpenCL Sample
 * 2020-01-01 K.OHWADA
 * original : https://software.intel.com/en-us/android/articles/opencl-basic-sample-for-android-os
 */
package jp.ohwada.android.opencl4;

// Copyright (c) 2014 Intel Corporation. All rights reserved.
//
// WARRANTY DISCLAIMER
//
// THESE MATERIALS ARE PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INTEL OR ITS
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
// OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THESE
// MATERIALS, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// Intel Corporation is the author of the Materials, and requests that all
// problem reports or change requests be submitted to it directly

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.Manifest;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * class MainActivity
 */
public class MainActivity extends Activity
{

    // debug
    private final static String TAG = "MainActivity";


    private final static String CL_FILE_NAME = "step.cl";

    // Pre-defined value to distinguish the camera-shot event, used in the Intents handler.
    private static final int CAMERA_SHOT = 1;

    private static final int REQUEST_CODE_STORAGE_PERMISSIONS = 101;


    // Input bitmap object, serves as a single source for the image-processing kernel.
    private Bitmap inputBitmap;
    // Output bitmap object, serves as a destination for the image-processing kernel.
    private Bitmap outputBitmap;
    // Second bitmap object is required for the simple double-buffering scheme.
    // For example, the kernel outputs first frame to the outputBitmap, second to the outputBitmapStage,
    // then to outputBitmap again, and so on.
    private Bitmap outputBitmapStage;

    // Single ImageView that is used to output the resulting bitmap object.
    private ImageView outputImageView;
    // This is a dedicated (worker) thread for computing needs, required as
    // potentially long operations should be avoided in the GUI thread.
    private Thread backgroundThread;
    // A conditional that signals the worker thread that the application is active, which means it is
    // not minimized.
    private ConditionVariable isGoing;
    // A flag that signals the worker thread that the application is exiting, so it should wrap up all
    // the activities as well.
    private volatile boolean isShuttingDown = false;

    // A conditional that guards the compute and rendering stages of the pipeline so that the bitmap object
    // being processed by the image-processing kernel is not displayed until done.
    private ConditionVariable isRendering;
    // A counter that is responsible for expansion of the "circle of effect" that appears when you
    // touch the screen.
    private int stepCount;

    // These are the "snapshot" values for the touch event used in the GUI thread.
    private int xTouchUI;
    private int yTouchUI;
    private int stepTouchUI;

    // These are the touch coordinates to be applied in the image-processing filter.
    private int xTouchApply;
    private int yTouchApply;
    private int stepTouchApply;

    // Image file (when you take a shot with the camera) and it's path.
    private File image;
    private String  newBitmapPath;



    // Time values in tickmarks, used for performance statistics.
    private long stepStart;
    private long stepEnd;
    private long prevFrameTimestamp;

    // Number of iterations over which the specific metrics are collected.
    private int itersAccum;
    // Frame time, accumulated over the number of iterations. Used for averaging the value.
    private long frameDurationAccum;
    // Image-processing kernel execution time, accumulated over the number of iterations. Used for
    // averaging the value.
    private long effectDurationAccum;
    // Threshold for the accumulated frames time, after which the averaged FPS values are calculated
    // and reported.
         // Consider this approach rather than relying on the threshold for the number of the elapsed
         // frames (for example, 100 frames). The problem with relying on the number of frames
         // rather than time is that on a slow device getting 100 frames might take significant time.
         // Yet collecting statistics over smaller number (for example, 10 frames) might produce
         // volatile FPS values on fast devices. From this perspective, accumulating performance
         // statistics with the time threshold is more reliable.
        private static final long maxFrameDurationAccum = 500000000;

    // A few simple text views to output performance statistics.
    private TextView FPSLabel;
    private TextView frameDurationLabel;
    private TextView effectDurationLabel;

    private Permission mStoragePerm;

    private CameraFile mCameraFile;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputImageView = (ImageView)findViewById(R.id.outputImageView);

        FPSLabel = (TextView)findViewById(R.id.FPS);
        FPSLabel.setBackgroundColor(Color.WHITE);

        frameDurationLabel = (TextView)findViewById(R.id.FrameDuration);
        frameDurationLabel.setBackgroundColor(Color.WHITE);

        effectDurationLabel = (TextView)findViewById(R.id.EffectDuration);
        effectDurationLabel.setBackgroundColor(Color.WHITE);

        isGoing  = new ConditionVariable(false);
        isRendering = new ConditionVariable(true);
        image = null;
        newBitmapPath = null;

        initOpenCL(getOpenCLProgram(CL_FILE_NAME));

        Button buttonPhoto=(Button)findViewById(R.id.PhotoButton);
        int anyCamera = Camera.getNumberOfCameras();
        if(anyCamera==0)
            buttonPhoto.setVisibility(View.INVISIBLE);

        // Reset the touch state as if you have not touched the screen yet.
        ResetTouch();

       mStoragePerm = new Permission(this);
        mStoragePerm.setRequestCode(REQUEST_CODE_STORAGE_PERMISSIONS);
        mStoragePerm.setPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE);

        mCameraFile = new CameraFile(this);
    }


/**
  * onDestroy
 */
    @Override
    protected void onDestroy()
    {
        // Attempt to stop the background thread and make it release all
        // OpenCL resources.

        // Overriding this method is optional, and this code is
        // provided for illustrative purposes on how to release OpenCL resources.

        // The described resources are not system-critical and are released automatically even if
        // the Android OS kills the entire process without calling this method.

        Log.i("AndroidBasic", "onDestroy");

        // Tell the background thread that the application is closing.
        // Communicate with the thread via the isShuttingDown variable.
        isShuttingDown = true;
        isGoing.open();
        super.onDestroy();
    }


/**
  * getOpenCLProgram
 */
    private String getOpenCLProgram (String fileName )
    {
        /* OpenCL program text is stored in a separate file in
         * assets directory. Here you need to load it as a single
         * string.
         *
         * In fact, the program may be directly built into
         * native source code where OpenCL API is used,
         * it is useful for short kernels (few lines) because it doesn't
         * involve loading code and you don't need to pass it from Java to
         * native side.
         */

        try
        {
            StringBuilder buffer = new StringBuilder();
            InputStream stream = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String s;

            while((s = reader.readLine()) != null)
            {
                buffer.append(s);
                buffer.append("\n");
            }

            reader.close();
            return buffer.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
        
    }


/**
  * PhotoOnClick
 */
    public void PhotoOnClick(View v) {
            startCameraActivity();
    }


/**
  * startCameraActivity
 */
private void startCameraActivity() {

        if (mStoragePerm.requestPermissions()) {
            return;
        }

       File fileDir = mCameraFile.getFileDir();
        String fileName = mCameraFile.getFileName();

        Intent cameraShotIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Notice that for the sake of simplicity the activity orientation is set to landscape.
        // Operate the camera accordingly - capture photos also in the plain landscape mode only.

        image = new File(fileDir, fileName);

        Log.i("AndroidBasic", "file name for the intent: " + Uri.fromFile(fileDir) + "/" + fileName);

        try
        {
            if(image.createNewFile())
            {
                cameraShotIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                if (cameraShotIntent.resolveActivity(getPackageManager()) != null)
                {
                     startActivityForResult(cameraShotIntent, CAMERA_SHOT);
                }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
}


/**
  * onActivityResult
 */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_SHOT)
        {
            if(resultCode != RESULT_OK)
            {
                image.delete();
                return;
            }
            // Reset the previous touches.
            ResetTouch();

            newBitmapPath =  image.getAbsolutePath();
            Log.i("AndroidBasic", "newBitmapPath: " + newBitmapPath);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(image);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }


/** 
 *  onRequestPermissionsResult
 */
@Override
public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_STORAGE_PERMISSIONS ) {
                if (mStoragePerm.isGrantRequestPermissionsResult(requestCode, grantResults)) {
                    startCameraActivity();
                }
        }

}


/** 
 *  startBackgroundThread
 */
    private void startBackgroundThread ()
    {
        // Create a thread that periodically calls the filter method of this activity.
        backgroundThread = new Thread(new Runnable() {
            public void run() {
                while(!isShuttingDown)
                {
                    // Check that the application is not in the minimized state.
                    isGoing.block();
                    // Guard for flip-flopping the buffers so that the GUI thread does not try displaying a
                    // not updated bitmap object.
                    isRendering.block();
                    {
                        // If you need to update the image, it is a good place (in a dedicated thread) -
                        // in a synchronized way.
                        if(newBitmapPath!=null)
                        {
                            loadInputImage(newBitmapPath);
                        }
                        Log.i("AndroidBasic", "beforeStep");

                        {
                            // Swap target and staging bitmap objects.
                            Bitmap t = outputBitmap;
                            outputBitmap = outputBitmapStage;
                            outputBitmapStage = t;
                        }

                        stepStart = System.nanoTime();
                        step();
                        stepEnd = System.nanoTime();

                        if(newBitmapPath!=null)
                        {
                            newBitmapPath = null;
                        }


                        Log.i("AndroidBasic", "afterStep");
                        // Prevent the background thread from computing next frame to the same bitmap object.
                        isRendering.close();
                        outputImageView.post
                        (
                            new Runnable()
                            {
                                public void run ()
                                {
                                    {
                                        // Snapshot of the touch-event parameters that should be applied
                                        // until a new touch event happens.
                                        // In the GUI thread copy parameters into a separated variables
                                        // to guarantee the parameters are changed "atomically" (simultaneously)
                                        // for the worker thread.
                                        xTouchApply = xTouchUI;
                                        yTouchApply = yTouchUI;
                                        stepTouchApply = stepTouchUI;

                                        // Update the performance statistics.
                                        updatePerformanceStats();


                                        Log.i("AndroidBasic", "setImageBitmap and invalidate");
                                        outputImageView.setImageBitmap(outputBitmap);
                                        outputImageView.invalidate();
                                    }
                                    // Enable the background thread to compute the next frame.
                                    isRendering.open();
                                }
                            }
                        );
                    }
                }
                shutdownOpenCL();
                Log.i("AndroidBasic", "Exiting backgroundThread");
            }
        });

        backgroundThread.start();
    }


/** 
 *  updatePerformanceStats
 */
    private void updatePerformanceStats()
    {
        long curFrameTimestamp = System.nanoTime();

        if(prevFrameTimestamp != -1)
        {
            // Calculate the current frame duration value.
            long frameDuration = curFrameTimestamp - prevFrameTimestamp;
            long effectDuration = stepEnd - stepStart;
            frameDurationAccum += frameDuration;
            effectDurationAccum += effectDuration;
            itersAccum++;

            if(frameDurationAccum > maxFrameDurationAccum)
            {
                frameDuration  = frameDurationAccum / itersAccum;
                effectDuration = effectDurationAccum / itersAccum;
                frameDurationAccum = 0;
                effectDurationAccum = 0;
                itersAccum = 0;

                FPSLabel.setText((float)(int)((1e9f / frameDuration)*10)/10 + " FPS");
                frameDurationLabel.setText("Frame: " + frameDuration / 1000000 + " ms");
                effectDurationLabel.setText("Effect:  " + effectDuration / 1000000 + " ms");
            }
        }

        prevFrameTimestamp = curFrameTimestamp;
    }


/** 
 *  step
 *  this method runs in a separate working thread
 */
    private void step ()
    {
        Log.i("AndroidBasic", "step");

        nativeStepOpenCL(
            stepCount,
            xTouchApply,
            yTouchApply,
            stepTouchApply == -1 ? -1 : 10*(stepCount - stepTouchApply),
            newBitmapPath != null,
            inputBitmap,
            outputBitmap
        );

        stepCount++;
    }


/**
  * onStart
 */
    @Override
    protected void onStart()
    {
        super.onStart();

        // Unleash the "worker" thread.
        isGoing.open();
        Log.i("AndroidBasic", "onStart");
    }


/**
  * onStop
 */
    @Override
    protected void onStop()
    {
        super.onStop();

        // Stop the background filtering thread.
        isGoing.close();
        Log.i("AndroidBasic", "onStop");
    }


/**
  * onWindowFocusChanged
 */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        Log.i("AndroidBasic", "onWindowFocusChanged");
        // When the application is restarted, which means the inputBitmap doesn't exist,
        // load the inputBitmap first and (re)start the worker thread.
        // If the inputBitmap already exists, the application is likely to resume from the minimized state.
        if(inputBitmap == null)
        {
            String fromResources = null;
            loadInputImage(fromResources);
            startBackgroundThread();
        }
    }


/**
  * loadInputImage
 */
    private void loadInputImage (String path)
    {
        // To avoid potential issues with loading big images, scale the input image to fit the output view.
        // Obtain the actual dimensions from the outputImageView.
        int displayWidth = outputImageView.getWidth();
        int displayHeight = outputImageView.getHeight();
        Log.i("AndroidBasic", "display dimensions: " + displayWidth + ", " + displayHeight);

        // Obtain the original dimensions of the input picture from resources.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;    // This avoids the decoding itself and reads the image statistics.

        if(path==null)
        {
            BitmapFactory.decodeResource(getResources(), R.drawable.picture, options);
        }
        else
        {
            BitmapFactory.decodeFile(path, options);
        }

        int origWidth  = options.outWidth;
        int origHeight = options.outHeight;

        // According to the display and the original dimensions, calculate the scale factor that reduces
        // the amount of memory needed to store an image, and, at the same time, is not too high to avoid
        // significant image quality loss.
        options.inSampleSize = Math.min(origWidth/displayWidth, origHeight/displayHeight);

        // Now decode the real picture content with scaling.
        options.inJustDecodeBounds = false;
        if(path==null)
        {
            inputBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.picture, options);
        }
        else
        {
            inputBitmap = BitmapFactory.decodeFile(path, options);
        }

        inputBitmap = Bitmap.createScaledBitmap(inputBitmap, displayWidth, displayHeight, false);

        int imageWidth  = inputBitmap.getWidth();
        int imageHeight = inputBitmap.getHeight();
        // Two bitmap objects for the simple double-buffering scheme, where first bitmap object is rendered,
        // while the second one is being updated, then vice versa.
        outputBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        outputBitmapStage = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
    }


/**
  * onTouchEvent
 */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        xTouchUI = (int)(event.getX());
        yTouchUI = (int)(event.getY());

        stepTouchUI = stepCount;

        Log.i("AndroidBasic", "x = " + event.getX() + ", y = " + event.getY());
        return super.onTouchEvent(event);
    }


/**
  * ResetTouch
 */
    private void ResetTouch()
    {
        stepTouchUI = stepTouchApply = -1;
    }


/**
  * native code
 */
    static
    {
        System.loadLibrary("step");
    }

    private native void initOpenCL (String openCLProgramText);

    private native void shutdownOpenCL ();

    private native void nativeStepOpenCL (
        int stepCount,
        int xTouch,
        int yTouch,
        int radius,
        boolean updateInputBitmap,
        Bitmap inputBitmap,
        Bitmap outputBitmap
    );


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    Log.d( TAG,  msg );
} 


} // class MainActivity
