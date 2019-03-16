/**
 * Osmdroid Sample
 * OSM with WMS Tile
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroidwms1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
 *  class WmsUtil
 *  reference : https://github.com/osmdroid/osmdroid/wiki/WMS-Support
 *  国交省 ＷＭＳ機能
 *  http://nlftp.mlit.go.jp/webmapc/help.html#N3
 */
public class WmsUtil {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "WmsUtil";


    // WMSTileSource
    private static final String NAME = "MLIT WMS";

    private static final String[] BASE_URL = { "http://nrb-www.mlit.go.jp/webmapc/gis/webmap/wms?SERVICE=WMS&" };
                                                              
    private static final String VERSION = "1.1.1";
    private static final String STYLE = "";
    private static final int TILE_SIZE = 256;

    // 世界測地系1984
    private static final String SRS = "EPSG:4326"  ; 

    // 平成２９年度行政区域 市区町村界
    private static final String LAYER_BORDER = "N03-180101_200";

    private Context mContext;

    private MapView mMapView;

/**
 *  constractor
 */
public WmsUtil(Context context) {
    mContext = context;
} // WmsUtil

/**
 *  setMapView
 */
public void setMapView(MapView view) {
		mMapView = view;
	    mMapView.setTilesScaledToDpi(true);
} // setMapView

/**
 *  setupTile
 */
public void setupTile() {

		// Add tiles layer
		MapTileProviderBasic provider = new MapTileProviderBasic(mContext);

    // Tile Source
    WMSTileSource tileSource = new WMSTileSource(NAME, BASE_URL,  LAYER_BORDER, VERSION, SRS, STYLE, TILE_SIZE );

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
	 * write into logcat
	 */ 
	private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class WmsUtil
