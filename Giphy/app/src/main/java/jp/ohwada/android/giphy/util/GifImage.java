/**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;

/**
   * class GifImage
   * 
   * A POJO mirroring a JSON object for an image with one particular url, size and dimension
   * returned from Giphy's api.
  * original : https://github.com/bumptech/glide/tree/master/samples/giphy
   */
  public class GifImage {

    public String url;
    public int width;
    public int height;

  
    /*
     * == toString ==
     */
    @Override
    public String toString() {
      return "GifImage{" + "url='" + url + '\'' + ", width=" + width + ", height=" + height + '}';
    } // oString

  } // class GifImage
