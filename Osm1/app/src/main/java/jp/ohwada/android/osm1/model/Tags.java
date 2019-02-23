/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1.model;

import com.google.gson.annotations.SerializedName;

/**
 * class Tags
 * reference : OpenStreetMatsuya
 * https://wiki.openstreetmap.org/wiki/User:Okano_t/OpenStreetMatsuya
 */
public class Tags {
 
    	private final static String COMMA  = " , ";

    	private final static String LF  = "\n";

@SerializedName("addr:full")
public String addr_full;

public String amenity;
public String cuisine;
public String branch;
public String capacity;

public String name;

@SerializedName("name:en")
public String name_en;

@SerializedName("name:ja")
public String name_ja;

@SerializedName("name:ja_rm")
public String name_ja_rm;

public String opening_hours; 
public String operator;
public String note;
public String drive_through;
public String internet_access;
public String level;

@SerializedName("payment:cash")
public String payment_cash;

@SerializedName("payment:icsf")
public String payment_icsf;

public String phone;
public String smoking;
public String source;
public String takeaway;
public String toilets;

@SerializedName("toilets:access")
public String toilets_access;

public String wheelchair;

@SerializedName("wheelchair:description")
public String wheelchair_description; 

public String getName() {
    if(name != null) {
        return name;   
    }
    if(name_ja != null) {
        return name_ja;   
    }
    return null;
} // getName

/**
 * getInfo
 */
public String getInfo() {
    StringBuilder builder = new StringBuilder();

    if(branch != null) {
          builder.append(branch);
         builder.append(LF);
    }
    if(opening_hours != null) {
           builder.append("営業時間: ");
          builder.append(opening_hours);
         builder.append(LF);
    }
    if(phone != null) {
           builder.append("電話番号: ");
          builder.append(phone);
         builder.append(LF);
    }
    if(addr_full != null) {
           builder.append("住所: ");
          builder.append(addr_full);
         builder.append(LF);
    }
    if(smoking != null) {
           builder.append("喫煙: ");
          builder.append(smoking);
         builder.append(LF);
    }
    if(payment_icsf != null) {
           builder.append("suica: ");
          builder.append(payment_icsf);
         builder.append(LF);
    }
    if(wheelchair != null) {
           builder.append("車いす: ");
          builder.append(wheelchair);
         builder.append(LF);
    }
    if(wheelchair_description != null) {
          builder.append(wheelchair_description);
         builder.append(LF);
    }
    if(note != null) {
          builder.append(note);
         builder.append(LF);
    }

    return builder.toString();
} // getInfo

/**
 * toString
 */
@Override
public String toString() {
    StringBuilder builder = new StringBuilder();
           builder.append("Tags [ ");

       builder.append("addr_full: ");
          builder.append(addr_full);
         builder.append(COMMA);
       builder.append("amenity: ");
          builder.append(amenity);
         builder.append(COMMA);
       builder.append("branch: ");
          builder.append(branch);
         builder.append(COMMA);
       builder.append("capacity: ");
          builder.append(capacity);
         builder.append(COMMA);
       builder.append("cuisine: ");
          builder.append(cuisine);
         builder.append(COMMA);

       builder.append("name: ");
          builder.append(name);
         builder.append(COMMA);
       builder.append("name_en: ");
          builder.append(name_en);
         builder.append(COMMA);
       builder.append("name_ja: ");
          builder.append(name_ja);
         builder.append(COMMA);
       builder.append("name_ja_rm: ");
          builder.append(name_ja_rm);
         builder.append(COMMA);

       builder.append("drive_through: ");
          builder.append(drive_through);
         builder.append(COMMA);
      builder.append("note: ");
          builder.append(note);
         builder.append(COMMA);
      builder.append("drive_through: ");
          builder.append(drive_through);
         builder.append(COMMA);
      builder.append("internet_access: ");
          builder.append(internet_access);
         builder.append(COMMA);
      builder.append(" level: ");
          builder.append(level);
         builder.append(COMMA);
       builder.append("opening_hours: ");
          builder.append(opening_hours);
         builder.append(COMMA);
       builder.append("operator: ");
          builder.append(operator);
         builder.append(COMMA);
       builder.append("payment_cash: ");
          builder.append(payment_cash);
         builder.append(COMMA);
       builder.append("payment_icsf: ");
          builder.append(payment_icsf);
         builder.append(COMMA);
           builder.append("phone:");
          builder.append(phone);
         builder.append(COMMA);
           builder.append("smoking: ");
          builder.append(smoking);
         builder.append(COMMA);
           builder.append("source: ");
          builder.append(source);
         builder.append(COMMA);
           builder.append("takeaway: ");
          builder.append( takeaway);
         builder.append(COMMA);
           builder.append("toilets: ");
          builder.append( toilets );
         builder.append(COMMA);
           builder.append("toilets_access: ");
          builder.append( toilets_access );
         builder.append(COMMA);
           builder.append("wheelchair: ");
          builder.append(wheelchair);
         builder.append(COMMA);
           builder.append("wheelchair_description: ");
          builder.append(wheelchair_description);
         builder.append(COMMA);

          builder.append("] ");
          builder.append(LF);
    return builder.toString();
}

} // lass Tags
