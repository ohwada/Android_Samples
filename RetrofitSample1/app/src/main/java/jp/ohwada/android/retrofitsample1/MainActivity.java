 	/**
	 * Retrofit sample
	 * 2018-03-01
	 */ 

package jp.ohwada.android.retrofitsample1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;


 	/**
	 * MainActivity
	 */ 
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "retrofit";
    	private final static String TAG_SUB = "MainActivity";

    // URL
    private final static  String URL_GET_BASE =  "https://raw.githubusercontent.com";
private final static  String URL_GET_PATH =  "/ohwada/Android_Samples/master/data/sample.txt";
  private final static  String URL_POST_BASE =  "http://ohwada.php.xdomain.jp";
  private final static  String URL_POST_PATH =  "/post_echo.php";

private TextView mTextView1;


 

 	/**
	 * onCreate
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
    log_d("procGet");
RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(URL_GET_BASE)
        .setConverter(new StringConverter())
        .build();
         
restAdapter.create( ISampleApi.class).getText( new Callback<String>() {
    @Override
    public void success(String result, Response response) {
        log_d("get success; " + result);
        mTextView1.setText(result);
    }
 
    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
    }
});

} // procGet


 	/**
	 * procPost
	 */ 
private void procPost() {
    log_d("procPostt");
RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(URL_POST_BASE)
        .setConverter(new StringConverter())
        .build();
         
restAdapter.create( ISampleApi.class).postParm( "bar", "1234", new Callback<String>() {
    @Override
    public void success(String result, Response response) {
        log_d("post success; " + result);
        mTextView1.setText(result);
    }
 
    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
    }
});

} // procPost



 	/**
	 * ISampleApi
	 */ 
public interface ISampleApi {
 
    @GET(URL_GET_PATH)
    void getText( Callback<String> callback );
 
@FormUrlEncoded
@POST(URL_POST_PATH)
    void  postParm(@Field("foo") String foo, @Field("hoge") String hoge, Callback<String> callback );

} //  ISampleApi


 	/**
	 * class StringConverter
	 * original: https://stackoverflow.com/questions/21881943/how-can-i-return-string-or-jsonobject-from-asynchronous-callback-using-retrofit
	 */ 
public static class StringConverter implements Converter {

 	/**
	 * == fromBody ==
	 */ 
    @Override
    public Object fromBody(TypedInput typedInput, Type type) throws ConversionException {
    log_d("fromBody");
        String text = null;
        try {
            text = fromStream(typedInput.in());
        } catch (IOException ignored) {/*NOP*/ }

        return text;
    } // fromBody


 	/**
	 * == toBody ==
	 */ 
    @Override
    public TypedOutput toBody(Object o) {
    log_d("toBody");
        return null;
    } // toBody

 	/**
	 * fromStream
	 */ 
    public static String fromStream(InputStream in) throws IOException {
        log_d("fromStream");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    } // fromStream

} // class StringConverte



 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class MainActivity
