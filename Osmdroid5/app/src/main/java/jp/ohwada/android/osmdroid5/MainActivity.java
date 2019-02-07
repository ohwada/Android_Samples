/**
 * Osmdroid Sample
 * OSM with MinimapItemizedoverlay and custom Icon
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid5;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import  org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import  org.osmdroid.api.IMapController;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MainActivity";


    // yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

    private static final double MAP_ZOOM = 10.0;

	private MapView mMapView;

	private ItemizedOverlay<OverlayItem> mMyLocationOverlay;


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

        setupMarker();

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
 *  setupMarker
 */
    private void setupMarker(){

    log_d( "setupMarker" );

	/* Itemized Overlay */

MarkerUtil  util = new MarkerUtil(this);
			final List<OverlayItem> items = util.getMarkers();

			/* OnTapListener for the Markers, shows a simple Toast. */

		{
			mMyLocationOverlay = new ItemizedIconOverlay<>(items,
					new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
						@Override
						public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        toast_long(item.getTitle());

							return true; // We 'handled' this event.
						}

						@Override
						public boolean onItemLongPress(final int index, final OverlayItem item) {
                        toast_long(item.getTitle());
							return false;
						}
					}, getApplicationContext());
			this.mMapView.getOverlays().add(this.mMyLocationOverlay);
		}

		/* MiniMap */

			final MinimapOverlay miniMapOverlay = new MinimapOverlay(this,
			mMapView.getTileRequestCompleteHandler());
			mMapView.getOverlays().add(miniMapOverlay);

} //  setupMarker


/**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // toast_long


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
