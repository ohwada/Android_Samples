/**
 * Cloud Vision Sample
 *  VisionClient
 * web detection with remote image
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision3;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


import com.google.api.client.extensions.android.http.AndroidHttp;
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

import com.google.api.services.vision.v1.model.ImageSource;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import  jp.ohwada.android.cloudvision3.util.VisionClientBase;


/**
 *  class VisionClient
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class VisionClient  extends VisionClientBase {

    // debug
    private final static String TAG_SUB = "VisionClient";


/**
  * Url of remote image
 */	 
    private String mImageUrl;


/**
  * constractor 
 */	    
public VisionClient( Activity activity  ) {
        super(activity  ); 
} 


/** 
 *  callCloudVisionByUrl
 */
public void callCloudVisionByUrl(String imageUrl, VisionCallback callback ) {

        mImageUrl = imageUrl;
        mCallback = callback;

        Vision.Images.Annotate annotate = prepareAnnotationRequest();

        AsyncTask<Object, Void, AnnotateImageResponse> visionTask = new VisionTask(mActivity, annotate);
        visionTask.execute();

} // callCloudVisionByUrl


/**
 * createAnnotateImageRequestList
 */
    @Override
protected List<AnnotateImageRequest> createAnnotateImageRequestList() {

    List<AnnotateImageRequest> list = new ArrayList<AnnotateImageRequest>();
    AnnotateImageRequest annotateImageRequest = createAnnotateImageRequestByUrl(mImageUrl);
    list.add(annotateImageRequest);
    return list;
} // createAnnotateImageRequestList


/**
 * createAnnotateImageRequestByUrl
 */
private AnnotateImageRequest createAnnotateImageRequestByUrl(String imageUrl) {

    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

    // create Image
    Image image = crateImageByUrl(imageUrl);
    annotateImageRequest.setImage(image);

    // add the features we want
    List<Feature> features = createFeatureList();
    annotateImageRequest.setFeatures(features);

    return annotateImageRequest;
} // createAnnotateImageRequestByUrl


/**
 * crateImageByUrl
 */ 
private Image crateImageByUrl(String imageUrl) {

         // create ImageSource
        ImageSource source = crateImageSourceByUrl(imageUrl);

        // create the Image
        Image image = new Image();
        image.setSource(source);

        return image;
} // crateImageByUrl


/**
 * crateImageSourceByUrl
 */
private ImageSource crateImageSourceByUrl(String imageUrl) {
        ImageSource source = new ImageSource();
        source.setImageUri(imageUrl);
        return source;
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class VisionClient
