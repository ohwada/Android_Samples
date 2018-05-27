/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;



/**
 * class Api
 * 
 * A java wrapper for Giphy's http api based on https://github.com/Giphy/GiphyAPI.
 * original : https://github.com/bumptech/glide/tree/master/samples/giphy
 */
public class Api {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Giphy";
    private final static String TAG_SUB = "Api";

  private static final String BETA_KEY = "dc6zaTOxFJmzC";
  private static final String BASE_URL = "https://api.giphy.com/";
  private static final String TRENDING_PATH = "v1/gifs/trending";
  private static final int LIMIT = 100;
  private static final int OFFSET = 0;

  private static volatile Api api = null;

  private final Handler bgHandler;
  private final Handler mainHandler;
  private final HashSet<Monitor> monitors = new HashSet<>();


  /**
   * === interface Monitor ===
   * 
   * An interface for listening for search results.
   */
  public interface Monitor {
    /**
     * Called when a search completes.
     *
     * @param result The results returned from Giphy's search api.
     */
    void onSearchComplete(SearchResult result);
  } // interface Monitor


    /*
     * == constractor ==
     */
  public Api() {
    HandlerThread bgThread = new HandlerThread("api_thread");
    bgThread.start();
    bgHandler = new Handler(bgThread.getLooper());
    mainHandler = new Handler(Looper.getMainLooper());
    // Do nothing.
  } // Api


    /*
     * signUrl
     */
  public String signUrl(String url) {
    return url + "&api_key=" + BETA_KEY;
  } // signUrl

    /*
     * getTrendingUrl
     */
  public String getTrendingUrl() {
    return signUrl(BASE_URL + TRENDING_PATH + "?limit=" + LIMIT + "&offset=" + OFFSET);

// https://api.giphy.com/v1/gifs/random?limit=100&offset=0&api_key=dc6zaTOxFJmzC
  } // getTrendingUrl


    /*
     * get
     */
  public static Api get() {
    if (api == null) {
      synchronized (Api.class) {
        if (api == null) {
          api = new Api();
        }
      }
    }
    return api;
  } // get


    /*
     * addMonitor
     */
  public void addMonitor(Monitor monitor) {
    monitors.add(monitor);
  } // addMonitor

    /*
     * removeMonitor
     */
  public void removeMonitor(Monitor monitor) {
    monitors.remove(monitor);
  } // removeMonitor

    /*
     * getTrending
     */
  public void getTrending() {
    String trendingUrl = getTrendingUrl();
    query(trendingUrl);
  } // getTrending

    /*
     * query
     */
  public void query(final String apiUrl) {
        String msg = "query: " + apiUrl;
        log_d(msg);
    bgHandler.post(new Runnable() {
      @Override
      public void run() {
        URL url;

        log_d("bg run");
        try {
          url = new URL(apiUrl);
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        } // try

        HttpURLConnection urlConnection = null;
        InputStream is = null;
        SearchResult result = new SearchResult();
        try {
          urlConnection = (HttpURLConnection) url.openConnection();
          is = urlConnection.getInputStream();
          InputStreamReader reader = new InputStreamReader(is);
          result = new Gson().fromJson(reader, SearchResult.class);
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (is != null) {
            try {
              is.close();
            } catch (IOException e) {
              // Do nothing.
            } // try
          } // try

          if (urlConnection != null) {
            urlConnection.disconnect();
          }
        }

        final SearchResult finalResult = result;
        mainHandler.post(new Runnable() {
          @Override
          public void run() {
            log_d("main run");
            for (Monitor monitor : monitors) {
              monitor.onSearchComplete(finalResult);
            } // for
          } // run
        }); // mainHandler.post

      }
    }); // bgHandler.post

  } // query

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class Api
