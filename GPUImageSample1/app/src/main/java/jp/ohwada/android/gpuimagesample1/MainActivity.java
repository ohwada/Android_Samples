package jp.ohwada.android.gpuimagesample1;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;

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
 *  display retouched image EdgeDetection
 */
private void procRetouch() {
        Bitmap source = BitmapFactory.decodeResource(getResources(), RESOURCE_ID);
    Bitmap retouched = procEdgeDetection(source);
    // Bitmap retouched = procToon(source);
    // Bitmap retouched = procSepia(source);
    mImageView2.setImageBitmap(retouched);
} // procRetouch


/**
 *  procEdgeDetection
 */
private Bitmap procEdgeDetection(Bitmap source) {
        GPUImage mGPUImage = new GPUImage(this);
        mGPUImage.setFilter(new GPUImageSobelEdgeDetection());
        mGPUImage.setImage(source);
        Bitmap retouched = mGPUImage.getBitmapWithFilterApplied();
        return retouched;
} // procEdgeDetection

/**
 *  procToon
 */
private Bitmap procToon(Bitmap source) {
GPUImage gpuImage = new GPUImage(this);
gpuImage.setImage(source);
gpuImage.setFilter(new GPUImageToonFilter());
 Bitmap retouched = gpuImage.getBitmapWithFilterApplied();
        return retouched;
} // procToon

/**
 *  procSepia
 */
private Bitmap procSepia(Bitmap source) {
GPUImage gpuImage = new GPUImage(this);
gpuImage.setImage(source);
gpuImage.setFilter(new GPUImageSepiaFilter());
 Bitmap retouched = gpuImage.getBitmapWithFilterApplied();
        return retouched;
} // procSepia


} // class MainActivity
