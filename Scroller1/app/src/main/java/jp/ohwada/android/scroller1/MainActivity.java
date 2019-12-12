/**
 * Scroller Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/keiji/adventcalendar_2015_mincomi
 */
package jp.ohwada.android.scroller1;



import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



/**
 * class MainActivity
 */
//public class ScrollerActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    private ScrollerView mScrollerView;

/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScrollerView = new ScrollerView(this);
        setContentView(mScrollerView);
    }

} // class MainActivity
