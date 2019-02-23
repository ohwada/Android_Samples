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
 * class Element
 */
public class Element {

    	private final static String COMMA  = " , ";

    	private final static String LF  = "\n";

  public String type;
  public long  id;
  public double  lat;
  public double  lon;
  public Tags tags;

/**
 *  constractor
 */
  public Element() {

  }

/**
 * getIdString
 */
private String getIdString() {
    return Long.toString(id);
}

/**
 * getLat
 */
public double getLat() {
    return lat;
}

/**
 * getLon
 */
public double getLon() {
    return lon;
}

/**
 * getLatString
 */
private String getLatString() {
    return Double.toString(lat);
}

/**
 * getLonString
 */
private String getLonString() {
    return Double.toString(lon);
}

/**
 * getName
 */
public String getName() {
    String ret = null;
    if ( tags != null ) {
        ret = tags.getName();
    }
    return ret;
} // getName

/**
 * getInfo
 */
public String getInfo() {
    String ret = null;
    if ( tags != null ) {
        ret = tags.getInfo();
    }
    return ret;
} // getName


/**
 * toString
 */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
           builder.append("Element [ ");
           builder.append("type: ");
          builder.append( type);
         builder.append(COMMA);
           builder.append("id: ");
          builder.append( getIdString() );
         builder.append(COMMA);
           builder.append("lat: ");
          builder.append( getLatString() );
         builder.append(COMMA);
           builder.append("lon: ");
          builder.append( getLonString() );
         builder.append(COMMA);

    if (tags != null ) {
           builder.append("tags; ");
          builder.append( tags.toString() );
         builder.append(LF);
    }

          builder.append("] ");
          builder.append(LF);
    return builder.toString();
  }  // toString

} // class OsmJson
