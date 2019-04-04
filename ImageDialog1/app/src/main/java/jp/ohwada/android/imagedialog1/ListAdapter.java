 /**
 * ImageDialog Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.imagedialog1;

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
 * adapter for ListView
 */
public class ListAdapter extends ArrayAdapter<File>
{

	// dubug
	private  final static boolean D = true; 
	private  final static String TAG = "ImageDialog";
	private  final static String TAG_SUB = "ListAdapter";

  		public final static int LAYOUT_RESOURCE_ID = R.layout.list_item;

  		private final static ViewGroup INFLATE_ROOT = null;

	// image size
  		private final static int IMAX_WIDTH = 100;
  		private final static int IMAX_HEIGHT = 100;


	private LayoutInflater mInflater = null;

		private int mResource = 0;

	private FileUtility mFileUtil;

			
    /**
     * === constractor ===
	 * @return void	 
     */
	public  ListAdapter( Context context, int resource, List<File> objects ) {
		super( context, resource, objects );
		mInflater = (LayoutInflater) context.getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
		mResource = resource;
		mFileUtil = new FileUtility(context);
	} // ListAdapter



    /**
     * === get view ===
     * @param int position 
     * @param View convertView    
     * @param  ViewGroup parent      
	 * @return View	 
     */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		log_d("getView");
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
		File file = (File) getItem( position );

		// set value
		// h.tv_version.setText( item.version ) ;
		h.tv_filename.setText( file.getName() ) ;

		Bitmap bitmap =  mFileUtil.getScaledBitmap(file, IMAX_WIDTH, IMAX_HEIGHT );
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
