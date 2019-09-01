/**
 * Cloud Vision Sample
 *  VisionClientBase
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision3.util;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import com.google.api.services.vision.v1.model.Status;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jp.ohwada.android.cloudvision3.Constant;
import jp.ohwada.android.cloudvision3.R;


/**
 *  class VisionClientBase
 *  original : https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
 */
public class VisionClientBase  {


    // debug
	protected final static boolean D = true;
    protected final static String TAG = "CloudVision";
    private final static String TAG_BASE = "VisionClientBase";


/**
 * API_KEY forCloudVision
 */ 
    protected static final String CLOUD_VISION_API_KEY = Constant.CLOUD_VISION_API_KEY;


/**
 * HTTP Header
 */ 
    protected static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    protected static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";


/**
 * Request Feature Param
 */ 
    protected static final String DETECTION_TYPE = "LABEL_DETECTION";
    protected static final int MAX_RESULTS = 10;


/**
 * String Format for Response
 */ 
    protected static final String FORMAT_LABEL_SCORE = "%s: %.3f";


/**
 * char
 */ 
    protected static final String LF = "\n";


/**
  * interface VisionCallback
 */	
public interface VisionCallback {
    void onPostExecute(String result);
    void onError(String error);
}


/**
  * Activity 
 */	 
	protected Activity mActivity;


/**
  * PackageName 
 */
    protected String mPackageName;


/**
  * SHA1 signature of this app
 */
    protected String mSignature;


/**
  * Bitmap for uploard
 */	 
   protected Bitmap mBitmap;


/**
  * VisionCallback
 */
    protected VisionCallback  mCallback;


/**
  * ProgressDialog
 */
    protected ProgressDialog mProgressDialog;


/**
  * ProgressDialog
 */
    protected VisionTask mVisionTask;


/**
  * constractor 
 */	    
public VisionClientBase( Activity activity  ) {
		mActivity = activity;
        PackageManager packageManager = activity.getPackageManager();
        mPackageName = activity.getPackageName();
        mSignature = PackageManagerUtils.getSignature(packageManager, mPackageName);
        setUpProgressDialog(activity);
} 


/** 
 *  cancel
 */
public void cancel() {
        log_base("cancel");
        if ( mVisionTask != null) {
                mVisionTask.cancel(true);
        }
        if( mProgressDialog != null) {
                mProgressDialog.dismiss();
        }
}


/** 
 *  setUpProgressDialog
 */
protected void setUpProgressDialog(Context context) {

        Resources res = context.getResources();
        String message = res.getString(R.string.progress_dialog_message);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

} // setUpProgressDialog


/** 
 *  callCloudVision
 */
public void callCloudVision(Bitmap bitmap, VisionCallback callback ) {

        mBitmap = bitmap;
        mCallback = callback;

        // Do the real work in an async task, because we need to use the network anyway
                Vision.Images.Annotate annotate = prepareAnnotationRequest();

        mVisionTask = new VisionTask(mActivity, annotate);
        mVisionTask.execute();

} // callCloudVision


/** 
 *  prepareAnnotationRequest
 */
protected Vision.Images.Annotate prepareAnnotationRequest()  {

        Vision vision = createVision();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();

        List<AnnotateImageRequest> annotateImageRequests = createAnnotateImageRequestList();
        batchAnnotateImagesRequest.setRequests(annotateImageRequests);

    Vision.Images.Annotate annotateRequest =
            null;
    try {
        annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        log_base( "created Cloud Vision request object, sending request");

        return annotateRequest;

} // prepareAnnotationRequest


/**
 * createVision
 */
protected Vision createVision() {

        Vision.Builder builder = createVisionBuilder();
        Vision vision = builder.build();
        return vision;

} // createVision


/**
 * createVision.Builder
 */
protected Vision.Builder createVisionBuilder() {

        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);

        VisionRequestInitializer requestInitializer = createVisionRequestInitializer() ;
        builder.setVisionRequestInitializer(requestInitializer);
        return  builder;

} // createVision.Builder


/**
 * createVisionRequestInitializer
 */
protected VisionRequestInitializer createVisionRequestInitializer() {

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {

                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest) {

                        try {
                            super.initializeVisionRequest(visionRequest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // PackageName
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, mPackageName);

                        // SHA1 signature
                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, mSignature);

                    }

                }; // VisionRequestInitializer

        return requestInitializer;
} // createVisionRequestInitializer


/**
 * createAnnotateImageRequestList
 */
protected List<AnnotateImageRequest> createAnnotateImageRequestList() {

    List<AnnotateImageRequest> list = new ArrayList<AnnotateImageRequest>();
    AnnotateImageRequest annotateImageRequest = createAnnotateImageRequest(mBitmap);
    list.add(annotateImageRequest);
    return list;
} // createAnnotateImageRequestList


