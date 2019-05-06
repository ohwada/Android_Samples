/**
 * Vision Sample
 * Face Detection
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.vision1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *  class MainActivity
 */
public class MainActivity extends ListActivity {

   		// dubug
	private final static boolean D = true; 
	private final static  String TAG = "Vision";
    private final static String TAG_SUB = "MainActivity";

    private final static String IMAGE_EXT = ".jpg";

    private ListView mListView ;

   	private ListAdapter mAdapter;

	private List<String> mList;

    private FileUtil mFileUtil;

    private FaceUtil mFaceUtil;


/**
 * onCreate 
 */
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mListView = getListView();

        mFileUtil = new FileUtil(this);
        mFaceUtil = new FaceUtil(this);

        mList = new ArrayList<String>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

        showList();

}// onCreate


/** 
 *   showList
 */
private void showList() {

    mList = mFileUtil.getAssetList( IMAGE_EXT );
    mAdapter.clear();
    mAdapter.addAll(mList);
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

        String item = mList.get( position );
        showImage(item);

} // procItemClick


/** 
 *  showImage
 */
private void showImage(String fileName) {

    Bitmap bitmap_orig = mFileUtil.getAssetsBitmap(fileName);
    Bitmap bitmap_landmark = mFaceUtil.getLandmarkImage(bitmap_orig);
showImageDialog(fileName,  bitmap_landmark);

} // showImage


/** 
 *  showImageDialog
 */
private void showImageDialog(String title, Bitmap bitmap) {

    ImageView imageView = new ImageView(this);
    imageView.setImageBitmap( bitmap );

    new AlertDialog.Builder(this)
         .setTitle( title )
        .setView(  imageView )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();

} // showImageDialog


/**
	 * toast_long
	 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // toast_long


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
