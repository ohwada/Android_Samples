/**
 * Cloud Vision Sample
 * HtmlUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision2;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;


/**
 *  class HtmlUtil
 */
public class HtmlUtil {


/**
 *  regular expression for HTML tag
 */
     private static final String REGEX_HTML_TAG = "<.+?>";


/**
 *  char
 */
     private static final String SPACE = " ";


/**
 *  removeTags
 */
public static String removeTags(String str) {
	String ret = str.replaceAll(REGEX_HTML_TAG, SPACE);
    return ret;
}


/**
 *  fromHtml
 *  Html.fromHtml is deprecated in Android N+
 */
@SuppressWarnings("deprecation")
public static Spanned fromHtml(String html){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
       return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    } else {
       return Html.fromHtml(html);
    }
} // fromHtml


} // class HtmlUtil