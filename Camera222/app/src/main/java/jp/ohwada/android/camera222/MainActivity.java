/**
 * Camera2 Sample
 * streaming H264 using MediaCodec
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera222;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import jp.ohwada.android.camera222.util.Camera2Source;
import jp.ohwada.android.camera222.util.CameraPerm;
import jp.ohwada.android.camera222.util.ToastMaster;

import jp.ohwada.android.camera222.ui.CameraSourcePreview;


/**
 * class MainActivity
 * streaming H264 
 * 
 * Note :  
 * the ffplay can play H264 stream
 * can not play neither H263 nor VP8
 * 
  * original : https://github.com/get2abhi/Camera2PreviewStreamMediaCodecVideoRecording
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
    private final static String KEY_ENCODER =  SettingsFragment.KEY_ENCODER;

    private final static int PORT_DEFAULT = TcpServer.PORT_DEFAULT;
    private final static String PREF_PORT_DEFAULT = Integer.toString(PORT_DEFAULT);


/**
  * Request code 
  */
    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int REQUEST_CODE_SETTINGS = 102;


/**
  * VideoEncoder 
  */
    private final static String[] VIDEO_ENCODER_TYPE_ARRAY = {"video/avc", "video/3gpp", "video/x-vnd.on2.vp8"};


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
    private TextView mTextViewConnect;
    private TextView mTextViewSize;


/**
  * Requesting Permission class for CAMERA
  */
    private CameraPerm mCameraPerm;
 

 /**
   * VideoEncoder
   */
    private static VideoEncoder mEncoder;


 /**
   * TcpServer
   */
    private static TcpServer mServer;


    /**
     * Flag whether sending data to client or not
     */
    private boolean isRunning = false;



/**
 * Setting param
  */ 
    private String mVideoEncoderType;

    private VideoParam mVideoParam;
    private int  mVideoWidth = 640;
    private int mVideoHeight = 480;

    private  int mPort = PORT_DEFAULT;


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

    // view
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


    /**
     * Requesting Permission class 
     */
    mCameraPerm = new CameraPerm(this);
    mCameraPerm.setRequestCode(REQUEST_CODE_CAMERA);

} // onCreate



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
} // onResume


/**
 * onPause
 */ 
@Override
protected void onPause() {
        log_d("onPause");
        super.onPause();
        stopCameraSource();
        stopServer();
        stopEncoder();
}


/**
 * onDestroy
 */ 
    @Override
    protected void onDestroy() {
        log_d("onDestroy");
        super.onDestroy();
} // onDestroy


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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String port = pref.getString(KEY_PORT, PREF_PORT_DEFAULT);
        mPort = parseInt(port);


        // default : 640x480
        String strResolution = pref.getString(KEY_RESOLUTION, "2");
        int numResolution = parseInt(strResolution);

        List<VideoParam> list = VideoParamUtil.getList();

        mVideoParam = list.get(numResolution);
        log_d( mVideoParam.toString() );

        mVideoWidth = mVideoParam.getWidth();
        mVideoHeight = mVideoParam.getHeight();

        Resources res = getResources();
        String[] entries = res.getStringArray(R.array.resolution_entries);
        String entry = entries[numResolution];
        mTextViewSize.setText(entry);


        // default : H264
        String strEncoder = pref.getString(KEY_ENCODER, "0");
        int numEncoder = parseInt(strEncoder);
        mVideoEncoderType = VIDEO_ENCODER_TYPE_ARRAY[numEncoder];

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
    
        camera2Source.setImageSize(mVideoWidth, mVideoHeight);
    camera2Source.setPreviewCallback(cameraPreviewCallback);
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
 * StartServer
 */ 
 private void startServer() {

        log_d("StartServer");
    mServer = new TcpServer();
    mServer.setPort(mPort);
    mServer.setCallback(serverCallback);
    mServer.start();

} // startServer


 /**
 *  stopServer
 */
private void stopServer() {
        log_d("stopServer");
        isRunning = false;
         if (mServer != null) {
                mServer.stop();
                mServer = null;
        }
} // stopServer




 /**
 *  createEncoder
 */
public VideoEncoder createEncoder() {
        log_d("createEncoder");

    VideoEncoder encoder = new VideoEncoder();
    encoder.setVideoSize(mVideoWidth, mVideoHeight);
    encoder.setFrameRate(mVideoParam.getFrameRate());
    encoder.setBitRate(mVideoParam.getBitRate());
    encoder.setIframeInterval(mVideoParam.getIframeInterval());
    encoder.setCallback(encoderCallback); 
    return encoder;
} // createEncoder


/**
 * stopEncoder
 */ 
 private void stopEncoder() {
    log_d("stopEncoder");
    isRunning = false;
    if(mEncoder != null) {
        mEncoder.stop();
        mEncoder = null;
    }
} // stopEncoder


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
private static void log_d( int res_id ) {
    //log_d( getString(res_id) );
} // log_d


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * .ServerCallback
 */ 
private  TcpServer.ServerCallback  serverCallback = new TcpServer.ServerCallback() {
        @Override
        public void onWait() {
                log_d("onWait");
                String msg = "waiting";
                showTextConnect_onUI(msg);
                showToast_onUI(msg);
        }
        @Override
        public void onConnect(String clientAddress) {
                String msg = "onConnect: " + clientAddress;
                procConnect(clientAddress);
                showTextConnect_onUI(msg);
                showToast_onUI(msg);
        }
        @Override
        public void onDisConnect() {
                log_d("onDisConnect");
                showTextConnect_onUI("");
                showToast_onUI("disConnect");
        }

}; // ServerCallback


 /**
 *  procConnect
 *  start the encoder after connecting from the client
 */
public void procConnect(String clientAddress) {
        log_d("procConnect");

    mEncoder = createEncoder();
    mEncoder.start();
    isRunning = true;

    String msg = "Connect " + clientAddress;
    showTextConnect_onUI(msg);
    showToast_onUI(msg);

} // procConnect


/**
  * showTextConnect_onUI
 */
public  void showTextConnect_onUI(final String msg) {
    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextViewConnect.setText(msg);
                }
    }); // Runnable

} // showTextConnect_onUI


/**
 * EncoderCallback
 */ 
VideoEncoder.EncoderCallback encoderCallback = new VideoEncoder. EncoderCallback() {
        @Override
        public void onOutput(byte[] data) {

                    // send data when callback from the encoder
                    if (isRunning && (mServer != null)) {
                        mServer.sendData(data);
                    }
        }
 }; // EncoderCallback

/**
 * PreviewCallback
 */ 
Camera2Source.PreviewCallback cameraPreviewCallback = new Camera2Source.PreviewCallback() {
        public void onPreviewFrame(byte[] frame, long timestamp) {

            // input data in the encoder when callback from camera
            if (isRunning && (mEncoder != null)) {
                mEncoder.setFrame(frame, timestamp);
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
