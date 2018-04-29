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

import jp.ohwada.android.livedoorweather.model.*;

/**
 * adapter for ListView
 */
public class PinpointLocationAdapter extends ArrayAdapter<PinpointLocation> 
{

	// dubug
	private  final static boolean D = Constant.DEBUG; 
	private String TAG_SUB = "PinpointLocationAdapter";

  		public final static int LAYOUT_ID = R.layout.location_item;

  		private final static ViewGroup INFLATE_ROOT = null;

	private LayoutInflater mInflater = null;


			
    /**
     * === constractor ===
     * @param Context context
     * @param int resource
     * @param List<PinpointLocation> objects     
	 * @return void	 
     */
	public  PinpointLocationAdapter( Context context, int resource, List<PinpointLocation> objects ) {
		super( context, resource, objects );
		mInflater = (LayoutInflater) context.getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
	} // PinpointLocationAdapter



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
			h.tv_text = (TextView) view.findViewById( R.id.TextView_locationt_text );
			view.setTag( h ); 

		} else {
			// load  
			h = (ListHolder) view.getTag();  
		}  
     
		// get item from Adapter
		PinpointLocation item = (PinpointLocation) getItem( position );

		// set value
        if ( item != null ) {
            String name = item.name;
            log_d( item.toString() );

		    h.tv_text.setText( name ) ;
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
    } // class ListHolder

} // class PinpointLocationAdapter
