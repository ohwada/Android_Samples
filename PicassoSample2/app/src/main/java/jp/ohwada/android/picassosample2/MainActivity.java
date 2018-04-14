/**
 * Picasso sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.picassosample2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    private  final static int RESOURCE_ID = R.drawable.demo;

    // rounded
    private  final static int RADIUS = 100;
    private  final static int MARGIN = 50;

    private ImageView mImageView1;
    private ImageView mImageView2;

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   mImageView1 = (ImageView) findViewById(R.id.ImageView_1);
   mImageView2 = (ImageView) findViewById(R.id.ImageView_2);
        Button btnRetouch = (Button) findViewById(R.id.Button_retouch);
        btnRetouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procRetouch();
            }
        }); // btnRetouch.setOnClickListener

    procOrig();
    } // onCreate

/**
 *  procOrig
 *  display original image
 */
private void procOrig() {
Picasso.with(this).load(RESOURCE_ID).into(mImageView1);
} // procResource

/**
 *  procRetouch
 *  display transformed image、rounded corners
 */
private void procRetouch() {
RoundedTransformation transform = new RoundedTransformation(RADIUS, MARGIN);
Picasso.with(this).load(RESOURCE_ID).transform(transform).into(mImageView2);
} // procRetouch

} // class MainActivity
