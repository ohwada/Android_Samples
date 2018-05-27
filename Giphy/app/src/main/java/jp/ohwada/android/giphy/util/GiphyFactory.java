/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;



  /**
 * class GiphyFactory
 *   
 * The default factory for {@link com.bumptech.glide.samples.giphy.GiphyModelLoader}s.
 * original : https://github.com/bumptech/glide/tree/master/samples/giphy
   */
  public class GiphyFactory implements ModelLoaderFactory<GifResult, InputStream> {

  /**
   * == build ==
   */
    @NonNull
    @Override
    public ModelLoader<GifResult, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new GiphyModelLoader(multiFactory.build(GlideUrl.class, InputStream.class));
    } // build

  /**
   * == teardown ==
   */
    @Override
    public void teardown() {
      // Do nothing.
    } // teardown

  } // class GiphyFactory
