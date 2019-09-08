/**
 * SSDP Client
 * send SSDP Discovery  command
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.ssdpclient;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * class MainActivity 
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "SSDP";
    private final static String TAG_SUB = "MainActivity";

    private final static String LF = "\n";

/**
 * ListView
 */ 
    private ListView mListView ;

/**
 * ListAdapter for ListView
 */ 
   	private ListAdapter mAdapter;


/**
 * List for ListAdapter
 */ 
	private List<SearchResult> mList;

	private SsdpClient mClient;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = new ArrayList<SearchResult>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );

    Button btnStart = (Button) findViewById(R.id.Button_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startDiscovery();
            }
        }); // btnStart

        mListView = (ListView)findViewById(R.id.list);
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

    mClient = new SsdpClient(this);

    }

/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        super.onPause();
        if( mClient != null) {
                mClient.cancel();
        }
    }

/**
 * startDiscovery
 */ 
private void startDiscovery() {

    mClient.discovery(new SsdpClient.DiscoveryCallback(){
       @Override
            public void onResult(List<SearchResult> list){
                    log_d("onResult");
                    mList = list ;
                    showList(list);
            }
    }); // Discovery Callback

} // startDiscovery



/** 
 *   showList
 */
private void showList(List<SearchResult> list) {
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

        SearchResult item = mList.get( position );
       showDialog(item);

} // procItemClick

/** 
 * showDialog
 */
private void showDialog(SearchResult item) {

    log_d("showDialog: ");
    String title = item.getTitle();
    String message = item.getMessage();

   AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle( title );
            builder.setMessage(  message );
            builder.setPositiveButton(R.string.button_ok, null );
            builder.show();
}

/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class MainActivity