/**
 * createAnnotateImageRequest
 */ 
protected AnnotateImageRequest createAnnotateImageRequest(Bitmap bitmap) {

        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // create the image
            Image base64EncodedImage = crateEncodedImage(bitmap);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            List<Feature> features = createFeatureList();
            annotateImageRequest.setFeatures(features);

        return annotateImageRequest;

} // createAnnotateImageRequest


/**
 * crateEncodedImage
 */ 
protected Image crateEncodedImage(Bitmap bitmap) {

            // create the image
            Image image = new Image();

            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands 
            // but Cloud Vision
            byte[] imageBytes = BitmapUtil.convJpegByteArray(bitmap);

            // Base64 encode the JPEG
            image.encodeContent(imageBytes);

            return image;

} // crateEncodedImage


/**
 * createFeatureList
 */ 
protected List<Feature> createFeatureList() {
log_base("createFeatureList");
    List<Feature> list = new ArrayList<Feature>();
    Feature labelDetection = createLabelDetectionFeature();
    list.add(labelDetection);
    return list;

} // createFeatureList


/**
 * createLabelDetectionFeature
 */ 
protected Feature createLabelDetectionFeature() {
log_base("createLabelDetectionFeature");
                Feature labelDetection = new Feature();
                labelDetection.setType(DETECTION_TYPE);
                labelDetection.setMaxResults(MAX_RESULTS);
        return labelDetection;

} // reateLabelDetectionFeature


/**
 * write into logcat
 */ 
protected void log_base( String msg ) {
	    if (D) Log.d( TAG, TAG_BASE + " " + msg );
} // log_base


/** 
 *  class VisionTask
 */
public  class VisionTask extends AsyncTask<Object, Void, AnnotateImageResponse> {


/**
  * WeakReference
 */	
        protected final WeakReference<Activity> mActivityWeakReference;


/**
  * Vision.Images.Annotate
 */	
        protected Vision.Images.Annotate mRequest;


/**
  * constractor 
 */	
public VisionTask(Activity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }


/** 
 *  onPreExecute
 */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        procPreExecute();
    }


/** 
 *  doInBackground
 */
        @Override
        protected AnnotateImageResponse doInBackground(Object... params) {
            log_base("doInBackground");
            AnnotateImageResponse response = sendRequest(mRequest);
            return response;
    } // doInBackground


/** 
 *  onPostExecute
 */
        protected void onPostExecute(AnnotateImageResponse response) {
                log_base("onPostExecute: ");
                boolean isCancelled = isCancelled();
                procPostExecute(response, isCancelled );
        } // onPostExecute


} // class VisionTask


/** 
 * procPreExecute
 */
protected void procPreExecute() {
        mProgressDialog.show();
} // procPreExecute


/** 
 *  procPostExecute
 */
protected void procPostExecute(AnnotateImageResponse response, boolean isCancelled ) {
        log_base(" procPostExecute");
        mProgressDialog.dismiss();
        if(isCancelled) {
            return;
        }
        callbackResponse(response);
} // procPostExecute


/** 
 *  sendRequest
 */
protected AnnotateImageResponse sendRequest(Vision.Images.Annotate request) {

            BatchAnnotateImagesResponse batchResponse = null;
            GoogleJsonError jsonError = null;
            try {
                log_base("created Cloud Vision request object, sending request");
                 batchResponse = request.execute();
            } catch (GoogleJsonResponseException e) {
                    jsonError = e.getDetails();
			        e.printStackTrace();
            } catch (IOException e) {
			        e.printStackTrace();
            }

            if (jsonError != null) {
                callbackJsonError(jsonError);
            }

            if(batchResponse == null) return null;
            log_base("batchResponse: " + batchResponse.toString() );

            AnnotateImageResponse response = null;
            List<AnnotateImageResponse> list 
                = batchResponse.getResponses();
            if((list != null)&&(list.size() > 0)) {
                        response = list.get(0);
            }
            return response;
} // sendRequest


/** 
 *  callbackJsonError
 */
protected void  callbackJsonError(GoogleJsonError jsonError) {

        if(jsonError == null) return;

        String error = getJsonError( jsonError);
        if(error == null) return;
        log_base( "error: " + error );

        if (mCallback != null ) {
                mCallback.onError(error);
        }

} // callbackJsonError


/** 
 *  getJsonError
 */
