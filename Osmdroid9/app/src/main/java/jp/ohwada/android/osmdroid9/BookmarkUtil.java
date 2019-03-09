/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;

import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.util.StorageUtils;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Bookmark
 * original : https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/bookmarks/BookmarkSample.java
 */
public class BookmarkUtil 
implements LocationListener {

public static final String DIR_NAME = "osmdroid";

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "BookmarkUtil";

    // GPS
	private final static long GPS_MIN_TIME = 0l;
	private final static float GPS_MIN_DISTANCE = 0f;


    private Activity mActivity;
    private  Context mContext;
    private LocationManager mLocationManager;
    private MenuInflater mMenuInflater;

    private BookmarkHelper mHelper = null;

    private PickerUtil mPickerUtil;

    private MyLocationNewOverlay mMyLocationOverlay = null;
    private Location mCurrentLocation = null;

    private  MapView mMapView;

    // AlertDialog
    private  AlertDialog mAlertDialog = null;
    private EditText mEditTextLat;
    private EditText mEditTextLon;
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;


/**
 * constractor
 */
public BookmarkUtil(Activity activity) {
    mActivity = activity;
    mContext = activity;
    mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
mMenuInflater = activity.getMenuInflater();
    mHelper = new BookmarkHelper(activity);
    mPickerUtil = new PickerUtil(activity);
} // BookmarkUtil


/**
 * setup
 */
public void setup( MapView view ) {

        mMapView = view;
        mPickerUtil.setMapView( view );

        //add all our bookmarks to the view
        mMapView.getOverlayManager().addAll( getAllMarkers());

        mMyLocationOverlay = new MyLocationNewOverlay(mMapView);
        mMyLocationOverlay.setEnabled(true);


        mMapView.getOverlays().add(mMyLocationOverlay);

        // support long press to add a bookmark
        MapEventsOverlay events = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                showDialog(p);
                return true;
            }
        });
        mMapView.getOverlayManager().add(events);

} // setup


/**
 * showDialog 
 * enter title and description
 */
    private void showDialog(GeoPoint point) {

        if ( point == null ) return;

        if ( mAlertDialog != null ) {
            mAlertDialog.dismiss();
        }

        // prompt for user input
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View view = View.inflate(mContext, R.layout.bookmark_add_dialog, null);
        builder.setView(view);
        mEditTextLat = (EditText)view.findViewById(R.id.bookmark_lat);
        mEditTextLat.setText( toString( point.getLatitude() ) );
        mEditTextLon = (EditText)view.findViewById(R.id.bookmark_lon);
        mEditTextLon.setText( toString( point.getLongitude() ) );
        mEditTextTitle = (EditText)view.findViewById(R.id.bookmark_title);
        mEditTextDescription = (EditText)view.findViewById(R.id.bookmark_description);

        view.findViewById(R.id.bookmark_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        }); // button cancel

        view.findViewById(R.id.bookmark_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookmark( mEditTextLat, mEditTextLon, mEditTextTitle, mEditTextDescription );
                mAlertDialog.dismiss();
            }
        }); // button ok

        mAlertDialog = builder.show();
} // showDialog


/**
 * addBookmark
 */
private void addBookmark( EditText editLat, EditText editLon, EditText editTitle, EditText editDescription ) {

                double latD = parseDouble( editLat.getText().toString() );
                double lonD = parseDouble( editLon.getText().toString() );
        if (( latD == 0 )&&( lonD == 0 )) {
                    log_d("lat:0 lon:0");
            return ;
        }

                //basic validate input
                if (!mMapView.getTileSystem().isValidLatitude(latD) ) {
                    log_d("invalid lat");
                    mAlertDialog.dismiss();
                    return;
                }
                if (!mMapView.getTileSystem().isValidLongitude(lonD) ) {
                    log_d("invalid lat");
                    mAlertDialog.dismiss();
                    return;
                }

        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();

                    // add record
                    BookmarkRecord r = new BookmarkRecord( title, description, latD, lonD  );
                   long id = mHelper.insert(r);
                    if (id > 0) {
                        toast_long("add Successful");
                    } else {
                        toast_long("add Faild");
                    }

                    // show marker
                    Marker m = new Marker(mMapView);
                    m.setPosition(new GeoPoint(latD, lonD));
                    m.setId(UUID.randomUUID().toString());
                    m.setTitle( title );
                 m.setSnippet( description);
                    m.setSubDescription( m.getPosition().toDoubleString() );
                    mMapView.getOverlayManager().add(m);
                    mMapView.invalidate();

    } // addBookmark


