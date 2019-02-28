/**
 * CSV Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.csv1;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
                readSample1();
            }
        }); // btnRead1

        Button btnRead2 = (Button) findViewById(R.id.Button_2);
         btnRead2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readSample2();
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
	 *  readSample1
	 */
private void readSample1() {

   List<Shopping> list = mCsvUtil.readSample1();
    updatelListView(list);

} // readSample 1

	/** 
	 *  readSample2
	 */
private void readSample2() {

   List<Shopping> list = mCsvUtil.readSample2();
    updatelListView(list);

} // readSample 2


	/** 
	 *  writeCsv
	 */
private void writeCsv() {
    mCsvUtil.writeTest();
} // writeCsv




 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class MainActivity
