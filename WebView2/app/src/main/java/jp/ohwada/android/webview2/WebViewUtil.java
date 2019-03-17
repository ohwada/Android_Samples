/**
 *  WebView sample
 *  click listener
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.webview2;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *  class WebViewUtil
 */
public class WebViewUtil  {

        // debug
	private final static boolean D = true;
    private final static String TAG = "WebView";
    private final static String TAG_SUB = "WebViewUtil";

     private final static  String MIME_TYPE = "text/html";
    private final static  String ENCODING = "utf-8";
    private final static  String BLANK = "about:blank";


    private Context mContext;

    private WebView mWebView;

/**
 *  constractor
 */
public WebViewUtil(Context context) {
    mContext = context;
} // WebViewUtily

/**
 *  setWebView
 */
public void setWebView(WebView view) {
    mWebView = view;
} // setWebView

/**
 * setChromeClient
 */ 
public void setChromeClient() {
    mWebView.setWebChromeClient( new WebChromeClient() );
    setSettings();
} // setChromeClient

/**
 * setCustomWebViewClient
 */ 
public void setCustomWebViewClient() {
    mWebView.setWebViewClient( new CustomWebViewClient() );
    // setSettings();
} // setCustomWebViewClient

/**
 * setSettings
 */ 
private void setSettings() {
    mWebView.getSettings().setJavaScriptEnabled( true );
    mWebView.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
} // setSettings

/**
 * .loadData
 */ 
public void loadData(String html) {
        mWebView.loadData(html, MIME_TYPE, ENCODING);
} // .loadData


 	/**
	 * procUrl
	 */ 
public void loadUrl(String url) {
    mWebView.loadUrl(url);
} // procUrl


 	/**
	 * clearView
	 */ 
public void clearView() {
    mWebView.loadUrl(BLANK);
} // clearView


/**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

 	/**
	 * write into logcat
	 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

/**
 * class CustomWebViewClient
 * click listener for a tag
 * original : https://www.ipentec.com/document/android-webview-detect-link-click-and-cancel-page-load
 */ 
public class CustomWebViewClient extends WebViewClient {
  
     // example  :  app://apple/ 
    private final static String REGEX = "app://(\\w+)/";

    private Pattern mPattern;

/**
 * constractor
 */ 
public CustomWebViewClient() {
    super();
    mPattern = Pattern.compile(REGEX);
} // CustomWebViewClient

/**
 * shouldOverrideUrlLoading
 */ 
  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {

    log_d("url: " + url);
    Matcher m = mPattern.matcher(url);
    if (m.find()){
        String item = m.group(1);
            toast_long( item );
    }
   
    view.stopLoading();
    return false;

  } // shouldOverrideUrlLoading

} // class CustomWebViewClient

} //  class WebViewUtil
