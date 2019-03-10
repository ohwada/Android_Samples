/**
 * Osmdroid Sample
 * 地理院タイル
 * 2019-02-01 K.OHWADA 
 */


package jp.ohwada.android.osmdroid10;


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
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 *  class MainActivity
 *  参考 : 地理院タイル一覧
 *  https://maps.gsi.go.jp/development/ichiran.html
 */
public class MainActivity extends Activity {

    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

 private static final double MAP_ZOOM = 8.0;

    // 地理院 標準地図
    private static final String TILE_SERVER_1 = "https://cyberjapandata.gsi.go.jp/xyz/std/";

    // 地理院 空中写真・衛星画像
    private static final String TILE_SERVER_2  = "https://cyberjapandata.gsi.go.jp/xyz/ort/";

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

        Button btn1 = (Button) findViewById(R.id.Button_1);
		btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 overlayTile(1);
            }
        }); // btn1

        Button btn2 = (Button) findViewById(R.id.Button_2);
		btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 overlayTile(2);
            }
        }); // btn2

        Button btn3 = (Button) findViewById(R.id.Button_3);
		btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearOverlay();
            }
        }); // btn3

    mMapView = (MapView) findViewById(R.id.mapView);

     mMapView.setBuiltInZoomControls(true);
     mMapView.setMultiTouchControls(true);

       IMapController mapController =  mMapView.getController();
         mapController.setZoom(MAP_ZOOM);
          GeoPoint centerPoint = new GeoPoint(MAP_LAT, MAP_LON);
          mapController.setCenter(centerPoint);

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
 *  overlayTile
 */
private void overlayTile(int id) {

	    mMapView.setTilesScaledToDpi(true);
		mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);


		// Add tiles layer with Custom Tile Source
		final MapTileProviderBasic tileProvider = new MapTileProviderBasic(getApplicationContext());

    ITileSource tileSource =  null;
    if (id ==1) {
        tileSource = new XYTileSource( "GSI",  12, 18, 256, ".png",
				new String[] { TILE_SERVER_1 });
    } else if (id ==2) {
        tileSource = new XYTileSource( "GSI",  14, 18, 256, ".jpg",
				new String[] { TILE_SERVER_2 });
    }

		tileProvider.setTileSource(tileSource);
		final TilesOverlay tilesOverlay = new TilesOverlay(tileProvider, this.getBaseContext());
		tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(tilesOverlay);
        mMapView.invalidate();

} //  overlayTile

/**
 * clearOverlay
 */
private void clearOverlay() {
		mMapView.getOverlays().clear();
        mMapView.invalidate();
} // clearOverlay

} // class MainActivity
