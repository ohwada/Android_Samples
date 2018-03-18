/**
 * ListView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.listviewsample2;

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
public class ListAdapter extends ArrayAdapter<VersionItem> 
{

	// dubug
	private  final static boolean D = Constant.DEBUG; 
	private String TAG_SUB = "ListAdapter";

  		public final static int LAYOUT_RESOURCE_ID = R.layout.list_item;

  		private final static ViewGroup INFLATE_ROOT = null;

	private LayoutInflater mInflater = null;

		private int mResource = 0;

	private ImageFile mImageFile;
			
    /**
     * === constractor ===
     * @param Context context
     * @param int resource
     * @param List<VersionItem> objects     
	 * @return void	 
     */
	public  ListAdapter( Context context, int resource, List<VersionItem> objects ) {
		super( context, resource, objects );
		mInflater = (LayoutInflater) context.getSystemService( 
			Context.LAYOUT_INFLATER_SERVICE ) ;
		mResource = resource;
		mImageFile = new ImageFile(context);
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
			h.tv_version = (TextView) view.findViewById( R.id.TextView_item_version );
			h.tv_codename = (TextView) view.findViewById( R.id.TextView_item_coodename );
			h.iv_image = (ImageView) view.findViewById( R.id.ImageView_item_image );
			view.setTag( h ); 

		} else {
			// load  
			h = (ListHolder) view.getTag();  
		}  
     
		// get item form Adapter
		VersionItem item = (VersionItem) getItem( position );

		// set value
		h.tv_version.setText( item.version ) ;
		h.tv_codename.setText( item.codename ) ;

		Bitmap bitmap =  getBitmap(item.codename);
		h.iv_image.setImageBitmap( bitmap ) ;

		return view;
	} // getView


	/**
	 * getBitmap
	 */
public Bitmap getBitmap( String codename ) {
	log_d("getBitmap");
String fileName = codename.toLowerCase() + ".png";
		Bitmap bitmap = mImageFile.getAssetBitmap(fileName);
	return bitmap;
	} // getAssetBitmap


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
		public TextView tv_version;
		public TextView tv_codename;
		public ImageView iv_image;
    } // class ListHolder

} // class ListAdapter
