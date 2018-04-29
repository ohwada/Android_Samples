/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import jp.ohwada.android.livedoorweather.model.*;

import static android.app.Activity.RESULT_OK;

/**
 * class MainActivity
 */
public class MainActivity extends TabActivity  {

// http://weather.livedoor.com/weather_hacks/webservice
    private final static String URL_BASE = "http://weather.livedoor.com/forecast/webservice/json/v1?city=";
    private final static String CITY_TOKYO = "130010";
    private final static String CITY_YOKOHAMA = "140010";

// http://weather.livedoor.com/forecast/webservice/json/v1?city=140010
    private final static String URL = URL_BASE + CITY_YOKOHAMA;

// liveddor server needs request header
    private final static String REQUEST_HEADER_KEY = "X-Requested-With";

  private final static String REQUEST_HEADER_VALUE = "XMLHttpRequest";

static final int REQUEST_WEB_BROWSER = 1;


/**
 * constractor
 */
public MainActivity() {
    super();
    TAG_SUB = "TabActivity";
} // MainActivity


/**
 * == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for debug
            // readWeather();

    } // onCreate


/**
 * onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // onCreateOptionsMenu

/**
 * == onOptionsItemSelected ==
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            toast_short( "settings" );
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected


/**
 * == onActivityResult ==
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    log_d("onActivityResult: " + requestCode + " , "+ resultCode );
    // notng to do
} // onActivityResult



/**
	 *  procUpdateClick
	 */
    @Override
    protected void procUpdateClick() {
        log_d("procUpdateClick");
        toast_short("update");
        getWeather();
        // for debug
        // readWeather();
    } // procUpdateClick


/**
	 * procLocationItemClick
	 */
    @Override
    protected void procLocationItemClick( PinpointLocation location ) {
        log_d("procLocationItemClick");
        if ( location != null ) {
            toast_short( location.name );
            startActivityWeb( location.link );
        }
    } // procLocationItemClick



/**
 * get Weather from web server
 */
private void getWeather() {
        log_d("getWeather");

    try {
        Ion.with(MainActivity.this)
                .load(URL)
                .setHeader(REQUEST_HEADER_KEY, REQUEST_HEADER_VALUE)
                .as(new TypeToken<Weather>() {
                })
                .setCallback(new FutureCallback<Weather>() {
                    @Override
                    public void onCompleted(Exception e, Weather weather) {
                        procCompleted(e, weather);
                    }
                }); // FutureCallback

    } catch(Exception e) {
            e.printStackTrace();
            toast_short("update failed");
    }
} // procWeather



 	/**
	 * procCompleted
	 */ 
private void procCompleted(Exception e, Weather weather) {
    // failure
    if (e != null) {
            e.printStackTrace();
            toast_short("update failed");
            return;
    }
    // success
    String result = weather.toString();
     log_d( "procCompleted: " + result);
        toast_short("updated");

        showWeather(weather);

    } // procCompleted




 	/**
	 * readWeather from asssets holder
	 *  for debug
	 */ 
private void readWeather() {
    AssetFile file = new AssetFile(this);
    String json = file.readTextFile( "yokohama.json" );
    Gson gson = new Gson();
    Weather weather = gson.fromJson(json, Weather.class);
    log_d(weather.toString());
    showWeather(weather);

} // eadWeather



 	/**
	 * showWeather
	 */ 
private void  showWeather(Weather weather) {
        if (mOverviewFragment != null) {
            mOverviewFragment.showWeather(weather);
        }
        if (mForecastFragment != null) {
            mForecastFragment.showWeather(weather);
        }
        if (mLocationFragment != null) {
            mLocationFragment.showWeather(weather);
        }
} // showWeather



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
	 * toast_short
	 */
	protected void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

} // class MainActivity
