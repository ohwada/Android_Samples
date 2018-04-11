/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample2;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

    /*
     * class SampleAdapter
     */ 
    public  class SampleAdapter extends RecyclerView.Adapter<SampleHolder> {

    	// debug
    	private final static String TAG_SUB = "SampleAdapter";

        private static final int LAYOUT_ITEM_ID = R.layout.list_item;
 
 // callback 
    private OnItemClickListener mListener;  


        private final Object lock = new Object();

    // codename
        private  List<String> mDataset;


            /*
     * callback interface
     */    
    public interface OnItemClickListener {
        public void onItemClick( int position, String codename );
    } // interface


    /*
     * callback
     */ 
    public void setOnItemClickListener( OnItemClickListener listener ) {
        log_d("setOnItemClickListener");
        mListener = listener;
    } // setOnItemClickListener


    /*
     * == constractor ==
     */ 
    public  SampleAdapter( List<String> dataset ) {
        super();
        log_d("SampleAdapter");
        mDataset = dataset;
} // SampleAdapter


    /*
     * == onCreateViewHolder ==
    * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     */ 
         @Override
         public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    log_d("onCreateViewHolder");
    SampleHolder holder = createHolder(parent);
            return holder;
        } // onCreateViewHolder


    /*
     * createHolder
     */ 
         private SampleHolder createHolder(ViewGroup parent) {
  log_d("createHolder");
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
           View itemView = inflater.inflate(LAYOUT_ITEM_ID, parent, false);
           final SampleHolder holder =  new SampleHolder(itemView);

           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    log_d("itemView onClick");
                    int position =  holder.getAdapterPosition();
                    String codename = mDataset.get(position);
                    notifyItemClick(position, codename );
                } // onClick
            }); // setOnClickListener
            return holder;
        } // onCreateHolder



    /*
     * == onBindViewHolder 
    * Called by RecyclerView to display the data at the specified position
     */ 
        @Override
        public void onBindViewHolder(SampleHolder holder, int position) {
                    log_d("onBindViewHolder " + position);
            String text = mDataset.get(position);
            holder.setText(text);
        } // onBindViewHolder



    /*
     * == getItemCount ==
    * Returns the total number of items in the data set held by the adapter
     */ 
        @Override
        public int getItemCount() {
                    log_d("getItemCount");
            return mDataset.size();
        } // getItemCount



    /**
     * add
     */
        public void add(String codename) {
                    log_d("add " + codename);
            mDataset.add(codename);
            int position = mDataset.size();
            notifyItemInserted(position);
    } // add



    /**
     * remove
     */
        public void remove(int position) {
                    log_d("remove " + position);
            if (position >= 0 &&  position < mDataset.size() ) {
                mDataset.remove(position);
            } // if
            notifyItemRemoved(position);
    } // remove



    /**
     * notifyItemClick
     */
    private void notifyItemClick( int position, String codename ) {
                    log_d("nnotifyItemClick " + position);
           if ( mListener != null ) {
            mListener.onItemClick( position, codename );
        } 
}	// notifyItemClick


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class SampleAdapter

