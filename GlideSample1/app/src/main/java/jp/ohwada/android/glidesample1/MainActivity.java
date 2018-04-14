/**
 * Glide sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.glidesample1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    // image sources
    private  final static  int RESOURCE_ID = R.drawable.droid;

    private  final static  String ASSET_PATH = "file:///android_asset/demo.png";

    private  final static  String URL = "https://raw.githubusercontent.com/ohwada/Android_Samples/master/images/palau02.jpg";

private ImageView mImageView1;
private ImageView mImageView2;
private ImageView mImageView3;

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

 mImageView1 = (ImageView) findViewById(R.id.ImageView_1);
   mImageView2 = (ImageView) findViewById(R.id.ImageView_2);
   mImageView3 = (ImageView) findViewById(R.id.ImageView_3);
        Button btnInternet = (Button) findViewById(R.id.Button_internet);
        btnInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procInternet();
            }
        });

    procResource();
    procAsset();
    } // onCreate


/**
 *  procResource
 *  display image in resource folder
 */
private void procResource() {
  Glide.with(this).load(RESOURCE_ID).into(mImageView1);
} // procResource


/**
 *  procAsset
 *  display image in asssets folder
 */
private void procAsset() {
 Glide.with(this).load(ASSET_PATH).into(mImageView2);
} // procAsset


/**
 *  procInternet
 *   download image via the Internet、and display image
 */
private void procInternet() {
 Glide.with(this).load(URL).into(mImageView3);
} // procInternet

    } // MainActivity
