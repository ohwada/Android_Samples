/**
 * Camera2 Sample
 * record Video using Camera2Source
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera213;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


import jp.ohwada.android.camera213.util.Camera2Source;
import jp.ohwada.android.camera213.util.CameraPerm;
import jp.ohwada.android.camera213.util.Permission;
import jp.ohwada.android.camera213.util.VideoParam;
import jp.ohwada.android.camera213.util.FileUtil;
import jp.ohwada.android.camera213.util.ToastMaster;
import jp.ohwada.android.camera213.ui.CameraSourcePreview;


/**
 * class MainActivity 
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
    public static final int REQUEST_CODE_CAMERA = 101;
    public static final int REQUEST_CODE_AUDIO = 102;
    public static final int REQUEST_CODE_STORAGE = 103;


/**
 *  Key for Preferences 
 */
    private final static String KEY_AUDIO = SettingsPreferenceFragment.KEY_AUDIO;
    private final static String KEY_STORAGE = SettingsPreferenceFragment.KEY_STORAGE;
    public final static String KEY_FORMAT = SettingsPreferenceFragment.KEY_FORMAT;


    /**
     * Permissions 
     */
    private static final String PERMISSION_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;


    /**
     * Camera2Source
     */
    private Camera2Source mCamera2Source = null;


    /**
     * CameraSourcePreview
     */
    private CameraSourcePreview mPreview;


    /**
     * Requesting Permission class for CAMERA
     */
    private CameraPerm mCameraPerm;
 

    /**
     * Requesting Permission class for RECORD_AUDIO
     */
    private Permission mAudioPerm;


    /**
     * Requesting Permission class for WRITE_EXTERNAL_STORAGE
     */
    private Permission mStoragePerm;


    /**
     * Button to record Video, or stop
     */
    private Button mButtonVideo;


    /**
     * Flag whether use the Front Camera or not
     * use the Back Camera, if false
     */
    private boolean usingFrontCamera = false;


    /**
     * Flag whether recording vide or not
     * use the Back Camera, if false
     */
   private boolean isRecordingVideo = false;



/**
 * onCreate
 */ 
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnFLip = (Button) findViewById(R.id.Button_flip);
    btnFLip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flipCameraFace();
                }
            }); // btnFLip

        Button btnSetting = (Button) findViewById(R.id.Button_setting);
    btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSettingsFragment();
                }
            }); // btnSetting

            mButtonVideo = (Button) findViewById(R.id.Button_video);
            mButtonVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procVideo();
                }
            }); // mButtonVideo

    // view
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);


    /**
     * Requesting Permission class 
     */
    mCameraPerm = new CameraPerm(this);

    mAudioPerm = new Permission(this);
    mAudioPerm.setPermission(PERMISSION_AUDIO);
    mAudioPerm.setRequestCode(REQUEST_CODE_AUDIO);

    mStoragePerm = new Permission(this);
    mStoragePerm.setPermission(PERMISSION_STORAGE);
    mStoragePerm.setRequestCode(REQUEST_CODE_STORAGE);

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
            case REQUEST_CODE_AUDIO:
                    mAudioPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults);
                    // nothing to do
                    break;
            case REQUEST_CODE_STORAGE:
                    boolean ret2 = mStoragePerm.onRequestPermissionsResult(requestCode, permissions,  grantResults);
                    if(ret2) {
                        // make the directory for app in DCIM,  if granted
                        FileUtil.mkDirInDCIM();
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
        if (KEY_AUDIO.equals(key) && value) {
                // request Permission,  when enable the feature
                mAudioPerm.requestPermissions();
        } else if (KEY_STORAGE.equals(key) && value) {
                // request Permission,  when enable the feature
                mStoragePerm.requestPermissions();
        }
    }


/**
 * flipCameraFace
 */ 
private void flipCameraFace() {
        if(usingFrontCamera) {
                stopCameraSource();
                usingFrontCamera = false;
                startCameraSource();
                showToast("flip to Back");
        } else {
                stopCameraSource();
                usingFrontCamera = true;
                startCameraSource();
                showToast("flip to Front");
           }
} // flipCameraFace


 /**
 *  procVideo
 */
private void procVideo() {
     if(isRecordingVideo) {
        stopVideo();
    } else {
        startVideo();
    }
} // procVideo


 /**
 *  startVideo
 */
private void startVideo() {
    log_d("startVideo");
    VideoParam param = createVideoParam();
    if(mCamera2Source != null){
        mCamera2Source.recordVideo(param, VideoCallback);
    }
} // startVideo


 /**
 *  createVideoParam
 */
