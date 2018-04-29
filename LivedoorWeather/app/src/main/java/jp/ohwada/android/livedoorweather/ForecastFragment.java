/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.koushikdutta.ion.Ion;
import com.google.gson.Gson;

import jp.ohwada.android.livedoorweather.model.*;

/**
 * class ForecastFragment
 */
public class ForecastFragment extends Fragment {

        // debug
	    private  final static boolean D = Constant.DEBUG; 
    	private final static String TAG = "Weather";
    	private final static String TAG_SUB = "ForecastFragment";

    	private final static int LAYOUT_ID = R.layout.fragment_forecast;


	private ForecastAdapter  mAdapter;

    private ListView mListView ;

   private TextView mTextViewCopyright;

    private ImageView mImageViewCopyright;

    private Weather mWeather;


/**
 * constractor
 */
public ForecastFragment(Context context) {
    super();
    List<Forecast> list = new ArrayList<Forecast>();
    mAdapter = new ForecastAdapter( context, ForecastAdapter.LAYOUT_ID, list );
} // ForecastFragment;

/**
 * onCreateView
 */
    @Override
    public View onCreateView(
    		LayoutInflater inflater,
    		ViewGroup container,
    		Bundle savedInstanceState) {
    log_d("onCreateView");
        View view = inflater.inflate(LAYOUT_ID, container, false);
        return view;
    } // onCreateView

/**
 * onViewCreated
 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    log_d("onViewCreated");
     mListView = (ListView) view.findViewById(R.id.ListView_forecast);
    mTextViewCopyright = (TextView) 
view.findViewById(R.id.TextView_forecast_copyright);
    mImageViewCopyright = (ImageView) view.findViewById(R.id.ImageView_forecast_copyright);

    showWeather(mWeather);
    } // onViewCreated



 	/**
	 * showWeather
	 */ 
public void  showWeather(Weather weather) {
    log_d("showWeather");
    if (weather == null) return;

    mWeather = weather;
     List<Forecast> list = weather.forecasts;

    String copyright = weather.getCopyrightTtitle();
    String url = weather.getCopyrightImageUrl();

    mAdapter.clear();
    mAdapter.addAll(list);
    mAdapter.notifyDataSetChanged();

    if (mListView != null) {
	    mListView.setAdapter( mAdapter );
        mListView.invalidateViews();
    }

    if (mTextViewCopyright != null ) {
        mTextViewCopyright.setText(copyright);
    }

    if ((mImageViewCopyright != null )&&(url != null) ) {
        Ion.with( mImageViewCopyright ).load(url);
    }

} // showWeather


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class ForecastFragment
