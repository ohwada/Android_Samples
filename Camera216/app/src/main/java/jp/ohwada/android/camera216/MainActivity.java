/**
 * Camera2 Sample
 * take Picture using Camera2Source
 * save Location with EXIF 
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.camera216;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Date;


import jp.ohwada.android.camera216.util.Camera2Source;
import jp.ohwada.android.camera216.util.CameraPerm;
import jp.ohwada.android.camera216.util.Permission;
import jp.ohwada.android.camera216.util.ToastMaster;
import jp.ohwada.android.camera216.ui.CameraSourcePreview;


/**
 * class MainActivity 
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 */
public class MainActivity extends Activity
implements
        SettingPreferenceFragment.FragmentListener {


    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";

/**
 *  request code 
 */
    private final static int REQUEST_CODE_CAMERA_PERMISSIONS = 101;
    private final static int REQUEST_CODE_LOCATION_PERMISSIONS = 102;
    private final static int REQUEST_CODE_STORAGE_PERMISSIONS = 103;


/**
 *  Key for Preferences 
 */
    private final static String KEY_LOCATION = SettingPreferenceFragment.KEY_LOCATION;
    private final static String KEY_STORAGE = SettingPreferenceFragment.KEY_STORAGE;


/**
 * Constant for output File
 */ 
    private static final String FILE_PREFIX = "camera_";
    private static final String FILE_EXT = ".jpg";


/**
 * Camera2Source
 */ 
    private Camera2Source mCamera2Source = null;


/**
 * CameraSourcePreview
 */ 
    private CameraSourcePreview mPreview;


    /**
     * Requesting Permission class
     */
    private CameraPerm mCameraPerm;
    private Permission mLocationPerm;
    private Permission mStoragePerm;


/**
 * LocationUtil
 */ 
    private LocationUtil mLocationUtil;



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

     // permission
    mCameraPerm = new CameraPerm(this);
mCameraPerm.setRequestCode(REQUEST_CODE_CAMERA_PERMISSIONS);

    mLocationPerm = new Permission(this);
    mLocationPerm.setPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    mLocationPerm.setRequestCode(REQUEST_CODE_LOCATION_PERMISSIONS);

mStoragePerm = new Permission(this);
    mStoragePerm.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    mStoragePerm.setRequestCode(REQUEST_CODE_STORAGE_PERMISSIONS);

    mLocationUtil = new LocationUtil(this);

} // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        if( mCameraPerm.requestCameraPermissions() ) {
                log_d("not permit");
                return;
        }
        if( mLocationPerm.checkSelfPermission() ) {
                // start detecting the Location
                mLocationUtil.requestLocationUpdates();
        }
        if( mStoragePerm.checkSelfPermission() ) {
                // make Directory in ExternalStoragePublicDCIM
                FileUtil.mkDirInExternalStoragePublicDCIM();
        }
        startCameraSource();
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        log_d("onPause");
        super.onPause();
        mLocationUtil.removeUpdates();
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

        switch(requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSIONS:
                    if( mLocationPerm.isGrantRequestPermissionsResult( grantResults) ) {
                        // start detecting the Location
                        mLocationUtil.requestLocationUpdates();
                    }
                    break;
            case REQUEST_CODE_STORAGE_PERMISSIONS:
                    if( mStoragePerm.isGrantRequestPermissionsResult( grantResults) ) {
                            // make Directory in ExternalStoragePublicDCIM
                            FileUtil.mkDirInExternalStoragePublicDCIM();
                    }
                    break;
            case REQUEST_CODE_CAMERA_PERMISSIONS:
            default:
                    if( mCameraPerm.isGrantRequestPermissionsResult(grantResults)) {
                        startCameraSource();
                    }
                    break;
        }
} // onRequestPermissionsResult


/**
 * onSwitchPreferenceChange
 * FragmentListener
 */ 
    @Override
    public void onSwitchPreferenceChange(String key, boolean value) {
        log_d("onSwitchPreferenceChange: " + key + " , " + value);
        if(KEY_LOCATION.equals(key) && value) {
            mLocationPerm.requestPermissions();
        } else if (KEY_STORAGE.equals(key) && value) {
            mStoragePerm.requestPermissions();
        }
    } // onSwitchPreferenceChange


 /**
 * takePicture
 */
private void takePicture() {
              if(mCamera2Source != null) {
                mCamera2Source.takePicture(camera2SourcePictureCallback);
    }
} // takePicture


/**
 * startSettingsFragment
 */
private void startSettingsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(android.R.id.content, new SettingPreferenceFragment());
        fragmentTransaction.commit();
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

        Camera2Source  camera2Source 
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
        public void onPictureTaken(Image image) {
            procPictureTaken(image);
        }
    }; // PictureCallback 


/**
 * procPictureTaken
 */ 
private void procPictureTaken(Image image) {

        Date date = FileUtil.getDate();
        File file = createOutputFile(date);

        ImageUtil.saveImageAsJpeg(image, file);

        Location location =  mLocationUtil.getLocation();
        ExifUtil.addExif(date, location, file);

        FileUtil.scanFile(this, file);

       String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);
} // procPictureTaken


/**
 * createOutputFile
 */ 
private File createOutputFile(Date date) {

        // save to ExternalStorage, 
        // because Photo app can read EXIF
        File file = null;
        if( mStoragePerm.checkSelfPermission() ) {
                file = FileUtil.createOutputFileInExternalStoragePublicDCIM(FILE_PREFIX, FILE_EXT, date);
        } else {
                file = FileUtil.createOutputFileInExternalFilesDir(this, FILE_PREFIX, FILE_EXT, date);
        }
        return file;
}



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
