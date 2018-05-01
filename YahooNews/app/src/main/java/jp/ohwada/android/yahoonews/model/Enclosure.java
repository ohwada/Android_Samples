/**
 * Yahoo News
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.yahoonews.model;

public class Enclosure {

	public String length;

	public String url;

	public String type;

	public Enclosure() {
	}
	
	public void setLength(String _length) {
        this.length  = _length;
	} // setLength

	public void setUrl(String _url) {
        this.url  = _url;
	} // setTtitle

	public void setType(String _type) {
        this.type  = _type;
	} // setTtitle


/**
 * toString
 */
    @Override
    public String toString() {
        return "Enclosure { " +
                " length= " + length + 
                ", url= " + url + 
                ", type= " +type + 
               " } ";
    } // toString

} // class Enclosure
