/**
 * RSS Sample
 * with Android RSS Libraly
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.rsssample;


import android.view.ViewGroup;

import org.mcsoxford.rss.MediaEnclosure;
import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  class Entry
 * reference : https://github.com/ahorn/android-rss
 */
public class Entry {

    	private static final String LF = "\n";

  		private final static ViewGroup INFLATE_ROOT = null;

         private final static String RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

    private SimpleDateFormat mSimpleDateFormat;

    private RSSItem item;


/**
 * constractor
 */
public Entry() {
    initEntry();
} // Entry


/**
 * constractor
 */
public Entry(RSSItem _item) {
    this.item = _item;
    initEntry();
} // Entry

/**
 * constractor
 */
private void initEntry() {
    mSimpleDateFormat = new SimpleDateFormat(RFC822_FORMAT);
} // initEntry


/**
 * toString
 */
    @Override
    public String toString() {
        return "Entry { " +
                ", title= " + getTitle() +
                " , link= " + getLinkString() + 
                " , pubDate= " + getPubDateRFC822() + 
                " , Category= " + getFirstCategory() +
                " , enclosure= " + getEnclosureUri() +
                " , thumbnail= " + getFirstThumbnailInfo() +
                " , description= " + getDescription() + 
               " } ";
    } // toString


/**
 * getTitle
 */
  public String getTitle() {
        return item.getTitle();
  } // getTitle


/**
 * getDescription
 */
  public String getDescription() {
        return item.getDescription();
  } // getDescription


/**
 * getLinkString
 */
  public String getLinkString() {
        android.net.Uri link = item.getLink();
        if (link == null) return null;
        return link.toString();
  } // getLinkString


/**
 * getPubDateRFC822
 */
public String getPubDateRFC822() {
            Date date = item.getPubDate();
            if (date == null) return null;
            return mSimpleDateFormat.format ( date );
} // getPubDateRFC822


/**
 * getFirstCategory
 */
  public String getFirstCategory() {

        List<String> list = item.getCategories();
        if ((list == null)||(list.size() == 0)) return null;         
        return list.get(0);

} // getFirstCategory


/**
 *  getEnclosureUri
 */
    public String getEnclosureUri() {
            MediaEnclosure enclosure = item.getEnclosure();
            if (enclosure == null) return null;
            android.net.Uri uri = enclosure.getUrl();
                if (uri == null) return null;
                return uri.toString();
} // getEnclosureUri


/**
 *  getFirstThumbnail
 */
  public MediaThumbnail getFirstThumbnail() {
        List<MediaThumbnail> list = item.getThumbnails();
            if ((list == null)||(list.size() == 0)) return null;
            return list.get(0);

} // getFirstThumbnail


/**
 *  getFirstThumbnailInfo
 */
  public String getFirstThumbnailInfo() {
            String info = "MediaThumbnail { ";
            info += "uri=" + getFirstThumbnailUri();
            info += " , width=" + getFirstThumbnailWidth();
            info += " , height=" + getFirstThumbnailHeight();
            info += " } ";
            return info;
} // getFirstThumbnailInfo

/**
 *  getFirstThumbnailUri
 */
  public String getFirstThumbnailUri() {
            MediaThumbnail thum = getFirstThumbnail();
            if (thum == null) return null;
            android.net.Uri uri = thum.getUrl();
            if (uri == null) return null;
            return uri.toString();
} // getFirstThumbnailUri


/**
 *  getFirstThumbnailWidth
 */
  public int getFirstThumbnailWidth() {
            MediaThumbnail thum = getFirstThumbnail();
            if (thum == null) return 0;
            return thum.getWidth();
} // getFirstThumbnailWidth

/**
 *  getFirstThumbnailHeight
 */
  public int getFirstThumbnailHeight() {
            MediaThumbnail thum = getFirstThumbnail();
            if (thum == null) return 0;
            return thum.getHeight();
} // getFirstThumbnailHeight


} // class Entry
