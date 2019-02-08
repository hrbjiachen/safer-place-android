package ca.bcit.psychopass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class myLocationUtil {

    public interface myLocationCallback{
        void onLocationChange(Location location);
    }

    public static final int UPDATE_INTERVAL = 10000;

    public static final int FASTEST_INTERVAL = 2000;

    private static myLocationUtil instance = new myLocationUtil();

    public myLocationUtil(){

    }

    private boolean checkPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(activity);
            return false;
        }
    }

    private void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

//    @SuppressLint("MissingPermission")
//    public static void registerCallback(final myLocationCallback mCallback, Activity activity){
//        // Create LocationSettingsRequest object using location request
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(instance.mLocationRequest);
//        LocationSettingsRequest locationSettingsRequest = builder.build();
//
//        // Check whether location settings are satisfied
//        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
//        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
//        settingsClient.checkLocationSettings(locationSettingsRequest);
//
//        if(instance.checkPermissions(activity)){
//            LocationCallback callback = new LocationCallback(){
//                @Override
//                public void onLocationResult(LocationResult locationResult){
//                    mCallback.onLocationChange(locationResult.getLastLocation());
//                }
//            };
//
//            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//            getFusedLocationProviderClient(activity)
//                    .requestLocationUpdates(instance.mLocationRequest, callback, Looper.myLooper());
//        }
//    }

    @SuppressLint("MissingPermission")
    public static boolean registerCallback(final myLocationCallback mCallback, final Activity activity){
        if(instance.checkPermissions(activity)) {

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(provider, 5000, 1000, new LocationListener() {
                public void onLocationChanged(Location location) {
                    mCallback.onLocationChange(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            });
            return true;
        }
        return false;
    }
}
