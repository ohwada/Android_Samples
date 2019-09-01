/**
 * Cloud Vision Sample
 * label detection with remote image
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision3;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


import com.squareup.picasso.Picasso;


import jp.ohwada.android.cloudvision3.util.ToastMaster;


/** 
 *  class MainActivity
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "CloudVision";
    private final static String TAG_SUB = "MainActivity";


/**
 * TextView for the result of image recognition
 */ 
    private TextView mTextViewDetails;

   private TextView mTextViewError;

/**
 * ImageView for selected image
 */ 
    private ImageView mImageViewMain;


/**
 * VisionClient
 */ 
    private VisionClient mVisionClient;


/**
 * utility for ImageUrl
 */
    private ImageUrlUtil mImageUrlUtil;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.Button_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageUrlListDialog();
            }
        }); // btnStart

        mTextViewDetails = (TextView)findViewById(R.id.TextView_details);

        mTextViewError = (TextView)findViewById(R.id.TextView_error);

        mImageViewMain = (ImageView)findViewById(R.id.ImageView_main);

        // utility
        mVisionClient = new VisionClient(this);
        mImageUrlUtil = new ImageUrlUtil(this);

}


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        super.onPause();
        mVisionClient.cancel();
    }


/**
  *  showImageUrlListDialog
 */
private void showImageUrlListDialog() {

    final List<ImageUrlItem> list = mImageUrlUtil.createFromCsv();
    if( list == null) return;

    int size = list.size();
    if( size == 0) return;

    // setup items
    CharSequence[] items = new CharSequence[size];
    for(int i=0; i<size; i++) {
        ImageUrlItem item = list.get(i);
        items[i] = item.getName();
    } // for

      AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle( R.string.image_url_list_dialog_title );

        builder.setItems( items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            ImageUrlItem item = list.get(which);
                            showImage( item.getImageUrl() );
                    } // onClick
                } // OnClickListener
        ); // setItems

        // show it
        AlertDialog dialog = builder.create();
        dialog.show();

}


/**
  *  showImage
  */
private void showImage(String imageUrl) {

        log_d("showImage: " + imageUrl );
        boolean ret = ImageUrlUtil.checkUrlFormat(imageUrl);
        if( !ret ) {
                String msg = getString(R.string.msg_wrong_format);
                log_d(msg);
                showToast(msg);
                return;
        }

        Picasso.with(this).load(imageUrl).into(mImageViewMain);

        showConfirmDialog(imageUrl);
} // showImage


/** 
 *  showConfirmDialog
 *  comfirm to request to CloudVision
 */
private void showConfirmDialog(final String imageUrl) {

    log_d("showConfirmDialog: " + imageUrl );
    ImageView imageView = new ImageView(this);
    Picasso.with(this).load(imageUrl).into(imageView);

             new AlertDialog.Builder(this)
            .setTitle(R.string.confirm_dialog_title)
            .setView(  imageView )
            .setPositiveButton(R.string.button_ok, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                     callCloudVision(imageUrl);
                }
            }) // setPositiveButton
            .setNegativeButton(R.string.button_cancel, null)
            .show();
} // showConfirmDialog


/** 
 *  callCloudVision
 */
private void callCloudVision(String imageUrl) {
log_d("callCloudVisionByUrl: " + imageUrl );
        // Switch text to loading
        mTextViewDetails.setText(R.string.progress_dialog_message);

        mVisionClient.callCloudVisionByUrl(imageUrl, new VisionClient.VisionCallback(){

            @Override
            public void onPostExecute(String result) {
                procPostExecute(result);
            }

            @Override
            public void onError(String error) {
                procResponseError(error);
            }

    }); // VisionCallback

 } // callCloudVision

/**
 * procPostExecute
 */
private void procPostExecute(String result) {

                    String text = getString(R.string.response_header);
                    String msg = "";
                    if (result == null ) {
                           text += getString(R.string.response_no_result);
                           msg = getString(R.string.msg_request_faild);
                    } else {
                            text += result;
                           msg = getString(R.string.msg_request_successful);
                    }
                    mTextViewDetails.setText(text);
                    log_d("procPostExecute:" + text);
                    showToast(msg);
} // procPostExecute


/**
 * procResponseError
 */ 
private void procResponseError(String error) {
        mTextViewError.setText(error);
        mTextViewError.setTextColor(Color.RED);
        showToast(R.string.msg_request_faild);
        log_d("procResponseError:" + error);
}


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
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class MainActivity
