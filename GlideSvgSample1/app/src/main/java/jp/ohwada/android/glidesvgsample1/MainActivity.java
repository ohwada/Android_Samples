/**
 * Glide SVG sample
 * 2018-05-01 K.OHWADA 
 */

package jp.ohwada.android.glidesvgsample1;

import android.content.ContentResolver;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.File;
import java.io.InputStream;

/**
 *  class MainActivity
 * reference : https://github.com/bumptech/glide/tree/3.0/samples/svg/src/main/java/com/bumptech/glide/samples/svg
 */
public class MainActivity extends AppCompatActivity {

// debug
	private  final static boolean D = true; 
	private String TAG = "SVG";
    	private static final String TAG_SUB = "MainActivity";

    // image sources
    private  final static  int RES_ID = R.raw.android_toy_h;

    private  final static  String URL = "http://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg";

private TextView  mTextViewHello;
private TextView mTextViewDescription;

private ImageView mImageViewRes;
private ImageView  mImageViewNet;

    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> mRequestBuilder;


/**
 *  == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

  mTextViewHello = (TextView) findViewById(R.id.TextView_hello);
 mTextViewDescription = (TextView) findViewById(R.id.TextView_description);

 mImageViewRes = (ImageView) findViewById(R.id.ImageView_res);
    mImageViewNet = (ImageView) findViewById(R.id.ImageView_net);

        Button btnRes = (Button) findViewById(R.id.Button_res);
        btnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRes();
            }
        }); // btnRes


        Button btnNet = (Button) findViewById(R.id.Button_net);
        btnNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNet();
            }
        }); // btnNet

        Button btnClear = (Button) findViewById(R.id.Button_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCache();
            }
        }); // btnClear

        Button btnCycle = (Button) findViewById(R.id.Button_cycle);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cycleScaleType();
            }
        }); // btnCycle


        mRequestBuilder = Glide.with(this)
                .using(Glide.buildStreamModelLoader(Uri.class, this), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_error)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());

    } // onCreate


/**
 *  == onStart ==
 */
    @Override
    protected void onStart() {
        super.onStart();
        // reload();
    } // onStart


/**
 *  clearCache
 */
private void clearCache() {
        log_d("clearing cache");
        Glide.clear(mImageViewRes);
        Glide.clear( mImageViewNet);
        Glide.get(this).clearMemory();
        File cacheDir = Glide.getPhotoCacheDir(this);
        if (cacheDir.isDirectory()) {
            for (File child : cacheDir.listFiles()) {
                if (!child.delete()) {
                    log_d( "cannot delete: " + child);
                }
            }
        }
        reload();
    } // clearCache


/**
 *  cycleScaleType
 * reference : https://developer.android.com/reference/android/widget/ImageView.ScaleType
 */
private void cycleScaleType() {
        ImageView.ScaleType curr = mImageViewRes.getScaleType();
       log_d("cycle: current=" + curr);
        ImageView.ScaleType[] all = ImageView.ScaleType.values();
        int nextOrdinal = (curr.ordinal() + 1) % all.length;
        ImageView.ScaleType next = all[nextOrdinal];
        log_d("cycle: next=" + next);
        toast_short("ScaleType: " + next);
        mImageViewRes.setScaleType(next);
         mImageViewNet.setScaleType(next);
        reload();
    } // cycleScaleType


/**
 *  reload
 */
    private void reload() {
        log_d("reloading");
        showDescription();
        loadRes();
        loadNet();
        toast_short("reloading");
    } // reload


/**
 *  showDescription
 */
private void showDescription() {
    mTextViewDescription.setText(getString(R.string.scaleType, mImageViewRes.getScaleType()));
} // showDescription

/**
 *  hideHello
 */
private void hideHello() {
mTextViewHello.setVisibility(View.GONE);
} // hideHello

/**
 *  loadRes
 */
    private void loadRes() {
        log_d("loadRes");
        hideHello();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/"
                + RES_ID);
        mRequestBuilder
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        // SVG cannot be serialized so it's not worth to cache it
                        // and the getResources() should be fast enough when acquiring the InputStream
                .load(uri)
                .into(mImageViewRes);
            showDescription();
    } // loadRes

/**
 *  loadNet
 */
    private void loadNet() {
        log_d("loadNet");
        hideHello();
        Uri uri = Uri.parse(URL);
        mRequestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        // SVG cannot be serialized so it's not worth to cache it
                .load(uri)
                .into( mImageViewNet);
            showDescription();
    } // loadNet


/**
	 * toast_short
	 */
	protected void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


/**
 * write into logcat
 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


    } // MainActivity
