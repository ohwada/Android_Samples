/**
 * RSS Sample
 * with Android RSS Libraly
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.rsssample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.*;

/**
 * class MainActivity
 * reference : https://github.com/ahorn/android-rss
 */
public class MainActivity extends Activity {

// debug
	private  final static boolean D = true; 
	private String TAG = "RSS";
    	private static final String TAG_SUB = "MainActivity";

    	private static final String LF = "\n";

    	private final static int HEADER_FOOTER_ID = -1;

    	private static final int REQUEST_WEB_BROWSER = 1;

    private final static String FEED_TITLE = "BBC World News";

    	private final static int FEED_TITLE_TEXT_SIZE = 24;

    private final static String URL1 = "http://feeds.bbci.co.uk/news/world/rss.xml";

    private final static String URL2 = "https://news.yahoo.co.jp/pickup/rss.xml";

    private final static String FEED_FILE_NAME = "bbc_world_news.xml.txt";

	private EntryAdapter  mAdapter;
	// private RSSItemAdapter  mAdapter;

	private RSSReaderTask mRSSReaderTask;

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

        Button btnFile = (Button) findViewById(R.id.Button_file);
         btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile();
            }
        }); // btnFile

        Button btnUrl = (Button) findViewById(R.id.Button_url);
         btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadURL();
            }
        }); // btnUrl

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




/**
 * loadURL
 * get feed XML from web server
 */
private void loadURL() {
        log_d("loadURL");

    mRSSReaderTask  =  new RSSReaderTask();
    mRSSReaderTask.execute();

} // loadURL


/**
 * procFinish
 * 
 */
private void procFinish(RSSFeed feed) {

    log_d("procFinish");
    if (feed == null ) {
        log_d("can not load : feed null");
        toast_short("can not load");
        return;
    }

    mTextView1.setText( FEED_TITLE );
    mTextView1.setTextSize( FEED_TITLE_TEXT_SIZE );

    showFeed(feed);

} // procFinish


/**
 * showFeed
 * 
 */
private void showFeed(RSSFeed feed) {

                log_d("showFeed");

    if (feed == null ) {
        log_d("feed null");
        toast_short("no feed");
        return;
    }

    List<RSSItem> list = feed.getItems();
    if ((list == null)||(list.size() == 0)) {
        log_d("no item");
        toast_short("no item");
        return;
    }

    mList = new ArrayList<Entry>();
    for(RSSItem item: list) {
        mList.add(new Entry(item) );
    }

    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();

	mListView.setAdapter( mAdapter );
    mListView.invalidateViews();

} // showFeed


/**
 * procError
 * 
 */
private void procError(Exception e) {

                log_d("procError");
            if (e == null) return;

                String msg = e.getMessage();
                log_d( msg );
                toast_short( msg );

} // procError






 	/**
	 * readFile
	 * read XML File from assets 
	 */ 
private void readFile() {

    AssetFile file = new AssetFile(this);
   InputStream is = file.getAssetInputStream( FEED_FILE_NAME );
    if (is == null ) {
    log_d("can not read: is null");
   toast_short("can not read");
     return;
    }

    RSSParser parser = new RSSParser(new RSSConfig());

    RSSFeed feed = parser.parse(is);
    if (feed == null ) {
    log_d("can not read: feed null");
   toast_short("can not read");
     return;
    }

    showFeed(feed);

} // readFile



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
            String title = item.getTitle();
           String link = item.getLinkString();

            toast_short( title );
            // startActivityWeb( link );
            showDialog(item);
        }

	} // procItemClick


/**
 * showDialog
 */
private void showDialog(Entry item) {
        log_d("showDialog");

            String title = item.getTitle();
            String date = item.getPubDateRFC822();
            final String link = item.getLinkString();
            String description = item.getDescription();
            String image_url = item.getFirstThumbnailUri();
            int image_width = item.getFirstThumbnailWidth();
            int image_height = item.getFirstThumbnailHeight();

       final EntryDialog dialog = new EntryDialog( this );
        dialog.setContentTtitle( title );
        dialog.setDate( date );
        dialog.setDescription( description );
        dialog.setImageUrl( image_url, image_width, image_height );      
              
        dialog.setOnChangedListener(
            new EntryDialog.OnChangedListener() {
            	
            public void onClickYes() {
                log_d("onClickYes");
                dialog.dismiss();
                startActivityWeb( link );
            }
            
            public void onClickNo() {
                log_d("onClickNo");
                dialog.dismiss();
            }
            
          } );

          dialog.show();

    } // showDialog


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
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


/**
     * == class RSSReaderTask ==
     */
    private class RSSReaderTask extends AsyncTask<Void, Void, RSSFeed> {



        private Exception mException = null;

    /**
     * == constractor ==
     */
        RSSReaderTask() {
            log_d("RSSReaderTask");
            /// nothing todo
        } // RSSReaderTask

        /**
     * == doInBackground ==
         */
        @Override
        protected RSSFeed doInBackground(Void... params) {

                    log_d("doInBackground");

  RSSReader reader = new RSSReader();

            try {
                return reader.load(URL1);

            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
                cancel(true);
                return null;
            }

        } // doInBackground

    /**
     * == onPreExecute ==
     */
        @Override
        protected void onPreExecute() {
                    log_d("onPreExecute");
                // nothing to do
        } // onPreExecute

    /**
     * == onPostExecute ==
     */
        @Override
        protected void onPostExecute(RSSFeed feed) {

                    log_d("onPostExecute");
                procFinish(feed);

        } // onPostExecute

    /**
     * == onCancelled ==
     */
        @Override
        protected void onCancelled() {

                log_d("onCancelled");
                procError(mException);

        } // onCancelled

    } // class RSSReaderTask

} //MainActivity
