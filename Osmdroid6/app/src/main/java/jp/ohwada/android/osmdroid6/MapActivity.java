/**
 * Osmdroid Sample
 * Offline Only Tiles 
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid6;

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
import android.widget.Toast;

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
 *  reference : https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/tileproviders/SampleOfflineOnly.java
 */
public class MapActivity extends Activity {

    public static final String DIR_NAME = "osmdroid";

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MapActivity";

    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

    private static final double MAP_ZOOM = 6.0;

    private MapView mMapView = null;


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

    setupOfflineMap() ;

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
 *  setupOfflineMap
 */
private void setupOfflineMap() {

        //not even needed since we are using the offline tile provider only
         mMapView.setUseDataConnection(false);

        mMapView.getTileProvider().setTileLoadFailureImage(getResources().getDrawable(R.drawable.notfound));

File  file = getOfflineTileFile();
if ( file == null ) return;

        OfflineTileProvider  provider = createOfflineTileProvider(file);
    if ( provider == null ) return;

    ITileSource source = getTilesource(provider);
    if (source == null ) return;

    mMapView.setTileProvider( provider );
    mMapView.setTileSource( source );
    mMapView.invalidate();

} // setupOfflineMap



/**
 * getOfflineTileFile
 */
 private File  getOfflineTileFile() {

    File tileFile = null;

    String msg;

        //first we'll look at the default location for tiles that we support
        File main_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
File sub_dir = new File(main_dir, DIR_NAME);
 String path = sub_dir.getAbsolutePath();
        if ( !sub_dir.exists()) {
    msg = path + " dir not found";
    toast_long( msg );
    log_d( msg );
    return null;
        }
            File[] list = sub_dir.listFiles();
            if (list == null) {
        msg = path + " have no files";
     toast_long( msg );
    log_d( msg );
    return null;
    }
                // select first one
                for (int i = 0; i < list.length; i++) {

                  String name = list[i].getName().toLowerCase();
log_d( i + " : "+ name );

                    if (list[i].isDirectory()) {
                        continue;
                    }
                    if (!name.contains(".")) {
                        continue; //skip files without an extension
                    } 
                    name = name.substring(name.lastIndexOf(".") + 1);
                    if (name.length() == 0) {
                        continue;
                    }
                    if ( !ArchiveFileFactory.isFileExtensionRegistered(name)) {
            // not match extension : "zip" "sqlite" "mbtiles" "gemf"
                        continue;
            }
                            //ok found a file we support and have a driver for the format, for this demo, we'll just use the first one

        tileFile = list[i];
        break;

        } // for

if ( tileFile == null ) {
    msg = path + " not find  Tile files in " + path;
     toast_long( msg );
    log_d( msg );
}

    return tileFile;
} // getOfflineTileFile


/**
 * createOfflineTileProvider
 */
private OfflineTileProvider createOfflineTileProvider(File file) {

                            //create the offline tile provider, it will only do offline file archives
                        OfflineTileProvider tileProvider = null;
    try {
        tileProvider = new OfflineTileProvider(new SimpleRegisterReceiver( this),
                                    new File[]{file});
                        } catch (Exception ex) {
                            if (D) ex.printStackTrace();
                        } 
    return tileProvider;
} // createOfflineTileProvider


/**
 * getTilesource
 */
private  ITileSource getTilesource(OfflineTileProvider tileProvider) {

    ITileSource  tileSource = null;
                            IArchiveFile[] archives = tileProvider.getArchives();
                            if (archives.length == 0 ) {
        log_d( "archives.length == 0" );
        return null;
    }

                      //cheating a bit here, get the first archive file and ask for the tile sources names it contains
                                Set<String> sourceNames = archives[0].getTileSources();
                                //presumably, this would be a great place to tell your users which tiles sources are available

                                if (!sourceNames.isEmpty()) {
                                    //ok good, we found at least one tile source, create a basic file based tile source using that name
                                    //and set it. If we don't set it, osmdroid will attempt to use the default source, which is "MAPNIK",
                                    //which probably won't match your offline tile source, unless it's MAPNIK
                                    String source = sourceNames.iterator().next();
                 tileSource = FileBasedTileSource.getSource(source);

              String msg = " source : " + source;
  toast_long( msg);
log_d( msg );

} else {
    // sourceNamess isEmpty
    // set Mapnik to tile source
 tileSource = TileSourceFactory.DEFAULT_TILE_SOURCE;
log_d( "DEFAULT_TILE_SOURCE" );
 }  // if (!sourceNamess.isEmpty()) {

return  tileSource;
} // getTileSource


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
