/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.google.gson.Gson;

import jp.ohwada.android.giphy.util.*;

/**
 * class FullscreenActivity
 * 
 * An {@link android.app.Activity} for displaying full size original GIFs.
 * original : https://github.com/bumptech/glide/tree/master/samples/giphy
 */
public class FullscreenActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Giphy";
    	private final static String TAG_SUB = "FullscreenActivity";

  private static final String EXTRA_RESULT_JSON = "result_json";

  private GifDrawable gifDrawable;

    /*
     * getIntent
     */
    // TODO: pass GifResult as JSON, the url  of image should be okay
  public static Intent getIntent(Context context, GifResult result) {
    String msg = "getIntent: " + result.toString();
    log_d(msg);
    Intent intent = new Intent(context, FullscreenActivity.class);
    intent.putExtra(EXTRA_RESULT_JSON, new Gson().toJson(result));
    return intent;
  } // getIntent

    /*
     * == onCreate ==
     */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    log_d("onCreate");
    setContentView(R.layout.fullscreen_activity);

    String resultJson = getIntent().getStringExtra(EXTRA_RESULT_JSON);
    final GifResult result = new Gson().fromJson(resultJson, GifResult.class);

    String msg = "result: " + result.toString();
    log_d(msg);

    ImageView gifView = (ImageView) findViewById(R.id.fullscreen_gif);

    gifView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        procClick(result);
      }
    }); // gifView.setOnClickListener

    RequestBuilder<Drawable> thumbnailRequest = GlideApp.with(this)
        .load(result)
        .decode(Bitmap.class);

    GlideApp.with(this)
        .load(result.images.original.url)
        .thumbnail(thumbnailRequest)
        .listener(new RequestListener<Drawable>() {

          @Override
          public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
              boolean isFirstResource) {

            log_d("onLoadFailed");
            return false;
          } // onLoadFailed

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
              DataSource dataSource, boolean isFirstResource) {

            log_d("onResourceReady");
            if (resource instanceof GifDrawable) {
              gifDrawable = (GifDrawable) resource;
            } else {
              gifDrawable = null;
            }
            return false;
          } // onResourceReady

        }) //listener
        .into(gifView);

  } // onCreate

/**
	 * procClick
	 */
      private void procClick(GifResult result) {
            log_d("procClick");
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("giphy_url", result.images.original.url);
        clipboard.setPrimaryClip(clip);

        if (gifDrawable != null) {
          if (gifDrawable.isRunning()) {
            gifDrawable.stop();
            log_d("gifDrawable.stop");
           toast_short("stop");
          } else {
            gifDrawable.start();
            log_d("gifDrawable.start");
           toast_short("start");
          } // if
        } // if

      } // procClick


/**
	 * toast_short
	 */
	protected void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class FullscreenActivity
