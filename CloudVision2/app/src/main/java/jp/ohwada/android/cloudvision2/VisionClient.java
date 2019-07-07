/**
 * Cloud Vision Sample
 *  VisionClient
 * web detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.cloudvision2;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import com.google.api.services.vision.v1.model.WebDetection;
import com.google.api.services.vision.v1.model.WebLabel;
import com.google.api.services.vision.v1.model.WebPage;
import com.google.api.services.vision.v1.model.WebEntity;
import com.google.api.services.vision.v1.model.WebImage;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 *  class VisionClient
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class VisionClient  extends VisionClientBase {

    // debug
    private final static String TAG_SUB = "VisionClient";


/**
 * Request Feature Param
 */ 
    private static final String TYPE_WEB = "WEB_DETECTION";


/**
  * interface WebDetectionCallback
 */	
public interface WebDetectionCallback {
    void onPostExecute(WebDetection response);
    void onError(String error);
}


/**
  * WebDetectionCallback
 */
   private WebDetectionCallback  mWebDetectionCallback;


/**
  * constractor 
 */	    
public VisionClient( Activity activity  ) {
        super(activity  ); 
} 


/** 
 *  callWebDetection
 */
public void callWebDetection(Bitmap bitmap, WebDetectionCallback callback ) {
        mWebDetectionCallback = callback;

        // Do the real work in an async task, because we need to use the network anyway
                Vision.Images.Annotate annotate = prepareAnnotationRequest(bitmap);

                AsyncTask<Object, Void, AnnotateImageResponse> visionTask = new VisionTask(mActivity, annotate);
                visionTask.execute();

} // callWebDetection


/**
 * createFeatureList
 */ 
@Override
protected List<Feature> createFeatureList() {

    List<Feature> list = new ArrayList<Feature>();
    Feature feature = createWebDetectionFeature();
    list.add(feature);
    return list;

} // createFeatureList


/**
 * createWebDetectionFeature
 */ 
private Feature createWebDetectionFeature() {

                Feature feature = new Feature();
                feature.setType(TYPE_WEB);
                feature.setMaxResults(MAX_RESULTS);
        return feature;

} // createWebDetectionFeature


/** 
 *  callbackJsonError
 */
@Override
protected void  callbackJsonError(GoogleJsonError jsonError) {

        if(jsonError == null) return;

        String error = getJsonError( jsonError);
        if(error == null) return;
        log_d( "error: " + error );

        if (mWebDetectionCallback != null ) {
                mWebDetectionCallback.onError(error);
        }

} // callbackJsonError


/** 
 *  callbackResponse
 */
@Override
protected void callbackResponse(AnnotateImageResponse response) {
        if(response == null) return;
        WebDetection webDetection = convResponseToWebDetection(response);
        if(mWebDetectionCallback != null ) {
                // callback to Activity
                mWebDetectionCallback.onPostExecute(webDetection);
        }
} // callbackResponse


/** 
 *  convResponseToWebDetection
 */
private  WebDetection convResponseToWebDetection(AnnotateImageResponse response) {

        log_d("convResponseToWebDetection: " + response.toString());
        WebDetection webDetection 
        = response.getWebDetection();
        if (webDetection == null)  return null;
        log_d("webDetection:" + webDetection.toString());
        return webDetection;        
} // convResponseToWebDetection


/**
 * convWebDetectionToString
 */ 
public String convWebDetectionToString(WebDetection webDetection) {

        if(webDetection == null) return null;

        List<WebLabel>	bestGuessLabels = webDetection.getBestGuessLabels();
        List<WebPage>	pagesWithMatchingImages = webDetection.getPagesWithMatchingImages();
        List<WebEntity> webEntities = webDetection.getWebEntities();
        List<WebImage>	 fullMatchingImages = webDetection.getFullMatchingImages();
        List<WebImage>	 partialMatchingImages = webDetection.getPartialMatchingImages();
        List<WebImage> visuallySimilarImages = webDetection.getVisuallySimilarImages();


        StringBuilder sb = new StringBuilder();


        sb.append("bestGuessLabels: ");
        sb.append(LF);
        String str_bestGuessLabels = convWebLabelsToString(bestGuessLabels);
        sb.append(str_bestGuessLabels);
        sb.append(LF);


        sb.append("webEntities: ");
        sb.append(LF);
        String str_webEntities = convWebEntitiesToString(webEntities);
        sb.append(str_webEntities);
        sb.append(LF);


        sb.append("pagesWithMatchingImages: ");
        sb.append( convListToString(pagesWithMatchingImages));
        sb.append(LF);


        sb.append("fullMatchingImages: ");
        sb.append( convListToString(fullMatchingImages));
        sb.append(LF);


        sb.append("partialMatchingImages: ");
        sb.append( convListToString(partialMatchingImages));
        sb.append(LF);


        sb.append("visuallySimilarImages: ");
        sb.append( convListToString(visuallySimilarImages));
        sb.append(LF);


        return sb.toString();
} // convWebDetectionToString


/**
 * getWebItemList
 */ 
