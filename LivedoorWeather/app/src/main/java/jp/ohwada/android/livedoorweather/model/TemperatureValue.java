/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather.model;

/**
 * class TemperatureValue
 */
public class TemperatureValue {
    public String celsius;
    public String fahrenheit;

/**
 * toString
 */
    @Override
    public String toString() {
        return "TemperatureValue { " +
                "celsius= " + celsius + 
                ", fahrenheit= " +fahrenheit + 
                " } ";
    } // toString
} // class TemperatureValue
