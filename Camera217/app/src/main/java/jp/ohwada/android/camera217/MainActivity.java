/**
 * Camera2 Sample
 * Manual Mode
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import jp.ohwada.android.camera217.util.Camera2Source;
import jp.ohwada.android.camera217.util.CameraPerm;
import jp.ohwada.android.camera217.util.Permission;
import jp.ohwada.android.camera217.util.CameraParam;
import jp.ohwada.android.camera217.util.ToastMaster;
import jp.ohwada.android.camera217.util.ImageUtil;
import jp.ohwada.android.camera217.util.FileUtil;

import jp.ohwada.android.camera217.ui.CameraSourcePreview;


/**
 * class MainActivity 
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class MainActivity extends Activity 
implements
        SettingsPreferenceFragment.FragmentListener {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";


    /**
     * Request code for Permissions
     */
    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int REQUEST_CODE_STORAGE = 102;


/**
 *  Key for Preferences 
 */
    private final static String KEY_STORAGE = SettingsPreferenceFragment.KEY_STORAGE;
    private final static String KEY_RAW = SettingsPreferenceFragment.KEY_DNG;


 /**
  *  Value of increment / decrement
  */
    private static final int STEP_UP = +1;
    private static final int STEP_DOWN = -1;


 /**
  *  Flag whether to specify aperture
  */
    private static final boolean USE_APERTURE = true;


/**
  *  Sensor Sensitivity ISO
  */
    private static final int[] ISO_ARRAY = {40, 50, 80, 100, 200, 300, 400, 600, 800, 1000, 1600, 2000, 3200, 4000, 6400, 8000, 10000};
 
    private static final int MIN_ISO_INDEX = 0;
    private static final int MAX_ISO_INDEX = ISO_ARRAY.length;

    // ISO 100
    private  int mIsoIndex = 3;


 /**
  *  Exposure Time
  *  array of denominators 
  *  to order to show in the format of fractions like "1 / 60"
  */
    private static final int[] EXPOSURE_TIME_DENOMINATOR_ARRAY = {2, 4, 6, 8, 15, 30, 60, 100, 125, 250, 500, 750, 1000, 1500, 2000, 3000, 4000, 5000, 6000, 8000, 10000, 20000, 30000, 75000};


    private static final int MIN_EXPOSURE_TIME_INDEX = 0;
    private static final int MAX_EXPOSURE_TIME_INDEX = EXPOSURE_TIME_DENOMINATOR_ARRAY.length;

    // 1 sec : units nano-seconds
    private static final long EXPOSURE_TIME_ONE_SEC =1000000000l;

    // ExposureTime 1 / 60
    private int mExposureTimeIndex = 6;


 /**
  *  TextView for ISO
  */
        private TextView mTextViewIso;


 /**
  *  TextView for Exposure Time
  */
        private TextView mTextViewExposureTime;


/**
 *   CompoundButton for Auto Exposure 
 */ 
      private CompoundButton mButtonAe;


/**
 * Camera2Source
 */ 
    private Camera2Source mCamera2Source;


/**
 * CameraSourcePreview
 */ 
    private CameraSourcePreview mPreview;


/**
 * Camera permssion
 */ 
    private CameraPerm mCameraPerm;

     /**
     * Requesting Permission class for WRITE_EXTERNAL_STORAGE
     */
    private Permission mStoragePerm;


/**
 * utility for Image
 */ 
      private ImageUtil mImageUtil;


/**
 *   Flag whether to use Front Camera
 */ 
    private boolean usingFrontCamera = false;


/**
 * SharedPreferences
 */ 
      private SharedPreferences mSharedPreferences;



