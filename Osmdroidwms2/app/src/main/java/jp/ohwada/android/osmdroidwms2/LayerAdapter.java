/**
 * Osmdroid Sample
 * OSM with WMS Tile
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroidwms2;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Log;

import org.osmdroid.wms.WMSEndpoint;
import org.osmdroid.wms.WMSLayer;
import org.osmdroid.wms.WMSParser;
import org.osmdroid.wms.WMSTileSource;

/**
 * adapter for ListView
 */
public class LayerAdapter extends ArrayAdapter<WMSLayer> 
{

	// dubug
    public final static boolean D = true; 
	public final static String TAG = "OSM";
	private final static String TAG_SUB = "MainActivity";


	// Layout Inflater
	private LayoutInflater mInflater = null;
			
    /**
     * === constractor ===
     * @param Context context
     * @param int textViewResourceId
     * @param List<PersonRecord> objects     
	 * @return void	 
     */
	public LayerAdapter( Context context, int textViewResourceId, List<WMSLayer> objects ) {
		super( context, textViewResourceId, objects );
		mInflater = (LayoutInflater) super.getContext().getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
	}

    /**
     * === get view ===
     * @param int position 
     * @param View convertView    
     * @param  ViewGroup parent      
	 * @return View	 
     */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View view = convertView;
		LayerHolder h = null;
            
		// once at first
		if ( view == null ) {
			// get view form xml
			view = mInflater.inflate( R.layout.db_row, null );
			
			// save 
			h = new LayerHolder(); 
			h.tv_title = (TextView) view.findViewById( R.id.TextView_row_message );
			view.setTag( h ); 

		} else {
			// load  
			h = (LayerHolder) view.getTag();  
		}  
     
		// get item form Adapter
		WMSLayer item = (WMSLayer) getItem( position );

		// set value
		h.tv_title.setText( item.getTitle() ) ;

		return view;
	}

 	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

	/**
	 * class holder
	 */	
	static class LayerHolder { 
		public TextView tv_title;
    } 
} // class PersonAdapter
