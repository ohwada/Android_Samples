/**
 * Cloud Vision Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.cloudvision2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


/** 
 *  class BaseActivity
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class BaseActivity extends Activity {


    // debug
	protected final static boolean D = true;
    protected final static String TAG = "CloudVision";
    protected final static String TAG_BASE = "BaseActivity";


/**
 * Request Code
 */ 
    protected static final int REQUEST_CODE_GALLERY_PERMISSIONS = 101;

    public static final int REQUEST_CODE_CAMERA_PERMISSIONS = 102;

    public static final int REQUEST_CODE_COPY_PERMISSIONS = 103;

    protected static final int  REQUEST_CODE_GALLERY_IMAGE = 104;
    public static final int  REQUEST_CODE_CAMERA_IMAGE = 105;


/**
 * File extension of File in Asset folder
 */ 
    protected final static String FILE_EXT = ".jpg";


/**
 * TextView for the result of image recognition
 */ 
    protected TextView mTextViewDetails;


/**
 * ImageView for selected image
 */ 
    protected ImageView mImageViewMain;

/**
 * VisionClient
 */ 
    protected VisionClient mVisionClient;

/**
 * utility for File
 */ 
      protected FileUtil mFileUtil;


/**
 * utility for Bitmap
 */
      protected BitmapUtil mBitmapUtil;


/**
 * utility for Camera
 */
      protected CameraUtil mCameraUtil;


/**
 * Permission
 */ 
      protected Permission mGalleryPerm;
      protected Permission mCameraPerm;
      protected Permission mCopyPerm;


/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


/** 
 *  procCreate()
 */
    protected void procCreate() {

        setContentView(R.layout.activity_main);

        Button btnCopy = findViewById(R.id.Button_copy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyFiles();
            }
        }); // btnCopy


        Button btnStart = findViewById(R.id.Button_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDialog();
            }
        }); // btnStart


        mTextViewDetails = (TextView)findViewById(R.id.TextView_details);
        mImageViewMain = (ImageView)findViewById(R.id.ImageView_main);

        mVisionClient = new VisionClient(this);
        mCameraUtil = new CameraUtil(this);
        mFileUtil = new FileUtil(this);
        mBitmapUtil = new BitmapUtil(this);

        mGalleryPerm = new Permission(this);
        mGalleryPerm.setRequestCode(REQUEST_CODE_GALLERY_PERMISSIONS);
        mGalleryPerm.setPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        mCameraPerm = new Permission(this);
        mCameraPerm.setRequestCode(REQUEST_CODE_CAMERA_PERMISSIONS);
        mCameraPerm.setPermissions( Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);

        mCopyPerm = new Permission(this);
        mCopyPerm.setRequestCode(REQUEST_CODE_COPY_PERMISSIONS);
        mCopyPerm.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    } // procCreate


/** 
 *  onRequestPermissionsResult
 */
@Override
public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSIONS:
                if (mCameraPerm.isGrantRequestPermissionsResult(requestCode, grantResults)) {
                    startCamera();
                }
                break;
            case REQUEST_CODE_GALLERY_PERMISSIONS:
                if (mGalleryPerm.isGrantRequestPermissionsResult(requestCode, grantResults)) {
                            startGalleryChooser();
                }
                break;
            case REQUEST_CODE_COPY_PERMISSIONS:
                if (mCopyPerm.isGrantRequestPermissionsResult(requestCode, grantResults)) {
                            copyFiles();
                }
                break;
        }
}


/** 
 *  onActivityResult
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log_base("onActivityResult: " + requestCode);
        procActivityResult(requestCode, resultCode, data);
} // onActivityResult


/** 
 *  procActivityResult
 */
protected void procActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode ==  REQUEST_CODE_GALLERY_IMAGE ) && (resultCode == RESULT_OK ) && (data != null )) {
            procImageUri(data.getData());
        } else if ((requestCode ==  REQUEST_CODE_CAMERA_IMAGE ) && (resultCode == RESULT_OK )) {
            Uri photoUri = mCameraUtil.getPhotoUri();
            procImageUri(photoUri);
        }

} // procActivityResult



