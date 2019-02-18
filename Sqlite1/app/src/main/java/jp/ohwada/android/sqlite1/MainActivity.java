/**
 * SQLite Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.sqlite1;
 
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
 * MainActivity
 */	
public class MainActivity extends ListActivity {

	// dubug
    public final static boolean D = true; 
	public final static String TAG = "SQLite";
	private final static String TAG_SUB = "MainActivity";

	private ListView mListView;

	private EditText mEditTextName;

	private EditText mEditTextAge;

	private TextView mTextViewId;

   	private PersonAdapter mAdapter;

	private List<PersonRecord> mList;

	private PersonHelper mHelper;

private PersonRecord currentRecord;
/**
 * MainActivity
 */	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mTextViewId = (TextView) findViewById(R.id.TextView_id);

         mEditTextName = (EditText) findViewById(R.id.EditText_name);
         mEditTextAge = (EditText) findViewById(R.id.EditText_age);

        Button btnInsert = (Button) findViewById(R.id.Button_insert);
        btnInsert.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditTextName.getText().toString();
                String age = mEditTextAge.getText().toString();
                if ( name.equals("")||age.equals("") ) {
                    toast_long( R.string.toast_input );
                    return;
                }

           long id = mHelper.insert( new PersonRecord(name, age ) );
if (id < 0) {
    toast_long(R.string.toast_failed);
    return;
}
            updatelListView();
            toast_long(R.string.toast_inserted);

            }
        }); // btnInsert

        Button btnUpdate = (Button) findViewById(R.id.Button_update);
        btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditTextName.getText().toString();
                String age = mEditTextAge.getText().toString();
                currentRecord.setName(name); 
                currentRecord.setAge(age); 

                if ( name.equals("")||age.equals("")) {
                    toast_long( R.string.toast_input );
                    return;
                }
                    long id = mHelper.update( currentRecord );
if (id < 0) {
    toast_long(R.string.toast_failed);
    return;
}
            updatelListView();
            toast_long(R.string.toast_updateed);
                }

        }); // btnUpdate


        Button btnDelete = (Button) findViewById(R.id.Button_delete);
        btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                    long id = mHelper.delete( currentRecord );
if (id < 0) {
    toast_long(R.string.toast_failed);
    return;
}
            updatelListView();
    toast_long(R.string.toast_deleteed);

            }
        }); // btnDelete


        Button btnDeleteAll = (Button) findViewById(R.id.Button_delete_all);
        btnDeleteAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long id = mHelper.deleteAll();
if (id < 0) {
    toast_long(R.string.toast_failed);
    return;
}

            updatelListView();
    toast_long(R.string.toast_deleteed);

            }
        }); // btnDeleteAll

 
		mList = new ArrayList<PersonRecord>();
		 mAdapter = new PersonAdapter( this, R.layout.db_row, mList );

        mListView = getListView();
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // mListView

        mHelper = new PersonHelper(this);
 
        updatelListView();

    } // onCreate

	/** 
	 *  updatelListView
	 */
private void updatelListView() {

    mTextViewId.setText("");
    mEditTextName.setText("");
    mEditTextName.setHint(R.string.hint_name);
    mEditTextAge.setText("");
    mEditTextAge.setHint(R.string.hint_age);
    currentRecord = new PersonRecord();

    long count = mHelper.getCountAll();
    log_d("count: " + count);

    mList = mHelper.getAllRecordList();
    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();;
} // updatelLstView


	/** 
	 *  procItemClick
	 */
	private void procItemClick( int position, long id ) {
		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position > mList.size() )) return;
        currentRecord = mList.get( position );
        mTextViewId.setText( currentRecord.getId() );
        mEditTextName.setText( currentRecord.getName() );
        mEditTextAge.setText( currentRecord.getAge() );
	} // execItemClick

   /**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

   /**
 * toast_long
 */
	private void toast_long( int res_id ) {
		ToastMaster.makeText( this, res_id, Toast.LENGTH_LONG ).show();
	} // toast_long

 	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // MainActivity