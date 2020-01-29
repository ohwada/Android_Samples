/**
 * Crop Intent Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/java-lang-programming/Android-Media-Demo/blob/master/app/src/main/java/java_lang_programming/com/android_media_demo/ImageSelectionCropDemo.java
 */
package jp.ohwada.android.cropintent1;



/**
 * Copyright (C) 2017 Programming Java Android Development Project
 * Programming Java is
 * <p>
 * http://java-lang-programming.com/ja/articles/74
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * class MainActivity
 * crop selected Image with System Intent
 */
public class MainActivity extends Activity {

    // debug
    private static final String TAG = "MainActivity";


/**
 * Request Code
 */
    private final static int REQUEST_CODE_CHOOSER = 101;

    private final static int REQUEST_CODE_CROP = 102;

    private final static int REQUEST_CODE_PERMISSION_CHOOSER = 103;

    private final static int REQUEST_CODE_PERMISSION_COPY = 104;

    private static final String FILE_EXT = ".png";


/**
 * Chooser Intent
 */
    private final static String INTENT_TYPE = "image/*";

    private static final List<String> CHOOSER_INTENT_MIME_TYPES = Collections
            .unmodifiableList(new LinkedList<String>() {
                {
                    add("image/jpeg");
                    add("image/jpg");
                    add("image/png");
                }
            });


    private final static String FILE_NAME_TEMP = "selected_temp_image.jpg";


    /**
     * Permissions required to read and write external storage.
     */
    private static String[] EXTERNAL_STORAGE_PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE, 
        Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private float ALPHA_BLANK = 0.1f;

    private float ALPHA_SELECTED = 1.0f;

    private ImageView mImageViewSelected;


    private Uri mSelectedUri;

    private Bitmap mSelectedBitmap;

    private File mSelectedFile;

    private File mOutputFile;


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mImageViewSelected = (ImageView) findViewById(R.id.imageView);
        mImageViewSelected.setAlpha(ALPHA_BLANK);
        mImageViewSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop();
            }
        }); // mImageViewSelected


        Button btnCopy = (Button) findViewById(R.id.Button_copy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyFilesWithCheck();
            }
        }); // btnCopy

        Button btnSelectImage = (Button) findViewById(R.id.Button_select);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageChooserWithCheck();
            }
        }); // btnSelectImage

    }




    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case (REQUEST_CODE_PERMISSION_CHOOSER):
                if (verifyPermissions(grantResults)) {
                    startImageChooser();
                } else {
                    showToast(R.string.msg_permissions_not_granted);
                }
                break;
            case (REQUEST_CODE_PERMISSION_COPY):
                if (verifyPermissions(grantResults)) {
                    copyFiles();
                } else {
                    showToast(R.string.msg_permissions_not_granted);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        } // switch
    }


/**
 * onActivityResult
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_CHOOSER):
                if (resultCode != RESULT_OK) {
                    showToast(R.string.msg_image_unselected_message);
                    return;
                }
                showSelectedImage(data.getData());
                break;

            case (REQUEST_CODE_CROP):
                if (resultCode != RESULT_OK) {
                    showToast(R.string.msg_crop_image_failure);
                    return;
                }
                showCroppedImage(data.getData());
                break;

            default:
                break;
        }
    }


    /**
     * Called when the "Select Image button" is clicked.
     */
    private void startImageChooserWithCheck() {

        if( !checkExternalStoragePermissions() ) {
                // reques Permission, if permission has not been granted.
                requestExternalStoragePermissions(REQUEST_CODE_PERMISSION_CHOOSER);
                return;
        }
        startImageChooser();
    }


    /**
     * Requests the READ_EXTERNAL_STORAGE permission and WRITE_EXTERNAL_STORAGE.
     * the permission is requested directly.
     */
    private void requestExternalStoragePermissions(int requestCode) {
        ExternalStoragePermission.requestPermissions(this, requestCode);
    }


    /**
     * checkExternalStoragePermissions
     */
private boolean checkExternalStoragePermissions() {

    return ExternalStoragePermission.checkPermissions(this);

}


/** 
 *   copyFilesWithCheck
 */
