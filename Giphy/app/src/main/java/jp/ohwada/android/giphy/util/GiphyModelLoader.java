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
 * class GiphyModelLoader
 * 
 * A model loader that translates a POJO mirroring a JSON object representing a single image from
 * Giphy's api into an {@link java.io.InputStream} that can be decoded into an
 * {@link android.graphics.drawable.Drawable}.
 * original : https://github.com/bumptech/glide/tree/master/samples/giphy
 */
public class GiphyModelLoader extends BaseGlideUrlLoader<GifResult> {

  /**
   * == handles ==
   */
  @Override
  public boolean handles(@NonNull GifResult model) {
    return true;
  } // handles

  /**
   * == constractor ==
   */
 public GiphyModelLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
    super(urlLoader);
  } // GiphyModelLoader

  /**
   * == getUrl ==
   */
  @Override
  protected String getUrl(GifResult model, int width, int height, Options options) {
    GifImage fixedHeight = model.images.fixed_height;
    int fixedHeightDifference = getDifference(fixedHeight, width, height);
    GifImage fixedWidth = model.images.fixed_width;
    int fixedWidthDifference = getDifference(fixedWidth, width, height);
    if (fixedHeightDifference < fixedWidthDifference && !TextUtils.isEmpty(fixedHeight.url)) {
      return fixedHeight.url;
    } else if (!TextUtils.isEmpty(fixedWidth.url)) {
      return fixedWidth.url;
    } else if (!TextUtils.isEmpty(model.images.original.url)) {
      return model.images.original.url;
    } else {
      return null;
    }
  } // getUrl

  /**
   * getDifference
   */
  private static int getDifference(GifImage gifImage, int width, int height) {
    return Math.abs(width - gifImage.width) + Math.abs(height - gifImage.height);
  } // getDifference


} // class GiphyModelLoader
