/**
 * Osmdroid Sample
 * OSM with Tiles Overlay and Custom Tile Source
 * 2019-02-01 K.OHWADA 
 */


package jp.ohwada.android.osmdroid4;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

 import org.osmdroid.config.Configuration;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import org.osmdroid.views.MapController;
import  org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import  org.osmdroid.api.IMapController;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 *  class MainActivity
 *  reference : https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samples/SampleWithTilesOverlayAndCustomTileSource.java
 */
public class MainActivity extends Activity {

    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

 private static final double MAP_ZOOM = 10.0;

// https://openstreetmap.jp/
 private static final String TILE_SERVER = "http://tile.openstreetmap.jp/";

    private MapView mMapView = null;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_main);

    mMapView = (MapView) findViewById(R.id.mapView);

     mMapView.setBuiltInZoomControls(true);
     mMapView.setMultiTouchControls(true);

       IMapController mapController =  mMapView.getController();
         mapController.setZoom(MAP_ZOOM);
          GeoPoint centerPoint = new GeoPoint(MAP_LAT, MAP_LON);
          mapController.setCenter(centerPoint);

         // setupCustomTile();

    } //  onCreate



/**
 *  onResume
 */
    @Override
    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mMapView!=null) {
            mMapView.onResume();
        }
    } // onResume


/**
 *  onPause
 */
    @Override
    public void onPause(){
        super.onPause();
        Configuration.getInstance().save(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mMapView!=null) {
            mMapView.onPause();
        }
    } // onPause


/**
 *  setupCustomTile
 */
private void setupCustomTile() {

	    mMapView.setTilesScaledToDpi(true);
		mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);


		// Add tiles layer with Custom Tile Source
		final MapTileProviderBasic tileProvider = new MapTileProviderBasic(getApplicationContext());

		final ITileSource tileSource = new XYTileSource("FietsRegionaal",  3, 18, 256, ".png",
				new String[] { TILE_SERVER });

		tileProvider.setTileSource(tileSource);

		final TilesOverlay tilesOverlay = new TilesOverlay(tileProvider, this.getBaseContext());
		tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		mMapView.getOverlays().add(tilesOverlay);

    } //  setupCustomTile


} // class MainActivity
