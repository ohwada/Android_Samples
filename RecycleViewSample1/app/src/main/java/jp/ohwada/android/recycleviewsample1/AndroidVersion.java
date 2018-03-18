/**
 * RecycleView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recycleviewsample1;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


    /*
     * class AndroidVersion
     */  
public class AndroidVersion  {

   public  static final String[] ANDROID_CODE_NAMES = {
            "Cupcake", "Donuts", "Eclair", "Froyo", "Gingerbread", "Honeycomb",
            "IceCreamSandwich", "JellyBean", "Kitkat", "Lollipop", "Marshmallow", "Nougat", "Oreo" };


 /*
    * constractor
    */  
public AndroidVersion()  {
} // AndroidVersion



 /*
    * getListCodename
    */  
public static List<String> getListCodename()  {
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(ANDROID_CODE_NAMES));
        return list;
} // getListCodename

} //  class AndroidVersion

