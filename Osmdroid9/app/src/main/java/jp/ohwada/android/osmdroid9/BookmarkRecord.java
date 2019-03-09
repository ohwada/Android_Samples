/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;

/**
 * class BookmarkRecord
 */
public class BookmarkRecord {

    	private final static String LF  = "\n";

    	private final static String SPACE  = " ";
    	private final static String COMMA  = ", ";

	/** items */
	public int id = 0;
	public String title = "";
	public String description = "";
	public double lat = 0;
	public double lon = 0;

    /**
     * === constractor === 
     */	
	public BookmarkRecord() {
		// nop
	} // BookmarkRecord

    /**
     * === constractor ===  
     * for insert  
     */	
	public BookmarkRecord( String _title, String _description, double _lat, double _lon ) {
    id = 0;
    title = _title;
    description =  _description;
    lat  = _lat;
    lon = _lon;

	} // BookmarkRecord

    /**
     * === constractor ===  
     * for list 
     */	
	public BookmarkRecord( int _id, String _title,   String _description, double _lat, double _lon ) {
    id = _id;
    title = _title;
    description =  _description;
    lat  = _lat;
    lon = _lon;
	} // BookmarkRecord


public String getIdString() {
    return Long.toString(id);
}

public String getLatString() {
    return Double.toString(lat);
}

public String getLonString() {
    return Double.toString(lon);
}

/**
 * getMessage
 */
  public String getMessage() {
   String str = getIdString() + " : " + title + SPACE +  description + SPACE + getLatString()+ SPACE + getLonString();
    return str;
} // getMessage


    /**
     * getWriteRow
     */	
public String[] getWriteRow() {
 String[]  row = new String[] { getLatString(), getLonString(), title, description };
    return row;
} // getWriteRow

/**
 * toString
 */
	public String toDoubleString() {

    StringBuilder builder = new StringBuilder();
    builder.append( getLatString() );
    builder.append(COMMA);
	builder.append( getLonString() );

		return builder.toString();
	} // toDoubleString

/**
 * toString
 */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
           builder.append("[ ");
          builder.append( title );
         builder.append(COMMA);
          builder.append( description );
         builder.append(COMMA);
          builder.append( getLatString() );
           builder.append(COMMA);
          builder.append( getLonString() );
         builder.append("]");

    return builder.toString();
  }  // toString
	
} // class BookmarkRecord
