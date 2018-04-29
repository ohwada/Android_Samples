/**
 * Livedoor Weather
 *  2018-04-10 K.OHWADA
 */

package jp.ohwada.android.livedoorweather.model;

import java.util.List;

/**
 * class Weather
 */
public class Weather {

	private final static String LF = "\n";

    public String title;
    public String link;
    public String publicTime;
    public Image image;
    public Description description;
    public Copyright copyright;
    public AreaLocation location;
    public List<PinpointLocation> pinpointLocations;
    public List<Forecast> forecasts;


/**
 * getOverview
 */
    public String getOverview() {
        String str = title + LF;
        if ( description != null ) {
            str += description.publicTime + LF;
            str += description.text + LF;
        }
        return str;
} // getOverview


/**
 * getCopyrightTtitle
 */
    public String getCopyrightTtitle() {
        if (copyright == null) return null;
        return copyright.title;
} // getCopyrightTtitle


/**
 * getCopyrightImageUrl
 */
    public String getCopyrightImageUrl() {
        if (copyright == null) return null;
        return copyright.getImageUrl();
} // getCopyrightImageUrl


/**
 * toString
 */
    @Override
    public String toString() {
        return "Weather { " +
                ", title='" + title + 
                ", link='" + link + 
                ", publicTime= " +publicTime + 
                ", description= " + description +
                ", location= " + location +
                ", image= " + image +
                ", copyright= " + copyright +
                "pinpointLocations= " + pinpointLocations +
                ", forecasts= " + forecasts +
                " } ";
    } // toString

} // Weather
