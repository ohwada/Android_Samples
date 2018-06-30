/**
 *  Kryo Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.kryosample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 *  class MainActivity
 * reference : https://github.com/keiji/serializer_benchmarks/tree/kryo
 */
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "kryo";
    	private final static String TAG_SUB = "MainActivity";

     	private TextView mTextViewResult;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

 mTextViewResult = (TextView) findViewById(R.id.TextView_result);

        Button btnTest = (Button) findViewById(R.id.Button_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test();
            }
        }); // btnTest

    } // onCreate

/**
 *   test
 */
private void test() {
    KryoTest test = new KryoTest();
    String result = null;
    try {
        test.prepare();
         result = test.test();
    } catch(Exception e) {
            e.printStackTrace();
    }
    mTextViewResult.setText(result);
    log_d(result);
}

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
