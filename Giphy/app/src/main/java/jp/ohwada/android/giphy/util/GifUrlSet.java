/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;
 
 /**
   * class GifUrlSet
   * 
   * A POJO mirroring a JSON object with a put of urls of different sizes and dimensions returned
   * for a single image from Giphy's api.
  * original : https://github.com/bumptech/glide/tree/master/samples/giphy
   */
  public class GifUrlSet {

      public GifImage original;
      public GifImage fixed_width;
      public GifImage fixed_height;

    /*
     * == toString ==
     */
    @Override
    public String toString() {
      return "GifUrlSet{" + "original=" + original + ", fixed_width="
          + fixed_width + ", fixed_height=" + fixed_height
          + '}';
    } // toString

  } // class GifUrlSet
