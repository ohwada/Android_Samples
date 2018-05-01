/**
 * Yahoo News
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.yahoonews.model;

import java.util.List;

/**
 * class Rss
 */
public class Rss {

	public Channel channel;

	public List<Entry> entries;


/**
 * setChannel
 */
	public void setChannel(Channel _channel) {
        this.channel  = _channel;
	} // setChannel

/**
 * setAllEntry
 */
	public void setAllEntry(List<Entry> _entries) {
        this.entries  = _entries;
	} // setAllEntry


/**
 * getChannelInfo
 */
    public String getChannelInfo() {
        if (channel == null ) return null;
        return channel.getInfo();
} // getChannelInfo

/**
 * toString
 */
    @Override
    public String toString() {
        return "Rss { " +
                " channel= " + channel + 
                ", entries= " + entries + 
               " } ";
    } // toString

} // class Rss
