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
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecyclerListener;
import android.support.v7.widget.RecyclerView.ViewHolder;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import java.util.Collections;
import java.util.List;

import jp.ohwada.android.giphy.util.*;

/**
 * class MainActivity
 * 
 * The primary activity in the Giphy sample that allows users to view trending animated GIFs from
 * Giphy's api.
 * original : https://github.com/bumptech/glide/tree/master/samples/giphy
 */
public class MainActivity extends AppCompatActivity implements Api.Monitor {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Giphy";
    	private final static String TAG_SUB = "MainActivity";

  private GifAdapter mAdapter;

    /*
     * == onCreate ==
     */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ImageView giphyLogoView = (ImageView) findViewById(R.id.giphy_logo_view);

    GlideApp.with(this)
        .load(R.raw.large_giphy_logo)
        .into(giphyLogoView);

    RecyclerView gifList = (RecyclerView)findViewById(R.id.gif_list);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    gifList.setLayoutManager(layoutManager);

        Button btnGiphy = (Button) findViewById(R.id.Button_giphy);
        btnGiphy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGiphyTrending();
            }
        });  // btnGiphy.setOnClickListener


    RequestBuilder<Drawable> gifItemRequest = GlideApp.with(this)
        .asDrawable();

    ViewPreloadSizeProvider<GifResult> preloadSizeProvider =
        new ViewPreloadSizeProvider<>();

    mAdapter = new GifAdapter(this, gifItemRequest, preloadSizeProvider);
        mAdapter.setOnItemClickListener( new GifAdapter.OnItemClickListener() {
        @Override
        public void onItemClick( GifResult result ) {
    log_d( "onItemClick");
    procItemClick( result );
}
}); // setOnItemClickListener

    gifList.setAdapter(mAdapter);
    RecyclerViewPreloader<GifResult> preloader =
        new RecyclerViewPreloader<>(GlideApp.with(this), mAdapter, preloadSizeProvider, 4);
    gifList.addOnScrollListener(preloader);
    gifList.setRecyclerListener(new RecyclerListener() {

      @Override
      public void onViewRecycled(ViewHolder holder) {
        log_d( "onViewRecycled");
        // This is an optimization to reduce the memory usage of RecyclerView's recycled view pool
        // and good practice when using Glide with RecyclerView.
        GifViewHolder gifViewHolder = (GifViewHolder) holder;
        GlideApp.with(MainActivity.this).clear(gifViewHolder.gifView);
      }

    }); // setRecyclerListener

  } //onCreate

    /*
     * == onStart ==
     */
  @Override
  protected void onStart() {
    super.onStart();
  } // onStart

    /*
     * == onStop ==
     */
  @Override
  protected void onStop() {
    super.onStop();
    Api.get().removeMonitor(this);
  } // onStop


    /*
     * == onSearchComplete ==
     */
  @Override
  public void onSearchComplete(SearchResult result) {
        log_d("onSearchComplete");
    mAdapter.setResults(result.data);
  } // onSearchComplete


    /*
     * getGiphyTrending
     */
  private void getGiphyTrending() {
    Api.get().addMonitor(this);
    if (mAdapter.getItemCount() == 0) {
      Api.get().getTrending();
    }
  } // getGiphyTrending


    /*
     * procItemClick
     */
    private void procItemClick( GifResult result ) {
        log_d("procItemClick");
          Intent fullscreenIntent = FullscreenActivity.getIntent(this, result);
          startActivity(fullscreenIntent);
} // procItemClick


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

 
} // class MainActivity
