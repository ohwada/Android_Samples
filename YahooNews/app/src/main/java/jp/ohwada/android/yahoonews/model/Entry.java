/**
 * Yahoo News
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.yahoonews.model;

/**
 *  class Entry
 */
public class Entry {

    	private static final String LF = "\n";

	public String title;

	public String link;

	public String pubDate;

	public Enclosure enclosure;

	public String guid;

	
/**
 * etTitle
 */
	public void setTitle(String _title) {
        this.title  = _title;
	} // setTtitle

/**
 * setLink
 */
	public void setLink(String _link) {
        this.link  = _link;
	} // setLink

/**
 * setPubdate
 */
	public void setPubdate(String _pubdate) {
        this.pubDate  = _pubdate;
	} // setPubdate

/**
 * setGuid
 */
	public void setGuid(String _guid) {
        this.guid  = _guid;
	} // setGuid

/**
 * etEnclosure
 */
	public void setEnclosure(Enclosure _enclosure) {
        this.enclosure  = _enclosure;
	} // setEnclosure

/**
 * getInfo
 */
    public String getInfo() {
        String text = title +LF;
        text += pubDate +LF;
        return text;
} // getInfo


/**
 * getImageUrl
 */
    public String getImageUrl() {
        if (enclosure == null) return null;
        return enclosure.url;
} // getImageUrl


/**
 * toString
 */
    @Override
    public String toString() {
        return "Entry { " +
                ", title= " + title + 
                " , link= " + link + 
                " , pubDate= " +pubDate + 
                " , guid= " + guid + 
                " , enclosuree= " + enclosure + 
               " } ";
    } // toString

} // class Entry
