/**
 * Osmdroid Sample
 * OSM  with loading KML
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmbonuspack1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import  org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import  org.osmdroid.api.IMapController;

import org.osmdroid.util.BoundingBox;

import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.views.overlay.FolderOverlay;


/**
 *  class MainActivity
 *  refetence : https://stackoverflow.com/questions/43158879/how-to-import-kml-with-osmdroid
 */
public class MainActivity extends Activity {

    // Yokohama
    private static final double MAP_LAT = 35.4472391;
    private static final double MAP_LON = 139.6414945;

    private static final double MAP_ZOOM = 6.0;

     private static final int KML_RAW_ID = R.raw.paris_tour;

    private MapView mMapView = null;

    private IMapController mapController;

    private Context mContext;


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

// unnecessary to set latitude and longitude of center,zoom 
// set automatically from kml file
       IMapController mapController =  mMapView.getController();
         mapController.setZoom(MAP_ZOOM);
          GeoPoint centerPoint = new GeoPoint(MAP_LAT,MAP_LON);
          mapController.setCenter(centerPoint);

        loadKml();

    } //  onCreate


/**
 *  onResume
 */
    @Override
    public void onResume(){
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        super.onResume();
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
 *  loadKml
 */
private void loadKml() {
    mContext = this;
        new KmlLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    } // loadKml


/**
 *  class KmlLoader
 */
    class KmlLoader extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        KmlDocument kmlDocument;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading Project...");
            progressDialog.show();
        } // onPreExecute

    @Override
        protected Void doInBackground(Void... voids) {
            kmlDocument = new KmlDocument();
            kmlDocument.parseKMLStream(getResources().openRawResource(KML_RAW_ID), null);

    FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(mMapView, null, null, kmlDocument);

        mMapView.getOverlays().add(kmlOverlay);

            return null;
        } // doInBackground

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            mMapView.invalidate();
            BoundingBox bb = kmlDocument.mKmlRoot.getBoundingBox();

            mMapView.zoomToBoundingBox(bb, true);
            mMapView.getController().setCenter(bb.getCenter());

            super.onPostExecute(aVoid);
        } // onPostExecute

    } // class KmlLoader

} // class MainActivity
