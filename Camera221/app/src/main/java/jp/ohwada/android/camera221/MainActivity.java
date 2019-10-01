/**
 * Camera2 Sample
 * record Video using MediaCodec
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera221;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import jp.ohwada.android.camera221.util.Camera2Source;
import jp.ohwada.android.camera221.util.CameraPerm;
import jp.ohwada.android.camera221.util.ToastMaster;

import jp.ohwada.android.camera221.ui.CameraSourcePreview;


/**
 * class MainActivity 
 * record Video
 * 
 * Note :  
 * the VLC and the ffplay can play H264 file as Video
 * they can not play neither H263 nor VP8
 * 
  * original : https://github.com/get2abhi/Camera2PreviewStreamMediaCodecVideoRecording
 */
public class MainActivity extends Activity {


    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MainActivity";

/**
 * key  for SharedPreferences
 */
    private final static String KEY_RESOLUTION =  SettingsFragment.KEY_RESOLUTION;
    private final static String KEY_ENCODER =  SettingsFragment.KEY_ENCODER;


/**
  * Request code 
  */
    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int REQUEST_CODE_SETTINGS = 102;


/**
  * VideoEncoder 
  */
    private final static String[] VIDEO_ENCODER_TYPE_ARRAY = {"video/avc", "video/3gpp", "video/x-vnd.on2.vp8"};

    private final static String[] VIDEO_EXT_ARRAY = {".h264", ".h263", ".vp8"};


/**
 * Camera2Source
 */
    private Camera2Source mCamera2Source = null;


/**
  * CameraSourcePreview
 */
    private CameraSourcePreview mPreview;

/**
  * Button to record Video, or stop
  */
    private Button mButtonVideo;


/**
  * Requesting Permission class for CAMERA
 */
    private CameraPerm mCameraPerm;
 

 /**
   * VideoEncoder
   */
    private static VideoEncoder mEncoder;


 /**
   * OutputFile
   */
      private File mOutputFile;
      private FileOutputStream   mFileOutputStream;


    /**
     * Flag whether recording vide or not
     */
   private boolean isRunning = false;


/**
 * Setting param
  */ 
    private String mVideoEncoderType;
    private String mVideoExt;
    private VideoParam mVideoParam;
    private int  mVideoWidth = 640;
    private int mVideoHeight = 480;


/**
 * onCreate
 */ 
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // view
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

        mButtonVideo = (Button) findViewById(R.id.Button_video);
        mButtonVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procVideo();
                }
        }); // mButtonVideo

        Button btnSetting = (Button) findViewById(R.id.Button_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSettingsActivity();
                }
        }); //  btnSetting


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
        stopEncoder();
}


/**
 * onDestroy
 */ 
    @Override
    protected void onDestroy() {
        log_d("onDestroy");
        super.onDestroy();
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

        // default : 640x480
        String strResolution = pref.getString(KEY_RESOLUTION, "1");
        int numResolution = parseInt(strResolution);

        List<VideoParam> list = VideoParamUtil.getList();

        mVideoParam = list.get(numResolution);
        log_d( mVideoParam.toString() );

        mVideoWidth = mVideoParam.getWidth();
        mVideoHeight = mVideoParam.getHeight();

        // default : H264
        String strEncoder = pref.getString(KEY_ENCODER, "0");
        int numEncoder = parseInt(strEncoder);
        mVideoEncoderType = VIDEO_ENCODER_TYPE_ARRAY[numEncoder];
        mVideoExt = VIDEO_EXT_ARRAY[numEncoder];

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
 *  procVideo
 *  toggle to start and stop Video
 */
private void procVideo() {
     if(isRunning) {
        stopVideo();
    } else {
        startVideo();

    }
} // procVideo


 /**
 *  startVideo
 *  start the encoder after click button
 */
private void startVideo() {
    log_d("startVideo");

    mOutputFile = FileUtil.getOutputFile(this, mVideoWidth, mVideoHeight, mVideoExt);
    mFileOutputStream = getFileOutputStream(mOutputFile);

    mEncoder = createEncoder();
    mEncoder.start();

    isRunning = true;

    mButtonVideo.setText(R.string.button_stop);
    showToast("start");
} // startVideo


 /**
 *  stopVideo
 */
private void stopVideo() {
    log_d("stopVideo");
    isRunning = false;
    stopEncoder();
    clloseFileOutputStream();

    mButtonVideo.setText(R.string.button_start);

    String msg = "saved " + mOutputFile.toString();
    showToast( msg );
} //  stopVideo


 /**
 *  createEncoder
 */
public VideoEncoder createEncoder() {
        log_d("createEncoder");

    VideoEncoder encoder = new VideoEncoder();
    encoder.setVideoEncoderType(mVideoEncoderType);
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
    if(mEncoder != null) {
        mEncoder.stop();
        mEncoder = null;
    }
} // stopEncoder

 /**
 *  getFileOutputStream
 */
private FileOutputStream getFileOutputStream(File file) {
    FileOutputStream fos = null;
    try {
            fos = new FileOutputStream(file, true);
    } catch (IOException e) {
            e.printStackTrace();
        }
        return fos;
}


/**
 * writeFileOutputStream
 */ 
private void writeFileOutputStream(byte[] data) {
        try {
                if (mFileOutputStream != null) {
                    mFileOutputStream.write(data);
                }
        } catch (IOException e) {
                        e.printStackTrace();
        }
}


/**
 * clloseFileOutputStream
 */ 
private void clloseFileOutputStream() {
    try {
        if(mFileOutputStream != null) {
                    mFileOutputStream.flush();
                    mFileOutputStream.close();
        }
    } catch (IOException e) {
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
 * EncoderCallback
 */ 
private VideoEncoder.EncoderCallback encoderCallback = new VideoEncoder.EncoderCallback() {
        @Override
        public void onOutput(byte[] data) {
                writeFileOutputStream(data);
         }
}; // EncoderCallback


/**
 * PreviewCallback
 */ 
Camera2Source.PreviewCallback cameraPreviewCallback = new Camera2Source.PreviewCallback() {
    @Override
    public void onPreviewFrame(byte[] frame, long timestamp){            
            //log_d("onPreviewFrame");
        if(isRunning&&( mEncoder != null)) {
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



/**
 * setVideoButtonText_onUI
 */ 
private void setVideoButtonText_onUI(final int res_id) { 
            final String text = getString(res_id);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mButtonVideo.setText(text);
                }
            }); // runOnUiThread
} // setVideoButtonText_onUI


} // class MainActivity 
