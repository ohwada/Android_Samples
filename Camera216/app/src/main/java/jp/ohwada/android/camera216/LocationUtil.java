/**
 * Camera2 Sample
 * LocationUtil
 * 2019-02-01 K.OHWADA
 */


package jp.ohwada.android.camera216;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * class LocationUtil
 */
public class LocationUtil {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "class LocationUtil";


 /**
 * Flag whether to use the sample location
 * for debug
 */
    private final static boolean USE_SAMPLE = false;


 /**
 * Constant for GPS min time, min distance
 */
    private final static long GPS_MIN_TIME = 60000;
    private final static float GPS_MIN_DISTANCE = 3;


 /**
 * LocationManager
 */
    private LocationManager mLocationManager;


 /**
 * current Location
 */
    private Location mLocation;


/**
 * constractor
 */ 
public LocationUtil(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
} 


/**
 * requestLocationUpdates
 * register for location updates
 */ 
public void requestLocationUpdates() {
        try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME, GPS_MIN_DISTANCE, mLocationListener);
        } catch (SecurityException e) {
                e.printStackTrace();
        }
} // requestLocationUpdates


/**
 * removeUpdates
 * removes all location updates
 */ 
public void removeUpdates() {
        try {
                mLocationManager.removeUpdates(mLocationListener);
        } catch (SecurityException e) {
                e.printStackTrace();
        }
} // removeUpdates


/**
 * getLocation
 */ 
public Location getLocation() {
        if(USE_SAMPLE ) {
                return createSampleLocation();
        }
        return mLocation;
}


/**
 * createSampleLocation
 * for debug
 */ 
private Location createSampleLocation() {
    Location location = new Location(LocationManager.GPS_PROVIDER);

    // Yokohama, Japan
    double LATITUDE = 35.4515374;
    double LONGITUDE = 139.6472035;
    double ALTITUDE = 10.0;

	location.setLatitude(LATITUDE);
    location.setLongitude(LONGITUDE);
	location.setAltitude(ALTITUDE);
    return location;
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * LocationListener
 */
private LocationListener mLocationListener = new LocationListener() {

/**
 * onLocationChanged
 */ 
    @Override
    public void onLocationChanged(Location location) {
        log_d("onLocationChanged: " + location.toString());
        mLocation = location;
    }

/**
 * onStatusChanged
 */ 
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        log_d("onStatusChanged");
    }

/**
 * onProviderEnabled
 */ 
    @Override
    public void onProviderEnabled(String s) {
        log_d("onProviderEnabled");
    }

/**
 * onProviderDisabled
 */ 
    @Override
    public void onProviderDisabled(String s) {
        log_d("onProviderDisabled");
    }

}; // LocationListener


} // class LocationUtil
