package ca.bcit.psychopass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity  {

    private WebView webView;
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    List<Crime> crimeList = new ArrayList<Crime>();

    public static final String GOOGLE_MAP_URL = "https://www.google.com/maps/";
    public static final String SERVICE_URL = "https://opendata.arcgis.com/datasets/28c37c4693fc4db68665025c2874e76b_7.geojson";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setupGPS();

        webView = findViewById(R.id.webview1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.google.com/maps/place/Maple+Ridge,+BC/@49.2599033,-122.6800957,11z/data=!3m1!4b1!4m5!3m4!1s0x5485d3614f013ecb:0x47a5c3ea30cde8ea!8m2!3d49.2193226!4d-122.5983981");

        new GetCrimeData().execute();
    }

    public void onClickBtn(View v) {
        double testLongitude = -122.6039533;
        double testLatitude = 49.2178709;
        DataAnalysis d = new DataAnalysis(testLongitude,testLatitude,crimeList);
        List<Crime> newList = d.getNearbyCrime();
        Toast.makeText(this, "Total number of crime nearby is " + newList.size(), Toast.LENGTH_LONG).show();
    }

    private void setupGPS(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 5000, 100, new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                //BigDecimal.valueOf(location.getLongitude()).setScale(7, RoundingMode.HALF_UP).doubleValue()
                Toast.makeText(MainActivity.this, longitude + " " + latitude, Toast.LENGTH_SHORT).show();
                String url = GOOGLE_MAP_URL + "@" + latitude + "," + longitude;
                Log.d("googlemap", url);
                webView.loadUrl("https://www.google.com/maps/@47.2093246,-122.5276514,17.04z");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        });
    }

    private class GetCrimeData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(SERVICE_URL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONObject dataObj = new JSONObject(jsonStr);
                    JSONArray crimeDataArray = dataObj.getJSONArray("features");

                    // looping through All countries
                    for (int i = 0; i < crimeDataArray.length(); i++) {
                        JSONObject geometry = crimeDataArray.getJSONObject(i).getJSONObject("geometry");
                        JSONObject properties = crimeDataArray.getJSONObject(i).getJSONObject("properties");

                        Double Longitude = geometry.getJSONArray("coordinates").getDouble(0);
                        Double Latitude = geometry.getJSONArray("coordinates").getDouble(1);
                        String City = properties.getString("City");
                        String OccuranceYear = properties.getString("OccuranceYear");
                        String ReportedTime = properties.getString("ReportedTime");
                        String ReportedWeekday = properties.getString("ReportedTime");
                        String StreetName = properties.getString("StreetName");
                        String Offense = properties.getString("Offense");
                        String OffenseCategory = properties.getString("OffenseCategory");

                        Crime crime = new Crime();

                        // adding each child node to HashMap key => value
                        crime.setLongitude(Longitude);
                        crime.setLatitude(Latitude);
                        crime.setCity(City);
                        crime.setOccuranceYear(OccuranceYear);
                        crime.setReportedTime(ReportedTime);
                        crime.setReportedWeekday(ReportedWeekday);
                        crime.setStreetName(StreetName);
                        crime.setOffense(Offense);
                        crime.setOffenseCategory(OffenseCategory);

                        // adding contact to contact list
                        crimeList.add(crime);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.e(TAG, "Load data complete!");
        }
    }

}