private void copyFilesWithCheck() {
        if( !checkExternalStoragePermissions() ) {
                // reques Permission, if permission has not been granted.
                requestExternalStoragePermissions(REQUEST_CODE_PERMISSION_COPY);
                return;
        }
        copyFiles();
}


/** 
 *   copyFiles from Asset folder to ExternalStoragePublicPictures
 */
private void copyFiles() {

    AssetFile assetFile = new AssetFile(this);
    assetFile.mkDirInExternalStoragePublicPictures();
    boolean ret = assetFile.copyFilesAssetToExternalStoragePublicPictures(FILE_EXT);
    if (ret) {
        showToast(R.string.msg_copy_successful);
    } else {
        showToast(R.string.msg_copy_failed);
    }
}


    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    private boolean verifyPermissions(int[] grantResults) {

        return ExternalStoragePermission.verifyPermissions(grantResults);

    }


/**
 * startImageChooser
 * if the required READ_EXTERNAL_STORAGE permission has been granted.
 */
    private void startImageChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(INTENT_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, CHOOSER_INTENT_MIME_TYPES.toArray());
        }
        startActivityForResult(Intent.createChooser(intent, null), REQUEST_CODE_CHOOSER);
    }





 /**
  * showSelectedImage
  */
private void showSelectedImage(Uri uri) {

        log_d("showSelectedImage:" + uri.toString());

        mSelectedUri = uri;
        mSelectedBitmap = uri2Bitmap(uri);

        mImageViewSelected.setImageBitmap(mSelectedBitmap);
       mImageViewSelected.setAlpha(ALPHA_SELECTED);

        showToast(R.string.msg_image_selected);
}


 /**
  * showCroppedImage
  */
private void showCroppedImage(Uri uri) {

        log_d("howCroppedImage:" + uri.toString());

        Bitmap bitmap = uri2Bitmap(uri);
        mImageViewSelected.setImageBitmap(bitmap);

        mSelectedUri = null;
        //deleteExternalStorageTempStoreFile();

}


 /**
  * uri2Bitmap
  */
private Bitmap uri2Bitmap(Uri uri) {

        Bitmap bitmap = CropFile.getBitmapFromUri(this, uri);
        return bitmap;
}



 /**
  * start Crop
  */
private void startCrop() {

        if(mSelectedUri == null) {
            log_d("startCrop: not set uri");
            showToast(R.string.msg_image_unselected_message);
            return;
        }

        createExternalStorageTempStoreFile(mSelectedBitmap);

        try{
            Intent intent = CropFile.createCropIntent(mSelectedFile, mOutputFile);
            startActivityForResult(intent, REQUEST_CODE_CROP);
        } catch (Exception e) {
			    e.printStackTrace();
                showToast(R.string.msg_start_crop_failed);
        }

}


    /**
     * createExternalStorageTempStoreFile
     */
    private boolean createExternalStorageTempStoreFile(Bitmap bitmap) {

        if(bitmap == null) {
            log_d("createExternalStorageTempStoreFile: not set bitmap");
            return false;
        }

        if( !checkExternalStoragePermissions() ) {
            log_d("createExternalStorageTempStoreFile: not permit");
            return false;
        }

        CropFile.mkDir();
        File[] files = CropFile.createTempFiles();
        if(( files == null)||( files.length == 0)) {
                log_d("createExternalStorageTempStoreFile: can not create files");
                return false;
        }

        mSelectedFile = files[0];
        mOutputFile = files[1];
        return CropFile.saveImage(bitmap, mSelectedFile);

    }


    /**
     * Delete temporary stored file.
     */
    private void deleteExternalStorageTempStoreFile() {

        if (mSelectedFile != null) {
            if (!mSelectedFile.delete()) {
                log_d("File deletion failed:" + mSelectedFile.toString());
            }
        }

        if (mOutputFile != null) {
            if (!mOutputFile.delete()) {
                log_d("File deletion failed:" + mOutputFile.toString());
            }
        }

        mSelectedFile = null;
        mOutputFile = null;
    }


/**
 * showToast
 */
private void showToast( String msg ) {
		Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
}


/**
 * showToast
 */
private void showToast( int res_id ) {
		Toast.makeText( this, res_id, Toast.LENGTH_LONG ).show();
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    Log.d( TAG, msg );
} 


}
