/**
 * CSV Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.csv2;

import com.univocity.parsers.annotations.*;

import lombok.Data;

/**
 * class Shopping
 */
@Data
public class Shopping {

            private final static String COMMA = ",";
            private final static String SPACE = " ";

	/** items */
	@Parsed(field = "Name")
	public String name = "";

	@Parsed(field = "Price")
	public double price = 0;

	@Parsed(field = "Quantity")
	public int quantity = 0;


    /**
     * getPriceString
     */	
public String getPriceString() {
    return Double.toString( price );
} // getPriceString

    /**
     * getQuantityString
     */	
public String getQuantityString() {
    return Integer.toString( quantity );
} // getQuantityString

    /**
     * getMessage
     */	
public String getMessage() {
    StringBuilder builder = new StringBuilder();
          builder.append( name );
         builder.append(SPACE);
          builder.append( getPriceString() );
         builder.append(SPACE);
          builder.append( getQuantityString() );
    return builder.toString();
} // getMessage


    /**
     * getWriteRow
     */	
public String[] getWriteRow() {
 String[]  row = new String[] {name, getPriceString(), getQuantityString() };
    return row;
} // getWriteRow


 	/**
	 * toString
	 */ 
public String toString() {
    StringBuilder builder = new StringBuilder();
           builder.append(" [ ");
          builder.append( name );
         builder.append(SPACE);
          // builder.append( getPriceString() );
         builder.append(SPACE);
          // builder.append( getQuantityString() );
          builder.append(" ] ");
    return builder.toString();
}  // toString

} // class Shopping
