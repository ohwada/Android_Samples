/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import java.util.Collections;
import java.util.List;

import jp.ohwada.android.giphy.R;



    /*
     * class GifAdapter
     * 
     * run in Glide 4.4.0
    * original : https://github.com/bumptech/glide/tree/master/samples/giphy
     */
public class GifAdapter extends RecyclerView.Adapter<GifViewHolder>
      implements ListPreloader.PreloadModelProvider<GifResult> {

    	        // debug
	private final static boolean D = true;
    	private final static String TAG = "Giphy";
    	private final static String TAG_SUB = "GifAdapter";

        private static final int LAYOUT_ITEM_ID = R.layout.gif_list_item;

    private static final GifResult[] EMPTY_RESULTS = new GifResult[0];

    private final RequestBuilder<Drawable> requestBuilder;

    private final ViewPreloadSizeProvider<GifResult> preloadSizeProvider;

    private final LayoutInflater layoutInflater;

    private final ClipboardManager clipboardManager;

    private GifResult[] results = EMPTY_RESULTS;

 // callback 
    private OnItemClickListener mListener;  

            /*
     * callback interface
     */    
    public interface OnItemClickListener {
        public void onItemClick( GifResult result );
    } // interface

    /*
     * == constractor ==
     */
    public GifAdapter(Activity activity, RequestBuilder<Drawable> requestBuilder,
        ViewPreloadSizeProvider<GifResult> preloadSizeProvider) {

      this.requestBuilder = requestBuilder;
      this.preloadSizeProvider = preloadSizeProvider;

      this.layoutInflater = activity.getLayoutInflater();

      this.clipboardManager  =
              (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);

    } // GifAdapter

    /*
     * callback
     */ 
    public void setOnItemClickListener( OnItemClickListener listener ) {
        log_d("setOnItemClickListener");
        mListener = listener;
    } // setOnItemClickListener

    /*
     * setResults
     */
    public void setResults(GifResult[] results) {

      if (results != null) {
        String msg = "setResults; length =" + results.length;
        log_d(msg);
        this.results = results;
      } else {
        log_d("setResults; null");
        this.results = EMPTY_RESULTS;
      }
      notifyDataSetChanged();
    } // setResults

    /*
     * ==  onCreateViewHolder ==
     */ 
    @Override
    public GifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view =  layoutInflater.inflate(LAYOUT_ITEM_ID, parent, false);
      return new GifViewHolder(view);
    } // onCreateViewHolder

    /*
     * ==  onBindViewHolder ==
     */ 
    @Override
    public void onBindViewHolder(GifViewHolder holder, int position) {


      final GifResult result = results[position];

        String msg = "onBindViewHolder; " + position + " , " + result.toString();
        log_d(msg);

      holder.gifView.setOnClickListener(new View.OnClickListener() {

    /*
     * onClick
     * start FullscreenActivity, when click
     */ 
        @Override
        public void onClick(View view) {
            procClick(result);
            notifyItemClick( result );
        } // onClick

      }); // setOnClickListener

      // clearOnDetach let's us stop animating GifDrawables that RecyclerView hasn't yet recycled
      // but that are currently off screen.

// TODO : delete clearOnDetach, becouse there is no method in Glide 4.4.0
//requestBuilder.load(result).into(holder.gifView).clearOnDetach();
      requestBuilder.load(result).into(holder.gifView);

      preloadSizeProvider.setView(holder.gifView);

    } // onBindViewHolder


    /*
     * procClick
     */
        private void procClick(GifResult result) {

// TODO : set it as clipboard, but I do not know how to use it
          ClipData clip =
              ClipData.newPlainText("giphy_url", result.images.fixed_height.url);
            Preconditions.checkNotNull(clipboardManager).setPrimaryClip(clip);
} // procClick


    /*
     * ==  getItemIdt ==
     */ 
    @Override
    public long getItemId(int i) {
      return 0;
    } //  getItemId

    /*
     * == getItemCount ==
     */ 
    @Override
    public int getItemCount() {
      return results.length;
    } // getItemCount

    /*
     * == getPreloadItems ==
     */ 
    @NonNull
    @Override
    public List<GifResult> getPreloadItems(int position) {
      return Collections.singletonList(results[position]);
    } // getPreloadItems

    /*
     * == getPreloadRequestBuilder ==
     */ 
    @Nullable
    @Override
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull GifResult item) {
      return requestBuilder.load(item);
    } // getPreloadRequestBuilder

    /**
     * notifyItemClick
     */
    private void notifyItemClick( GifResult result ) {
                    log_d("nnotifyItemClick");
           if ( mListener != null ) {
            mListener.onItemClick( result );
        } 
}	// notifyItemClick

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class GifAdapter
