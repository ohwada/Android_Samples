/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather.model;

/**
 * class Temperature
 */
public class Temperature {
    public  TemperatureValue min;
    public  TemperatureValue max;

/**
 * getMinMaxCelsius
 */
    public String getMinMaxCelsius() {
    String str = "Temperature: ( ";
    if ( min != null ) {
        str += " min: " + min.celsius + ",";
    }
    if ( max != null ) {
        str += " max: " + max.celsius;
    }
    str += " ) ";
    return str;
} // getMinMaxCelsius()

/**
 * toString
 */
    @Override
    public String toString() {
        return "Temperature { " +
                "min= " + min + 
                ", max= " +max + 
                " } ";
    } // toString

} // class Temperature
