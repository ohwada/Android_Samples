/**
 *  WebView sample
 *  click listener
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.webview2;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private WebView mWebView;

    private WebViewUtil  mWebViewUtil;

    private FileUtil mFileUtil;

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);

        Button btn1 = (Button) findViewById(R.id.Button_1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHtml();
            }
        }); // btn1

        Button btn2 = (Button) findViewById(R.id.Button_2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procListener();
            }
        }); // btn2

        Button btn3 = (Button) findViewById(R.id.Button_3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebViewUtil.clearView();
            }
        }); // btn3

    mWebViewUtil = new WebViewUtil(this);
    mWebViewUtil.setWebView(mWebView);

    mFileUtil = new FileUtil(this);

    }  // onCreate


 	/**
	 * procHtml
	 */ 
private void showHtml() {
    mWebViewUtil.setChromeClient() ;
    String html = mFileUtil.readAssetTextFile( "sample1.html" );
    mWebViewUtil.loadData(html);
} // procHtml

 	/**
	 * procListener
	 */ 
private void procListener() {
    mWebViewUtil.setCustomWebViewClient();
    String html = mFileUtil.readAssetTextFile( "sample2.html" );
    mWebViewUtil.loadData(html);
} // procListener


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} //  class MainActivity