/** 
 *   copyFiles from Asset folder to ExternalStoragePublicPictures
 */
protected void copyFiles() {
        if (mCopyPerm.requestPermissions()) {
            log_base("copyFiles: not perm");
            return;
        }
    mFileUtil.mkDirInExternalStoragePublicPictures();
    boolean ret = mFileUtil.copyFilesAssetToExternalStoragePublicPictures( FILE_EXT );
    if(ret) {
        showToast("copy successful");
    } else {
        showToast("copy faild");
    }
} // copyFiles



/** 
 *  showSelectDialog
 */
protected void showSelectDialog() {
             new AlertDialog.Builder(this)
            .setMessage(R.string.dialog_select_prompt)
            .setPositiveButton(R.string.dialog_select_gallery, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startGalleryChooser();
                }
            }) // setPositiveButton
            .setNegativeButton(R.string.dialog_select_camera, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startCamera();
                }
            }) // setPositiveButton
            .show();

}


/** 
 *  startGalleryChooser
 */
protected void startGalleryChooser() {
        if (mGalleryPerm.requestPermissions()) {
            log_base("startGalleryChooser: not perm");
            return;
        }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            String title = getString(R.string.chooser_title_gallery);
            startActivityForResult(Intent.createChooser(intent, title),
                     REQUEST_CODE_GALLERY_IMAGE);

} // startGalleryChooser


/** 
 *  startCamera
 */
protected void startCamera() {

        if (mCameraPerm.requestPermissions()) {
            log_base("startCamera: not perm");
            return;
        }

            Intent intent = mCameraUtil.createIntent();
            startActivityForResult(intent,  REQUEST_CODE_CAMERA_IMAGE);

} // startCamera





/** 
 *  procImageUri
 *  get Bitmap from Uri
 */
protected void procImageUri(Uri uri) {
    log_base("procImageUri: " + uri.toString());
    Bitmap bitmap_orig = mBitmapUtil.getBitmapFromMediaStore(uri);
    if (bitmap_orig == null ) {
                showToast(R.string.image_picker_error);
                return;
    }
    Bitmap bitmap_scaled  =
                        mBitmapUtil.getScaledBitmap( bitmap_orig);
    mImageViewMain.setImageBitmap(bitmap_scaled);
    showUploadDialog(bitmap_scaled);
}


/** 
 *  showUploadDialog
 *  comfirm to upload to CloudVision
 */
protected void showUploadDialog(final Bitmap bitmap) {
    log_base("showUploadDialog");
    if(bitmap == null) return;
    ImageView imageView = new ImageView(this);
    imageView.setImageBitmap(bitmap);
             new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_upload_title)
            .setView(  imageView )
            .setPositiveButton(R.string.button_ok, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                     callCloudVision(bitmap);
                }
            }) // setPositiveButton
            .setNegativeButton(R.string.button_cancel, null)
            .show();
} // showUploadDialog


/** 
 *  callCloudVision
 */
    protected void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        mTextViewDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        mVisionClient.callCloudVision(bitmap, new VisionClient.VisionCallback(){

            @Override
            public void onPostExecute(String result) {
                procPostExecute(result);
            }

    }); // VisionCallback

 } // callCloudVision


/**
 * procPostExecute
 */
protected void procPostExecute(String result) {
                    String text = getString(R.string.response_header);
                    if (result == null ) {
                            text += "no result";
                    } else {
                            text += result;
                    }
                    mTextViewDetails.setText(text);
} // procPostExecute


/**
 * showToast
 */
protected void showToast( int res_id ) {
		ToastMaster.makeText( this, res_id, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * showToast
 */
protected void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
protected void log_base( String msg ) {
	    if (D) Log.d( TAG, TAG_BASE + " " + msg );
} // log_base


} // class BaseActivity
