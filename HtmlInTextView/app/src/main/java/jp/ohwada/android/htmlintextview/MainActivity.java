/**
 * HTML in TextView
* support Android N+
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.htmlintextview;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

private static final String HTML1 = "<font color=\"blue\"><b><big>Nintendo</big></b></font><font color=\"red\"><sup><small>&reg;</small></sup></font>";

 static final String HTML2 = "<a href=\"https://developer.android.com/reference/android/text/Html\">Html</a>";

 static final String HTML3 = "<img src=\"droid\"> <b>Droid-kun </b>";

private TextView mTextView1;
private TextView mTextView2;
private TextView mTextView3;
private TextView mTextView4;
private TextView mTextView5;
private TextView mTextView6;

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
mTextView4 = (TextView) findViewById(R.id.TextView_4);
mTextView5 = (TextView) findViewById(R.id.TextView_5);
mTextView6 = (TextView) findViewById(R.id.TextView_6);

procHtml();

    } // onCreate


/**
 *  procHtml
 */
private void procHtml() {

    mTextView1.setText(HTML1);
    mTextView2.setText(fromHtml(HTML1));
    mTextView3.setText(HTML2);
    mTextView4.setText(fromHtml(HTML2));
    mTextView4.setMovementMethod(LinkMovementMethod.getInstance());
    mTextView5.setText(HTML3);

    // FATAL ERROR, if specify an image resource that does not exist 
    try {
        mTextView6.setText(fromHtmlImage(HTML3));
    } catch (Exception e) {
       // nothing to do
    }

} // procHtml


/**
 *  fromHtml
 *  Html.fromHtml is deprecated in Android N+
 */
@SuppressWarnings("deprecation")
private Spanned fromHtml(String html){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
       return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    } else {
       return Html.fromHtml(html);
    }
} // fromHtml

/**
 *  fromHtmlImage
 *  Html.fromHtml is deprecated in Android N+
 */
@SuppressWarnings("deprecation")
private Spanned fromHtmlImage(String html) {
    ImageGetterImpl imageGetter = new ImageGetterImpl(getApplicationContext());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
       return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imageGetter, null );
    } else {
       return Html.fromHtml(html, imageGetter, null);
    }
} // fromHtmlImage


/**
 *  === class ImageGetterImpl ===
 */
public class ImageGetterImpl implements Html.ImageGetter {

   Context mContext ;
    Resources mResources;
    String mPackageName;

/**
 *  constractor
 */
    public ImageGetterImpl(Context context){

        mContext = context;
        mResources = context.getResources();
        mPackageName = context.getPackageName();
    } // ImageGetterImpl

/**
 *  == getDrawable ==
 */
            @Override
            public Drawable getDrawable(String source)  {

                int id = mResources.getIdentifier(source, "drawable", mPackageName);
                
                Drawable d = getDrawableResource(id);
                if (d != null) {
                    int w = d.getIntrinsicWidth();
                    int h = d.getIntrinsicHeight();
                    d.setBounds(0, 0, w, h);
                } 
            return d;
    } // getDrawable

/**
 *  getDrawableResource
 */
    @SuppressWarnings("deprecation")
    private Drawable getDrawableResource(int id){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return mContext.getDrawable(id);
        }
        else{
            return mResources.getDrawable(id);
        }
    } // getDrawableResource

} // class ImageGetterImpl

} // class MainActivity
