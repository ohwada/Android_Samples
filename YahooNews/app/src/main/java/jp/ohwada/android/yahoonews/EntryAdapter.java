/**
 * Yahoo News
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.yahoonews;

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

import jp.ohwada.android.yahoonews.model.*;

/**
 * adapter for ListView
 */
public class EntryAdapter extends ArrayAdapter<Entry> 
{

	// dubug
	private  final static boolean D = Constant.DEBUG; 
	private String TAG_SUB = "EntryAdapter";

  		public final static int LAYOUT_ID = R.layout.entry_item;

  		private final static ViewGroup INFLATE_ROOT = null;

	private LayoutInflater mInflater = null;


			
    /**
     * === constractor ===
     * @param Context context
     * @param int resource
     * @param List<Entry> objects     
	 * @return void	 
     */
	public  EntryAdapter( Context context, int resource, List<Entry> objects ) {
		super( context, resource, objects );
		mInflater = (LayoutInflater) context.getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
	} // EntryAdapter



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
			h.tv_title = (TextView) view.findViewById( R.id.TextView_entry_title );
			h.tv_date = (TextView) view.findViewById( R.id.TextView_entry_date );
			h.iv_image = (ImageView) view.findViewById( R.id.ImageView_entry_image );
			view.setTag( h ); 

		} else {
			// load  
			h = (ListHolder) view.getTag();  
		}  
     
		// get item from Adapter
		Entry item = (Entry) getItem( position );

		// set value
        if ( item != null ) {
            String url = item.getImageUrl();
            log_d( item.toString() );

		    h.tv_title.setText( item.title ) ;
		    h.tv_date.setText( item.pubDate ) ;
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
		public TextView tv_title;
		public TextView tv_date;
		public ImageView iv_image;
    } // class ListHolder

} // class EntryAdapter
