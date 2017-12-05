 	/**
	 * CallerLog
	 * 2017-11-01 K.OHWADA    
	 */

package jp.ohwada.android.callersample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hoge.hogehoge();
    } // onCreate

} // class MainActivity
