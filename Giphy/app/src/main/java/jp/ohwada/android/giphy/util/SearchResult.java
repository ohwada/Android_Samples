  /**
 * Glide Giphy Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.giphy.util;

  import java.util.Arrays;

  /**
   * class SearchResult
   * 
   * A POJO mirroring the top level result JSON object returned from Giphy's api.
  * original : https://github.com/bumptech/glide/tree/master/samples/giphy
   */
  public class SearchResult {

    public GifResult[] data;

    /*
     * == toString ==
     */
    @Override
    public String toString() {
      return "SearchResult{" + "data=" + Arrays.toString(data) + '}';
    } // toString

  } // SearchResult