/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;

  /**
   * class GifResult
   * 
   * A POJO mirroring an individual GIF image returned from Giphy's api.
  * original : https://github.com/bumptech/glide/tree/master/samples/giphy
   */
  public class GifResult {

    public String id;
    public GifUrlSet images;

    /*
     * == toString ==
     */
    @Override
    public String toString() {
      return "GifResult{" + "id='" + id + '\'' + ", images=" + images
          + '}';
    } // toString

  } // class GifResult