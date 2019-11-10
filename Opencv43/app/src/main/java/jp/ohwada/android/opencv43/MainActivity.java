/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/tutorial-3-cameracontrol
 */
package jp.ohwada.android.opencv43;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;


import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;


/** 
 *  class MainActivity
 *  save a still picture to file. 
*  TODO : change to use Camera2 API
 */
//public class Tutorial3Activity extends CameraActivity implements CvCameraViewListener2, OnTouchListener {
public class MainActivity extends CameraActivity implements CvCameraViewListener2, OnTouchListener {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";


    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";
    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT = ".jpg";


    // private Tutorial3View mOpenCvCameraView;
    private MyCameraView mOpenCvCameraView;


    private List<Size> mResolutionList;
    private Menu mMenu;
    private boolean mCameraStarted = false;
    private boolean mMenuItemsCreated = false;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;


/** 
 *  BaseLoaderCallback
 */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
/** 
 *  onManagerConnected
 */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    log_d("OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }

    }; // BaseLoaderCallback


/** 
 *  constractor
 */
    //public Tutorial3Activity() {
    public MainActivity() {
        log_d( "Instantiated new " + this.getClass());
    }


/** 
 *  onCreate
 *  Called when the activity is first created.
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        log_d("called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (MyCameraView) findViewById(R.id.camera_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }


/** 
 * onPause
 */
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


/** 
 *  onResume
 */
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            log_d("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            log_d("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


/** 
 *  getCameraViewList
 */
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


/** 
 *  onDestroy
 */
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

/** 
 *  onCameraViewStarted
 */
    @Override
    public void onCameraViewStarted(int width, int height) {
        mCameraStarted = true;
        setupMenuItems();
    }

/** 
 *  onCameraViewStopped
 */
    public void onCameraViewStopped() {
        // nop
    }


/** 
 *  onCameraFrame
 */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }


/** 
 *  onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mMenu = menu;
        setupColorEffectsMenu();
        setupResolutionMenu();

        return true;
    }


/** 
 *  setupColorEffectsMenu()
 */
private void setupColorEffectsMenu() {

        List<String> effects = mOpenCvCameraView.getEffectList();
        if (effects == null) {
            log_d("Color effects are not supported by device!");
            return;
        }

        mColorEffectsMenu = mMenu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        for (String effect: effects) {
            mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, effect);
            idx++;
        }
}


/** 
 *  setupResolutionMenu
 */
privste void setupResolutionMenu() {
        mResolutionMenu = mMenu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        idx = 0;
        for (Size size: mResolutionList) {
            String width = Integer.valueOf(size.width).toString();
            String height = Integer.valueOf(size.height).toString();
            String  resolution = width + "x" + height;
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE, resolution);
            idx++;
        }
}


/** 
 *  setupMenuItems
 */
    private void setupMenuItems() {
        if (mMenu == null || !mCameraStarted || mMenuItemsCreated) {
            return;
        }
        List<String> effects = mOpenCvCameraView.getEffectList();

        if (effects == null) {
            log_d("Color effects are not supported by device!");
            return;
        }

        mColorEffectsMenu = mMenu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        for (String effect: effects) {
            mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, effect);
            idx++;
        }

        mResolutionMenu = mMenu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        idx = 0;
        for (Size resolution: mResolutionList) {
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString());
            idx++;
        }
        mMenuItemsCreated = true;
    }


/** 
 *  onOptionsItemSelected
 */
    public boolean onOptionsItemSelected(MenuItem item) {
        log_d("called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1) {
            setOptionEffect(item);
        } else if (item.getGroupId() == 2) {
            setOptionResolution(item);
        }
        return true;
    }


/** 
 *  setOptionEffect
 */
private void setOptionEffect(MenuItem item) {
            String effect = (String) item.getTitle();
            mOpenCvCameraView.setEffect(effect);
            log_d("effect " + effect);
            showToast(effect);
}


/** 
 *  setOptionResolution
 */
private void setOptionResolution(MenuItem item) {
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            String width = Integer.valueOf(resolution.width).toString();
            String height = Integer.valueOf(resolution.height).toString();
            String caption = 
            width + "x" + height;
            log_d("resolution " + caption);
            showToast(caption);
}


/** 
 *  onTouch
 *  take picture when touch the screen
 */
    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        log_d("onTouch event");

        // create output file
        File file = createOutputFile(this);
        String filePath = file.toString();

        // take picture
        mOpenCvCameraView.takePicture(file);

        // showToast
        String msg = " saved " + filePath;
        log_d(msg);
        showToast(msg);
        return false;
    }


/**
 * createOutputFile
 */ 
private File createOutputFile(Context context) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String currentDateandTime = sdf.format(new Date());
        String fileName = 
                              FILE_PREFIX + currentDateandTime + FILE_EXT;
        File dir = context.getExternalFilesDir(null);
        File file = new File(dir, fileName);
        return file;
}


/**
 * showToast
 */
private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class MainActivity
