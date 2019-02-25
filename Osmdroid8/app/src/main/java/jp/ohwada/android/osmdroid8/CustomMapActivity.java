/**
 * Osmdroid Sample
 * OSM with Marker, custom Icon
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid8;

import android.app.Activity;
import android.graphics.drawable.Drawable;
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
import  org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import static jp.ohwada.android.osmdroid8.R.id.mapView;


/**
 *  class CustomMapActivity
 */
public class CustomMapActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "CustomMainActivity";


    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

    private static final double MAP_ZOOM = 11.0;

	private MapView mMapView;

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_map);

    mMapView = (MapView) findViewById(mapView);

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

    Drawable icon = getResources().getDrawable(R.drawable.marker);

    MarkerUtil util = new MarkerUtil(this );
    List<MarkerUtil.Node> nodes =  util.getNodes();

    // create and show markers
    for(MarkerUtil.Node node: nodes ) {
        Marker marker = new Marker( mMapView );
        marker.setPosition( new GeoPoint(node.lat, node.lon ) );
        marker.setTitle ( node.title );
        marker.setSnippet( node.description );
        marker.setIcon(icon);
        mMapView.getOverlays().add(marker);
    } // for

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

} // class CustomMapActivity
