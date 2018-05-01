/**
 * Yahoo News
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.yahoonews.model;

public class Channel {

    private static final String LF = "\n";

	public String title;

	public String link;

	public String description;

	public String language;

	public String pubDate;

/**
 * setTitle
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
 * setDescription
 */
	public void setDescription(String _description) {
        this.description  = _description;
	} // setDescription

/**
 * setLanguage
 */
	public void setLanguage(String _language) {
        this.language  = _language;
	} // setLanguage

/**
 *  getInfo
 */
	public String  getInfo() {
            String text = title + LF;
            text += pubDate + LF;
            return text;
} //  getInfo


/**
 * toString
 */
    public String toString() {
        return "Channel { " +
                "title= " + title + 
                ", link= " + link + 
                ", description= " + description + 
                ", language= " + language + 
                ", pubDate= " +pubDate + 
               " } ";
    } // toString

} // class Channel
