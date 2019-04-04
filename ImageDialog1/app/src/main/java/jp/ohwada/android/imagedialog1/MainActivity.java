/**
 * ListView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.imagedialog1;

import android.app.Activity;
import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
	private final static  String TAG = "ImageDialog";
    	private final static String TAG_SUB = "MainActivity";

    private final static String IMAGE_EXT = ".jpg";

    private ListView mListView ;

   	private ListAdapter mAdapter;

	private List<File> mList;

    private FileUtility mFileUtility;

    private ImageDialog mImageDialog;


/**
 * onCreate 
 */
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mListView = getListView();

        Button btnCopy = (Button) findViewById(R.id.Button_copy);
		btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyFiles();
            }
        }); // btnCopy

        mFileUtility = new FileUtility(this);
        mImageDialog = new ImageDialog(this);

        mList = new ArrayList<File>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

}// onCreate


/** 
 *   copyFiles
 */
private void copyFiles() {

        boolean ret = mFileUtility.copyAssetToAppDir( IMAGE_EXT );
    if(ret) {
        toast_short("copy sucessful");
    } else {
        toast_short("copy faild");
    }

    mList = mFileUtility.getAppListFile( IMAGE_EXT );
    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();

} // copyFiles


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

        File file = mList.get( position );
        mImageDialog.showImage(file);

} // procItemClick

/**
	 * toast_short
	 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_shor


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
