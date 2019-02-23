/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1.model;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * class OsmJson
 */
public class OsmJson {

    	private final static String LF  = "\n";

  public String version;
  public String  generator;
  public Osm3s  osm3s;
  public List<Element> elements;


/**
 *  constractor
 */
  public OsmJson() {

  }

/**
 * getElements
 */
public List<Element> getElements() {
    List<Element> ret = new ArrayList<Element>();
if ( elements != null ) {
    ret = elements;
}
    return ret;
} // getElements


/**
 * toString
 */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
           builder.append("Osmjson [ ");
           builder.append("version: ");
          builder.append( version);
         builder.append(LF);
           builder.append("generator: ");
          builder.append( generator);
          builder.append(LF);

    if (osm3s != null) {
           builder.append("osm3s: ");
          builder.append( osm3s.toString() );
          builder.append(LF);
    }

    if (elements != null ) {
        for(Element element: elements) {
           builder.append("element: ");
          builder.append( element.toString() );
          builder.append(LF);
        } // for
    } // if

          builder.append("] ");
          builder.append(LF);
    return builder.toString();
  }  // toString

} // class OsmJson
