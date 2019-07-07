/**
 * Cloud Vision Sample
 * WebItem
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.cloudvision2;


import java.util.Locale;

/**
 * class WebItem
 */
public class WebItem {

    private String  pageTitle;

    private String pageUrl;

    private String imageUrl;


/** 
 * constractor
 */
public WebItem(String _title, String _page, String  _image) {
    pageTitle = _title;
    pageUrl = _page;
    imageUrl = _image;
}


/** 
 * constractor
 */
public WebItem(String _image) {
    imageUrl = _image;
}


/** 
 *  getPageTitle
 */
public String getPageTitle() {
    return pageTitle;
}


/** 
 *  getPageUrl
 */
public String getPageUrl() {
    return pageUrl;
}


/** 
 *  getImageUrl
 */
public String getImageUrl() {
    return imageUrl;
}


/** 
 *  getTitle
 */
public String getTitle() {
    String title = "unkown";
    if(pageTitle != null ) {
        // remove HTML tags
        title = HtmlUtil.removeTags(pageTitle);
    } else if(pageUrl != null ) {
        title = pageUrl;
    } else if(imageUrl != null ) {
        title = imageUrl;
    }
    return title;
} // getTitle


} // class class WebItem
