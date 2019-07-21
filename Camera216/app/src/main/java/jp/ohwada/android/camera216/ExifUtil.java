/**
 * Camera2 Sample
 * ExifUtil
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera216;


import android.content.Context;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * class ExifUtil
 */ 
public class ExifUtil {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "ExifUtil";


/**
 * Constant for EXIF date time
 */ 
    private static final String EXIF_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";


/**
 * Constant for GPS altitude
 * 0 : above sea level.
 */ 
    private static final String ALTITUDE_REF = "0";


/**
 * Char for debug log
 */
    private final static String COMMA = " , ";


/**
 * addExif
 */ 
public static void addExif(Date date, Location location, File file) {

    String dateTime =  getDateTime(date);
    log_d("DateTime: " + date.toString() + COMMA + dateTime);

    ExifInterface exif = getExifInterface(file);
            exif.setAttribute(ExifInterface.TAG_MODEL, Build.MODEL);
            exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
            exif = setGpsAttributes(exif, location);
    saveAttributes(exif);
} // addExif


/**
 * getExifInterface
 */ 
public static ExifInterface getExifInterface(File file) {
    String path = file.toString();
    ExifInterface exif = null;
      try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exif;
}


/**
 * saveAttributes
 */ 
public static void saveAttributes(ExifInterface exif) {
        try {
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
}


/**
 * setGpsAttributes
 */ 
public static ExifInterface setGpsAttributes(ExifInterface exif, Location location) {
// 
    if (location == null) {
            //return the original exif, if no location
            return exif;
    }

    double lat =  location.getLatitude() ;
    double lon =  location.getLongitude() ;
	double alt =  location.getAltitude();

    String str_lat = GPS.convert( lat );
    String str_lon =  GPS.convert( lon );
    String ref_lat = GPS.latitudeRef( lat );
    String ref_lon = GPS.longitudeRef( lon );
    String str_alt =  GPS.convAltitude(alt);

    log_d("Latitude: " + lat + COMMA + ref_lat +COMMA + str_lat);
    log_d("Longitude: " + lon + COMMA + ref_lon + COMMA + str_lon);
    log_d("Altitude: " + alt + COMMA + str_alt);

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, str_lat);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, str_lon);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, ref_lat);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, ref_lon);
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, str_alt);
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, ALTITUDE_REF);
    return exif;
}


/**
 * getDateTime
 */ 
public static String getDateTime(Date date) {
   SimpleDateFormat sdf = new SimpleDateFormat(EXIF_DATE_FORMAT, Locale.US);
    String dateTime =  sdf.format(date);
    return dateTime;
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class ExifUtil

