package ca.bcit.psychopass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyLocationService extends Service {

    public static boolean isRunning = false;

    private static final String TAG = "BACKGROUNDLOCATION";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private LocationManager mLocationManager = null;
    private final IBinder mBinder = new LocationBinder();

    private Map<String, LocationCallback> callbackMap = new HashMap<>();

    public interface LocationCallback {
        void onCallback(Location location);
    }

    public class LocationBinder extends Binder {
        MyLocationService getService() {
            return MyLocationService.this;
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);

            for(Map.Entry<String, LocationCallback> entry : callbackMap.entrySet()){
                entry.getValue().onCallback(location);
            }

            DataAnalysis da = new DataAnalysis(location.getLongitude(),location.getLatitude(),MyJsonUtil.crimeList);
            if(da.isDangerZone()){
                sendNotification();
            }
            mLastLocation.set(location);
        }

        public void sendNotification() {
            Intent intent = new Intent(MyLocationService.this,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyLocationService.this, 0, intent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyLocationService.this)
                    .setSmallIcon(R.drawable.ic_warning)
                    .setContentTitle("Warning")
                    .setVibrate(new long[]{1000,1000,1000})
                    .setContentIntent(pendingIntent)
                    .setContentText("You have entered a dangrous zone. Be careful!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Notification notification;
            notification = mBuilder
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //to show content on lock screen
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            NotificationManager notificationManager = (NotificationManager) MyLocationService.this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void removeCallback(Class<? extends Context> classType) {
        callbackMap.remove(classType.getName());
    }

    public void registerCallback(Class<? extends Context> classType, LocationCallback cb){
        String className = classType.getName();
        callbackMap.put(className, cb);
    }

    public Location getLastLocation(){
        return mLocationListeners[0].mLastLocation;
    }

}
