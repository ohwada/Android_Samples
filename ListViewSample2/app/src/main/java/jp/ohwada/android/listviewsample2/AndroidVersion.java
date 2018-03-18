/**
 * ListView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.listviewsample2;

import java.util.ArrayList;
import java.util.List;


    /*
     * class AndroidVersion
     */  
public class AndroidVersion  {


   public  static final String[] VERSION_NAME = {
            "1.5", "Cupcake",
            "1.6", "Donuts",
            "2.0", "Eclair",
            "2.2", "Froyo",
            "2.3", "Gingerbread",
            "3.0", "Honeycomb",
            "4.0", "IceCreamSandwich",
            "4.2", "JellyBean",
            "4.4", "Kitkat",
            "5.0", "Lollipop",
            "6.0", "Marshmallow",
            "7.0", "Nougat",
            "8.0", "Oreo" };




 /*
    * constractor
    */  
public AndroidVersion()  {
} // AndroidVersion



 /*
    * getListVersionItem
    */  
public static List<VersionItem> getListVersionItem()  {
        List<VersionItem> list = new ArrayList<VersionItem>();
        int max=VERSION_NAME.length / 2 ;
        for (int i=0;i<max;i++) {
            int i2 = 2*i;
            list.add( new VersionItem(VERSION_NAME[i2], VERSION_NAME[i2+1]));
}// for
        return list;
} // getListCodename


} //  class AndroidVersion

