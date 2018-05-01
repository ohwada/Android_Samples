/**
 * Yahoo News
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.yahoonews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import jp.ohwada.android.yahoonews.model.*;

/**
 * class MainActivity
 */
public class MainActivity extends Activity {

// debug
    	private String TAG_SUB = "MainActivity";

    	private static final String LF = "\n";

    	private final static int HEADER_FOOTER_ID = -1;

    	private static final int REQUEST_WEB_BROWSER = 1;

    private final static String URL = "https://news.yahoo.co.jp/pickup/rss.xml";

	private EntryAdapter  mAdapter;

    private ListView mListView ;
    private TextView mTextView1;

      private List<Entry> mList = new ArrayList<Entry>();


/**
 * == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     mTextView1 = (TextView) findViewById(R.id.TextView_1);
     mListView = (ListView) findViewById(R.id.ListView_list);
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // mListView setOnClickListener

        Button btnGet = (Button) findViewById(R.id.Button_get);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procClick();
            }
        }); // btnGet

    mAdapter = new EntryAdapter( this, EntryAdapter.LAYOUT_ID, mList );

    } // onCreate

/**
 * == onActivityResult ==
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    log_d("onActivityResult: " + requestCode + " , "+ resultCode );
    // notng to do
} // onActivityResult


    /*
     *  startActivityWeb
     */
private void startActivityWeb( String url ) {
    log_d("startActivityWeb: " + url);
    if (( url == null )||( url.isEmpty() )) return;
    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url) );
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivityForResult( intent, REQUEST_WEB_BROWSER );
} // startActivityWeb

    /*
     *  procClick
     */
private void procClick() {
        log_d("procClick");
        getNews();

        // for debug
        // readNews();
} // procClick


/**
 * get News RSS from web server
 */
private void getNews() {
        log_d("getNews");
    try {
        Ion.with(MainActivity.this)
                .load(URL)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String resullt) {
                        procCompleted(e, resullt);
                    }
                }); // FutureCallback

    } catch(Exception e) {
            e.printStackTrace();
            toast_short("update failed");
    }
} // getNews


 	/**
	 * procCompleted
	 */ 
private void procCompleted(Exception e, String result) {
    // failure
    if (e != null) {
            e.printStackTrace();
            toast_short("update failed");
            return;
    }
    // success
     log_d( "procCompleted: " + result);
        toast_short("updated");

        parseRss(result);

    } // procCompleted


 	/**
	 * read News RSS from asssets holder
	 *  for debug
	 */ 
private void readNews() {
    AssetFile file = new AssetFile(this);
    String xml = file.readTextFile( "yahoo.xml.txt" );
    parseRss(xml);
} // readNews

private void parseRss(String xml) {

    Rss rss = null;

    try {
        RssParser parser = new RssParser();
        rss = parser.parse(xml);
    } catch (Exception e) {
        e.printStackTrace();
    }

    showNews(rss);
} // parseRss


/**
 * showNews
 */ 
private void showNews(Rss rss) {

        if (rss == null) return;
        // log_d(rss.toString());

          mList = rss.entries;
            String text = rss.getChannelInfo();

        if (mTextView1 != null) {
                mTextView1.setText(text);
        }

    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();


    if (mListView != null) {
	    mListView.setAdapter( mAdapter );
        mListView.invalidateViews();
    }

} // showNews

/** 
 *  procItemClick
 */
	private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		log_d(msg );
		// header footer
		if ( id == HEADER_FOOTER_ID )  return;

		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        Entry item = mList.get( position );
        if (item != null ) {
            toast_short( item.title );
            startActivityWeb( item.link );
        }

	} // procItemClick

/**
 * toast_short
 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

/**
 * write into logcat
 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} //MainActivity
