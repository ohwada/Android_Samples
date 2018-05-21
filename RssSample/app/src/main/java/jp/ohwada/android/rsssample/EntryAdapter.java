/**
 * RSS Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.rsssample;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.squareup.picasso.Picasso;

import org.mcsoxford.rss.*;


/**
 * adapter for ListView
 */
public class EntryAdapter extends ArrayAdapter<Entry> 
{

	// dubug
	private  final static boolean D = true; 
	private String TAG = "RSS";
	private String TAG_SUB = "EntryAdapter";

    	private static final String LF = "\n";

  		public final static int LAYOUT_ID = R.layout.list_item;

  		private final static ViewGroup INFLATE_ROOT = null;

        // date format
         private final static String RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

         private final static String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";

        // image size
            private final static int IMAGE_WIDTH = 400;
            private final static int IMAGE_HEIGHT = 150;


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
			h.tv_title = (TextView) view.findViewById( R.id.TextView_item_title );
			h.tv_date = (TextView) view.findViewById( R.id.TextView_item_date );
			h.iv_image = (ImageView) view.findViewById( R.id.ImageView_item_image );
			view.setTag( h ); 

		} else {
			// load  
			h = (ListHolder) view.getTag();  
		}  
     
		// get item from Adapter
		// Entry item = (Entry) getItem( position );
		Entry item = (Entry) getItem( position );

		// set value
        if ( item != null ) {
            // String url = item.getImageUrl();

            String title = item.getTitle();

            String url = item.getFirstThumbnailUri();


            String date = item.getPubDateRFC822();

            String msg = item.toString();
            log_d( msg );

		    h.tv_title.setText( title ) ;
		    h.tv_date.setText( date ) ;

            Picasso.with( getContext() ).load(url).resize( IMAGE_WIDTH, IMAGE_HEIGHT ).into(h.iv_image);
           // Ion.with( h.iv_image ).load(url);
 
        }

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
		public TextView tv_title;
		public TextView tv_date;
		public ImageView iv_image;
    } // class ListHolder

} // class EntryAdapter
