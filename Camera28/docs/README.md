Camera 28 Docs - Android Samples
===============

Face Detection using  Camera2 API <br/>

### correspondence between xy coordinates of camera sensor and view <br/>

the camera sensor is in landscape orientation and is rotating relative to the device <br/>
the camera sensor will be portrait for portrait views, so
the image of the camera sensor is projected without rotating. <br/>
However, the xy axis of the camera sensor is rotated with respect to the view. <br/>
the xy coordinates of the  camera sensor and the view are as shown in the figure below. <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera28/docs/correspondence_xy_coordinates_of_camera_sensor_and_view.png" width="300" /><br/>

the equation to convert sensor xy coordinates to view xy coordinates is as follows. <br/>
xy coordinate is ratio to each side <br/>

```
viewX = 1- sensorY 
viewY = sensorX 
```

### Reference <br/>
- https://developer.android.com/reference/android/hardware/camera2/package-summary
- https://developer.android.com/reference/android/hardware/camera2/params/Face
- https://developers.google.com/android/reference/com/google/android/gms/vision/CameraSource

