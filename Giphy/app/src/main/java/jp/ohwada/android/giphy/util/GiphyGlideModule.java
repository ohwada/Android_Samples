/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import java.io.InputStream;



/** 
 * lass GiphyGlideModule
* Configures Glide for the Giphy sample app.
* create GlideApp class
 * original : https://github.com/bumptech/glide/tree/master/samples/giphy
 */
@GlideModule
public class GiphyGlideModule extends AppGlideModule {

/**
 *  == registerComponents ==
 */
  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide,
      @NonNull Registry registry) {
    registry.append(GifResult.class, InputStream.class, new GiphyFactory());
  } // registerComponents

/**
 *  == isManifestParsingEnabled ==
 */
  // Disable manifest parsing to avoid adding similar modules twice.
  @Override
  public boolean isManifestParsingEnabled() {
    return false;
  } // isManifestParsingEnabled

} // class GiphyGlideModule
