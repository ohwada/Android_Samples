/**
 * RSS Parser
 * 2018-04-10 K.OHWADA 
 */

package jp.ohwada.android.rssparsersample;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * ArticleAdapter
 * original : https://github.com/prof18/RSS-Parser/tree/master/app/src/main/java/com/prof/rssparser/example
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

   	// debug
    	private final static String TAG_SUB = "MainActivity";

  		public final static int LAYOUT_RESOURCE_ID = R.layout.list_item;

 // callback 
    private OnItemClickListener mListener;  

    private List<Article> mArticles;

   //  private int rowLayout;
    private Context mContext;
    // WebView articleView;

/**
 * callback interface
 */    
    public interface OnItemClickListener {
        public void onItemClick( Article article );
    } // interface

/**
 * callback
 */ 
    public void setOnItemClickListener( OnItemClickListener listener ) {
        log_d("setOnItemClickListener");
        mListener = listener;
    } // setOnItemClickListener


/**
 * constractor
 */
    public ArticleAdapter(List<Article> list, int rowLayout, Context context) {

        this.mArticles = list;
        this.mContext = context;
    } // ArticleAdapter


/**
 * == getItemId == 
 */
    @Override
    public long getItemId(int item) {
        // TODO Auto-generated method stub
        return item;
    } // getItemId



/**
 * == onCreateViewHolder == 
 */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(LAYOUT_RESOURCE_ID, viewGroup, false);
        return new ViewHolder(v);
    } // onCreateViewHolder


/**
 * == onBindViewHolder == 
 */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Article article = mArticles.get(position);
        if(article == null) return;

        // log_d(article.toString());
        Locale.setDefault(Locale.getDefault());
        Date date = article.getPubDate();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf = new SimpleDateFormat("dd MMMM yyyy");
        final String pubDateString = sdf.format(date);

        viewHolder.tv_title.setText(article.getTitle());

        //load the image. If the parser did not find an image in the article, it set a placeholder.
        Picasso.with(mContext)
                .load(article.getImage())
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(viewHolder.iv_image);

        viewHolder.tv_pubDate.setText(pubDateString);

        String categories = buildCategory(article);
        viewHolder.tv_category.setText(categories);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                                log_d("itemView onClick");
                    Article article = mArticles.get(position);
                    notifyItemClick( article );
             } //onClick

         }); // itemView.setOnClickListener

    } // onCreateViewHolder


/**
 * == getItemCount == 
 */
    @Override
    public int getItemCount() {

        return mArticles == null ? 0 : mArticles.size();

    } // getItemCount


/**
 * clearData
 */
    public void clearData() {
        if (mArticles != null) {
            mArticles.clear();
        }
    } // clearData


/**
 * addAllData
 */
    public void addAllData(List<Article> list) {
            if ((list != null)&&(list.size() > 0)) {
                mArticles = list;
            }
    } // clearData

/**
 * buildCategory
 */
private String buildCategory(Article article) {
        String categories = "";
        if (article == null) return "";

        List<String> list = article.getCategories();
        if (list == null)  return "";

            int size = list.size();
            String category = "";

            for (int i = 0; i < size; i++) {
                category = list.get(i);

                // at last
                if (i == (size - 1)) {
                    categories = categories + category;
                } else {
                    categories = categories + category + ", ";
                }
            }

        return categories;
} // buildCategory


    /**
     * notifyItemClick
     */
    private void notifyItemClick( Article article ) {
                    log_d("nnotifyItemClick");
           if ( mListener != null ) {
            mListener.onItemClick( article );
        } 
}	// notifyItemClick


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


/**
 * == class ViewHolder ==
 */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_pubDate;
        ImageView iv_image;
        TextView tv_category;

/**
 *  constractor
 */
        public ViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.TextView_item_title);
            tv_pubDate = (TextView) itemView.findViewById(R.id.TextView_item_pubDate);
            iv_image = (ImageView) itemView.findViewById(R.id.ImageView_item_image);
            tv_category = (TextView) itemView.findViewById(R.id.TextView_item_categories);
        } // ViewHolder

    } // class ViewHolder

} // class ArticleAdapter