/**
 *  Video Player Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.videoplayer1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * class MainActivity 
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Audio";
    private final static String TAG_SUB = "MainActivity";


	private final static String EXTRA_KEY_FILE_NAME = VideoActivity.EXTRA_KEY_FILE_NAME;

	private final static int REQUEST_CODE_VIDEO = 101;


/**
 * File extension of File in Asset folder
 */ 
    private final static String FILE_EXT = ".mp3";

    private final static String LF = "\n";

    private FileUtil mFileUtil;

    private ListView mListView ;

   	private ListAdapter mAdapter;

	private List<String> mList;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            mListView = (ListView) findViewById(R.id.list);

     
        Button btnCopy = (Button) findViewById(R.id.button_copy);
            btnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyFiles();
                }
            }); // btnCopy


        mList = new ArrayList<String>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

mFileUtil = new FileUtil(this);

    } // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        showList();
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        log_d("onPause");
        super.onPause();
} // onPause


/**
 * onPause
 */ 
@Override
public void onDestroy() {
    super.onDestroy();
}


/**
 * onActivityResult
 */ 
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data){
    // nop
} // onActivityResult


/** 
 *   copyFiles from Asset folder to ExternalFilesDir
 */
private void copyFiles() {
    boolean ret = mFileUtil.copyFilesAssetToExternalFilesDir();
    if(ret) {
        showList();
        showToast("copy successful");
    } else {
        showToast("copy faild");
    }
} // copyFiles


/** 
 *   showList
 *   show File Name in ExternalFilesDir
 */
private void showList() {

    mList = mFileUtil.getFileNameListInExternalFilesDir();
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
		//log_d(msg );

		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        String item = mList.get( position );
        openActivity(item);

} // procItemClick


 /**
 *  openActivity
 */
private void openActivity(String fileName) {
    log_d("openActivity:" + fileName);
    Intent intent = new Intent(this, VideoActivity.class);
    intent.putExtra(EXTRA_KEY_FILE_NAME, fileName);
    startActivityForResult(intent, REQUEST_CODE_VIDEO);
    showToast( fileName );
} // openActivity



/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d




} // class MainActivity
