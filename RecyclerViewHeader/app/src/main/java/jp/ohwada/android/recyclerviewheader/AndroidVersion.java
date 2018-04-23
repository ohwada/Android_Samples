/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewheader;


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
public static List<String> getListCodename(int size)  {
        int len = ANDROID_CODE_NAMES.length;
        if (size > len ){
            size = len;
        }
        List<String> list = new ArrayList<String>();
        for(int i=0; i<size; i++ ) {
            list.add(ANDROID_CODE_NAMES[i]);
        }
        return list;
} // getListCodename

} //  class AndroidVersion

