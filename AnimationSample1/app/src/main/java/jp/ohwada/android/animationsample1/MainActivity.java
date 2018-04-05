/**
 * Animation sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.animationsample1;

import android.app.Activity;
import android.os.Bundle;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;


import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


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
        }); // button.setOnClickListener

} // onCreate


/**
 * procAnimate
* move down and become transparent gradually 
 */
private void procAnimate() {

    ViewPropertyAnimatorCompat animator = ViewCompat.animate(mImageView);
    animator.translationY(200) // move downward
        .alpha(0) // To become transparent 
        .setDuration(10000) // 10 sec
         .setInterpolator(new FastOutSlowInInterpolator())
        .setListener(new ViewPropertyAnimatorListenerAdapter(){
            @Override
            public void  onAnimationEnd(View view) {
                // hide view when animation ends
                view.setVisibility(View.INVISIBLE);
            }
            }) // setListener
        .start();

} // procAnimate

} // MainActivity