/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;
 
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
 * BookmarkListActivity
 */	
public class BookmarkListActivity extends ListActivity {

	// dubug
    public final static boolean D = true; 
	public final static String TAG = "OSM";
	private final static String TAG_SUB = "BookmarkListActivity";

    private final static String LF  = "\n";


	private ListView mListView;

   	private BookmarkAdapter mAdapter;

	private List<BookmarkRecord> mList;

	private BookmarkHelper mHelper;

	private BookmarkRecord currentRecord;

 	private TextView  mTextViewRecord;
/**
 * MainActivity
 */	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mTextViewRecord = (TextView) findViewById(R.id.TextView_record);

        Button btnDelete = (Button) findViewById(R.id.Button_delete);
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOne();
            }
        }); // btnDelete

        Button btnDeleteAll = (Button) findViewById(R.id.Button_delete_all);
        btnDeleteAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
            }
        }); // btnDeleteAll

		mList = new ArrayList<BookmarkRecord>();
		 mAdapter = new BookmarkAdapter( this, R.layout.bookmark_row, mList );

        mListView = getListView();
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // mListView

        mHelper = new BookmarkHelper(this);
 
        updatelListView();

    } // onCreate

	/** 
	 *  deleteOne
	 */
private void deleteOne() {
                long id = mHelper.delete(currentRecord);
    if (id < 0) {
    toast_long("Failed");
    return;
    }
    updatelListView();
    toast_long("Deleteed");

} // deleteAll

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

    mTextViewRecord.setText("");

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
        currentRecord  = mList.get( position );
mTextViewRecord.setText( currentRecord.getMessage() );
	} // procItemClick


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

} // BookmarkListActivity