/**
 * Animation sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.animationsample3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    private ImageView mImageView;


/**
 *  == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.ImageView_1);
        Button btnAnimate = (Button) findViewById(R.id.Button_animate);
    btnAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procAnimate();
            }
        });

    } // onCreate


/**
 * procAnimate
 * start anim.disappear
 */
private void procAnimate() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.disappear);
        animation.setFillAfter(true);   // keep state after end
        animation.setFillEnabled(true);
        mImageView.startAnimation(animation);
} // procAnimate


} // class MainActivity
