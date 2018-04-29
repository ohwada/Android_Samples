/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather.model;

/**
 * cclass Image
 */
public class Image {
    public String title;
    public String link;
    public String url;
    public String width;
    public String height;

/**
 * toString
 */
    @Override
    public String toString() {
        return "Image{ " +
                "title= " + title +
                ",  link= " + link +
                ",  url= " + url + 
                ",  width= " + width +
              ",  height= " + height + "}";
    } // toString

} // class Image