protected String  getJsonError(GoogleJsonError jsonError) {

        if(jsonError == null) return null;
        log_base("getJsonError: " + jsonError.toString() );

       Integer code = jsonError.getCode();
        String message = jsonError.getMessage();
        List<GoogleJsonError.ErrorInfo> list = jsonError.getErrors();
        String infos = getErrorInfoList( list );
        StringBuilder sb = new StringBuilder();
        sb.append("code: ");
        sb.append(code);
        sb.append(LF);
        sb.append(message);
        sb.append(LF);
        sb.append(infos);
        return sb.toString();

} // getJsonError



/** 
 *  getErrorInfoList
 */
protected String getErrorInfoList(List<GoogleJsonError.ErrorInfo> list ) {

        log_base("getErrorInfoList");
         StringBuilder sb = new StringBuilder();
        for(GoogleJsonError.ErrorInfo info: list) {
                if(list == null) continue;
                String str = getErrorInfo(info);
                if(str == null) continue;
                sb.append(str);

        } // for
        return sb.toString();

} // getErrorInfoList


/** 
 *  getErrorInfo
 */
protected String getErrorInfo(GoogleJsonError.ErrorInfo info) {

        if(info == null) return null;
        log_base("getErrorInfo");

        String domain = info.getDomain();
        String message = info.getMessage();
        String reason = info.getReason();
        StringBuilder sb = new StringBuilder();
        sb.append("domain: ");
        sb.append(domain);
        sb.append(LF);
        sb.append("reason: ");
        sb.append(reason);
        sb.append(LF);
        sb.append(message);
        sb.append(LF);
        return sb.toString();

} // getErrorInfo


/** 
 *  callbackResponse
 */
protected void callbackResponse(AnnotateImageResponse response) {

                if(response == null) return;
                log_base("callbackResponse: " + response.toString() );

                String result = convertResponseToString(response);
                String error = getResponseError(response);
                if(mCallback != null ) {
                        // callback to Activity
                        mCallback.onPostExecute(result);
                        if(error != null ) {
                                mCallback.onError(error);
                        }
                }
} // callbackResponse


/** 
 *  getResponseError
 */
protected  String getResponseError(AnnotateImageResponse response) {
    log_base("getResponseError");
    Status  status = response.getError();
    if(status == null ) return null;
    log_base( "getResponseError: status= " +  status.toString() );

    Integer code = status.getCode();
    String message = status.getMessage();
    String details = getStatusDetails(status);
    StringBuilder sb = new StringBuilder();
        sb.append("code: ");
        sb.append(code);
         sb.append(LF);
        sb.append("message: ");
        sb.append(message);
        sb.append(LF);
    if(details != null) {
        sb.append("details: ");
        sb.append(details);
        sb.append(LF);
    }
    return sb.toString();
}


/** 
 *  getStatusDetails
 */
protected  String getStatusDetails(Status status) {
    List<Map<String,Object>> list = status.getDetails();
    if(list == null) return null;
    StringBuilder sb = new StringBuilder();
    for(Map<String,Object> map: list) {
            String details = getMapDetails(map);
            if(details != null ) {
                    sb.append(details);
            }
    }
    sb.append(LF);
    return sb.toString();
}


/** 
 *  getMapDetails
 */
protected  String getMapDetails(Map<String,Object> map) {

        if(map == null) return null;
        Set<String> keySet = map.keySet();
        StringBuilder sb = new StringBuilder();

        for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                Object obj = map.get(key);
                sb.append(key);
                sb.append(": ");
                if(obj != null) {
                        sb.append(obj.toString());
                }
                sb.append(LF);
        } // for
        return sb.toString();
}

/** 
 *  convertResponseToString
 */
protected  String convertResponseToString(AnnotateImageResponse response) {
log_base("convertResponseToString: " + response.toString() );
        List<EntityAnnotation> labels = response.getLabelAnnotations();
        if (labels == null)  return null;
        return convertEntityAnnotationsToString( labels);

    } // convertResponseToString


/** 
 *  convertEntityAnnotationsToString
 */
protected  String convertEntityAnnotationsToString(List<EntityAnnotation> labels) {
log_base("convertEntityAnnotationsToString");
        if (labels == null) return null;

        StringBuilder message = new StringBuilder();

        for (EntityAnnotation label : labels) {
                String str_label = convertLabelToString(label);
                message.append(str_label);
                message.append(LF);
        }// for

        return message.toString();

} // convertEntityAnnotationsToString


/** 
 *  convertLabelToString
 */
protected String convertLabelToString(EntityAnnotation label) {

        float score = label.getScore();
        String description = label.getDescription();
        String str_label = String.format(Locale.US, FORMAT_LABEL_SCORE, description, score);
        return str_label;

} // convertLabelToString


} // class VisionClientBase
