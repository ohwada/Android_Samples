/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
 *  class MapActivity
 */
public class MapActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MapActivity";


    // Yokohama
    private static final double MAP_LAT = 35.4441625;
    private static final double MAP_LON = 139.6385441;

   private static final double MAP_ZOOM = 15.0;

    // search area
    private static final int DISTANCE_1K =1000;
    private static final int DISTANCE_5K =5000;
    private static final int DISTANCE_10K =10000;

	
private MapView mMapView;

    private NodeUtil mNodeUtil;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_map);

    mMapView = (MapView) findViewById(R.id.mapView);

     mMapView.setBuiltInZoomControls(true);
     mMapView.setMultiTouchControls(true);

       IMapController mapController =  mMapView.getController();
          mapController.setZoom(MAP_ZOOM);
          GeoPoint centerPoint = new GeoPoint(MAP_LAT,MAP_LON);
          mapController.setCenter(centerPoint);

        Button btnSearch1k = (Button) findViewById(R.id.Button_search_1k);
         btnSearch1k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMarker(DISTANCE_1K) ;
            }
        }); // Search1k

        Button btnSearch5k = (Button) findViewById(R.id.Button_search_5k);
         btnSearch5k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMarker(DISTANCE_5K) ;
            }
        }); // Search5k

        Button btnSearch10k = (Button) findViewById(R.id.Button_search_10k);
         btnSearch10k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMarker(DISTANCE_10K) ;
            }
        }); // Search10k

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
    private void showMarker(int distance){

    mNodeUtil = new NodeUtil(this);

IGeoPoint point = mMapView.getMapCenter();
double lat = point.getLatitude();
double lon = point.getLongitude();

			final List<OverlayItem> items = mNodeUtil.getMarkers( lat,  lon,  distance );

    int size = items.size();
    log_d(" showMarker size: " +  size );

    if (size == 0) {
        toast_long( "not found" );
        return;
    }

			/* OnTapListener for the Markers, shows AlertDialog. */

			ItemizedOverlay<OverlayItem> markerOverlay = new ItemizedIconOverlay<>(items,
					new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
						@Override
						public boolean onItemSingleTapUp(final int index, final OverlayItem item) { 
            showDialog( item.getTitle(), item.getSnippet() );
							return true; // We 'handled' this event.
						}

						@Override
						public boolean onItemLongPress(final int index, final OverlayItem item) {
            showDialog( item.getTitle(), item.getSnippet() );
							return false;
						}
					}, getApplicationContext());
	

		/* MiniMap */

			final MinimapOverlay miniMapOverlay = new MinimapOverlay(this,
			mMapView.getTileRequestCompleteHandler());

        // clear old markers, show new markers
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(markerOverlay);
		mMapView.getOverlays().add(miniMapOverlay);
        mMapView.invalidate();
        toast_long( size + " found" );

} //  showMarker


/**
 * showDialog
 */
	private void showDialog( String title, String message ) {
    
    TextView tvTitle = new TextView(this);
    tvTitle.setText(title);
    tvTitle.setTextSize(20);
    tvTitle.setGravity( Gravity.CENTER_HORIZONTAL );

new AlertDialog.Builder(this)
        .setCustomTitle( tvTitle )
        .setMessage( message )
        .setPositiveButton("OK", null)
        .show();

} // showDialog

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

} // class MapActivity
