/**
 * Osmdroid Sample
 * OSM with WMS Tile
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroidwms1;

import android.app.Activity;
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


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MainActivity";

    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

 private static final double MAP_ZOOM = 10.0;

    private MapView mMapView = null;

    private WmsUtil mWmsUtil;

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

         setContentView(R.layout.activity_main);

        Button btn1 = (Button) findViewById(R.id.Button_1);
		btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWmsUtil.setupTile();
            }
        }); // btn1

        Button btn2 = (Button) findViewById(R.id.Button_2);
		btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mWmsUtil.clearTile();
            }
        }); // btn2


    mMapView = (MapView) findViewById(R.id.mapView);

     mMapView.setBuiltInZoomControls(true);
     mMapView.setMultiTouchControls(true);

       IMapController mapController =  mMapView.getController();
         mapController.setZoom(MAP_ZOOM);
          GeoPoint centerPoint = new GeoPoint(MAP_LAT,MAP_LON);
          mapController.setCenter(centerPoint);

        mWmsUtil = new WmsUtil(this);
        mWmsUtil.setMapView(mMapView );

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
        if (mMapView != null) {
            mMapView.onPause();
        }
} // onPause


 	/**
	 * write into logcat
	 */ 
	private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
