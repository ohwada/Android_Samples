/**
 * CSV Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.csv2;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "CSV";
    	private final static String TAG_SUB = "MainActivity";

    	private final static String LF = "\n";

	private ListView mListView;

    private TextView mTextView1;

    private CsvUtil mCsvUtil;

   	private ShoppingAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button btnRead1 = (Button) findViewById(R.id.Button_1);
         btnRead1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readSample("sample1.csv");
            }
        }); // btnRead1

        Button btnRead2 = (Button) findViewById(R.id.Button_2);
         btnRead2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readSample("sample2.csv");
            }
        }); // btnRead2

        Button btnWrite = (Button) findViewById(R.id.Button_3);
         btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeCsv();
            }
        }); // btnWrite

		List<Shopping> list = new ArrayList<Shopping>();
		 mAdapter = new ShoppingAdapter( this, R.layout.shopping_row, list );

        mListView = getListView();
		mListView.setAdapter( mAdapter );

            mCsvUtil = new CsvUtil(this);

    } // onCreate


	/** 
	 *  updatelListView
	 */
private void updatelListView( List<Shopping> list ) {
    if ((list == null )||( list.size()==0 )) return;
    mAdapter.clear();
    mAdapter.addAll(list);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();
} // updatelListView


	/** 
	 *  readSample
	 */
private void readSample( String fileName) {

   List<Shopping> list = mCsvUtil.readSample(fileName);
    if (( list == null )||( list.size() ==0 )) {
        toast_long("can not read");
        return;
    }
    updatelListView(list);

} // readSample 


	/** 
	 *  writeCsv
	 */
private void writeCsv() {
    mCsvUtil.writeTest();
} // writeCsv


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

} //  class MainActivity
