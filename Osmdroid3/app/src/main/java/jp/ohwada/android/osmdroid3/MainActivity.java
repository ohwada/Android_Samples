/**
 * Osmdroid Sample
 * OSM with TilesOverlay
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid3;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

 import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import  org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import  org.osmdroid.api.IMapController;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.overlay.TilesOverlay;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

    private static final double MAP_ZOOM = 10.0;

    private MapView mMapView = null;

	private TilesOverlay mTilesOverlay;

	private MapTileProviderBasic mProvider;



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
          GeoPoint centerPoint = new GeoPoint(MAP_LAT,MAP_LON);
          mapController.setCenter(centerPoint);

     setupTile();

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
 *  setupTile
 */
private void setupTile() {

		// Add tiles layer
		mProvider = new MapTileProviderBasic(getApplicationContext());

 // Tiles
 // http://openptmap.org/
mProvider.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT);

		this.mTilesOverlay = new TilesOverlay(mProvider, this.getBaseContext());
		this.mMapView.getOverlays().add(this.mTilesOverlay);

} // setupTile

} // class MainActivity
