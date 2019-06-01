Camera 211 Docs - Android Samples
===============

Auto Focus using  Camera2 API <br/>

### correspondence between xy coordinates of camera sensor and view <br/>

the camera sensor is in landscape orientation and is rotating relative to the device <br/>
the camera sensor will be portrait for portrait views, so
the image of the camera sensor is projected without rotating. <br/>
However, the xy axis of the camera sensor is rotated with respect to the view. <br/>
the xy coordinates of the  camera sensor and the view are as shown in the figure below. <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera211/docs/correspondence_xy_coordinates_of_camera_sensor_and_view.png" width="300" /><br/>

the equation view xy coordinates to sensor xy coordinates is as follows. <br/>
xy coordinate is ratio to each side <br/>

```
sensorX = viewY
 sensorY = 1- viewX
```

### Reference <br/>
- https://developer.android.com/reference/android/hardware/camera2/package-summary
