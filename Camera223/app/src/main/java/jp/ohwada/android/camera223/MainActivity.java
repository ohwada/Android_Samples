/**
 * Camera2 Sample
 * RTSP Server
 * 2019-08-01 K.OHWADA
 */ 
package jp.ohwada.android.camera223;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jp.ohwada.android.camera223.ui.CameraSourcePreview;
import jp.ohwada.android.camera223.util.Camera2Source;
import jp.ohwada.android.camera223.util.CameraPerm;
import jp.ohwada.android.camera223.util.Permission;
import jp.ohwada.android.camera223.util.ToastMaster;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.video.VideoQuality;




/**
 * class MainActivity
 * 
 * TODO
 * the phenomenon varies depending on the size of the image.
 * SD Low quality( 176x144)
 * displayed black screen
 * 
 *SD High quality ( 480x360)
 *displayed green screen
 *
 * VGA ( 640x 480 )
 *displays correctly,  but response is slow
 * does not easily reflect, when chang direction of the camera. 
it takes about 10 seconds.
 *
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";


/**
 * Request code for Permissions
  */
    public static final int REQUEST_CODE_CAMERA = 101;
    public static final int REQUEST_CODE_AUDIO = 102;
    public static final int REQUEST_CODE_SETTINGS = 103;


/**
 * key  for SharedPreferences
 */
    private final static String KEY_AUDIO = SettingsFragment.KEY_AUDIO;
   private final static String KEY_PORT = SettingsFragment.KEY_PORT;
    private final static String KEY_RESOLUTION =  SettingsFragment.KEY_RESOLUTION;
    private final static String KEY_ENCODER =  SettingsFragment.KEY_ENCODER;

    private final static int PORT_DEFAULT = 
    RtspServer.DEFAULT_RTSP_PORT;
    private final static String PREF_PORT_DEFAULT = Integer.toString(PORT_DEFAULT);




 /**
 * Video param
  */
    // VGA size
    private static final int FRAME_WIDTH = 640;
    private static final int FRAME_HEIGHT = 480;
    private static final int FRAME_RATE = 10; // 10fps
    private static final int BIT_RATE = 100000;// 1Mbps

    private final static String COLON = " : ";


/**
 * CameraSourcePreview
 */ 
    private CameraSourcePreview mPreview;


/**
 *  View
 */ 
    private TextView mTextViewIpaddr;
    private TextView mTextViewConnect;
    private TextView mTextViewSize;


/**
 * Camera2Source
 */ 
    private Camera2Source  mCamera2Source;


/**
 * Camera permssion
 */ 
    private CameraPerm mCameraPerm;


/**
 * RtspServer
 */ 
    private RtspServer mServer;


/**
 * Setting param
  */ 
    private  int mPort = PORT_DEFAULT;

    private int mVideoEncoder = SessionBuilder.VIDEO_H264;

    private int  mVideoWidth = 640;
    private int mVideoHeight = 480;
    private int mFrameRate = 10; // 10fps
    private int mBitRate = 400000; // 400Kbps

    private int mAudioEncoder = SessionBuilder.AUDIO_NONE;


/**
 * onCreate
 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log_d( "onCreate");

        // Do not sleep the screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Make the screen FULL Screen		
		getWindow().addFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN );	

		setContentView(R.layout.activity_main);

        // View
		mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mTextViewIpaddr = (TextView) findViewById(R.id.TextView_ipaddr);
        mTextViewConnect = (TextView) findViewById(R.id.TextView_connect);
        mTextViewSize = (TextView) findViewById(R.id.TextView_size);
		
 ImageButton btnSettings = (ImageButton) findViewById(R.id.Button_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startSettingsActivity();
            }
        }); // btnSettings


        // permission
		mCameraPerm = new CameraPerm(this);
        mCameraPerm.setRequestCode(REQUEST_CODE_CAMERA);

        mServer = new RtspServer();
	}


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        loadPreferences();

        String ipAddress = NetworkUtil.getIPAddress();
        String text = ipAddress + COLON +  mPort;
        mTextViewIpaddr.setText(text);

		startServer();
        startCameraSource();
}


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        log_d("onPause");
        super.onPause();
        stopCameraSource();
		stopServer();
}


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        log_d("onRequestPermissionsResult: " + requestCode);
        switch(requestCode) {
            case REQUEST_CODE_CAMERA:
                mCameraPerm.onRequestPermissionsResult(requestCode, permissions, grantResults);
                // start Camera,  if granted
                startCameraSource();
                break;
            case REQUEST_CODE_SETTINGS:
                    // nop
                    break;
        }

}


/**
 * startSettingsActivity
 */ 
