/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1.model;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * class Osm3s
 */
public class Osm3s {

    	private final static String LF  = "\n";

  public String timestamp_osm_base;
  public String  copyright;

/**
 *  constractor
 */
  public Osm3s() {

  }





/**
 * toString
 */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
           builder.append("Osm3s [ ");
           builder.append("timestamp_osm_base: ");
          builder.append(timestamp_osm_base);
         builder.append(LF);
           builder.append("copyright: ");
          builder.append(copyright);
         builder.append(LF);

          builder.append("] ");
          builder.append(LF);
    return builder.toString();

  }  // toString

} // class OsmJson
