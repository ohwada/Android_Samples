/**
 * Camera2 Sample
 * Color Effects
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.camera224;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

import jp.ohwada.android.camera224.ui.CameraSourcePreview;
import jp.ohwada.android.camera224.ui.AutoFitTextureView;


import jp.ohwada.android.camera224.util.Camera2Source;
import jp.ohwada.android.camera224.util.CameraPerm;
import jp.ohwada.android.camera224.util.ToastMaster;


/**
 * MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Camera2";
    	private final static String TAG_SUB = "MainActivity";


        // menu
    	private final static int MENU_GROUP_ID_EFFECT = 1;
    	private final static int MENU_GROUP_ID_RESOLUTION = 2;
	    private final static String MENU_SUB_TITLE_EFFECT = "Color Effect";
	    private final static String MENU_SUB_TITLE_RESOLUTION = "Resolution";


    // view
    private CameraSourcePreview mPreview;

    // Camera2Source
    private Camera2Source mCamera2Source = null;


    // utility
    private Camera2Feature mCamera2Feature;
    private CameraPerm mCameraPerm;
     private ImageUtil mImageUtil;


    // menu
    private int mEffectId;
    private String mEffect;
    private Size mResolutionSize;
    private List<Size> mResolutionList;


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);


        Button btnPicture = (Button) findViewById(R.id.Button_picture);
            btnPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            }); // btnPicture

     // utility
    mCamera2Feature = new Camera2Feature(this);
    mCameraPerm = new CameraPerm(this);
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
        super.onPause();
        log_d("onPause");
        stopCameraSource();
    }


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        mCameraPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
        startCameraSource();
} // onRequestPermissionsResult


/** 
 *  onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        setupColorEffectsMenu(menu);
        setupResolutionMenu(menu);

        return true;
}


/** 
 *  setupColorEffectsMenu
 */
private void setupColorEffectsMenu(Menu menu) {

        SubMenu subMenuEffect = menu.addSubMenu(MENU_SUB_TITLE_EFFECT);

        List<String> list = mCamera2Feature.getEffectList();

        int idx = 0;
        for (String effect: list) {
            subMenuEffect.add(MENU_GROUP_ID_EFFECT, idx, Menu.NONE, effect);
            idx++;
        }
}


/** 
 *  setupResolutionMenu
 */
private void setupResolutionMenu(Menu menu) {

        SubMenu subMenuResolution = menu.addSubMenu(MENU_SUB_TITLE_RESOLUTION);

        mResolutionList = mCamera2Feature.getResolutionList();

        int idx = 0;
        for (Size size: mResolutionList) {
                String caption = getResolutionCaption(size);
                subMenuResolution.add(MENU_GROUP_ID_RESOLUTION, idx, Menu.NONE, caption);
                idx++;
        } // for
}


/** 
 *  getResolutionCaption
 */
private String getResolutionCaption(Size size) {
            String width = Integer.valueOf(size.getWidth()).toString();
            String  height = Integer.valueOf(size.getHeight()).toString();
            String  caption = width + "x" + height;
            return caption;
}


/** 
 *  onOptionsItemSelected
 */
public boolean onOptionsItemSelected(MenuItem item) {
        log_d("called onOptionsItemSelected; selected item: " + item);

        int group_id = item.getGroupId();
        switch(group_id) {
            case  MENU_GROUP_ID_EFFECT:
                    setOptionEffect(item);
                    break;
            case MENU_GROUP_ID_RESOLUTION:
                    setOptionResolution(item);
                    break;
        }

        return true;
}


/** 
 *  setOptionEffect
 */
private void setOptionEffect(MenuItem item) {
            String effect = (String) item.getTitle();
            mEffect = effect;
            mEffectId = mCamera2Feature.getEffectId(effect);
            doColorEffect();
            log_d("effect= " + effect +" , id= "  + mEffectId);
            showToast(effect);
}


/** 
 *  setOptionResolution
 */
private void setOptionResolution(MenuItem item) {
            int id = item.getItemId();
            mResolutionSize = mResolutionList.get(id);
            String caption = getResolutionCaption(mResolutionSize);
            log_d("resolution " + caption);
            showToast(caption);
}


 /**
 * doColorEffect
 */
private void doColorEffect() {
    if(mCamera2Source != null) {
        mCamera2Source.doColorEffect(mEffectId);
    }
}


 /**
 * takePicture
 */
private void takePicture() {
              if(mCamera2Source != null) {
                mCamera2Source.takePicture(mEffectId, camera2SourcePictureCallback);
    }
} // takePicture


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
        Camera2Source camera2Source = null;

        			       camera2Source = createCameraSourceBack();
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
 * showToast
 */
private void showToast(String msg) {
        ToastMaster.makeText(this, msg, Toast.LENGTH_LONG).show();
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
        File file = mImageUtil.getOutputFileInExternalFilesDir(mEffect);
        mImageUtil.saveImageAsJpeg(image, file, mResolutionSize);
       String msg = "saved: " + file.toString();
        log_d(msg);
        showToast_onUI(msg);
} // procPictureTaken


/**
 * CameraErrorCallback
 */ 
 Camera2Source.ErrorCallback cameraErrorCallback = new Camera2Source.ErrorCallback() {
        @Override
        public void onError(String msg) {
            showErrorDialog_onUI(msg);
        }
    }; // CameraErrorCallback 


} //  class MainActivity
