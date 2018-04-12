/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample3;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

// https://developer.android.com/reference/android/support/v7/util/SortedList.Callback.html

/**
 * class SampleCallback
 */
public class SampleCallback extends SortedList.Callback<SampleData> {

    	// debug
    	private final static String TAG_SUB = "SampleCallback";

        private RecyclerView.Adapter mAdapter;

/**
 * constractor
 */
public SampleCallback(RecyclerView.Adapter adapter) {
    log_d("SampleCallback");
            mAdapter = adapter;
        } // SampleCallback


/**
 * == compare ==
 * should compare two and return how they should be ordered
 * Return : a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
 */
        @Override
        public int compare(SampleData data1, SampleData data2) {
        log_d("compare");
            return data1.getId() - data2.getId();
        } // compare


/**
 * == areContentsTheSame ==
 *  Called by the SortedList when it wants to check whether two items have the same data or not.
 */
        @Override
        public boolean areContentsTheSame(SampleData oldData, SampleData newData) {
        log_d("areContentsTheSame");
            String oldText = oldData.getText();
            if (oldText == null) {
                return newData.getText() == null;
            }
            return oldText.equals(newData.getText());
        } // areContentsTheSame

/**
 * == areItemsTheSame ==
 *  Called by the SortedList to decide whether two objects represent the same Item or not.
 */
        @Override
        public boolean areItemsTheSame(SampleData data1, SampleData data2) {
        log_d("areItemsTheSame");
            return data1.getId() == data2.getId();
        } // areItemsTheSame


/**
 * == onInserted ==
 * Called when count number of items are inserted at the given position
 */
        @Override
        public void onInserted(int position, int count) {
        log_d("onInserted");
            mAdapter.notifyItemRangeInserted(position, count);
        } // onInserted


/**
 * == onRemoved ==
 * Called when count number of items are removed from the given position
 */
        @Override
        public void onRemoved(int position, int count) {
        log_d("onRemoved");
            mAdapter.notifyItemRangeRemoved(position, count);
        } // onRemoved


/**
 * == onMoved ==
 * Called when an item changes its position in the list
 */
        @Override
        public void onMoved(int fromPosition, int toPosition) {
        log_d("onMoved");
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        } /// onMoved


/**
 * == onChanged ==
 * Called when count number of items are updated at the given position.
 */
        @Override
        public void onChanged(int position, int count) {
        log_d("onChanged");
            mAdapter.notifyItemRangeChanged(position, count);
        } // onChanged


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

    } // class SampleCallback

