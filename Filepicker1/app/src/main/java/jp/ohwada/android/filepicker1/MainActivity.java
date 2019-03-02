/**
 * File Picker Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.filepicker1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity {

    private PickerUtil  mPickerUtil;


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnSetup = (Button) findViewById(R.id.Button_1);
         btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickerUtil.setup();
            }
        }); // btnSetup

        Button btnRead = (Button) findViewById(R.id.Button_2);
         btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickerUtil.showFilePicker();
            }
        }); // btnRead

        Button btnWrite = (Button) findViewById(R.id.Button_3);
         btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickerUtil.showDirPicker();
            }
        }); // btnWrite

        TextView tv1 = (TextView) findViewById(R.id.TextView_1);

    mPickerUtil = new PickerUtil(this);
    mPickerUtil.setTextView(tv1);

    } // onCreate


} //  class MainActivity
