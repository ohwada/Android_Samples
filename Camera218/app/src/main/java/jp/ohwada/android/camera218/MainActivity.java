/**
 * Camera2 Sample
 * take Picture with Burst mode using Camera2Source
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;


import jp.ohwada.android.camera218.util.CameraPerm;
import jp.ohwada.android.camera218.util.Camera2Source;
import jp.ohwada.android.camera218.util.ToastMaster;
import jp.ohwada.android.camera218.util.YuvImageUtil;
import jp.ohwada.android.camera218.util.FileUtil;
import jp.ohwada.android.camera218.util.Permission;
import jp.ohwada.android.camera218.util.BurstParam;

import jp.ohwada.android.camera218.ui.CameraSourcePreview;


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
    private final static String KEY_MANUAL = SettingsPreferenceFragment.KEY_MANUAL;
    private final static String KEY_SAVE = SettingsPreferenceFragment.KEY_SAVE;
    public final static String KEY_FORMAT = SettingsPreferenceFragment.KEY_FORMAT;
    public final static String KEY_NUMBER = SettingsPreferenceFragment.KEY_NUMBER;

/**
 * Camera2Source
 */ 
    private Camera2Source mCamera2Source = null;


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
 * YuvImageUtil
 */ 
      private YuvImageUtil mYuvImageUtil;


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


        Button btnPicture = (Button) findViewById(R.id.Button_picture);
            btnPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            }); // btnPicture

        Button btnSetting = (Button) findViewById(R.id.Button_setting);
    btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSettingsFragment();
                }
            }); // btnSetting

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

    mYuvImageUtil = new YuvImageUtil(this);


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

        boolean use_storage = getPrefStorage();
        boolean is_manual = getPrefManual();
        boolean use_save = getPrefSave();
        int format = getPrefImageFormat();
        int number = getPrefNumberOfShots();

        BurstParam burstParam = new BurstParam();
        burstParam.setUseStorage(use_storage);
        burstParam.setManualMode(is_manual);
        burstParam.setSaveTogether(use_save);
        burstParam.setImageFormat(format);
        burstParam.setNumberOfShots( number);

        if(mCamera2Source != null) {
                mCamera2Source.takePicture( burstParam, camera2SourcePictureCallback);
        }
} // takePicture


/**
 * getPrefStorage
 */ 
private boolean getPrefStorage() {
        boolean ret = mSharedPreferences.getBoolean(KEY_STORAGE, false);
        return ret;
} // getPrefStorage


/**
 * getPrefManual
 */ 
private boolean getPrefManual() {

        boolean ret = mSharedPreferences.getBoolean(KEY_MANUAL, false );
    return ret;
} // getPrefManual

/**
 * getPrefSave
 */ 
private boolean getPrefSave() {

        boolean ret = mSharedPreferences.getBoolean(KEY_SAVE, true );
    return ret;
} // getPrefManual

/**
 * getPrefImageFormat
 */ 
private int getPrefImageFormat() {

    String default_format = getString(R.string.pref_default_list_image_format);
        String str = mSharedPreferences.getString(KEY_FORMAT, default_format );
    return parseInt(str);
} // getPrefOutputFormat


/**
 * getPrefNumberOfShots
 */ 
private int getPrefNumberOfShots() {

    String default_nunber 
        = getString(R.string.pref_default_list_number_of_shots);
        String str = mSharedPreferences.getString(KEY_NUMBER, default_nunber );
    return parseInt(str);
} // getPrefNumberOfShots


/**
 * parseInt
 */ 
private int parseInt(String str) {
    int val = 0;
    try {
        val = Integer.parseInt(str);
    } catch(NumberFormatException e) {
        e.printStackTrace();
    }
    return val;
}


/**
 * createCameraSourceBack
 */ 
    private Camera2Source createCameraSourceBack() {
        log_d(" createCameraSourceBack");
        Camera2Source camera2Source = new 
Camera2Source.Builder(this) 
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_AE_AUTO_FLASH)
                    .setFacing(Camera2Source.CAMERA_FACING_BACK)
                    .setErrorCallback(cameraErrorCallback)
                    .build();

        return camera2Source;
} // createCameraSourceBackBack

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

        Camera2Source camera2Source = createCameraSourceBack();
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
        mCamera2Source = null;
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
          public void onPictureTaken(File file) {
                log_d("onPictureTaken");
                showSavedToast(file);
        }
        @Override
        public void onYuvBurstTaken(List<YuvImage> list, int jpegOrientation) {
            log_d("onYuvBurstTaken");
            procYuvBurstTaken(list, jpegOrientation);
        }

    }; // PictureCallback 



/**
 *  showSavedToast
 */ 
private void showSavedToast(File file) {
       String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);
}


/**
 *  procYuvBurstTaken
 *  save images
 */ 
private void procYuvBurstTaken(List<YuvImage> list, int jpegOrientation) {

        boolean use_storage = getPrefStorage();
        File file = mYuvImageUtil.saveImageBurst(list,  jpegOrientation, use_storage);

        showSavedToast(file);

} //  procYuvBurstTaken


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