/**
 * onCreate
 */ 
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTextViewIso = (TextView)findViewById(R.id.TextView_iso);
        mTextViewIso.setText( getIsoText() );

        mTextViewExposureTime = (TextView)findViewById(R.id.TextView_exposure_time);
        mTextViewExposureTime.setText( getExposureTimeText() );


        mButtonAe = (CompoundButton) findViewById(R.id.Switch_ae);
        mButtonAe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // nop
            }
        }); // mButtonAe


        ImageButton btnCamera = (ImageButton) findViewById(R.id.Button_camera);
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            }); // btnCamera


        ImageButton btnIsoUp = (ImageButton) findViewById(R.id.Button_iso_up);
            btnIsoUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // up ISO 
                    procIsoUpDown(STEP_UP);
                }
            }); // btnIsoUp


        ImageButton btnIsoDown = (ImageButton) findViewById(R.id.Button_iso_down);
        btnIsoDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // down ISO
                    procIsoUpDown(STEP_DOWN);
                }
            }); // btnIsoDown



        ImageButton btnExposureTimeUp = (ImageButton) findViewById(R.id.Button_exposure_time_up);
            btnExposureTimeUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // up ExposureTime
                    procExposureTimeUpDown(STEP_UP);
                }
            }); // btnExposureTimeUp

        ImageButton btnExposureTimeDown = (ImageButton) findViewById(R.id.Button_exposure_time_down);
            btnExposureTimeDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // down ExposureTime 
                    procExposureTimeUpDown(STEP_DOWN);
                }
            }); // btnExposureTimeDown


        ImageButton btnSetting = (ImageButton) findViewById(R.id.Button_settings);
            btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSettingsFragment();
                }
            }); //  btnSetting


    // view
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);



    /**
     * Requesting Permission
     */
    mCameraPerm = new CameraPerm(this);
    mCameraPerm.setRequestCode(REQUEST_CODE_CAMERA);

    mStoragePerm = new Permission(this);
    mStoragePerm.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    mStoragePerm.setRequestCode(REQUEST_CODE_STORAGE);

    mSharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);

     // utility
    mImageUtil = new ImageUtil(this);

} // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        startCameraSource();
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        log_d("onPause");
        super.onPause();
        stopCameraSource();
    }


/**
 * onDestroy
 */ 
    @Override
    protected void onDestroy() {
        log_d("onDestroy");
        super.onDestroy();
        stopCameraSource();
    }


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        log_d("onRequestPermissionsResult: " + requestCode);
        switch(requestCode) {
            case REQUEST_CODE_STORAGE:
                    boolean ret = mStoragePerm.onRequestPermissionsResult(requestCode, permissions,  grantResults);
                    if(ret) {
                        // make the directory for app in DCIM,  if granted
                        FileUtil.mkDirInExternalStoragePublicDCIM();
                    }
                    break;
            case REQUEST_CODE_CAMERA:
                    mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
                    // start Camera,  if granted
                    startCameraSource();
                    break;
        }
} // onRequestPermissionsResult


/**
 * onSwitchPreferenceChange
 */ 
    @Override
    public void onSwitchPreferenceChange(String key, boolean value) {
        log_d("onSwitchPreferenceChange: " + key + " , " + value);
        if (KEY_STORAGE.equals(key) && value) {
                // request Permission,  when enable the feature
                mStoragePerm.requestPermissions();
        }
    }


 /**
 * takePicture
 */
private void takePicture() {

    if(mCamera2Source == null) return;

    boolean isChecked = mButtonAe.isChecked();
    log_d("takePicture: ButtonAe=" + isChecked );

    CameraParam param = mCamera2Source.createCameraParam();

    int iso = ISO_ARRAY[mIsoIndex];

    long time = getExposureTime();

    if( isChecked ) {
        // auto exposure
        param.setManualMode(false);
        param.setAeMode(CameraParam.AE_MODE_ON);
    } else {
        // manual mode
        param.setManualMode(true);
        param.setAeMode(CameraParam.AE_MODE_OFF);
        param.setControlCaptureIntent(CameraParam.CAPTURE_INTENT_MANUAL);
        param.setOpticalStabilizationMode(
        CameraParam.STABILIZATION_MODE_ON);
        param.setSensitivity(iso);
        param.setExposureTime(time);
        if( USE_APERTURE ) {
                param.setAperture( getAperture() );
        }
    }

    boolean use_storage = mSharedPreferences.getBoolean(KEY_STORAGE, false);
    boolean use_raw = mSharedPreferences.getBoolean(KEY_RAW, false);
    param.setUseStorage( use_storage );
    param.setUseRaw( use_raw );

    mCamera2Source.takePictureWithParam(param, camera2SourcePictureCallback);

} // takePicture


/**
 * getAperture
 */ 
private float getAperture() {

    // Note : Nexus5 has only one aperture value
    float[] apertures = mCamera2Source.getApertures();
    float aperture = 0;
    if( apertures.length > 0) {
        aperture = apertures[0];
    }
    return aperture;
}


/**
 * getExposureTime
 * convert reciprocal to nano-seconds
 */ 
private long getExposureTime() {

    int denominator = EXPOSURE_TIME_DENOMINATOR_ARRAY[mExposureTimeIndex];

    // convert to nano-seconds
    long time = EXPOSURE_TIME_ONE_SEC / denominator;

    log_d("getExposureTime: denominator= " + denominator + " , time= " + time);
    return time;
}


/**
 * procIsoUp
 */ 
 private void procIsoUpDown(int step) {
    mIsoIndex += step;
    if(mIsoIndex < MIN_ISO_INDEX) {
        mIsoIndex = MIN_ISO_INDEX;
    }
    if(mIsoIndex > MAX_ISO_INDEX) {
        mIsoIndex = MAX_ISO_INDEX;
    }

    mTextViewIso.setText( getIsoText() );
}


