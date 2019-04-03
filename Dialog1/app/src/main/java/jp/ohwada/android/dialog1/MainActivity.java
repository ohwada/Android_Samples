/** 
 *  Dialog Sample
 *  Yes No Dialog 
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.dialog1;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** 
 *  class MainActivity
 */
// public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

	private DialogUtil mDialogUtil;


/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDialogUtil = new DialogUtil(this);

        Button btn1 = (Button) findViewById(R.id.Button_1);
         btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogUtil.showStandardDialog();
            }
        }); // btn1

        Button btn2 = (Button) findViewById(R.id.Button_2);
         btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogUtil.showSimpleDialog();

            }
        }); // btn2

        Button btn3 = (Button) findViewById(R.id.Button_3);
         btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogUtil.showCustomWidthDialog();
            }
        }); // btn3

        Button btn4 = (Button) findViewById(R.id.Button_4);
         btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogUtil.showCustemGravityDialog();
            }
        }); // btn4

        Button btn5 = (Button) findViewById(R.id.Button_5);
         btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogUtil.showCustomBackgroundDialog();
            }
        }); // btn5

        Button btn6 = (Button) findViewById(R.id.Button_6);
         btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogUtil.showTransparentBackgroundDialog();
            }
        }); // btn6

        Button btn7 = (Button) findViewById(R.id.Button_7);
         btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogUtil.showTransparentThemeDialog();
            }
        }); // btn7

    }// onCreate

}//  class MainActivity
