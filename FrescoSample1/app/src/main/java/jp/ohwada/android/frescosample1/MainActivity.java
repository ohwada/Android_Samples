/**
 * Fresco sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.frescosample1;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    // image sources
    private  final static  int RESOURCE_ID = R.drawable.droid;

    private  final static  String ASSET_PATH ="asset:///demo.png";

    private String URL = "https://raw.githubusercontent.com/ohwada/Android_Samples/master/images/palau02.jpg";
    

private SimpleDraweeView mDraweeView1;
private SimpleDraweeView mDraweeView2;
private SimpleDraweeView mDraweeView3;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);

        setContentView(R.layout.activity_main);

mDraweeView1 = (SimpleDraweeView) findViewById(R.id.SimpleDraweeView_1);
mDraweeView2 = (SimpleDraweeView) findViewById(R.id.SimpleDraweeView_2);
mDraweeView3 = (SimpleDraweeView) findViewById(R.id.SimpleDraweeView_3);

        Button btnInternet = (Button) findViewById(R.id.Button_internet);
        btnInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procInternet();
            }
        }); // btnInternet

    procResource();
    procAsset();
    } // onCreate


/**
 *  procResource
 */
private void procResource() {
Uri uri = new Uri.Builder()
    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
    .path(String.valueOf(RESOURCE_ID))
    .build();
 mDraweeView1.setImageURI(uri);
} // procResource


/**
 *  procAsset
 */
private void procAsset() {
Uri uri = Uri.parse(ASSET_PATH);
mDraweeView2.setImageURI(uri);
} // procAsset


/**
 *  procInterne
 */
private void procInternet() {
Uri uri = Uri.parse(URL);
mDraweeView3.setImageURI(uri);
} // procInternet

} // MainActivity

