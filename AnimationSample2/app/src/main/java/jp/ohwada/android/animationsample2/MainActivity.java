/**
 * Animation sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.animationsample2;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;


/**
 *  class MainActivity
 * https://developer.android.com/guide/topics/graphics/drawable-animation.html
 */
public class MainActivity extends Activity {

    private ImageView mImageView;
    private AnimationDrawable mAnimation;


/**
 *  == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   mImageView = (ImageView) findViewById(R.id.ImageView_1);
        
Button btnStart = (Button) findViewById(R.id.Button_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procStart();
        }
        }); // btnStart.setOnClickListener


        Button btnStop = (Button) findViewById(R.id.Button_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procStop();
        }
        }); //  btnStop.setOnClickListener

    } // onCreate


/**
 * procStart
* start animation, droid_rotation
 */
private void  procStart() {
 mImageView.setBackgroundResource(R.drawable.droid_rotation);
 mAnimation = (AnimationDrawable)  mImageView.getBackground();
mImageView.setImageBitmap(null);
 mAnimation.start();
} //  procStart


/**
 * procStop
* stop animation
 */
private void procStop() {
    if (mAnimation != null ) {
        mAnimation.stop();
    }
} // procStop

} // MainActivity

