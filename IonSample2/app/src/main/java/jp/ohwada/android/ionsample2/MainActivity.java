/**
 *  Ion sample
 *  2018-03-01 K.OHWADA
 */

package jp.ohwada.android.ionsample2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Ion";
    	private final static String TAG_SUB = "MainActivity";


       private final static  String URL =  "https://raw.githubusercontent.com/ohwada/Android_Samples/master/data/sample.json.txt";


private TextView mTextView1;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 mTextView1 = (TextView) findViewById(R.id.TextView_1);

        Button btnGet = (Button) findViewById(R.id.Button_get);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procGet();
            }
        }); // btnGet

    } // onCreate


 	/**
	 * procGet
	 */ 
private void procGet() {
try {
    Ion.with(this)
    .load(URL)
    .as(new TypeToken<Cart>() {
        })
    .setCallback(new FutureCallback<Cart>() {
                    @Override
                    public void onCompleted(Exception e, Cart cart) {
                        procCompleted(e, cart);
                    }
                });
    } catch(Exception e) {
            e.printStackTrace();
    }
} // procGet

 	/**
	 * procCompleted
	 */ 
private void procCompleted(Exception e, Cart cart) {
    // failure
    if (e != null) {
            e.printStackTrace();
            return;
    }
    // success
    String result = cart.toString();
    log_d( "result: " + result);
    mTextView1.setText(result);

    } // procCompleted

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class MainActivity
