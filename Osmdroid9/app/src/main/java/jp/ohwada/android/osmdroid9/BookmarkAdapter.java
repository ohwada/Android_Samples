/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * adapter for ListView
 */
public class BookmarkAdapter extends ArrayAdapter<BookmarkRecord> 
{
	// Layout Inflater
	private LayoutInflater mInflater = null;


			
    /**
     * === constractor ===
     * @param Context context
     * @param int textViewResourceId
     * @param List<BookmarkRecord> objects     
	 * @return void	 
     */
	public BookmarkAdapter( Context context, int textViewResourceId, List<BookmarkRecord> objects ) {
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
		BookmarkHolder h = null;
            
		// once at first
		if ( view == null ) {
			// get view form xml
			view = mInflater.inflate( R.layout.bookmark_row, null );
			
			// save 
			h = new BookmarkHolder(); 
			h.tv_message = (TextView) view.findViewById( R.id.TextView_row_message );
			view.setTag( h ); 

		} else {
			// load  
			h = (BookmarkHolder) view.getTag();  
		}  
     
		// get item form Adapter
		BookmarkRecord item = (BookmarkRecord) getItem( position );

		// set value
		h.tv_message.setText( item.getMessage() ) ;

		return view;
	}

	/**
	 * class holder
	 */	
	static class BookmarkHolder { 
		public TextView tv_message;
    } 
} // class BookmarkAdapter
