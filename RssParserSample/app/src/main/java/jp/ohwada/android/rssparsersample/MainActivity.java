/**
 * RSS Parser
 * 2018-04-10 K.OHWADA 
 */

package jp.ohwada.android.rssparsersample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;
import com.prof.rssparser.XMLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * class MainActivity
 * original : https://github.com/prof18/RSS-Parser/tree/master/app/src/main/java/com/prof/rssparser/example
 */
public class MainActivity extends AppCompatActivity {

   	// debug
    	private final static String TAG_SUB = "MainActivity";

   private final static  String URL1  = "https://www.androidauthority.com/feed";

    private final static  String URL2 = "https://wordpress.org/news/feed/";

 

    private final static  String MSG_LOAD_ERROR  = "Unable to load data.";


    private RecyclerView mRecyclerView;
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private Button mButtonLoad;

    private List<Article> mList = new ArrayList<Article>();

/**
 * == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_d("onCreate");
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mButtonLoad = (Button) findViewById(R.id. Button_load);
        mButtonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_d("Get onClick");
                  procLoadClick();
            }
        }); // mButtonLoad

        mRecyclerView = (RecyclerView)findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

         mAdapter = new ArticleAdapter(mList, ArticleAdapter.LAYOUT_RESOURCE_ID, MainActivity.this);
        mAdapter.setOnItemClickListener( new ArticleAdapter.OnItemClickListener() {
        @Override
        public void onItemClick( Article article ){
log_d( "onItemClick");
String title = article.getTitle();
toast_short( title );
 showArticleDialog(article);
}
}); // setOnItemClickListener


        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.canChildScrollUp();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                log_d("onRefresh");
                mAdapter.clearData();
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(true);
                // loadFeed();
            }
        });  // mSwipeRefreshLayout setOnRefreshListener


        if (isNetworkAvailable()) {
             // loadFeed();         
        } else {
            showNetworkAlertDialog();
        }

    } // onCreate


/**
 * == onResume == 
 */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    } // onResume


/**
 * == onDestroy == 
 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.clearData();
        }
    } // onDestroy


/**
 * == onOptionsItemSelected == 
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            toast_short("settings");
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected


/**
 * == onCreateOptionsMenu == 
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // onCreateOptionsMenu


/**
 *  showNetworkAlertDialog
 */
private void showNetworkAlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    finish();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
} // showNetworkAlertDialog


/**
 * procClick
 */
 private void procLoadClick() {
                log_d("procLoadClick");
        loadFeed();
        // for debug
        // readFeed();
} // procClick


/**
 * loadFeed
 */
    public void loadFeed() {

        if (!mSwipeRefreshLayout.isRefreshing())
            mProgressBar.setVisibility(View.VISIBLE);

        Parser parser = new Parser();
        parser.execute(URL1);
        parser.onFinish(new Parser.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Article> list) {
                        log_d("loadFeed onTaskCompleted");    
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            showFeed(list);
            } // onTaskCompleted

            @Override
            public void onError() {
                        log_d("loadFeed onError");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        toast_short(MSG_LOAD_ERROR);
                    }

                });  // runOnUiThread
            } // onError
        }); // parser.onFinish

    } // loadFeed


/**
 * showFeed
 */
private void showFeed(List<Article> list) {
                log_d("showFeed");
                mButtonLoad.setVisibility(View.GONE);

                mList = list;
                mAdapter.clearData();
                mAdapter.addAllData(mList);
                mAdapter.notifyDataSetChanged();

                mRecyclerView.setAdapter(mAdapter);
                // mRecyclerView.invalidateViews();
} // showFeed


/**
 * isNetworkAvailable
 */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    } // isNetworkAvailable



 	/**
	 * read Feed from asssets holder
	 *  for debug
	 */ 
private void readFeed() {
                log_d("readFeed");
    AssetFile file = new AssetFile(this);

    String xml = file.readTextFile( "android_ authority.xml.txt" );

    parseFeed(xml);
} // readFeed


/**
 * parseFeed
 */
private void parseFeed(String xml) {
                log_d("parseFeed");

       RssParser parser = new RssParser();
        parser.setCallback( new RssParser.Callback() {
        @Override 
        public void onSuccess(List<Article> list) {
            showFeed(list);
        }
        });

    try {
        parser.parse(xml);
    } catch (Exception e) {
        toast_short("parse error");
        e.printStackTrace();
    }
} // parseFeed



/**
 * showArticleDialog
 */
private void showArticleDialog(Article article) {
    log_d("showArticleDialog");
    log_d(article.toString());
    ArticleDialog dialog = new ArticleDialog(this);
    dialog.show(article);
    } // showArticleDialog
  

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


} // class MainActivity

