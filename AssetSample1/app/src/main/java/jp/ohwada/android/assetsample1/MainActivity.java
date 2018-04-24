/**
 *  Asset sample
 *  2018-03-01 K.OHWADA
 */

package jp.ohwada.android.assetsample1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

private TextView mTextView1;
private ImageView mImageView1;
private ListView mListView;
private AssetFile mAssetFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 mTextView1 = (TextView) findViewById(R.id.TextView_1);
 mImageView1 = (ImageView) findViewById(R.id.ImageView_1);

        Button btnText = (Button) findViewById(R.id.Button_text);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procText();
            }
        }); // btnText

        Button btnImage = (Button) findViewById(R.id.Button_image);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procImage();
            }
        }); // btnImage

        Button btnList = (Button) findViewById(R.id.Button_list);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procList();
            }
        }); // btnList

		mListView  = (ListView) findViewById( R.id.ListView_1 );

    mAssetFile = new AssetFile(this);
    } // onCreate

 	/**
	 * procText
	 */ 
private void procText() {
    String text = mAssetFile.readTextFile("sample.txt");
    mTextView1.setText(text);
} // procText

 	/**
	 * procImage
	 */ 
private void procImage() {
    Bitmap bitmap = mAssetFile.readImageFile("sample.png");
    mImageView1.setImageBitmap(bitmap);
} // procImage


 	/**
	 * procList
	 */ 
private void procList() {
    List<String> list = mAssetFile.getFileList();
    ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, list );
        mListView.setAdapter(arrayAdapter);
} // procImage


} // class MainActivity
