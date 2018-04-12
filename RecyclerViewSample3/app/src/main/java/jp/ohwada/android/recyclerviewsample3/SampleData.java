/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample3;

/**
 * class SampleData
 */
public class SampleData {

    private int id;
    private String text;

/**
 * constractor
 */
    public SampleData(int id, String text) {
        this.id = id;
        this.text = text;
    } // SampleData

/**
 * getId
 */
    public int getId() {
        return id;
    } // getId

/**
 * setId
 */
    public void setId(int id) {
        this.id = id;
    } // setId

/**
 * getText
 */
    public String getText() {
        return text;
    } // getText

/**
 * setTex
 */
    public void setText(String text) {
        this.text = text;
    } // setText

} // SampleData
