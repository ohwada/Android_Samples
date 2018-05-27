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
     * class GifViewHolder
     * original : https://github.com/bumptech/glide/tree/master/samples/giphy
     */ 
public class GifViewHolder extends RecyclerView.ViewHolder {

    public final ImageView gifView;

    /*
     * constractor
     */
    public GifViewHolder(View itemView) {
      super(itemView);
      gifView = (ImageView) itemView.findViewById(R.id.gif_view);
    } // GifViewHolder

  } // class GifViewHolder