public List<WebItem>  getWebItemList(WebDetection webDetection) {

        List<WebPage>	pagesWithMatchingImages = webDetection.getPagesWithMatchingImages();
        List<WebImage>	 fullMatchingImages = webDetection.getFullMatchingImages();
        List<WebImage>	 partialMatchingImages = webDetection.getPartialMatchingImages();
        List<WebImage> visuallySimilarImages = webDetection.getVisuallySimilarImages();

        // WebItem List
        List<WebItem> webItemList = new ArrayList<WebItem>();

        if (pagesWithMatchingImages != null ) {
            webItemList 
            = getWebItemlListFromWebPageList(pagesWithMatchingImages);
        }

        // ImageUrlList
        List<String> imageUrlList = new ArrayList<String>();

        if (fullMatchingImages != null ) {
            List<String> list1 = getImageUrlListFromWebImageList(fullMatchingImages);
            imageUrlList = addUniqList(imageUrlList, list1);
        }
        if (partialMatchingImages != null ) {
            List<String>list2 = getImageUrlListFromWebImageList(partialMatchingImages);
            imageUrlList = addUniqList(imageUrlList, list2);
        }
        if (visuallySimilarImages != null ) {
            List<String> list3 = getImageUrlListFromWebImageList(visuallySimilarImages);
            imageUrlList = addUniqList(imageUrlList, list3);
        }

        for(String imageUrl: imageUrlList) {
                webItemList.add( new WebItem(imageUrl) );
        }

        return webItemList;

} // getWebItemList


/**
 *  addUniqList
 */ 
private List<String> addUniqList(List<String> list1, List<String>list2) {

        Set<String> set = new HashSet<>(list1);
        for(String s: list2) {
            set.add(s);
        }
        List<String> list3 = new ArrayList<>(set);
        return list3;

} // addUniqList


/**
 * getWebItemlListFromWebPageList
 */ 
public List<WebItem>  getWebItemlListFromWebPageList(List<WebPage> webPageList) {

    List<WebItem> list = new ArrayList<WebItem>();

    for(WebPage webPage: webPageList) {
            WebItem webItem = getWebItemFromWebPage(webPage);
            list.add(webItem);
    }

    return list;
} // getWebItemlListFromWebPageList


/**
 * getWebItemFromWebPage
 */ 
public WebItem  getWebItemFromWebPage(WebPage webPage) {
    log_d("getWebItemFromWebPage:" + webPage.toString());

    String  pageTitle = webPage.getPageTitle();
    String  pageUrl = webPage.getUrl();
    String  imageUrl = getImageUrlFromWebPage(webPage);

    WebItem webItem = new WebItem(pageTitle, pageUrl, imageUrl);

    return webItem;
} // getWebItemFromWebPage


/**
 * getImageUrlFromWebPage
 */ 
public String  getImageUrlFromWebPage(WebPage webPage) {

    String imageUrl = null;

        List<WebImage>	 fullMatchingImages 
        = webPage.getFullMatchingImages();
        List<WebImage>	 partialMatchingImages 
        = webPage.getPartialMatchingImages();

    if(fullMatchingImages  != null) {
        List<String>  list1 = getImageUrlListFromWebImageList(fullMatchingImages);
        imageUrl = list1.get(0);
    } else if(partialMatchingImages  != null) {
        List<String>  list2 = getImageUrlListFromWebImageList(partialMatchingImages);
        imageUrl = list2.get(0);
    }

    return imageUrl;
} // getImageUrlFromWebPage


/**
 * getImageUrlList
 */ 
public List<String>  getImageUrlListFromWebImageList(List<WebImage> webImageList) {

        List<String> list = new ArrayList<String>();

    for(WebImage webImage: webImageList) {
        String url = webImage.getUrl();
        list.add( url );
    }
        return list;
} // getImageUrlList


/**
 * getVisuallySimilarImageList
 */ 
public List<String>  getVisuallySimilarImageList(WebDetection webDetection) {

        List<String> list = new ArrayList<String>();

        List<WebImage> visuallySimilarImages = webDetection.getVisuallySimilarImages();
        if(visuallySimilarImages == null) return list;

        for(WebImage webImage: visuallySimilarImages) {
            String url = webImage.getUrl();
            list.add( url );
        }
        return list;
} // getVisuallySimilarImageList


/**
 * convListToString
 */ 
private String convListToString( List<?> list) {
        int size = 0;
        if(list != null) {
            size = list.size();
        }
        String str = Integer.toString(size);
        return str;
} // convListToString


/**
 * convWebLabelsToString
 */ 
private String convWebLabelsToString(List<WebLabel> list) {

        if(list == null) return null;
        StringBuilder sb = new StringBuilder();

        for(WebLabel webLabel:	 list) {
            if (webLabel == null) continue;
            String label = webLabel.getLabel();
            sb.append(label);
            sb.append(LF);
        }

        return sb.toString();
} // convWebLabelsToString


/**
 * convWebEntitiesToString
 */ 
private String convWebEntitiesToString(List<WebEntity> list) {

        if(list == null) return null;
        StringBuilder sb = new StringBuilder();

        for(WebEntity webEntity:	 list) {
                if (webEntity == null) continue;
                String str = convWebEntityToString(webEntity);
                if (str == null) continue;
                sb.append(str);
                sb.append(LF);
        }

        return sb.toString();
} // convWebEntitiesToString


/**
 * convWebEntityToString
 */ 
private String convWebEntityToString(WebEntity webEntity) {
        if (webEntity == null) return null;
log_d("convWebEntityToString: " + webEntity.toString());
        String description = webEntity.getDescription();
        if(description == null) return null;
        float score = webEntity.getScore();
        String str = String.format(Locale.US, FORMAT_LABEL_SCORE, description, score);
        return str;
} // convWebEntityToString


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class VisionClient
