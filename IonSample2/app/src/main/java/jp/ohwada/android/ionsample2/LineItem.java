/**
 * Gson sample
 * 2018-04-13 K.OHWADA 
 */

package jp.ohwada.android.ionsample2;



/**
 * A line item in a cart. This is not a rest resource, just a dependent object
  * original https://github.com/google/gson/tree/master/examples/android-proguard-example
 */
public class LineItem {
  public String name;
  public int quantity;
  public long priceInMicros;
  public String currencyCode;

  public LineItem(String name, int quantity, long priceInMicros, String currencyCode) {
    this.name = name;
    this.quantity = quantity;
    this.priceInMicros = priceInMicros;
    this.currencyCode = currencyCode;
  } // LineItem

 	/**
	 * getName
	 */
  public String getName() {
    return name;
  } // getName

 	/**
	 * getQuantity
	 */
  public int getQuantity() {
    return quantity;
  } // getQuantity

 	/**
	 * getPriceInMicros
	 */
  public long getPriceInMicros() {
    return priceInMicros;
  } // getPriceInMicros

 	/**
	 * getCurrencyCode
	 */
  public String getCurrencyCode() {
    return currencyCode;
  } // getCurrencyCode

 	/**
	 *  toString
	 */
  @Override
  public String toString() {
    return String.format("(item: %s, qty: %s, price: %.2f %s)",
        name, quantity, priceInMicros / 1000000d, currencyCode);
  } //  toString

} // class LineItem