private void startSettingsActivity() {
        log_d("startSettingsActivity");

        //stopCameraSource();
        //stopServer();
        //stopEncoder();

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
}

/**
 * loadPreferences
 */ 
    private void loadPreferences() {
        log_d("loadPreferences");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);


        boolean use_audio = pref.getBoolean(KEY_AUDIO, false);
        if(use_audio) {
                // ok
		        mAudioEncoder = SessionBuilder.AUDIO_AMRNB;
        }


        String port = pref.getString(KEY_PORT, PREF_PORT_DEFAULT);
        mPort = parseInt(port);

        // default : 640x480
        String strResolution = pref.getString(KEY_RESOLUTION, "2");
        int numResolution = parseInt(strResolution);

        List<VideoParam> list = VideoParamUtil.getList();

        VideoParam videoParam = null;
        if((numResolution >= 0)&&(numResolution < list.size())) {
                videoParam = list.get(numResolution);
        }
        if(videoParam != null ) {
                log_d( videoParam.toString() );
                mVideoWidth = videoParam.getWidth();
                mVideoHeight = videoParam.getHeight();
                mFrameRate = videoParam.getFrameRate();
                mBitRate = videoParam.getBitRate();
        }

        Resources res = getResources();
        String[] entries = res.getStringArray(R.array.resolution_entries);
        String entry = entries[numResolution];
        mTextViewSize.setText(entry);


        // default : H264
        String strEncoder = pref.getString(KEY_ENCODER, "0");
        int numEncoder = parseInt(strEncoder);

    switch(numEncoder) {
        case 0:
            mVideoEncoder = SessionBuilder.VIDEO_H264;
            break;
        case 1:
            mVideoEncoder = SessionBuilder.VIDEO_H263;
            break;
    }

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
 * startServer
 */ 
private void startServer() {

        VideoQuality quality = new VideoQuality( mVideoWidth,   mVideoHeight, mFrameRate, mBitRate );

		// Configures the SessionBuilder
		SessionBuilder builder = SessionBuilder.getInstance();
		builder.setContext(getApplicationContext());
		builder.setAudioEncoder( mAudioEncoder);
		builder.setVideoEncoder(mVideoEncoder);
		builder.setVideoQuality(quality);

        if( mServer != null) {
            mServer.setConnectCallback(connectCallback);
            mServer.start();
        }
}


/**
 * stopServer
 */ 
private void stopServer() {
        if( mServer != null) {
            mServer .stop();
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

        //File file = VideoFile.getOutputFile(this);
        camera2Source.setImageSize(mVideoWidth, mVideoHeight);
        //camera2Source.setVideoParam(mFrameRate, mBitRate);
        //camera2Source.setVideoFile(file);
        camera2Source.setPreviewCallback(mPreviewCallback);

        return camera2Source;
} // createCameraSourceBackBack

 
/**
 * startCameraSource
 */ 
    private void startCameraSource() {
        log_d("startCameraSource");

        if(mCameraPerm.requestCameraPermissions()) {
                log_d("Camera not permit");
                return;
        }

        if(mCamera2Source != null) {
                log_d("already start");
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
  * showErrorDialog on the UI thread.
 */
public  void showErrorDialog_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorDialog(msg);
                }
    }); // Runnable

}


/**
  * shows an error message dialog.
 */
private void showErrorDialog(String msg) {
             new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton(R.string.button_ok, null)
                    .show();
} 


/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

/**
 * ConnectCallback
 */
private  RtspServer.ConnectCallback connectCallback = new RtspServer.ConnectCallback() {
        @Override
		public void onConnect(String clientAddress) {
                log_d("onConnect: " + clientAddress);
                String msg = "Connect " + clientAddress;
                procConnect_onUI(msg);
        }
        @Override
		public void onDisConnect() {
            log_d("onDisConnect");
                String msg = "disConnect ";
                procDisConnect_onUI(msg);
        }

}; // ConnectCallback


/**
  * procConnect_onUI
 */
public  void procConnect_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextViewConnect.setText(msg);
                    showToast(msg);
                }
    }); // Runnable

}


/**
  * procDisConnect_onUI
 */
public  void procDisConnect_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextViewConnect.setText("");
                    showToast(msg);
                }
    }); // Runnable

}


/**
 * PreviewCallback
 */ 
 Camera2Source.PreviewCallback mPreviewCallback = new Camera2Source.PreviewCallback() {
        @Override
          public void onPreviewFrame(byte[] data, long timestamp) {
                // log_d("onPreviewFrame");
                if(mServer != null ) {
                        mServer.setPreviewFrame(data, timestamp);
                }
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
