/**
 * Gson sample
 * 2018-04-13 K.OHWADA 
 */

package jp.ohwada.android.gsonsampl1;



/**
 * A line item in a cart. This is not a rest resource, just a dependent object
  * original https://github.com/google/gson/tree/master/examples/android-proguard-example
 */
public class LineItem {
  private final String name;
  private final int quantity;
  private final long priceInMicros;
  private final String currencyCode;

  public LineItem(String name, int quantity, long priceInMicros, String currencyCode) {
    this.name = name;
    this.quantity = quantity;
    this.priceInMicros = priceInMicros;
    this.currencyCode = currencyCode;
  }

 	/**
	 * getName
	 */
  public String getName() {
    return name;
  }

 	/**
	 * getQuantity
	 */
  public int getQuantity() {
    return quantity;
  }

 	/**
	 * getPriceInMicros
	 */
  public long getPriceInMicros() {
    return priceInMicros;
  }

 	/**
	 * getCurrencyCode
	 */
  public String getCurrencyCode() {
    return currencyCode;
  }

 	/**
	 *  toString
	 */
  @Override
  public String toString() {
    return String.format("(item: %s, qty: %s, price: %.2f %s)",
        name, quantity, priceInMicros / 1000000d, currencyCode);
  } //  toString

} // class LineItem
