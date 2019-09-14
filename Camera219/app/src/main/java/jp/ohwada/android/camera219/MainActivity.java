/**
 * Camera2 Sample
 * WebCamera : Motion JPEG Server
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera219;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import jp.ohwada.android.camera219.util.CameraPerm;
import jp.ohwada.android.camera219.util.Camera2Source;
import jp.ohwada.android.camera219.util.ToastMaster;

import jp.ohwada.android.camera219.ui.CameraSourcePreview;


/**
 * class MainActivity 
 * original : https://github.com/EzequielAdrianM/Camera2Vision
 * original : https://github.com/arktronic/cameraserve
 */
public class MainActivity extends Activity  {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";



    /**
     * key  for SharedPreferences
     */
   private final static String KEY_PORT = SettingsFragment.KEY_PORT;
   private final static String KEY_RESOLUTION =  SettingsFragment.KEY_RESOLUTION;
    private final static String KEY_ROTATION =  SettingsFragment.KEY_ROTATION;
    private final static String KEY_ALLOW_ALL_IPS = SettingsFragment.KEY_ALLOW_ALL_IPS;
    public final static String KEY_DISCOVERABLE = SettingsFragment.KEY_DISCOVERABLE;
   public final static String KEY_SSDP_ID = SettingsFragment.KEY_SSDP_ID;
    private final static String KEY_ABOVE_LOCK_SCREEN = SettingsFragment.KEY_ABOVE_LOCK_SCREEN;

    private final static int PORT_DEFAULT = MJpegServer.PORT_DEFAULT;
    private final static String PREF_PORT_DEFAULT = Integer.toString(PORT_DEFAULT);

   private final static String PREF_SSDP_ID_DEFAULT = "UNKNOWN";


/**
  * Request code 
  */
    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int REQUEST_CODE_SETTINGS = 102;

    private final static String COLON = " : ";


/**
 * Camera2Source
 */ 
    private Camera2Source mCamera2Source = null;


/**
 * CameraSourcePreview
 */ 
    private CameraSourcePreview mPreview;


/**
 *  TextView
 */ 
    private TextView mTextViewIpaddr;


/**
 * Camera permssion
 */ 
    private CameraPerm mCameraPerm;

/**
 * MJpegServer
 */ 
    private static MJpegServer mServer;

/**
 * SSDP Advertiser 
 */ 
    private static SsdpAdvertiser mSsdpAdvertiser;
    private static Thread mSsdpThread;


/**
 * Setting param
  */ 
    private  int mPortNum = PORT_DEFAULT;
    private  int mFrameWidth = Camera2Source.FRAME_WIDTH ;
    private  int mFrameHeight = Camera2Source.FRAME_HEIGHT;
    private  int mFrameRotation = Camera2Source.FRAME_ROTATION;

    private String mIpAddress;
    private  String mPort;
    private  String mSsdpId;
    private boolean isAllIps;
    private boolean isDiscoverable;
    private boolean isAboveLockScreen;


/**
 * onCreate
 */ 
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not sleep the screen
		getWindow().addFlags( 
			WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ); 

        // Make the screen FULL Screen		
		getWindow().addFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN );	

        setContentView(R.layout.activity_main);

// UI
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);
    mTextViewIpaddr = (TextView) findViewById(R.id.TextView_ipaddr);

 ImageButton btnSettings = (ImageButton) findViewById(R.id.Button_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startSettingsActivity();
            }
        }); // btnSettings

// util
    mCameraPerm = new CameraPerm(this);
    mCameraPerm.setRequestCode(REQUEST_CODE_CAMERA);

    mServer = new MJpegServer();

} // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");

        loadPreferences();

        if (isAboveLockScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }


        String text = mIpAddress + COLON +  mPort;
        mTextViewIpaddr.setText(text);


        // start Server
        mServer.setAllIpsAllowed(isAllIps);
        mServer.start(mPortNum);

        startSsdp();
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
        mServer.stop();
        //stopSsdp();
    }


/**
 * onDestroy
 */ 
    @Override
    protected void onDestroy() {
        log_d("onDestroy");
        super.onDestroy();
            // nop
    }


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        log_d("onRequestPermissionsResult: " + requestCode);
        switch(requestCode) {
            case REQUEST_CODE_CAMERA:
                mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
                // start Camera,  if granted
                startCameraSource();
                    break;
            case REQUEST_CODE_SETTINGS:
                    // nop
                    break;
        } // switch
} // onRequestPermissionsResult


/**
 * startSettingsActivity
 */ 
private void startSettingsActivity() {
        log_d("startSettingsActivity");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
}


/**
 * loadPreferences
 */ 
    private void loadPreferences() {
        log_d("loadPreferences");

        mIpAddress = NetworkUtil.getIPAddress();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        mIpAddress = NetworkUtil.getIPAddress();

        mPort= pref.getString(KEY_PORT, PREF_PORT_DEFAULT);
        mPortNum = parseInt(mPort);


// Camera2Source
    String strRotation = pref.getString(KEY_ROTATION, "0");
    mFrameRotation = parseInt(strRotation);

// default : 640x480
    String strResolution = pref.getString(KEY_RESOLUTION, "2");
    int num = parseInt(strResolution);
    Resources res = getResources();
    String[] labels = res.getStringArray(R.array.ResolutionText);
    String label = labels[num];
    String[] parts = label.split("x");
    mFrameWidth = Integer.parseInt(parts[0]);
    mFrameHeight = Integer.parseInt(parts[1]);

    isAllIps = pref.getBoolean(KEY_ALLOW_ALL_IPS, false);

// SsdpAdvertiser
    isDiscoverable = pref.getBoolean(KEY_DISCOVERABLE, false);
    String mSsdpId = pref.getString(KEY_SSDP_ID, PREF_SSDP_ID_DEFAULT);

// this Activity
        isAboveLockScreen = pref.getBoolean(KEY_ABOVE_LOCK_SCREEN, true);

    }

/**
 * parseInt
 */ 
private int parseInt(String str) {
        int val = 0;
        try {
                val = Integer.parseInt(str);
        } catch (NumberFormatException e) {
                e.printStackTrace();
        }
        return val;
}

/**
 * startSsdp()
 */ 
private void startSsdp() {
    mSsdpAdvertiser  = new SsdpAdvertiser();
    mSsdpAdvertiser.setIpAddres(mIpAddress);
    mSsdpAdvertiser.setPort(mPort);
    mSsdpAdvertiser.setServiceId(mSsdpId);
    mSsdpAdvertiser.setDiscoverable(isDiscoverable);
    mSsdpAdvertiser.start();
    mSsdpThread = new Thread(mSsdpAdvertiser);
    mSsdpThread.start();
}


/**
 * stopSsdp()
 */ 
private void stopSsdp() {
    if ( mSsdpAdvertiser != null) {
            mSsdpAdvertiser.stop();
    }
    try {
            if ( mSsdpThread != null) {
                mSsdpThread.join();
                mSsdpThread = null;
            }
    } catch (InterruptedException e) {
            // nop
    }
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

        camera2Source.setPreviewCallback(mPreviewCallback);
        camera2Source.setFrameParam(mFrameWidth, mFrameHeight, mFrameRotation);
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
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * PreviewCallback
 */ 
 Camera2Source.PreviewCallback mPreviewCallback = new Camera2Source.PreviewCallback() {
        @Override
          public void onPreview(byte[] bytes) {
                mServer.setFrame(bytes);
        }

    }; // PreviewCallback 

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
