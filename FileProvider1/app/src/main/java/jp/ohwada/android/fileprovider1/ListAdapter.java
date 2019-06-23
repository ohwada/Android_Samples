/**
 * File Provider Sample
 * ListAdapter
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.fileprovider1;

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
public class ListAdapter extends ArrayAdapter<File>
{

	// dubug
	private  final static boolean D = true; 
	private  final static String TAG = "FileProvider";
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
 * Maximum of image size 
 */
  		private final static int MAX_WIDTH = 100;
  		private final static int MAX_HEIGHT = 100;


/**
 * LayoutInflater
 */
	private LayoutInflater mInflater = null;


/**
 * FileUtil  
 */
	private FileUtil mFileUtil;


/**
 * resource ID for a layout file
 * constructor parameter
 * not use
 */
	private int mResource = 0;

			
/**
 * constractor  
 */
	public  ListAdapter( Context context, int resource, List<File> objects ) {
		super( context, resource, objects );
		mInflater = (LayoutInflater) context.getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
		mResource = resource;
		mFileUtil = new FileUtil(context);
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
			h.tv_filename = (TextView) view.findViewById( R.id.TextView_item_filename );
			h.iv_image = (ImageView) view.findViewById( R.id.ImageView_item_image );
			view.setTag( h ); 

		} else {
			// load  
			h = (ListHolder) view.getTag();  
		}  
     
		// get item form Adapter
		File item = (File) getItem( position );
		log_d("item: " + item.toString());
        String name = item.getName();
		h.tv_filename.setText( name ) ;

		Bitmap bitmap =  BitmapUtil.getScaledBitmap( item, MAX_WIDTH, MAX_HEIGHT);
		h.iv_image.setImageBitmap( bitmap ) ;

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
		public TextView tv_filename;
		public ImageView iv_image;
    } // class ListHolder

} // class ListAdapter
