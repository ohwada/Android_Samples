/**
 * MediaMuxer Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.mediamuxer1;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * class MainActivity
 * test EncodeVideo To Mp4
 */
public class MainActivity extends Activity {

    // QVGA at 2Mbps
    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    private static final int BIT_RATE = 2000000;

/**
  * onCreate
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            Button btnStart = (Button) findViewById(R.id.Button_start);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    testMediaMuxer();
                }
            }); // btnStart

    }

/**
 * testMediaMuxer
 */
private void testMediaMuxer() {
    EncodeAndMuxTest muxTest = new EncodeAndMuxTest(this,
    new EncodeAndMuxTest.Callback() {
                @Override
                public void onFinish() {
                    showToast( "Finished" );
                }
            }); // Callback

    muxTest.testEncodeVideoToMp4(WIDTH, HEIGHT, BIT_RATE);
}

/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast

} //class MainActivity
