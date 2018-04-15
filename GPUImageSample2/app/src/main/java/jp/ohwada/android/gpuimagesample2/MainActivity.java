package jp.ohwada.android.gpuimagesample2;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    private  final static int RESOURCE_ID = R.drawable.demo;

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
        Button  btnRetouch = (Button) findViewById(R.id.Button_retouch);
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
 *  display original  image 
 */
private void procOrig() {
    mImageView1.setImageResource(RESOURCE_ID);
} // procOrig


/**
 *  procRetouch
 *  display retouched image
 */
private void procRetouch() {
        Bitmap source = BitmapFactory.decodeResource(getResources(), RESOURCE_ID);
    Bitmap retouched = procBlurEffect(source);
    mImageView2.setImageBitmap(retouched);
} // procRetouch


/**
 *  procBlurEffect
 *  blur like frosted glass
 */
private Bitmap  procBlurEffect(Bitmap source) {
BlurEffecter effecter = new BlurEffecter(this);
Bitmap retouched = effecter.retouchFrostedGlass(source);
        return retouched;
} // procBlurEffect


} // class MainActivity
