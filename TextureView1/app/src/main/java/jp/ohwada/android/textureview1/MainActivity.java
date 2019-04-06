/**
 * TextureView Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.textureview1;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


/**
 * MainActivity
 * original : https://github.com/dalinaum/TextureViewDemo/tree/master/src/kr/gdg/android/textureview
 */
public class MainActivity extends  Activity {

/**
 * onCreate
 */
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.list);

        List<Demo> list = getDemoList();
        DemoAdapter adpter = new DemoAdapter( this, R.id.TextView_row_name, list);

        listView.setAdapter(adpter );
        listView.setClickable(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
procItemClick(parent, view,
                   position,id);
            }
        }); // .setOnItemClickListener

} // onCreate

private List<Demo> getDemoList() {

     List<Demo> list =  new ArrayList<Demo>();
            list.add( new Demo("Canvas", CanvasActivity.class) );
            list.add( new Demo("Canvas2", Canvas2Activity.class) );
            list.add( new Demo("GL Triangle", GLTriangleActivity.class) );
           list.add( new Demo("Camera", CameraActivity.class) );
            list.add( new Demo("Camera2", Camera2Activity.class) );
    return list;

} // getDemoList

/**
 * procItemClick
 */
private void procItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Demo demo = (Demo) parent.getAdapter().getItem(
                        position);
                if (demo.classType != null) {
                    Intent intent = new Intent(this, demo.classType);
                    startActivity(intent);
                } 

} // procItemClick


/**
 * class DemoAdapter
 */
private class DemoAdapter extends ArrayAdapter<Demo> {

/**
 * constractor
 */
public DemoAdapter(Context context, int textViewResourceId, List<Demo> objects ) {
		super( context, textViewResourceId, objects );
} // DemoAdapter

/**
 * getView
 */
@Override
public View getView(int position, View convertView,
                    ViewGroup parent) {
    // super.getView(position, convertView, parent);

		LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(
			Context.LAYOUT_INFLATER_SERVICE ) ;

			View view = inflater.inflate( R.layout.demo_row, null );

             Demo demo = getItem(position);

              TextView tvName = (TextView) view
                        .findViewById(R.id.TextView_row_name);
            tvName.setText(demo.name);

        return view;
} // getView

} // class DemoAdapter


/**
 * class Demo
 */
private class Demo {
        public String name;
        public Class<?> classType;

/**
 * constractor
 */
public Demo(String name, Class<?> classType) {
            this.name = name;
            this.classType = classType;
} // Demo

/**
 * toString
 */
@Override
public String toString() {
            return name;
} // toString

} // class Demo

} // class MainActivity
