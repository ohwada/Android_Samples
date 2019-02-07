/**
 * Osmdroid Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid5;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import  org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 *  class MarkerUtil
 */
public class MarkerUtil  {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MarkerUtil";

    	private final static String FILE_NAME = "tokyo_bay.csv";

            private final static String COMMA = ",";
            private final static String SPACE = " ";

	 private AssetManager mAssetManager;

   private Resources mResources;

   private List<Drawable> mMarkerIcons;


/**
 *  constractor
 */
public MarkerUtil(Context context) {
	mAssetManager = context.getAssets();
   mResources = context.getResources();
}


/**
 *  getMarkers
 */
    public List<OverlayItem> getMarkers(){
        setupMarkerIcon();

			/* Create ItemizedOverlay showing a some Markers on some cities. */

    List<OverlayItem> items = new ArrayList<>();

List<String> lines = readAsset(FILE_NAME);

    String DESC = "";

    for(String line: lines) {
        // title, latitude, longitude, icon_num
            String[] cols = line.split(COMMA);

    if (cols.length >= 4 ) {
            String title = cols[0];
            double lat = parseDouble(cols[1]);
            double lon = parseDouble(cols[2]);
            int iconNum = parseInt(cols[3]);
        log_d( title + SPACE +  lat + SPACE + lon + SPACE + iconNum  );

    OverlayItem item = new OverlayItem(title, DESC, new GeoPoint(lat, lon));

    Drawable icon = getMarkerIcon( iconNum );
    if ( icon != null ) {
        item.setMarker( icon );
    }

    items.add(item);

        } // if cols.length
    } // for

    return items;
} // getMarkers

 	/**
	 *  setupMarkerIcon
	 */ 
private void setupMarkerIcon() {
  
    mMarkerIcons = new ArrayList<Drawable>();

    mMarkerIcons.add( mResources.getDrawable(R.drawable.marker_1) );
    mMarkerIcons.add( mResources.getDrawable(R.drawable.marker_2) );
    mMarkerIcons.add( mResources.getDrawable(R.drawable.marker_3) );
    mMarkerIcons.add( mResources.getDrawable(R.drawable.marker_4) );

} // setupMarkeIcon


 	/**
	 *  getMarkerIcon
	 */ 
private Drawable getMarkerIcon( int num ) {
    Drawable icon = null;
    int index= num -1;
    
    if (index >= 0 && index < mMarkerIcons.size() ) {
        icon = mMarkerIcons.get(index);
    }

    return icon;
} // getMarkerIcon


 	/**
	 *  readAsset
	 */ 
private  List<String> readAsset(String fileName ) {

List<String> lines = new ArrayList<String>();

InputStream is = null;
BufferedReader br = null;

try {
    try {
        is = mAssetManager.open(fileName);
        br = new BufferedReader(new InputStreamReader(is));

        String str;
        while ((str = br.readLine()) != null) {
            lines.add( str);
        }
    } finally {
        if (is != null) is.close();
        if (br != null) br.close();
    }
    } catch (Exception e){
			if (D) e.printStackTrace();
    }

    return lines;
    }


 	/**
	 * parseDouble
	 */ 
private  double parseDouble(String str) {
    double d = 0;
    try {
        d = Double.parseDouble(str);
    } catch (Exception e){
			if (D) e.printStackTrace();
    }
    return d;
}

 	/**
	 * parseInt
	 */ 
private  int parseInt(String str) {
    int i = 0;
    try {
        i = Integer.parseInt( str.trim() );
    } catch (Exception e){
			if (D) e.printStackTrace();
    }
    return i;
}

 	/**
	 * write into logcat
	 */ 
	private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