private VideoParam createVideoParam() {
    log_d("createVideoParam");

    // get value of SharedPreferences
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean use_audio = pref.getBoolean(KEY_AUDIO, false);
        boolean use_storage = pref.getBoolean(KEY_STORAGE, false);
        String str_format = pref.getString(KEY_FORMAT, null);

    // output format 
        int int_format = 0;
    try {
        int_format = Integer.parseInt(str_format);
    } catch(NumberFormatException e) {
        e.printStackTrace();
    }

    // default output format : MPEG_4
        int audio_encoder = VideoParam.AUDIO_ENCODER_AAC;
        int video_encoder = VideoParam.VIDEO_ENCODER_H264;
        int output_format = VideoParam.VIDEO_OUTPUT_FORMAT_MPEG_4;
        String file_ext = VideoParam.VIDEO_FILE_EXT_MP4;

    // set output format
    switch(int_format) {
        case VideoParam.VIDEO_OUTPUT_FORMAT_THREE_GPP: 
            // 1:
            audio_encoder = VideoParam.AUDIO_ENCODER_AMR_NB;
            video_encoder = VideoParam.VIDEO_ENCODER_H263;
            output_format = VideoParam.VIDEO_OUTPUT_FORMAT_THREE_GPP;
             file_ext = VideoParam.VIDEO_FILE_EXT_3GP;
            break;
        case VideoParam.VIDEO_OUTPUT_FORMAT_WEBM:
            // 9:
            audio_encoder = VideoParam.AUDIO_ENCODER_AAC;
            video_encoder = VideoParam.VIDEO_ENCODER_VP8;
            output_format = VideoParam.VIDEO_OUTPUT_FORMAT_WEBM;
             file_ext = VideoParam.VIDEO_FILE_EXT_WEBM;
            break;
    }

        VideoParam param = new VideoParam();
        param.setVideoEncoder(video_encoder);
        param.setOutputFormat(output_format);

    // audio
    if(use_audio) {
            boolean ret1 = mAudioPerm.checkSelfPermission();
            if(ret1) {
                    // use the microphone when permission granted
                    param.setAudioSource(VideoParam.AUDIO_SOURCE_MIC);
                    param.setAudioEncoder(audio_encoder);
            }
    }

    // output file
        File file = FileUtil.getOutputFileInExternalFilesDir(this, VideoParam.VIDEO_FILE_PREFIX,  file_ext);
    if(use_storage) {
            boolean ret2 = mStoragePerm.checkSelfPermission();
            if(ret2) {
                    // save File in DCIM when permission granted
                    file = FileUtil.getOutputFileInDCIM(VideoParam.VIDEO_FILE_PREFIX,  file_ext); 
            }
    }
        param.setOutputFile(file);

    log_d("VideoParam:" + param.toString());
    return param;
} // createVideoParam



 /**
 *  stopVideo
 */
private void stopVideo() {
    if(mCamera2Source != null){
        mCamera2Source.stopVideo();
    }
} //  stopVideo


/**
 * createCameraSourceFront
 */ 
    private Camera2Source createCameraSourceFront() {
        log_d("createCameraSourceFront");
            Camera2Source camera2Source = new 
Camera2Source.Builder(this) 
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_FRONT)
                    .setErrorCallback(cameraErrorCallback)
                    .build();

        return camera2Source;
} // createCameraSourceFront


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
        if(mCamera2Source != null) {
            log_d("already startCameraSource");
            return;
        }

        if(mCameraPerm.requestCameraPermissions()) {
                log_d("not permit");
                return;
        }
        Camera2Source camera2Source = null;
        		if(usingFrontCamera) {
        			        camera2Source = createCameraSourceFront();
        		} else {
        			       camera2Source = createCameraSourceBack();
        		}
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
 * CameraErrorCallback
 */ 
 Camera2Source.ErrorCallback cameraErrorCallback = new Camera2Source.ErrorCallback() {
        @Override
        public void onError(String msg) {
            showErrorDialog_onUI(msg);
        }
    }; // CameraErrorCallback 


/**
 *  Camera2Source.VideoCallback
 */ 
   Camera2Source.VideoCallback VideoCallback = new Camera2Source.VideoCallback() {
        @Override
        public void onVideoStart() {
            log_d("onVideoStart");
            isRecordingVideo = true;
            // set "stop" the button display when start recording
            setVideoButtonText_onUI(R.string.stop_video);
            showToast_onUI("Video STARTED!");
        }
        @Override
        public void onVideoStop(String videoFile) {
            log_d("onVideoStop");
            isRecordingVideo = false;
            String msg = "saved: " + videoFile;
            setVideoButtonText_onUI(R.string.record_video);
            showToast_onUI(msg);
            log_d(msg);
        }
        @Override
        public void onVideoError(String error) {
            log_d("onVideoError");
            isRecordingVideo = false;
            setVideoButtonText_onUI(R.string.record_video);
            String msg = "Video Error: "+error;
            showToast_onUI(msg);
            log_d(msg);
        }
    }; // VideoCallback


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
