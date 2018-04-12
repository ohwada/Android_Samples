/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


    /*
     * class AndroidVersion
     */  
public class AndroidVersion  {

   private  static final String[] ANDROID_CODE_NAMES = {
            "Cupcake", "Donuts", "Eclair", "Froyo", "Gingerbread", "Honeycomb",
            "IceCreamSandwich", "JellyBean", "Kitkat", "Lollipop", "Marshmallow", "Nougat", "Oreo" };

   private  static final int LIST_ITEM_NUM = 3;

 /*
    * constractor
    */  
public AndroidVersion()  {
} // AndroidVersion



 /*
    * getListCodename
    *  return list of codenames
    */  
public static List<String> getListCodename()  {
        List<String> list = new ArrayList<String>();
        for (int i=0; i<LIST_ITEM_NUM; i++ ) {
            String codename = getRandomCodename();
            list.add(codename);
        }
        return list;
} // getListCodename



 /*
    * getRandomCodename
    *  return codename at random choise
    */  
public static  String getRandomCodename() {
    int len = ANDROID_CODE_NAMES.length;
    int index = (int)(Math.random()*(len-1));
    String codename = ANDROID_CODE_NAMES[index];
    return codename;
} // getRandomCodename


 /*
    * getRandomSampleData
     * for SortedList
    *  return  SampleData at random choise
    */  
public static   SampleData getRandomSampleData() {
    int len = ANDROID_CODE_NAMES.length;
    int index = (int)(Math.random()*(len-1));
    String codename = ANDROID_CODE_NAMES[index];
    SampleData data = new SampleData((index+1), codename);
    return  data;
} //  getRandomSampleData

} //  class AndroidVersion
