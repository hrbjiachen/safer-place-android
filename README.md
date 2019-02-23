# Project PsychoPass
New branch, refactored using google map view

##Installing
*get your Google Map api key at below
```
https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=D1:D9:F7:A1:3D:F0:2E:E1:4A:C7:7A:96:6C:60:F7:18:54:28:77:6B%3Bca.bcit
```
* Using emulator with Google Play Service

* Once pulled the branch, add a XML Resouce file under *res/values/google_maps_api.xml* with below code
```
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_API_KEY</string>
</resources>
```

*Run the project