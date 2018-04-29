/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather.model;

/**
 * class AreaLocation
 */
public class AreaLocation {   
    public String area;
    public String pref;
 public String city;

/**
 * toString
 */
    @Override
    public String toString() {
        return "AreaLocation{" +
                ", area= " + area+
                ", pref= " + pref +
                ", city= " + city +
                "}";
    } // toString

} // class AreaLocation
