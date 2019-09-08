/**
 * SSDP Client
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.ssdpclient;

import java.io.File;
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




/**
 * class ListAdapter
 * adapter for ListView
 */
public class ListAdapter extends ArrayAdapter<SearchResult>
{


	// dubug
	private  final static boolean D = true; 
	private  final static String TAG = "CloudVision";
	private  final static String TAG_SUB = "ListAdapter";


/**
 * Layout Resource ID
 */
  		public final static int LAYOUT_RESOURCE_ID = R.layout.list_item;


/**
 * Inflate Root
 */
  		private final static ViewGroup INFLATE_ROOT = null;


/**
 * LayoutInflater
 */
	private LayoutInflater mInflater = null;


/**
 * resource ID for a layout file
 * constructor parameter
 * not use
 */
	private int mResource = 0;

			
/**
 * constractor  
 */
	public  ListAdapter( Context context, int resource, List<SearchResult> objects ) {
		super( context, resource, objects );
		mInflater = (LayoutInflater) context.getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
		mResource = resource;
	} // ListAdapter



 /**
  * get view
 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View view = convertView;
        ListHolder h = null;  
            
		// once at first
		if ( view == null ) {
			// get view
			view = mInflater.inflate( LAYOUT_RESOURCE_ID, INFLATE_ROOT );		
			// save 
			h = new ListHolder(); 
			h.tv_item = (TextView) view.findViewById( R.id.TextView_item );
			view.setTag( h ); 

		} else {
			// load  
			h = (ListHolder) view.getTag();  
		}  
     
		// get item form Adapter
		SearchResult item = (SearchResult) getItem( position );
        String title = item.getLabel();

		log_d("item: " + title);
		h.tv_item.setText( title ) ;

		return view;
	} // getView


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


	/**
	 * class ListHolder
	 */	
	static class ListHolder { 
		public TextView tv_item;
    } // class ListHolder

} // class ListAdapter
