package jp.ohwada.android.glidesample2;


import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;


public class MainActivity extends Activity {

// image sources
  private  final static  int NORMAL_GIF_RES_ID = R.drawable.droid;
  private  final static  int ANIME_GIF_RES_ID = R.raw.demo;

private ImageView mImageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
mImageView1 = (ImageView) findViewById(R.id.ImageView_1);

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
* display animation gif
 */
private void  procStart() {
       GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(mImageView1);
        Glide.with(this).load(ANIME_GIF_RES_ID).into(target);
} //  procStart

/**
 * procStop
* stop animation
 */
private void procStop() {
    mImageView1.setImageResource(NORMAL_GIF_RES_ID);
} // procStop




} // class MainActivity
