/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1;


/**
 * class NodeRecord
 */
public class NodeRecord {

    	private final static String LF  = "\n";

    	private final static String SPACE  = " ";

	/** items */
	public int id = 0;
	public String name = "";
	public double lat = 0;
	public double lon = 0;
	public String info = "";

    /**
     * === constractor === 
     */	
	public NodeRecord() {
		// nop
	} // NodeRecord

    /**
     * === constractor ===  
     * for insert  
     */	
	public NodeRecord( String _name, double _lat, double _lon,  String _info ) {
    id = 0;
    name = _name;
    lat  = _lat;
    lon = _lon;
    info =  _info;
	} // DbRecord

    /**
     * === constractor ===  
     * for list 
     */	
	public NodeRecord( int _id, String _name, double _lat, double _lon,  String _info ) {
    id = _id;
    name = _name;
    lat  = _lat;
    lon = _lon;
    info =  _info;
	} // DbRecord

public String getName() {
    return name;
}

private String getIdString() {
    return Long.toString(id);
}

private String getLatString() {
    return Double.toString(lat);
}

private String getLonString() {
    return Double.toString(lon);
}

/**
 * getMessage
 */
  public String getMessage() {
   String str = getIdString() + " : " + getName() + SPACE + getLatString()+ SPACE + getLonString();
    return str;
} // getMessage


/**
 * toString
 */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
           builder.append("node [ ");
          builder.append( getName() );
         builder.append(", ");
          builder.append( getLatString() );
           builder.append(",  ");
          builder.append( getLonString() );
         builder.append("]");
         builder.append(LF);

    return builder.toString();
  }  // toString
	
} // class NodeRecord
