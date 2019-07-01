/**
 * HTML Sample
 * show rendered HTML in TextView
 * remove HTML tags
 * 2019-02-01 K.OHWADA 
 */
package jp.ohwada.android.html1;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "HTML";
    private final static String TAG_SUB = "MainActivity";

/**
 *  sample data
 */
private static final String HTML1 = "<font color=\"blue\">blue</font><br><font color=\"green\">green</font><br/><a href=\"exsample.com\">exsample</a><br />xyz<br/>";


/**
 *  file name
 */
private static final String FILE_NAME = "sample.html";


/**
 *  TextView
 */
private TextView mTextView1;
private TextView mTextView2;
private TextView mTextView3;


/**
 *  Util for File
 */
private FileUtil mFileUtil;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView1 = (TextView) findViewById(R.id.TextView_1);
        mTextView2 = (TextView) findViewById(R.id.TextView_2);
        mTextView3 = (TextView) findViewById(R.id.TextView_3);

        Button btnRead = (Button) findViewById(R.id.Button_read);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile();
            }
        }); // btnRead

        mFileUtil = new FileUtil(this);

        procHtml();

    } // onCreate


/**
 *  procHtml
 */
private void procHtml() {

    mTextView1.setText(HTML1);
    mTextView2.setText(fromHtml(HTML1));
    mTextView3.setText(removeHtmlTags(HTML1));
} // procHtml


/**
 *  readFile
 */
private void readFile() {
    String html = mFileUtil.readTextInAssets(FILE_NAME);
    mTextView1.setText(html);
    mTextView2.setText(fromHtml(html));
    mTextView3.setText(removeHtmlTags(html));
} // readFile


/**
 *  fromHtml
 */
private Spanned fromHtml(String html){
       return HtmlUtil.fromHtml(html);
} // fromHtml


/**
 *  removeHtmlTags
 */
private String removeHtmlTags(String html){
    return HtmlUtil.removeTags(html);
} // removeHtmlTags


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class MainActivity
