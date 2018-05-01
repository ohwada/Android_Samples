/**
 * Yahoo News
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.yahoonews;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import android.util.Log;
import android.util.Xml;

import jp.ohwada.android.yahoonews.model.*;

 	/**
	 * class RssParser
	 */ 
public class RssParser {
// debug
    	protected String TAG_SUB = "RssParser";

	private String RSS = "rss";
	private String CHANNEL = "channel";
	private String ITEM = "item";
	private String TITLE = "title";
	private String LINK = "link";
	private String PUBDATE = "pubDate";
	private String ENCLOSURE = "enclosure";
	private String GUID = "guid";
		private String DESCRIPTION = "description";
		private String LANGUAGE = "language";

		private String LENGTH = "length";
		private String URL = "url";
		private String TYPE = "type";


 	/**
	 * constractor
	 */ 
	public RssParser(){

	} // RssParser
	

 	/**
	 * parse
	 */ 
	public  Rss parse(String xml){
log_d("parse");
		List<Entry> listEntry = new ArrayList<Entry>();
		XmlPullParser parser = Xml.newPullParser();

			Channel currentChannel = null;
			Entry currentEntry = null;

				String name = "";
				String text = "";
			boolean isFinished = false;
			boolean isRunning = true;
			boolean isChannel = false;
		try{
			
			parser.setInput(new StringReader(xml));

			int eventType = parser.getEventType();

			while(eventType != XmlPullParser.END_DOCUMENT && isRunning){
				name = "";

				switch(eventType) {
					case XmlPullParser.START_DOCUMENT:
						// nothing to do
						break;

					case XmlPullParser.START_TAG:
						name = parser.getName();
				log_d("name: " + name);
					if(name.equalsIgnoreCase(RSS)) {
						// nothing to do

					} else if(name.equalsIgnoreCase(CHANNEL)) {
							currentChannel = new Channel();
							isChannel = true;
					} else if(name.equalsIgnoreCase(ITEM)) {
							currentEntry = new Entry();
							isChannel = false;

					} else if(name.equalsIgnoreCase(ENCLOSURE)) {
						Enclosure enclosure = parseEnclosure(parser);
						if (currentEntry != null) {
							currentEntry.setEnclosure(enclosure);
						}

						} else if(name.equalsIgnoreCase(TITLE)){
								text = parser.nextText();
			 					if (isChannel && (currentChannel != null)) {
									currentChannel.setTitle(text);
								}
 								if (currentEntry != null) {
									currentEntry.setTitle(text);
								}

						} else if(name.equalsIgnoreCase(LINK)){
								text = parser.nextText();
		 						if (isChannel && (currentChannel != null)) {
									currentChannel.setLink(text);
								}
 								if (currentEntry != null) {
									currentEntry.setLink(text);
								}

					} else if(name.equalsIgnoreCase(PUBDATE)){
								text = parser.nextText();
		 						if (isChannel && (currentChannel != null)) {
									currentChannel.setPubdate(text);
								}
 								if (currentEntry != null) {
									currentEntry.setPubdate(text);
								}

							} else if(name.equalsIgnoreCase(DESCRIPTION)){
					 			if (currentChannel != null) {
									currentChannel.setDescription(parser.nextText());
								}

							} else if(name.equalsIgnoreCase(LANGUAGE)){
					 			if (currentChannel != null) {
									currentChannel.setLanguage(parser.nextText());
								}

							} else if(name.equalsIgnoreCase(GUID)) {
					 			if (currentEntry != null) {
									currentEntry.setGuid(parser.nextText());
								}

						}
						break; // START_TAG

					case XmlPullParser.END_TAG:
						name = parser.getName();
						if ((name.equalsIgnoreCase(ITEM) && currentEntry != null)) {
							listEntry.add(currentEntry);
						} else if(name.equalsIgnoreCase(CHANNEL)) {
							// isFinished = true;
							isChannel = false;
							isRunning = false;
						}
						break; // END_TAG
				} // switch

				eventType = parser.next();
			} // while

		} catch(Exception e){
			Log.e("PARSE ERROR", e.getLocalizedMessage());
		}
		
		Rss rss = new Rss();
		rss.setChannel( currentChannel );
		rss.setAllEntry(listEntry);
		return rss;
	} // parse



 	/**
	 * parseEnclosure
	 */ 
private Enclosure parseEnclosure(XmlPullParser parser) {
	log_d("parseEnclosure");
	Enclosure enclosure = new Enclosure(); 
		String name;
	String value;

	// -1 if the current event type is not START_TAG
	int count = parser.getAttributeCount();

	try {
		for(int i = 0; i < count; i++) {
			name = parser.getAttributeName(i);
			value = parser.getAttributeValue(i);
			log_d(name + " : " + value);
				if(name.equalsIgnoreCase(LENGTH)) {
					enclosure.setLength(value);
				} else if(name.equalsIgnoreCase(URL)) {
					enclosure.setUrl(value);
				} else if(name.equalsIgnoreCase(TYPE)) {
					enclosure.setType(value);
				} 
		} // for
	} catch(Exception e){
			Log.e("PARSE ERROR", e.getLocalizedMessage());
	}
	return enclosure;
} // parseEnclosure


 	/**
	 * write into logcat
	 */ 
	protected void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class RssParser
