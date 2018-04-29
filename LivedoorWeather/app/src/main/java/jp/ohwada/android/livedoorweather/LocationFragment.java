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
 * class LocationFragment
 */
public class LocationFragment extends Fragment {

        // debug
	    private  final static boolean D = Constant.DEBUG; 
    	private final static String TAG = "Weather";
    	private final static String TAG_SUB = "LocationFragment";

    	private final static int LAYOUT_ID = R.layout.fragment_location;

    	private final static int HEADER_FOOTER_ID = -1;

    private PinpointLocationAdapter mAdapter;

    List<PinpointLocation> mList = new ArrayList<PinpointLocation>();

    private ListView mListView ;
 
   private TextView mTextViewCopyright;

    private ImageView mImageViewCopyright;

    private Weather mWeather;

 // callback 
    private OnClickListener mListener;  


   /*
     * callback interface
     */    
    public interface OnClickListener {
        public void onItemClick( PinpointLocation location );
    } // interface


    /*
     * callback
     */ 
    public void setOnClickListener( OnClickListener listener ) {
        log_d("setOnClickListener");
        mListener = listener;
    } // setOnClickListener


/**
 * constractor
 */
public LocationFragment(Context context) {
    super();
    mAdapter = new PinpointLocationAdapter( context, PinpointLocationAdapter.LAYOUT_ID, mList );
    
} // LocationFragment


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
    } //  onCreateView

/**
 * onViewCreated
 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    log_d("onViewCreated");
     mListView = (ListView) view.findViewById(R.id.ListView_location);
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnClickListener

    mTextViewCopyright = (TextView) view.findViewById(R.id.TextView_location_copyright);
    mImageViewCopyright = (ImageView) view.findViewById(R.id.ImageView_location_copyright);

    showWeather(mWeather);
    } // onViewCreated



 	/**
	 * showWeather
	 */ 
public void  showWeather(Weather weather) {
    log_d("showWeather");
    if (weather == null) return;

    mWeather = weather;
     mList = weather.pinpointLocations;
    String copyright = weather.getCopyrightTtitle();
    String url = weather.getCopyrightImageUrl();

    mAdapter.clear();
    mAdapter.addAll(mList);
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
	 *  procItemClick
	 * @param int position
	 * @param long id 
	 */
	private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		log_d(msg );
		// header footer
		if ( id == HEADER_FOOTER_ID )  return;

		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        PinpointLocation item = mList.get( position );
        notifyItemClick(item);

	} // procItemClick


    /**
     * notifyItemClick
     */
    private void notifyItemClick( PinpointLocation location ) {
        if (( mListener != null )&&( location != null )) {
            log_d("nnotifyItemClick: " + location.toString() );
            mListener.onItemClick( location );
        } 
}	// notifyItemClick


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // LocationFragment
