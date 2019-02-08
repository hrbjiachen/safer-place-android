package ca.bcit.psychopass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    List<Crime> crimeList = new ArrayList<Crime>();

    public static final String GOOGLE_MAP_URL = "https://www.google.com/maps/";
    public static final String SERVICE_URL = "https://opendata.arcgis.com/datasets/28c37c4693fc4db68665025c2874e76b_7.geojson";
    public static final String INITIAL_LOCATION =
        "https://www.google.com/maps/place/Maple+Ridge,+BC/@49.2599033,-122.6800957,11z/data=!3m1!4b1!4m5!3m4!1s0x5485d3614f013ecb:0x47a5c3ea30cde8ea!8m2!3d49.2193226!4d-122.5983981";

    public boolean permissionRequested = false;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        locationServiceCheck();


        webView = findViewById(R.id.webview1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(INITIAL_LOCATION);

        //new GetCrimeData().execute();
//        parseLocalJSON();
    }

    public void onClickBtn(View v) {
        double testLongitude = -122.6039533;
        double testLatitude = 49.2178709;
        DataAnalysis d = new DataAnalysis(testLongitude,testLatitude,crimeList);
        List<Crime> newList = d.getNearbyCrime();
        Toast.makeText(this, "Total number of crime nearby is " + newList.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationServiceCheck();
    }

    private void locationServiceCheck(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(getBaseContext(), MyLocationService.class);
            startService(intent);
        } else {
            if(permissionRequested){
                Timer timer = new Timer();

                timer.scheduleAtFixedRate(new TimerTask() {

                    synchronized public void run() {

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "This App won't work without location permission." +
                                        "Please turn it on in Setting.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(30));
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                permissionRequested = true;
            }
        }
    }
    public void parseLocalJSON(){

        String jsonStr;

        try {
            InputStream is = getAssets().open("Property_Crimes.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonStr = new String(buffer, "UTF-8");

            parseJsonFromInputStream(jsonStr);

        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file:" + e.getMessage());
        }
    }

    public void parseJsonFromInputStream(String jsonStr) {
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
                parseJsonFromInputStream(jsonStr);
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
