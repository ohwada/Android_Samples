/**
 * RecycleView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewheader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * class SampleAdapter
 * original : https://gist.github.com/willblaschko/1113be1eaff048a6ed14
 */
public class SampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    	// debug
    	private final static String TAG_SUB = "SampleAdapter";


    // generic layout
    private static final int GENERIC_LAYOUT_ID = R.layout.list_generic;
    private static final int GENERIC_TEXT_ID = R.id.TextView_generic_text;


    // type
    public static final int TYPE_HEADER = 111;
    public static final int TYPE_FOOTER = 222;
    public static final int TYPE_ITEM = 333;


    private Context mContext;

    // items
    List<String> mItems = new ArrayList<>();
    // headers
    List<View> mHeaders = new ArrayList<>();
    // footers
    List<View> mFooters = new ArrayList<>();


    /*
     * == constractor ==
     */ 
    public  SampleAdapter(Context context) {
        super();
        log_d("SampleAdapter");
        mContext = context;
     } // SampleAdapter



/**
 * == onCreateViewHolder ==
 */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        log_d("onCreateViewHolder: " + type);
        //if our position is one of our mItems (this comes from getItemViewType(int position) below)
        if(type == TYPE_ITEM) {
// TextView cannot be cast to FrameLayout
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(GENERIC_LAYOUT_ID, viewGroup, false);
            return new GenericViewHolder(view);
            //else we have a header/footer
        }else{
            //create a new framelayout, or inflate from a resource
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderFooterViewHolder(frameLayout);
        }

    } //  onCreateViewHolde


/**
 * == onBindViewHolder ==
 */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        log_d("onBindViewHolder: " + position);
        //check what type of view our position is
        if(position < mHeaders.size()){
            View v = mHeaders.get(position);
            //add our view to a header view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        }else if(position >= mHeaders.size() + mItems.size()){
            View v = mFooters.get(position-mItems.size()-mHeaders.size());
            //add oru view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        }else {
            //it's one of our mItems, display as required
            prepareGeneric((GenericViewHolder) vh, position-mHeaders.size());
        }

    } // onBindViewHolder


/**
 * == getItemCount ==
 */
    @Override
    public int getItemCount() {
            log_d("getItemCount");
        //make sure the adapter knows to look for all our mItems, mHeaders, and mFooters
        return mHeaders.size() + mItems.size() + mFooters.size();
    } // getItemCount



/**
 * == getItemViewType ==
 */
    @Override
    public int getItemViewType(int position) {
            log_d("getItemViewType: " + position);
        //check what type our position is, based on the assumption that the order is mHeaders > mItems > mFooters
        if(position < mHeaders.size()){
            return TYPE_HEADER;
        }else if(position >= mHeaders.size() + mItems.size()){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    } // getItemViewType


/**
 * prepareHeaderFooter
 */
    private void prepareHeaderFooter(HeaderFooterViewHolder vh, View view){
            log_d("prepareHeaderFooter");
        //empty out our FrameLayout and replace with our header/footer
        vh.base.removeAllViews();
        vh.base.addView(view);
    } // prepareHeaderFooter


/**
 * prepareGeneric
 */
    private void prepareGeneric(GenericViewHolder vh, int position){
            log_d("prepareGeneric: " + position);
        //do whatever we need to for our other type
        TextView textView = (TextView) vh.itemView.findViewById(GENERIC_TEXT_ID);
        String text = mItems.get(position);
        textView.setText(text);
    } // prepareGeneric


/**
 * addHeaderLayout
 */
    public void addHeaderLayout(int res_id) {
        View view = inflateLayout(res_id);
        addHeader( view);
} // addHeaderLayout


/**
 * addFooterLayout
 */
    public void addFooterLayout(int res_id) {
        View view = inflateLayout(res_id);
        addFooter( view);
} // addFooterLayout


/**
 * inflateLayout
 */
    public View inflateLayout(int res_id) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View view =  inflater.inflate(res_id, null);
    return view;
} // inflateLayout



/**
 * add a header to the adapter
 */
    public void addHeader(View header){
            log_d("addHeader");
        if(!mHeaders.contains(header)){
            mHeaders.add(header);
            //animate
            notifyItemInserted(mHeaders.size() - 1);
        }
    } // addHeader


/**
 * remove a header from the adapter
 */
    public void removeHeader(View header){
        if(mHeaders.contains(header)){
            log_d("removeHeader");
            //animate
            notifyItemRemoved(mHeaders.indexOf(header));
            mHeaders.remove(header);
            if(header.getParent() != null) {
                ((ViewGroup) header.getParent()).removeView(header);
            }
        }
    } // removeHeader


/**
 * add a footer to the adapter
 */
    public void addFooter(View footer){
            log_d("addFooter");
        if(!mFooters.contains(footer)){
            mFooters.add(footer);
            //animate
            notifyItemInserted(mHeaders.size()+mItems.size()+mFooters.size()-1);
        }
    } // addFooter


/**
 * remove a footer from the adapter
 */
    public void removeFooter(View footer){
            log_d("emoveFooter");
        if(mFooters.contains(footer)) {
            //animate
            notifyItemRemoved(mHeaders.size()+mItems.size()+mFooters.indexOf(footer));
            mFooters.remove(footer);
            if(footer.getParent() != null) {
                ((ViewGroup) footer.getParent()).removeView(footer);
            }
        }
    } // removeFooter


    /*
     * addAll
     */ 
    public  void addAll( List<String> list ) {
        log_d("addAll");
        this.mItems = list;
} // addAll


    /*
     * add
     */ 
    public  void add( String str ) {
        log_d("add");
        this.mItems.add(str);
} // add



/**
 * === class GenericViewHolder ===
 */
    public    class GenericViewHolder extends RecyclerView.ViewHolder {

        FrameLayout base;

        /**
         * constractor
         */
        public GenericViewHolder(View itemView) {
            super(itemView);
            log_d("GenericViewHolder");
            this.base = (FrameLayout) itemView;
        } // GenericViewHolder

} // GenericViewHolder


/**
 * === class HeaderFooterViewHolder ===
 * our header/footer RecyclerView.ViewHolder is just a FrameLayout
 */
    public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder{

        FrameLayout base;

        /**
         * constractor
         */
        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
            log_d("HeaderFooterViewHolder");
            this.base = (FrameLayout) itemView;
        } // HeaderFooterViewHolder

    } // class HeaderFooterViewHolder



 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // SampleAdapter