/**
 * onResume
 */
public void onResume() {
        try {
            //this fails on AVD 19s, even with the appcompat check, says no provided named gps is available
            // LocationListener : myself
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME, GPS_MIN_DISTANCE, this);
        } catch (Exception ex) {
            if (D )ex.printStackTrace();
        }
} // onResume

/**
 * onPause
 */
    public void onPause() {
        try {
            mLocationManager.removeUpdates(this);
        } catch (Exception ex) {
            if (D )ex.printStackTrace();
        }
    } // onPause


/**
 * onDestroy
 */
public void onDestroy() {
        if (mHelper != null) {
           mHelper.close();
        }
       mHelper = null;
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mAlertDialog= null;
    if( mPickerUtil != null) {
        mPickerUtil.onDestroy();
    }
} // onDestroy


/**
 * onCreateOptionsMenu
 */
    public void onCreateOptionsMenu(Menu menu) {

        mMenuInflater.inflate(R.menu.menu_main, menu );

    } // onCreateOptionsMenu


/**
 * onOptionsItemSelected
 */
    public void onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
               procSettings();
        } else if (id == R.id.action_add) {
               addCurrentLocation();
        } else if (id == R.id.action_import) {
            mPickerUtil.showImportPicker(DIR_NAME);
        } else if (id == R.id.action_export) {
            mPickerUtil.showExportPicker();
        }
    } // onOptionsItemSelected


/**
 * procSettings
 */
private void procSettings() {
    // for debug
    Location location = new Location("dummy provider");
    // hakkeijima
    location.setLatitude(35.3362762);
    location.setLongitude(139.6410229);
    onLocationChanged(location);
} // procSettings


/**
 * addCurrentLocation
 */
private void addCurrentLocation() {
         //TODO
            if ( mCurrentLocation == null ) {
                String msg = "not get Current Location";
                toast_long(msg);
                log_d(msg);
            return;
        }
                GeoPoint point = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                showDialog(point);

} // addCurrentLocation


/**
 * LocationListener
 */
    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude() );

        // TODO :  setLocation(Location)はMyLocationNewOverlayでprotectedアクセスされます
        // mMyLocationOverlay.setLocation( point );

        // moveto GPS point
       IMapController mapController =  mMapView.getController();
        mapController.animateTo(point);
        mapController.zoomTo(16);
        log_d("moveto " +  point.toDoubleString());

    } // onLocationChanged

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        log_d("onStatusChanged: " +provider + " " + status );
    } // onStatusChanged

    @Override
    public void onProviderEnabled(String provider) {
        log_d("onProviderEnabled: " + provider );
    } // onProviderEnabled

    @Override
    public void onProviderDisabled(String provider) {
        log_d("onProviderDisabled: " + provider );
    } // onProviderDisabled


/**
 * getAllMarkers
 */
private List<Marker> getAllMarkers() {

        List<Marker> markers = new ArrayList<>();

  List<BookmarkRecord> records = mHelper.getAllList();

    // create markers
    for(BookmarkRecord r: records) {
                Marker m = new Marker( mMapView );
                m.setPosition( new GeoPoint(r.lat, r.lon ) );
                m.setId( r.getIdString() );
                m.setTitle( r.title );
                m.setSnippet( r.description );
                m.setSubDescription( r.toDoubleString() );
                markers.add(m);
    } // for
    return markers;

} // getAllMarkers


/**
 * parseDouble
 */
private double parseDouble( String str ) {

            double d = 0;
                try {
                   d = Double.parseDouble( str );
                } catch (Exception ex) {
			        if (D) ex.printStackTrace();
                }

    return d;
} // parseDouble

/**
 * toString
 */
private String toString( double num) {
    return Double.toString(num);
} // toString

/**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
	} // toast_long


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class BookmarkUtil
