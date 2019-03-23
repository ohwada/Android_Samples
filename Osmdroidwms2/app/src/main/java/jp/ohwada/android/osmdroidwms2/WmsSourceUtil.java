/**
 * Osmdroid Sample
 * OSM with WMS Tile
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroidwms2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import  org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import  org.osmdroid.api.IMapController;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.tileprovider.BitmapPool;

import org.osmdroid.wms.WMSTileSource;

/**
 *  class MainActivity
 */
public class WmsSourceUtil {

    // intent key
    public final static  String KEY_BASE_URL = "base_url";
    public final static  String KEY_VERSION = "version";
    public final static  String KEY_LAYER_TITLE = "layer_title";
    public final static  String KEY_LAYER_NAME = "layer_name";
    public final static  String KEY_SRS = "srs";
    public final static  String KEY_STYLE = "style";
    public final static  String KEY_PIXEL_SIZE = "pixel_size";


        // debug
	private final static boolean D = true;
    private final static String TAG = "OSM";
    private final static String TAG_SUB = "WmsSourceUti";

    // WMSTileSource
    private final static int SIZE_DEFAULT = 256;

    private Context mContext;

    private MapView mMapView;

public WmsSourceUtil(Context context) {
    mContext = context;
} // WmsUtil

public void setMapView(MapView view) {
		mMapView = view;
	    mMapView.setTilesScaledToDpi(true);
} // setMapView

/**
 *  setupTile
 */
public String getLayerTitle(  Intent intent ) {
    if (intent == null) return null;
    return intent.getStringExtra(KEY_LAYER_TITLE);
} // getLayerTitle

/**
 *  setupTile
 */
public void setupTile(     Intent intent ) {

    if (intent == null) {
        toast_long("intent null");
        return;
    }

    String baseurl = intent.getStringExtra(KEY_BASE_URL);
    String version = intent.getStringExtra(KEY_VERSION);
    String  layerName = intent.getStringExtra(KEY_LAYER_NAME);
    String  srs = intent.getStringExtra(KEY_SRS);
    String  style = intent.getStringExtra(KEY_STYLE);
    int  size = intent.getIntExtra(KEY_PIXEL_SIZE, SIZE_DEFAULT);

    log_d( "baseurl: " + baseurl + " version: " + version + " layerName: " + layerName + " srs: " + srs + " style: " + style + " pixel size: " + size  );

    if ((baseurl == null)||(baseurl.length() == 0)) {
        toast_long("baseurl empty");
        return;
    }

    if (( version == null)||(version.length() == 0)) {
        toast_long(" version empty");
        return;
    }

    if ((srs == null)||(srs.length() == 0)) {
        toast_long("srs empty");
        return;
    }

    if (style == null) {
        toast_long("style null");
        return;
    }

    String[] baseurl_arr = { baseurl };


		// Add tiles layer
		MapTileProviderBasic provider = new MapTileProviderBasic(mContext);

    // Tile Source
    WMSTileSource tileSource = new WMSTileSource( layerName, baseurl_arr,  layerName, version, srs, style, size );

        provider.setTileSource( tileSource );

		final TilesOverlay tilesOverlay = new TilesOverlay(provider, mContext);
		tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(tilesOverlay);
        mMapView.invalidate();

} // setupTile

/**
 *  clearTile
 */
public void clearTile() {
		mMapView.getOverlays().clear();
        mMapView.invalidate();
} // clearTile


   /**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

 	/**
	 * write into logcat
	 */ 
	private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class WmsUtil
