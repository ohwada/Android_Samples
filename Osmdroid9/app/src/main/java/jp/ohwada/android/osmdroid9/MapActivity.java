/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

 import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import  org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import  org.osmdroid.api.IMapController;

import org.osmdroid.tileprovider.tilesource.ITileSource;

import android.os.Environment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import java.io.File;
import java.util.Set;

/**
 *  class MapActivity
 */
public class MapActivity extends AppCompatActivity {

    public static final String DIR_NAME = "osmdroid";

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MapActivity";

    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

    private static final double MAP_ZOOM = 11.0;

    private MapView mMapView = null;

    private  BookmarkUtil mUtil;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // should be called before any instances of MapView are created 
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

            mUtil = new BookmarkUtil(this);
    mUtil.setup(mMapView);

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
        if (mUtil!=null) {
            mUtil.onResume();
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
        if (mUtil!=null) {
            mUtil.onPause();
        }
    } // onPause

         @Override
    public void onDestroy() {
         super.onDestroy();
        if (mUtil!=null) {
            mUtil.onDestroy();
        }
} // onDestroy


/**
 * == onCreateOptionsMenu == 
 */


     // @Override
    public boolean onCreateOptionsMenu(Menu menu ) {        if (mUtil!=null) {
            mUtil.onCreateOptionsMenu( menu );
        }
        return true;
} // onCreateOptionsMenu
        

/**
 * == onOptionsItemSelected == 
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mUtil!=null) {
            mUtil.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);

} // onOptionsItemSelected


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
