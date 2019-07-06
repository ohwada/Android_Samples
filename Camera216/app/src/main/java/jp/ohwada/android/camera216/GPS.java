/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera216;


/*
 * class GPS
 * reference : https://stackoverflow.com/questions/5280479/how-to-save-gps-coordinates-in-exif-data-on-android
 */
public class GPS {


/**
 * latitudeRef
  * returns ref for latitude which is S or N.
  * @param latitude
  * @return S or N
  */
public static String latitudeRef(double latitude) {
        String str = (latitude<0.0d)?"S":"N";
        return str;
}


/**
 * longitudeRef
 * returns ref for latitude which is S or N.
  * @param latitude
  * @return S or N
  */
public static String longitudeRef(double longitude) {
        String str = (longitude<0.0d)?"W":"E";
        return str;
}


/**
  * convert 
  * convert latitude into DMS (degree minute second) format. For instance
  * -79.948862 becomes
  *  79/1,56/1,55903/1000
  * It works for latitude and longitude
  * @param latitude could be longitude.
  * @return String
  */
public static String convert(double latitude) {
        latitude = Math.abs(latitude);
        int degree = (int) latitude;
        latitude *= 60;
        latitude -= (degree * 60.0d);
        int minute = (int) latitude;
        latitude *= 60;
        latitude -= (minute * 60.0d);
        int second = (int) (latitude*1000.0d);

        StringBuilder sb = new StringBuilder(20);
        sb.setLength(0);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000");
        return sb.toString();
}

/**
 * convAltitude
  */
public static String convAltitude(double altitude) {

            StringBuilder sb = new StringBuilder(20);
            int int_altitude = (int) altitude;
            sb.setLength(0);
            sb.append(int_altitude);
            sb.append("/1,");
            return sb.toString();
}


} // class GPS