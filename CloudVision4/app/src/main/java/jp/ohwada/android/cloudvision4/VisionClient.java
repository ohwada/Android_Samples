/**
 * Cloud Vision Sample
 * Face Detection
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision4;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;


import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Landmark;
import com.google.api.services.vision.v1.model.Position;
import com.google.api.services.vision.v1.model.Vertex;


import java.util.ArrayList;
import java.util.List;

import jp.ohwada.android.cloudvision4.util.VisionClientBase;


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
    private static final String TYPE_FACE = "FACE_DETECTION";

     private static final int MAX_FACE_RESULTS = 1;


/**
  * interface FaceCallback
 */	
public interface FaceCallback {
    void onPostExecute(FaceAnnotation response);
    void onError(String error);
}


/**
  * FaceCallback
 */
    private FaceCallback  mFaceCallback;


/**
  * constractor 
 */	    
public VisionClient( Activity activity  ) {
        super(activity  ); 
} 


/** 
 *  callFaceDetection
 */
public void callFaceDetection(Bitmap bitmap, FaceCallback callback ) {
        
        mBitmap = bitmap;
        mFaceCallback = callback;

        // Do the real work in an async task, because we need to use the network anyway
        Vision.Images.Annotate annotate = prepareAnnotationRequest();
        
        mVisionTask  = new VisionTask(mActivity, annotate);
        mVisionTask.execute();

} // callFaceDetection


/**
 * createFeatureList
 */ 
@Override
protected List<Feature> createFeatureList() {

    List<Feature> list = new ArrayList<Feature>();
    Feature feature = createFaceDetectionFeature();
    list.add(feature);
    return list;

} // createFeatureList


/**
 * createFaceDetectionFeature
 */ 
private Feature createFaceDetectionFeature() {

                Feature feature = new Feature();
                feature.setType(TYPE_FACE);
                feature.setMaxResults(MAX_FACE_RESULTS);
        return feature;

} // createFaceDetectionFeature


/** 
 *  callbackJsonError
 */
@Override
protected void  callbackJsonError(GoogleJsonError jsonError) {

        if(jsonError == null) return;

        String error = getJsonError( jsonError);
        if(error == null) return;
        log_d( "error: " + error );

        if (mFaceCallback != null ) {
                mFaceCallback.onError(error);
        }

} // callbackJsonError


/** 
 *  callbackResponse
 */
@Override
protected void callbackResponse(AnnotateImageResponse response) {

        if(response == null) return;
        // log_d( "callbackResponse: " + response.toString() );

        FaceAnnotation faceAnnotation 
        = getFaceAnnotation(response);
        String error = getResponseError(response);

            if(mFaceCallback != null ) {
                        // callback to Activity
                        mFaceCallback.onPostExecute(faceAnnotation);
                        if(error != null ) {
                                mFaceCallback.onError(error);
                        }
            }

} // callbackResponse


/**
 * getFaceAnnotation
 */
private FaceAnnotation getFaceAnnotation(AnnotateImageResponse response) {

        List<FaceAnnotation> faceAnnotations = response.getFaceAnnotations();
        if( faceAnnotations == null ) return null;
        if( faceAnnotations.size() == 0 ) return null;
        FaceAnnotation faceAnnotation = faceAnnotations.get(0);
        return faceAnnotation;
} // getFaceAnnotationv


/**
 *  convFaceToString
  */
