/**
 * CSV Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.csv3;

import java.util.List;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * adapter for ListView
 */
public class ShoppingAdapter extends ArrayAdapter<Shopping> 
{

        // debug
	private final static boolean D = true;
    	private final static String TAG = "CSV";
    	private final static String TAG_SUB = "ShoppingAdapter";

	// Layout Inflater
	private LayoutInflater mInflater = null;

		
    /**
     * === constractor ===
     */
	public ShoppingAdapter( Context context, int textViewResourceId, List<Shopping> objects ) {

		super( context, textViewResourceId, objects );
		mInflater = (LayoutInflater) super.getContext().getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
	} // ShoppingAdapter


    /**
     * === get view === 
     */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View view = convertView;
		ShoppingHolder h = null;
            
		// once at first
		if ( view == null ) {
			// get view form xml
			view = mInflater.inflate( R.layout.shopping_row, null );
			
			// save 
			h = new ShoppingHolder(); 
			h.tv_message = (TextView) view.findViewById( R.id.TextView_row_message );
			view.setTag( h ); 

		} else {
			// load  
			h = (ShoppingHolder) view.getTag();  
		}  
     
		// get item form Adapter
		Shopping item = (Shopping) getItem( position );

		// set value
		h.tv_message.setText( item.getMessage() ) ;

		return view;
	}

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

	/**
	 * class holder
	 */	
	static class ShoppingHolder { 
		public TextView tv_message;
    } 
} // class ShoppingAdapter
