

# sms-listener-service
This is a plugin for getting OTP code via Restful API. Below is the list of use cases for using this plugin:
1. Get all SMS on mobile devices
2. Get the latest SMS that is sent to the mobile devices
3. Get a list of SMS by phone number

![android-demo](/docs/demo/android-demo.gif)
If you are interested in this project, please drop a ⭐!

## Usage

This plugin will start a netty server on an android mobile device and it will use port 8181. Below are the steps to install and run the plugin:
1. Download the [latest apk release](https://github.com/toilatester/sms-listener-service/releases)
1. Install plugin to an android device with grant all permission for it

> adb install -g sms-listener.apk

2. Start the plugin and run the sms-listener server on the Andorid device

> adb shell am start -n
> "com.toilatester.smslistener/com.toilatester.sms.listener.MainActivity"

3. Forward the request from adb server to sms-listener server (if you use a real device, you can get the device IP and don't need to run the command below). In case you have multiple devices on the machine, you should use the proxy server such as Nginx as the controller to get correctly SMS that is associated with the device or you can use the IP that is assigned to the device

> adb forward tcp:8181 tcp:8181

4. Get the SMS that contains OTP code with list of support APIs below:
- GET http://localhost:8181/all -> get all SMS
- GET http://localhost:8181/latest -> get latest SMS
- POST http://localhost:8181/byPhone -> find SMS by phone number with JSON post data format: {"phone": "192"}