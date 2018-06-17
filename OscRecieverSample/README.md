OSC Reciver Sample  - Android Samples
===============

recieve OSC Message <br/>
with JavaOSC Library  <br/>

### Screenshot <br/>
display IP Address and Port <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/OscReciverSample/screenshot/screenshot_osc_reciver_main.png" width="300" /><br/>

listening Port, when click "Setup" button <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/OscReciverSample/screenshot/screenshot_osc_reciver_listening.png" width="300" /><br/>

### Usage <br/>

Example  <br/>
send OSC message from MAC to Android <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/_overview_osc_mac_to_android.png" width="600" /><br/>

STEP 1 : 
connect Android and MAC to the same WiFi Router <br/>

STEP 2 : setup on Android <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/OscReciverSample/screenshot/screenshot_osc_reciver_listening.png" width="300" /><br/>

STEP 3 : edit PureData patch <br/>
edit IP Address for Android <br/>
ex) 192.168.1.3 <br/>
PureData patch:  osc_send.pd <br/>
https://github.com/ohwada/Android_Samples/tree/master/tools/pure_data <br/>

STEP 4 : run PureData on MAC <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/pd_osc_send_pd.png" width="500" /><br/>

STEP 4 : send OSC Message from MAC, when click "send" button   <br/>
and Android recieve it <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/OscReciverSample/screenshot/screenshot_osc_reciver_recieved_123.png" width="300" /><br/>

### Reference <br/>
* https://github.com/hoijui/JavaOSC <br/>
