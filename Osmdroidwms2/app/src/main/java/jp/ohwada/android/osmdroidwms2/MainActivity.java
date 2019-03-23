/**
 * Osmdroid Sample
 * OSM with WMS Tile
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroidwms2;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBox;

import org.osmdroid.wms.WMSEndpoint;
import org.osmdroid.wms.WMSLayer;
import org.osmdroid.wms.WMSParser;
import org.osmdroid.wms.WMSTileSource;

/**
 * MainActivity
 */	
public class MainActivity extends ListActivity {

	// dubug
    public final static boolean D = true; 
	public final static String TAG = "OSM";
	private final static String TAG_SUB = "MainActivity";

	private final static String LF = "\n";

	private final static String FILE_NAME = "mlit_wms_111.xml";

	private ListView mListView;

   	private LayerAdapter mAdapter;

	private List<WMSLayer> mList;

       	private WmsCapabilityUtil mWmsUtil;

       	private ProgressBar mProgressBar;

/**
 * onCreate
 */	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        Button btnParse = (Button) findViewById(R.id.Button_parse);
        btnParse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    parse();
                }
        }); // btnParse

    mWmsUtil  = new WmsCapabilityUtil(this);

		mList = new ArrayList<WMSLayer>();
		 mAdapter = new LayerAdapter( this, R.layout.db_row, mList );

        mListView = getListView();
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // mListView
 

    } // onCreate

private void parse() { 
log_d("parse");
        mProgressBar.setVisibility(View.VISIBLE);
        mWmsUtil.parse();
        mWmsUtil.setCallback(new WmsCapabilityUtil.Callback() {

            @Override
             public void onFinish( WmsCapabilityUtil.Capability cap ) { 
                stopProgressBar_onUI( cap);
            } 
}); //setCallback

} // parse

/** 
 *  stopProgressBar_onUI
 */
private void stopProgressBar_onUI(final WmsCapabilityUtil.Capability  cap) {
	runOnUiThread(new Runnable() {
        @Override
        public void run() {
                mProgressBar.setVisibility(View.GONE);
            if (cap==null) {
                    toast_long("parse Faild");
                    return;
            }
            mList = cap.list;
            showlListView(cap.header, mList);
            toast_long( "parse Successful: " + mList.size() );
        }
    });
} // stopProgressBar

/** 
 *  showlListView
 */
private void showlListView( String header, List<WMSLayer> list ) {

    TextView view = new TextView(this);
    view.setText(header);

    LayerAdapter adapter = new LayerAdapter( this, R.layout.db_row, list );

    mListView.addHeaderView(view);
	mListView.setAdapter( adapter );
    mListView.invalidate();

} // showlListView


	/** 
	 *  procItemClick
	 */
	private void procItemClick( int position, long id ) {
		// header footer
		if ( id == -1 )  return;
		// check position
int n = position - 1;
		if (( n < 0 )||( n > mList.size() )) return;
        WMSLayer layer = mList.get( n );
            mWmsUtil.showDialog( layer );
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