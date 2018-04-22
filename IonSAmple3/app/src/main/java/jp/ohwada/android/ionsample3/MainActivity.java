/**
 * Ion sample
 * 2018-03-01 K.OHWADA 
 */
package jp.ohwada.android.ionsample3;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

   private  final static  String URL = "https://raw.githubusercontent.com/ohwada/Android_Samples/master/images/palau02.jpg";

    private ImageView mImageView1;

/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView1 = (ImageView) findViewById(R.id.ImageView_1);

        Button btnInternet = (Button) findViewById(R.id.Button_internet);
        btnInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procInternet();
            }
        }); //  btnInternet.setOnClickListener

    } // onCreate


/**
 *  procInternet
 *   download image via the Internet、and display image
 */
private void procInternet() {
        Ion.with( mImageView1 ).load(URL);
} // procInternet


} // MainActivity
