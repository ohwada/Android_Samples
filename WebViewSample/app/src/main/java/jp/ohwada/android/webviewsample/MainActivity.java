/**
 *  WebView sample
 *  2018-03-01 K.OHWADA
 */

package jp.ohwada.android.webviewsample;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    private final static String TAG = "WebView";
    private final static String TAG_SUB = "MainActivity";


    private final static  String HTML = "<html><body><h1>WebView Sample</h1><img src=\"https://developer.android.com/_static/90580fbce7/images/android/lockup.svg\" /><br /><a href=\"https://developer.android.com/reference/android/webkit/WebView\">webkit WebView</a></body></html>";

     private final static  String MIME_TYPE = "text/html";
    private final static  String ENCODING = "utf-8";
    private final static  String BLANK = "about:blank";

    private final static  String URL1 = "https://www.android.com/intl/ja_jp/one/";

    // TODO : can not display correctly this URL 
    private final static  String URL2 = "https://www.android.com/versions/oreo-8-0/";


private WebView mWebView;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
       mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);

        // deprecated in API level 23
        //mWebView.setVerticalScrollbarOverlay(true);
        // mWebView.setHorizontalScrollbarOverlay(true);

        // added in API level 21
        // minSdkVersion must be over 21
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);


        Button btnHtml = (Button) findViewById(R.id.Button_html);
        btnHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procHtml();
            }
        }); // btnHtml

        Button btnUrl = (Button) findViewById(R.id.Button_url);
        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procUrl();
            }
        }); // btnClear

        Button btnClear = (Button) findViewById(R.id.Button_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procClear();
            }
        }); // btnClear


    }  // onCreate


 	/**
	 * procHtml
	 */ 
private void procHtml() {
        mWebView.loadData(HTML, MIME_TYPE, ENCODING);
} // procHtml


 	/**
	 * procUrl
	 */ 
private void procUrl() {
    mWebView.loadUrl(URL1);
} // procUrl


 	/**
	 * procClear
	 */ 
private void procClear() {
    mWebView.loadUrl(BLANK);
} // procClear


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} //  class MainActivity
