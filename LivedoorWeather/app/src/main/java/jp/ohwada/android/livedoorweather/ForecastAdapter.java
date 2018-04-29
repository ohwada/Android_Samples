/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */


package jp.ohwada.android.livedoorweather;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import jp.ohwada.android.livedoorweather.model.*;

/**
 * adapter for ListView
 */
public class ForecastAdapter extends ArrayAdapter<Forecast> 
{

	// dubug
	private  final static boolean D = Constant.DEBUG; 
	private String TAG_SUB = "ForecastAdapter";

  		public final static int LAYOUT_ID = R.layout.forecast_item;

  		private final static ViewGroup INFLATE_ROOT = null;


	    private LayoutInflater mInflater = null;

			
    /**
     * === constractor ===	 
     */
	public  ForecastAdapter( Context context, int resource, List<Forecast> objects ) {
		super( context, resource, objects );
		mInflater = (LayoutInflater) context.getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
	} // ListAdapter



    /**
     * === get view === 
     */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		log_d("getView");
		View view = convertView;
        ListHolder h = null;  
            
		// once at first
		if ( view == null ) {
			// get view
			view = mInflater.inflate( LAYOUT_ID, INFLATE_ROOT );
			// save 
			h = new ListHolder(); 
			h.tv_text = (TextView) view.findViewById( R.id.TextView_forecast_text );

			h.iv_image = (ImageView) view.findViewById( R.id.ImageView_forecast_image );
			view.setTag( h ); 

		} else {
			// load  
			h = (ListHolder) view.getTag();  
		}  
     
		// get item from Adapter
		Forecast item = (Forecast) getItem( position );

		// set value
        if ( item != null ) {
            String text = item.getInfo();
            String url = item.getImageUrl();
            log_d( item.toString() );

		    h.tv_text.setText( text ) ;
            Ion.with( h.iv_image ).load(url);
        }

		return view;
	} // getView





 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


	/**
	 * class ListHolder
	 */	
	static class ListHolder { 
		public TextView tv_text;
		public ImageView iv_image;
    } // class ListHolder

} // class ListAdapter
