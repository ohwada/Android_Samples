/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1;
 
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * NodeListActivity
 */	
public class NodeListActivity extends ListActivity {

	// dubug
    public final static boolean D = true; 
	public final static String TAG = "OSM";
	private final static String TAG_SUB = "NodeListActivity";

    private final static String LF  = "\n";


	private ListView mListView;

   	private NodeAdapter mAdapter;

	private List<NodeRecord> mList;

	private NodeHelper mHelper;


/**
 * MainActivity
 */	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button btnDeleteAll = (Button) findViewById(R.id.Button_delete_all);
        btnDeleteAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteAll();
            }
        }); // btnDeleteAll

		mList = new ArrayList<NodeRecord>();
		 mAdapter = new NodeAdapter( this, R.layout.node_row, mList );

        mListView = getListView();
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // mListView

        mHelper = new NodeHelper(this);
 
        updatelListView();

    } // onCreate


	/** 
	 *  deleteAll
	 */
private void deleteAll() {
                long id = mHelper.deleteAll();
    if (id < 0) {
    toast_long("Failed");
    return;
    }

    updatelListView();
    toast_long("Deleteed");

} // deleteAll


	/** 
	 *  updatelListView
	 */
private void updatelListView() {

    mList = mHelper.getAllList();
    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();

} // updatelLstView


	/** 
	 *  procItemClick
	 */
	private void procItemClick( int position, long id ) {
		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position > mList.size() )) return;
        NodeRecord r = mList.get( position );
toast_long( r.name + LF +r.info );
	} // execItemClick


   /**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // toast_long


 	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // NodeListActivity