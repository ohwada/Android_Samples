/**
 *  OKHttp sample
 *  2018-03-01 K.OHWADA
 */

package jp.ohwada.android.okhttpsample1;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Callback;
import okhttp3.Call;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OkHttp";
    	private final static String TAG_SUB = "MainActivity";

    // url
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

    } // onCreate


 	/**
	 * procGet
	 */ 
private void procGet() {
    Request request = new Request.Builder()
      .url(URL_GET)
      .build();
        procCall(request);
} // procGet



 	/**
	 * procPost
	 */ 
private void procPost() {
    RequestBody formBody = new FormBody.Builder()
        .add("foo", "bar")
        .add("hoge", "1234")
        .build();
    Request request = new Request.Builder()
      .url(URL_POST)
        .post(formBody)
      .build();
        procCall(request);
} // procPost

 	/**
	 * procCall
	 */ 
private void procCall(Request request) {

    OkHttpClient client = new OkHttpClient();

try {
    client.newCall(request).enqueue(new Callback() {

        // success
        @Override
        public void onResponse(Call call, Response response)throws IOException { 
            procResponse(response);
        } // onResponse

    // failure
    @Override
    public void onFailure(Call call, IOException e) {
e.printStackTrace();
} // onFailure

}); // Callback

} catch(Exception e){
    e.printStackTrace();
}

} // procCall


 	/**
	 * procResponse
	 */ 
private void procResponse(Response response) { 
    String  result = "";
    try {
        result = response.body().string();
        log_d( "procResponse: " + result);
    } catch(Exception e) {
        e.printStackTrace();
    }

    // run in different thread
    final String  text = result;
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            mTextView1.setText(text);
        }
    }); // runOnUiThread

} // procResponse


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
