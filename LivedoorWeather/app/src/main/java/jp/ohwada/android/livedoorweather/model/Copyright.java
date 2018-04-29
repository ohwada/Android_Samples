/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */
package jp.ohwada.android.livedoorweather.model;

import java.util.List;

/**
 * class Copyright
 */
public class Copyright {
    public String title;
    public String link;
    public List<Provider> provider;
    public Image image;


/**
 * getImageUrl
 */
    public String getImageUrl() {
        if (image == null) return null;
        return image.url;
} // getImageUrl


/**
 * getProviderName
 */
    public String getProviderName() {
        if ((provider == null)|| (provider.size() == 0) ) return null;
        Provider p0 = provider.get(0);
        if (p0 == null) return null;
        return p0.name;
} // getProviderName


/**
 * toString
 */
    @Override
    public String toString() {
        return "Copyrigh {" +
                "title=" + title +
                ",  linkt= " + link +
                ", provider= " + provider +
                ",  image= " + image +
                "} ";
    } // toString

} // class Copyright
