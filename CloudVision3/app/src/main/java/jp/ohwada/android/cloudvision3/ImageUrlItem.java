/**
 * Cloud Vision Sample
 * ImageUrlItem
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision3;


/**
 * class ImageUrlItem
 */
public class ImageUrlItem {

    private String  name;

   private String imageUrl;


/** 
 * constractor
 */
public ImageUrlItem(String _name, String  _image) {
    name = _name;
    imageUrl = _image;
}


/** 
 *  getName
 */
public String getName() {
    return name;
}


/** 
 *  getImageUrl
 */
public String getImageUrl() {
    return imageUrl;
}


} // class class ImageUrlItem
