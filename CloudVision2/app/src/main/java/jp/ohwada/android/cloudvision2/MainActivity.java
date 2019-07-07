/**
 * Cloud Vision Sample
 * web detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.cloudvision2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;


import com.google.api.services.vision.v1.model.WebDetection;

import com.squareup.picasso.Picasso;


/** 
 *  class MainActivity
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class MainActivity extends BaseActivity {

    // debug
    private final static String TAG_SUB = "MainActivity";


/**
 * Request Code
 */ 
    private final static int REQUEST_CODE_WEB_BROWSER = 106;


/**
 * ListView
 */ 
    private ListView mListView ;


/**
 * LinearLayout
 */ 
    private LinearLayout mLinearLayoutButton;


/**
 * ListAdapter for ListView
 */ 
   	private ListAdapter mAdapter;


/**
 * List for ListAdapter
 */ 
	private List<WebItem> mList;


/**
 * List for ListAdapter
 */ 
	private boolean isTextHide = false;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        procCreate();

        mList = new ArrayList<WebItem>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );

        mListView = (ListView)findViewById(R.id.list);
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

        mLinearLayoutButton = (LinearLayout)findViewById(R.id.LinearLayout_button);

        mTextViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open space on the screen to show ListView
                hideText();
            }
        }); // TextViewDetails


}


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        showList(mList);
}


/** 
 *  onActivityResult
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        procActivityResult(requestCode, resultCode, data);
        if (requestCode ==  REQUEST_CODE_WEB_BROWSER ) {
            // no action
        }
} // onActivityResult


/** 
 *  dispatchKeyEvent
 *  interupt BACK button
 */
@Override
public boolean dispatchKeyEvent(KeyEvent event) {
 
    boolean ret = false;
   if (event.getAction() == KeyEvent.ACTION_DOWN) {
        ret = procKeyEventActionDown(event);
   }
    if(ret) return true;
 
   return super.dispatchKeyEvent(event);
} // dispatchKeyEvent


/** 
 *  procKeyEventActionDown
 */
private boolean procKeyEventActionDown(KeyEvent event) {
 
    int key_code = event.getKeyCode();
    log_d("KeyCode: "+ key_code);

    if(key_code == KeyEvent.KEYCODE_BACK) {
        if (isTextHide) {
            showText();
        } else {
            finish();
        }
    }
    return true;

} // procKeyEventActionDown


/** 
 *   hideText
 */
private void hideText() {
    // open space on the screen to show ListView
    isTextHide = true;
    mLinearLayoutButton.setVisibility(View.GONE);
    mTextViewDetails.setVisibility(View.GONE);
} // hideText



/** 
 *   showText
 */
private void showText() {
    isTextHide = false;
    mLinearLayoutButton.setVisibility(View.VISIBLE);
    mTextViewDetails.setVisibility(View.VISIBLE);
    mTextViewDetails.setText("");
} // showText


/** 
 *   showList
 */
private void showList(List<WebItem> list) {
    mList = list;
    mAdapter.clear();
    mAdapter.addAll(list);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();
} // showList


/** 
 *  procItemClick
 */
private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		log_d(msg );

		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        WebItem item = mList.get( position );
       showImageDialog(item);

} // procItemClick


/** 
 * showImageDialog
 * show image in Dialog
 */
private void showImageDialog(WebItem item) {

    log_d("showImageDialog: " + item.toString());

    final String pageUrl = item.getPageUrl();
    String title = item.getTitle();
    String imageUrl = item.getImageUrl();

    ImageView imageView = new ImageView(this);

    // use different library than Listadapter, because the cache works and show small images
    Picasso.with(this).load(imageUrl).into(imageView);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle( title );
            builder.setView(  imageView );

    if( pageUrl != null) {
            builder.setPositiveButton(R.string.button_web, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startWeb( pageUrl);
                }
            });
            builder.setNegativeButton(R.string.button_cancel, null);
    } else {
            builder.setPositiveButton(R.string.button_ok, null);
    }

            builder.show();

} // showImageDialog


/** 
 *  startWeb
 */
private void startWeb( String url) {
    Uri uri = Uri.parse(url);
    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivityForResult(intent,
                     REQUEST_CODE_WEB_BROWSER);
} // startWeb


/** 
 *  callCloudVision
 */
    @Override
    protected void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        mTextViewDetails.setText(R.string.loading_message);

        mVisionClient.callWebDetection(bitmap, new VisionClient.WebDetectionCallback(){

            @Override
            public void onPostExecute(WebDetection response) {
                    log_d("onPostExecute");
                    procPostExecute(response) ;
            }

            @Override
            public void onError(String error) {
                log_d("onError");
                procResponseError_onUI(error);
            }

    }); // VisionCallback

 } // callCloudVision


/**
 * procPostExecute
 */ 
private void procPostExecute(WebDetection esponse) {

                    String text = getString(R.string.response_header);
                    String msg = "";
                    if (esponse == null ) {
                            text += "no result";
                            msg = "cloud vision faild";
                    } else {
                            String result = mVisionClient.convWebDetectionToString(esponse);
                            List<WebItem> list = mVisionClient.getWebItemList(esponse);
                            text += result;
                            msg = "cloud vision successful";
                            showList(list);
                    }
                    mTextViewDetails.setText(text);
                    log_d("onPostExecute:" + text);
                    showToast(msg);
} // procPostExecute



/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class MainActivity
