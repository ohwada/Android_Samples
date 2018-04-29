/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather.model;

/**
 * class Forecast
 */
public class Forecast {

	public final static String LF = "\n";

    public String dateLabel;
    public String telop;
    public String date;
    public Temperature temperature;
    public Image image;

/**
 * getInfo
 */
    public String getInfo() {
    String str = date + LF;
    str += telop + LF;
    if ( temperature != null ) {
        str += temperature.getMinMaxCelsius() + LF;
    } 
    return str;
} // getInfo

/**
 * getImageUrl
 */
    public String getImageUrl() {
    if (image == null) return null;
    return image.url;
} // getImageUrl

/**
 * toString
 */
    @Override
    public String toString() {
        return "Forecast {" +
                "telop= " + telop + 
                ", date= " + date + 
                ", temperature= " + temperature + 
                ", image= " + image + 
                "} ";
    } // toString

} // class Forecast