public String convFaceToString(FaceAnnotation faceAnnotation) {

        if (faceAnnotation == null) return null;

         log_d( "convFaceToString: " + faceAnnotation.toString() );

    // emotion
    String joyLikelihood = faceAnnotation.getJoyLikelihood();
    String angerLikelihood = faceAnnotation.getAngerLikelihood();
    String sorrowLikelihood = faceAnnotation.getSorrowLikelihood();
    String surpriseLikelihood = faceAnnotation.getSurpriseLikelihood();

    String headwearLikelihood = faceAnnotation.getHeadwearLikelihood();
    String blurredLikelihood = faceAnnotation.getBlurredLikelihood();
    String underExposedLikelihood = faceAnnotation.getUnderExposedLikelihood();

    // face angle
    Float panAngle = faceAnnotation.getPanAngle();
    Float rollAngle = faceAnnotation.getRollAngle();
    Float tiltAngle = faceAnnotation.getTiltAngle();

    // confidence
    Float detectionConfidence = faceAnnotation.getDetectionConfidence();
    Float landmarkingConfidence = faceAnnotation.getLandmarkingConfidence();

    // BoundingPoly
    List<Vertex>  list_fd_bounding = convFaceToFdBoundingPoly(faceAnnotation);
    int fd_bounding_size = getListSize(list_fd_bounding);

    List<Vertex> list_bounding =  convFaceToBoundingPoly(faceAnnotation);
    int bounding_size = getListSize(list_bounding);

    // Landmark
    List<Landmark> list_landmark = convFaceToLandmarkList(faceAnnotation);
    int landmark_size = 0;
    if (list_landmark != null) {
         landmark_size = list_landmark.size();
    }

            StringBuilder sb = new StringBuilder();

            // emotion
            sb.append("joyLikelihood: ");
            sb.append(joyLikelihood);
            sb.append(LF);
            sb.append("angerLikelihood: ");
            sb.append(angerLikelihood);
            sb.append(LF);
            sb.append("sorrowLikelihood: ");
            sb.append(sorrowLikelihood);
            sb.append(LF);
            sb.append("surpriseLikelihood: ");
            sb.append(surpriseLikelihood);
            sb.append(LF);
            sb.append("headwearLikelihood: ");
            sb.append(headwearLikelihood);
            sb.append(LF);
            sb.append("blurredLikelihood: ");
            sb.append(blurredLikelihood);
            sb.append(LF);
            sb.append("underExposedLikelihood: ");
            sb.append(underExposedLikelihood);
            sb.append(LF);

            sb.append("underExposedLikelihood: ");
            sb.append(underExposedLikelihood);
            sb.append(LF);
            sb.append("underExposedLikelihood: ");
            sb.append(underExposedLikelihood);
            sb.append(LF);
            sb.append("underExposedLikelihood: ");
            sb.append(underExposedLikelihood);
            sb.append(LF);

            // face angle
            sb.append("panAngle: ");
            sb.append(panAngle);
            sb.append(LF);
            sb.append("rollAngle: ");
            sb.append(rollAngle);
            sb.append(LF);
            sb.append("tiltAngle: ");
            sb.append(tiltAngle);
            sb.append(LF);

             // confidence
            sb.append("detectionConfidence: ");
            sb.append(detectionConfidence);
            sb.append(LF);
            sb.append("landmarkingConfidence: ");
            sb.append(landmarkingConfidence);
            sb.append(LF);

            // BoundingPoly
            sb.append("FdBoundingPoly: ");
            sb.append(fd_bounding_size);
            sb.append(LF);
            sb.append("BoundingPoly: ");
            sb.append(bounding_size);
            sb.append(LF);

            // Landmark
            sb.append("Landmark: ");
            sb.append( landmark_size);
            sb.append(LF);
            return sb.toString();

} //  convFaceToString


/**
 *  convFaceToBoundingPoly
  */
public  List<Vertex> convFaceToBoundingPoly(FaceAnnotation faceAnnotation) {
    BoundingPoly boundingPoly = faceAnnotation.getBoundingPoly();
    List<Vertex> list = boundingPoly.getVertices();
    return list;

} // convFaceToBoundingPoly


/**
 *  convFaceToFdBoundingPointList
  */
public List<Point> convFaceToFdBoundingPointList(FaceAnnotation faceAnnotation) {
    List<Vertex> listVertex = convFaceToFdBoundingPoly( faceAnnotation);
    return convPointList(listVertex);
}


/**
 *  convFaceToFdBoundingPoly
  */
private List<Vertex> convFaceToFdBoundingPoly(FaceAnnotation faceAnnotation) {
    BoundingPoly boundingPoly = faceAnnotation.getFdBoundingPoly();
    List<Vertex> list = boundingPoly.getVertices();
    return list;

} // convFaceToFdBoundingPoly



/**
 * convPointList
 */ 
private List<Point> convPointList(List<Vertex> listVertex) {

        List<Point> listPoint = new ArrayList<Point>();
        int size = listVertex.size();
        int x = 0;
        int y = 0;
    try{
            for (int i=0; i<size; i++) {
                Vertex vertex = listVertex.get(i);
                        // NullPointerException may occur
                        x = vertex.getX();
                        y = vertex.getY();
                listPoint.add( new Point(x,y) );
            } // for
    } catch (NullPointerException e) {
            e.printStackTrace();
    } // try

    return listPoint;
}



/**
 *  getListSize
  */
private int getListSize(List<Vertex> list) {
    int size = 0;
    if (list != null) {
        size = list.size();
    }
    return size;
} // getListSize


/**
 *  convFaceToLandmarkList
  */
public List<Landmark> convFaceToLandmarkList(FaceAnnotation faceAnnotation) {

    List<Landmark> list = faceAnnotation.getLandmarks();
    return list;
}


/**
 * convLandmarkListToString
 */ 
private String convLandmarkListToString( List<Landmark> list) {

    StringBuilder sb = new StringBuilder();
    for(Landmark landmark: list) {
        String str = landmark.toString();
        sb.append(str);
    } // for
    return sb.toString();

} // convLandmarkListToString


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class VisionClient
