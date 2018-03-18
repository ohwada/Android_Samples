/**
 * RecycleView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recycleviewsample1;


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
     * class RecycleAdapter
     */ 
    public  class RecycleAdapter extends RecyclerView.Adapter<RecycleHolder> {

    	// debug
    	private final static String TAG_SUB = "RecycleAdapter";

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
    // public  RecycleAdapter() {
        // super();
        // log_d("RecycleAdapter");
// } // RecycleAdapter

    public  RecycleAdapter( List<String> dataset ) {
        super();
        log_d("RecycleAdapter");
        mDataset = dataset;
} // RecycleAdapter



    /*
     * == onCreateViewHolder ==
    * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     */ 
         @Override
         public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    log_d("onCreateViewHolder");
    RecycleHolder holder = createHolder(parent);
            return holder;
        } // onCreateViewHolder


    /*
     * createHolder
     */ 
         private RecycleHolder createHolder(ViewGroup parent) {
  log_d("createHolder");
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
           View itemView = inflater.inflate(LAYOUT_ITEM_ID, parent, false);
           final RecycleHolder holder =  new RecycleHolder(itemView);

           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    log_d("itemView onClick");
                    // Returns the Adapter position of the item represented by this ViewHolder
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
        public void onBindViewHolder(RecycleHolder holder, int position) {
    //  // Replace the contents of a view (invoked by the layout manager)
            String text = mDataset.get(position);
            holder.setText(text);
        } // onBindViewHolder



    /*
     * == getItemCount ==
    * Returns the total number of items in the data set held by the adapter
     */ 
        @Override
        public int getItemCount() {
            return mDataset.size();
        } // getItemCount




    /**
     * notifyItemClick
     */
    private void notifyItemClick( int position, String codename ) {
                    log_d("nnotifyItemClick");
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

} // class RecycleAdapter

