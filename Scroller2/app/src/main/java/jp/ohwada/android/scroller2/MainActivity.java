/**
 * Scroller Sample
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.scroller2;

import android.app.Activity;
import android.os.Bundle;


/**
 * class MainActivity
 */
public class MainActivity extends Activity {


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlingView flingView = (FlingView) findViewById(R.id.flingView);
        flingView.resetPosition();
    }


} // class MainActivity
