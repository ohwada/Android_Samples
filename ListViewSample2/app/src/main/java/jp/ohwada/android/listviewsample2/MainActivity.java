/**
 * ListView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.listviewsample2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

   		// debug
    	private final static String TAG_SUB = "MainActivity";

		// header footer
  		private final static ViewGroup INFLATE_ROOT = null;
    	private final static String HEADER_DATA = null;
    	private final static boolean HEADER_ISSELECTABLE = true;
    	private final static String FOOTER_DATA = null;
    	private final static boolean FOOTER_ISSELECTABLE = true;

    private ListView mListView ;
   	private ListAdapter mAdapter;
	private List<VersionItem> mList;

/**
 *  == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		log_d("onCreate");
        setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById( R.id.ListView_1 );

		mList = AndroidVersion.getListVersionItem();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener


	// inflate(Context context, int resource, ViewGroup root)
		View header = View.inflate(this, R.layout.list_header, INFLATE_ROOT );
		mListView.addHeaderView( header, HEADER_DATA, HEADER_ISSELECTABLE );
		View footer = View.inflate(this, R.layout.list_footer, INFLATE_ROOT );
		mListView.addFooterView(footer, FOOTER_DATA, FOOTER_ISSELECTABLE );

    }// onCreate


	/** 
	 *  procItemClick
	 * @param int position
	 * @param long id 
	 */
	private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		log_d(msg );
		// header footer
		if ( id == -1 )  {
		// header
		if ( position == 0 ) {
        	toast_short( "header" );
			return;
		//  footer
		} else {
        	toast_short( "footer" );
			return;
		}
		}
		// check position
		int n = position - 1;
		if (( n < 0 )||( n >= mList.size() )) return;
        VersionItem item = mList.get( n );
        toast_short( item.codename );	
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
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
