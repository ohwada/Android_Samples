/**
 * Gson sample
 * 2018-04-13 K.OHWADA 
 */

package jp.ohwada.android.gsonsampl1;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;


/**
 * Activity class illustrating how to use proguard with Gson
 * original https://github.com/google/gson/tree/master/examples/android-proguard-example
 */
public class MainActivity extends Activity {

   	// debug
	private final static String TAG = "GSON";
    	private final static String TAG_SUB = "MainActivity";
    private final static boolean DEBUG = true; 

    private TextView mTextView1;

 	/**
	 * onCreate
	 */
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView1 = (TextView) findViewById(R.id.TextView_1);

        Button btnTo = (Button) findViewById(R.id.Button_to);
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procToJson();
            }
        }); //  setOnClickListener

        Button btnFrom = (Button) findViewById(R.id.Button_from);
        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procFromJson();
            }
        }); //  setOnClickListener

  } // onCreate


 	/**
	 * procToJson
	 */
private void procToJson() {
    Gson gson = new Gson();
    Cart cart = buildCart();
    String json = gson.toJson(cart);
log_d(json);
    StringBuilder sb = new StringBuilder();
    sb.append("Gson.toJson() example: \n");
    sb.append("  Cart Object: ").append(cart).append("\n");
    sb.append("  Cart JSON: ").append( json).append("\n");
    String text = sb.toString();
log_d(text);
mTextView1.setText(text);
    mTextView1.invalidate();
  } // procToJson


 	/**
	 * procFromJson
	 */
private void procFromJson() {
    Gson gson = new Gson();
    StringBuilder sb = new StringBuilder();
    sb.append("\n\nGson.fromJson() example: \n");
    String json = "{buyer:'Happy Camper',creditCard:'4111-1111-1111-1111',"
      + "lineItems:[{name:'nails',priceInMicros:100000,quantity:100,currencyCode:'USD'}]}";
        Cart cart = gson.fromJson(json, Cart.class);
    String str = cart.toString();
        log_d(str);
    sb.append("Cart JSON: ").append(json).append("\n");
    sb.append("Cart Object: ").append(str).append("\n");
    String text = sb.toString();
log_d(text);
mTextView1.setText(text);
    mTextView1.invalidate();
  } // procFromJson

 	/**
	 * procClick
	 */
  private Cart buildCart() {
    List<LineItem> lineItems = new ArrayList<LineItem>();
    lineItems.add(new LineItem("hammer", 1, 12000000, "USD"));
    return new Cart(lineItems, "Happy Buyer", "4111-1111-1111-1111");
  } // buildCart


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (DEBUG) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class Mainctivity
