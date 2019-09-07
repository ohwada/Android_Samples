/**
 * Cloud Vision Sample
 * Face Detection
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision4;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;


import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Landmark;


import jp.ohwada.android.cloudvision4.util.BaseActivity;


/** 
 *  class MainActivity
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class MainActivity extends BaseActivity {

    // debug
    private final static String TAG_SUB = "MainActivity";


/**
 * Mode for Face Image
 */ 
    private final static int  MODE_ORIGINAL = 0;
    private final static int  MODE_BOUNDING = 1;
    private final static int  MODE_LANDMARK = 2;

    private int  mModeFaceImage = MODE_BOUNDING;


/**
 * VisionClient
 */ 
    private VisionClient mVisionClient;


/**
 * Bitmap
 */ 
    private Bitmap mBitmapOriginal;


/**
 * FaceAnnotation
 */
    private FaceAnnotation  mFaceResponse;



/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        procCreate();

        Button btnSetting = (Button)findViewById(R.id.Button_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingDialog();
            }
        }); // btnSetting

        mVisionClient = new VisionClient(this);

} // onCreate

/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        super.onPause();
        mVisionClient.cancel();
    }

/** 
 *  onRequestPermissionsResult
 */
@Override
public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        log_d("onRequestPermissionsResult");
        procRequestPermissionsResult(
           requestCode, permissions, grantResults);
}

/** 
 *  onActivityResult
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log_d("onActivityResult");
        procActivityResult(requestCode, resultCode, data);
} // onActivityResult


/** 
 *  callCloudVision
 */
@Override
protected void callCloudVision(final Bitmap bitmap) {

        log_d("callCloudVision");

        // save for response
        mBitmapOriginal = bitmap;

        // clear response
        mFaceResponse = null;

        switchTextToLoading();

        // callCloudVision
        mVisionClient.callFaceDetection(bitmap, new VisionClient.FaceCallback() {

            @Override
           public void onPostExecute(FaceAnnotation response) {
                log_d("onPostExecute");
                procFacePostExecute(response);
            }

            @Override
            public void onError(String error) {
                log_d("onError");
                procResponseError_onUI(error);
            }

    }); // VisionCallback

} // callCloudVision



/**
 * procFacePostExecute
 */ 
private void procFacePostExecute(FaceAnnotation response) {

    mFaceResponse = response;

            String text = getStringResponseHeader();
            String msg = "";
            String result = mVisionClient.convFaceToString(response);
            log_d("procFacePostExecute: " + result);
            if (result == null ) {
                            text += getStringNoResult();
                            msg = "cloud vision Faild";
            } else {
                            text += result;
                            msg = "cloud vision Successful";
            }

        mTextViewDetails.setText(text);
        showFaceImage();
        showToast(msg);

} // procFacePostExecute


/**
 * showFaceImage
 */ 
private void showFaceImage() {

    if ( mFaceResponse == null ) return;
    if ( mBitmapOriginal == null ) return;

    Bitmap bitmap = getFaceImage(mFaceResponse, mBitmapOriginal );
    if(bitmap == null) {
        showToast("NOT get bitmap");
        return;
    }
    mImageViewMain.setImageBitmap(bitmap);

} // showFaceImage


/**
 * getFaceImage
 */ 
private Bitmap getFaceImage(FaceAnnotation faceAnnotation, Bitmap bitmap_orig ) {


    Bitmap bitmap = null;
    switch(mModeFaceImage) {
        case MODE_ORIGINAL:
            bitmap = mBitmapOriginal;
            break;
        case MODE_LANDMARK:
            bitmap = getLandmarkBitmap(faceAnnotation, bitmap_orig);
            break;
        case MODE_BOUNDING:
        default:
            bitmap = getBoundingBitmap(faceAnnotation, bitmap_orig);
            break;
    }
    return bitmap;
} // getFaceImage


/**
 * getBoundingBitmap
 */ 
private Bitmap getBoundingBitmap(FaceAnnotation faceAnnotation,  Bitmap bitmap_orig) {

    List<Point> list =   mVisionClient.convFaceToFdBoundingPointList(faceAnnotation);

        Bitmap bitmap = FaceImageUtil.createBoundingBitmap( 
                bitmap_orig, list);
        return bitmap;
} // getBoundingBitmap


/**
 * getLandmarkBitmap
 */ 
private Bitmap getLandmarkBitmap(FaceAnnotation faceAnnotation, Bitmap bitmap_orig) {

        List<Landmark> list =  mVisionClient.convFaceToLandmarkList(faceAnnotation);

       Bitmap bitmap = FaceImageUtil.createLandmarkBitmap(
            bitmap_orig, list);
        return bitmap;
} // getLandmarkBitmap


/**
 * showSettingDialog
 */ 
private void showSettingDialog() {

        Resources res = getResources();
        String[] ITEMS = res.getStringArray(R.array.setting_dialog_items);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle( R.string.setting_dialog_title );

        builder.setSingleChoiceItems( ITEMS, mModeFaceImage, 
        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mModeFaceImage = which;
                    } 
                } // OnClickListener
        ); // setSingleChoiceItems

        builder.setPositiveButton(R.string.button_ok, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    showFaceImage();
                }
        }); // setPositiveButton

        // show it
        AlertDialog dialog = builder.create();
        dialog.show();

} // showSettingDialog


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class MainActivity
