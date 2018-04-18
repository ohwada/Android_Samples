/**
 *  Volley sample
 *  2018-03-01 K.OHWADA
 */
package jp.ohwada.android.volleysample1;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import static com.android.volley.toolbox.Volley.newRequestQueue;


/**
     * class MainActivity
     */ 
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "volley";
    	private final static String TAG_SUB = "MainActivity";

    // url
    private final static  String URL_GET =  "https://raw.githubusercontent.com/ohwada/Android_Samples/master/data/sample.txt";

    private final static  String URL_POST =  "http://ohwada.php.xdomain.jp/echo.php";

private TextView mTextView1;

// sngleton in app
    private RequestQueue mQueue;


    /**
     * == onCreate ==
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

    // setup volley    
    mQueue = newRequestQueue(this);
     mQueue.start();

    } // onCreate


    /**
     * == onDestroy ==
     */ 
    @Override
    public void onDestroy() {
        log_d( "onDestroy" );
        super.onDestroy();
        mQueue.stop();
    }


 	/**
	 * procGet
	 */ 
private void procGet() {
        log_d( "procGet" );
        StringRequest request =
            new StringRequest( 
                Request.Method.GET, 
                URL_GET, 
                new Response.Listener<String>() {
                    @Override
                    public void onResponse( String response ) {
                        log_d("get onResponse: " + response);
                        mTextView1.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override 
                    public void onErrorResponse( VolleyError e ) {
                        e.printStackTrace();
                    }
                }
            );

        mQueue.add( request );
} // procGet


 	/**
	 * procPost
	 */ 
private void procPost() {
        log_d( "procPost" );
    StringRequest request = new StringRequest(
        Request.Method.POST,
        URL_POST,
        new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                        log_d("post onResponse: " + response);
                        mTextView1.setText(response);
                }
            },
            new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError e){
                    e.printStackTrace();
                }
            }){

        @Override
        protected Map<String,String> getParams(){
                        log_d("post getParams");
            Map<String,String> params = new HashMap<String,String>();
            params.put("foo","bar");
            params.put("hoge","1234");
            return params;
        } // getParams
    };

        mQueue.add( request );
} // procPost


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
