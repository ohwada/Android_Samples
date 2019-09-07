/**
 * Cloud Vision Sample
 * CameraUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision4.util;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;


/**
 * class CameraUtil
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class CameraUtil {


/**
  * Constant for Authority of FileProvider
  * refer : android:authorities
 */
    private static final String AUTHORITY_PROVIDER = ".provider";


/**
  * File Name for saving Camera Pictures
  * TODO : change File Name every time take a picture
 */
    private static final String FILE_NAME = "temp.jpg";


/**
  * Context 
 */
	private Context mContext;


/**
  * Authority for  FileProvider
  * refer : android:authorities
 */
    private String mAuthority;


/**
  * constractor 
 */
public CameraUtil(Context context) {
		mContext = context;
        String packageName = context.getPackageName();
        mAuthority = packageName + AUTHORITY_PROVIDER;

}

/**
  * createIntent 
 */
public Intent createIntent() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoUri());
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    return intent;
}


/** 
 *  getPhotoUri
 */
public Uri getPhotoUri() {
        Uri photoUri = FileProvider.getUriForFile(mContext, mAuthority, getCameraFile());
        return photoUri;
}


/** 
 *  getCameraFile
 */
public File getCameraFile() {
        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(dir, FILE_NAME);
        return file;
}


} // class class CameraUtil