/**
 * getIsoText
 */ 
private String getIsoText() {
    int iso = ISO_ARRAY[mIsoIndex];
    String text = "ISO " + Integer.toString(iso);
    return text;
}




/**
 * procExposureTimeUpDown
 */ 
 private void procExposureTimeUpDown(long step) {
    mExposureTimeIndex += step;
    if(mExposureTimeIndex < MIN_EXPOSURE_TIME_INDEX) {
        mExposureTimeIndex = MIN_EXPOSURE_TIME_INDEX;
    }
    if(mExposureTimeIndex > MAX_EXPOSURE_TIME_INDEX) {
        mExposureTimeIndex = MAX_EXPOSURE_TIME_INDEX;
    }

    mTextViewExposureTime.setText( getExposureTimeText() );
}


/**
 * geExposureTimeText
 * show in the format of fractions like "1 / 60"
 */ 
private String getExposureTimeText() {
    int time = EXPOSURE_TIME_DENOMINATOR_ARRAY[mExposureTimeIndex];

    // format of fractions like "1 / 60"
    String text = "1/" + Integer.toString(time);

    return text;
}


/**
 * createCameraSourceBack
 */ 
    private Camera2Source createCameraSourceBack() {
        log_d(" createCameraSourceBack");
        Camera2Source camera2Source = new 
Camera2Source.Builder(this) 
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_BACK)
                    .setErrorCallback(cameraErrorCallback)
                    .build();

        return camera2Source;
} // createCameraSourceBack

/**
 * startCameraSource
 */ 
    private void startCameraSource() {

        log_d("startCameraSource");
        if(mCameraPerm.requestCameraPermissions()) {
                log_d("not permit");
                return;
        }

        if( mStoragePerm.checkSelfPermission() ) {
                FileUtil.mkDirInExternalStoragePublicDCIM();
        }

        Camera2Source camera2Source 
        = createCameraSourceBack();

            if(camera2Source != null) {
                mCamera2Source = camera2Source;
                mPreview.start(camera2Source);
            }

} // startCameraSource


/**
 * stopCameraSource
 */ 
    private void stopCameraSource() {
        log_d("stopCameraSource");
        mPreview.stop();
    }


/**
 * startSettingsFragment
 */
private void startSettingsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(android.R.id.content, new SettingsPreferenceFragment());
        fragmentTransaction.commit();
}


/**
  * Shows an error message dialog.
 */
private void showErrorDialog(String msg) {
             new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton(R.string.button_ok, null)
                    .show();
} // showErrorDialog


/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
  * showErrorDialog on the UI thread.
 */
private void showErrorDialog_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorDialog(msg);
                }
    });
}


/**
  * ShowToast on the UI thread.
 */
private void showToast_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(msg);
                }
    });
} // showToast_onUI


/**
 * write into logcat
 */ 
private void log_d( int res_id ) {
    log_d( getString(res_id) );
} // log_d

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d



/**
 * PictureCallback
 */ 
 Camera2Source.PictureCallback camera2SourcePictureCallback = new Camera2Source.PictureCallback() {
        @Override
        public void onJpegTaken(Image image, Date date) {
            procJpegTaken(image, date);
        }
        @Override
        public void onRawTaken(File file) {
            procRawTaken(file);
        }
    }; // PictureCallback 


/**
 * procJpegTaken
 * save Image and scanFile
 */ 
private void procJpegTaken(Image image, Date date) {

        boolean use_storage = mSharedPreferences.getBoolean(KEY_STORAGE, false);
        File file = mImageUtil.getOutputFile(date, use_storage);
        boolean ret = mImageUtil.saveImageAsJpeg(image, file, use_storage);
        if(ret) {
            log_d("jpeg size= " +file.length());
        }
       String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);
} // procJpegTaken


/**
 * procRawTaken
 * scanFile
 */ 
private void procRawTaken(File file) {

        log_d("raw size= " +file.length());
       String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);
} // procRawTaken


/**
 * getPrefStorage
 */ 
private boolean getPrefStorage() {
        boolean ret = mSharedPreferences.getBoolean(KEY_STORAGE, false);
        return ret;
} // getPrefStorage
/**
 * CameraErrorCallback
 */ 
 Camera2Source.ErrorCallback cameraErrorCallback = new Camera2Source.ErrorCallback() {
        @Override
        public void onError(String msg) {
            showErrorDialog_onUI(msg);
        }
    }; // CameraErrorCallback 


} // class MainActivity 
