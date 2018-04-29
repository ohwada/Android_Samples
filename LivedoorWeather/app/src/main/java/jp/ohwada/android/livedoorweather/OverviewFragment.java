/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.koushikdutta.ion.Ion;

import jp.ohwada.android.livedoorweather.model.*;

/**
 * class OverviewFragment
 */
public class OverviewFragment extends Fragment {

    private TextView mTextViewContent;

    private TextView mTextViewCopyright;

    private ImageView mImageViewCopyright;

    private Weather mWeather;

        // debug
	    private  final static boolean D = Constant.DEBUG; 
    	private final static String TAG = "Weather";
    	private final static String TAG_SUB = "OverviewFragment";

    	private final static int LAYOUT_ID = R.layout.fragment_overview;

 // callback 
    private OnClickListener mListener; 

    private Context mContext;


   /*
     * callback interface
     */    
    public interface OnClickListener {
        public void onUpdateClick();
    } // interface

    /*
     * callback
     */ 
    public void setOnClickListener( OnClickListener listener ) {
        log_d("setOClickListener");
        mListener = listener;
    } // setOnItemClickListener

public  OverviewFragment(Context context) {
    super();
    mContext = context;
}

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
 mTextViewContent = (TextView) view.findViewById(R.id.TextView_overview_content);
mTextViewCopyright = (TextView) view.findViewById(R.id.TextView_overview_copyright);
mImageViewCopyright = (ImageView) view.findViewById(R.id.ImageView_overview_copyright);

Button btnUpdate = (Button) view.findViewById(R.id. Button_overview_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_d("Update onClick");
                  notifyUpdateClick();
            }
        }); // btnUpdate

    showWeather(mWeather);
    } // onViewCreated



 	/**
	 * showWeather
	 */ 
public void  showWeather(Weather weather) {
    log_d("showWeather");

    if (weather == null ) return;
        mWeather = weather;
    String overview = weather.getOverview();
    String copyright = weather.getCopyrightTtitle();
    String url = weather.getCopyrightImageUrl();
    
    log_d(overview);
    if (mTextViewContent != null ) {
     mTextViewContent.setText(overview);
    }

    if (mTextViewCopyright != null ) {
        mTextViewCopyright.setText(copyright);
    }

    if ((mImageViewCopyright != null )&&(url != null) ) {
        Ion.with( mImageViewCopyright ).load(url);
    }
} // showWeather



    /**
     * notifyUpdateClick
     */
    private void notifyUpdateClick( ) {
                    log_d("nnotifyUpdateClick");
           if ( mListener != null ) {
            mListener.onUpdateClick();
        } 
}	// notifyUpdateClick



 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class OverviewFragment
