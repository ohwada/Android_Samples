/**
 *  Ion sample
 *  2018-03-01 K.OHWADA
 */

package jp.ohwada.android.ionsample1;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    private final static  String URL_GET =  "https://raw.githubusercontent.com/ohwada/Android_Samples/master/data/sample.txt";

    private final static  String URL_POST =  "http://ohwada.php.xdomain.jp/post_echo.php";

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

        Button btnPost = (Button) findViewById(R.id.Button_post);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procPost();
            }
        }); // btnPost

    }  // onCreate


 	/**
	 * procGet
	 */ 
private void procGet() {
Ion.with(this)
.load(URL_GET)
.asString()
.setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        procCompleted(e, result);
                    }
                });

} // procGet




 	/**
	 * procPost
	 */ 
private void procPost() {
Ion.with(this)
.load(URL_POST)
.setBodyParameter("foo", "bar")
.setBodyParameter("hoge", "1234")
.asString()
.setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        procCompleted(e, result);
                    }
                });

} // procPost


 	/**
	 * procCompleted
	 */ 
private void procCompleted(Exception e, String result) {
    // failure
    if (e != null) {
            e.printStackTrace();
            return;
    }
    // success
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
