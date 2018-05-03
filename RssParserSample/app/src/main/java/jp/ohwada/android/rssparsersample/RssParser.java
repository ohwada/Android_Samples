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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


 	/**
	 * class RssParser
	 */ 
public class RssParser implements Observer {

   	// debug
    	private final static String TAG_SUB = "RssParser";

    private XMLParser xmlParser;

 // callback 
    private Callback mCallback;  


/**
 * callback interface
 */    
    public interface Callback {
        public void onSuccess( List<Article> list );
    } // interface


/**
 * callback
 */ 
    public void setCallback( Callback callback ) {
        log_d("setCallback");
        mCallback = callback;
    } // setCallback


 	/**
	 * constractor
	 */ 
    public RssParser() {
        xmlParser = new XMLParser();
        xmlParser.addObserver(this);
    } // RssParser


 	/**
	 * parse
	 */ 
    public void parse(String xml) throws XmlPullParserException, IOException {
            log_d("parse");
                xmlParser.parseXML(xml);

} // parse

 	/**
	 * pdate
	 */ 
    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable observable, Object data) {
        log_d("update");
        List<Article> list = (List<Article>) data;
           if ( mCallback != null ) {
            mCallback.onSuccess( list );
        } 
    } // update


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class RssParser



