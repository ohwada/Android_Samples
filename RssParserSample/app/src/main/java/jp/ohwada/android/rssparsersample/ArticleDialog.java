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
 * class ArticleDialog
 */
public class ArticleDialog {

   	// debug
    	private final static String TAG_SUB = "ArticleDialog";

    	private final static String STYLE = "<style>img{display: inline; height: auto; max-width: 100%;} " +
    "</style>\n" + 
    "<style>iframe{ height: auto; width: auto;}" + 
    "</style>\n";

    	private Context  mContext;

/**
 * constractor
 */
public ArticleDialog(Context context) {
    mContext = context;
} // ArticleDialog


/**
 * show
 */
public  void show(Article article) {
    WebView webView = createWebView(article);
    showWebViewDialog(article, webView);
} // show


/**
 * createWebView
 */
public  WebView createWebView(Article article) {
        log_d("createWebView");

                String title = article.getTitle();
                String content = article.getContent();

                WebView webView = new WebView(mContext);
  
                 webView.getSettings().setJavaScriptEnabled(true);
                 webView.setHorizontalScrollBarEnabled(false);
                 webView.setWebChromeClient(new WebChromeClient());
                 webView.loadDataWithBaseURL(null, (STYLE + content), null, "utf-8", null);

                return webView;
    } // createWebView


/**
 * showWebViewDialog
 */
private void showWebViewDialog(Article article, View webView) {
        log_d("showWebViewDialog");

                String title = article.getTitle();
                String content = article.getContent();
  
    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(title);
                builder.setView(webView);

                builder.setNeutralButton( "OK",
                         new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                log_d("NeutralButton onClick");
                                 dialog.dismiss();
                             }
                        }); // NeutralButton OnClickListener

        AlertDialog alertDialog = builder.create();
                 alertDialog.show();

                TextView tv = (TextView) alertDialog.findViewById(android.R.id.message);

                tv.setMovementMethod(LinkMovementMethod.getInstance());

    } // showWebViewDialog


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


} // class ArticleDialog

