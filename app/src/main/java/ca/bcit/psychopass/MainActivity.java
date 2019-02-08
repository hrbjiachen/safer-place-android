package ca.bcit.psychopass;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements myLocationUtil.myLocationCallback {

    private WebView webView;
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();

    public static final String GOOGLE_MAP_URL = "https://www.google.com/maps/";
    public static final String SERVICE_URL = "https://opendata.arcgis.com/datasets/28c37c4693fc4db68665025c2874e76b_7.geojson";
    public static final String INITIAL_LOCATION =
        "https://www.google.com/maps/place/Maple+Ridge,+BC/@49.2599033,-122.6800957,11z/data=!3m1!4b1!4m5!3m4!1s0x5485d3614f013ecb:0x47a5c3ea30cde8ea!8m2!3d49.2193226!4d-122.5983981";

    private boolean locationRegistered = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        locationRegistered =  myLocationUtil.registerCallback(this, this);

        webView = findViewById(R.id.webview1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(INITIAL_LOCATION);

        //new GetCrimeData().execute();
    }

    public void onClickBtn(View v) {
        double testLongitude = -122.6039533;
        double testLatitude = 49.2178709;

        Intent intent = new Intent(MainActivity.this,CrimeListActivity.class);
        intent.putExtra("Longitude", testLongitude);
        intent.putExtra("Latitude", testLatitude);
        startActivity(intent);
    }

    @Override
    public void onLocationChange(Location location) {
        double la = location.getLatitude();
        double lon = location.getLongitude();
        //Toast.makeText(this, la + ":" + lon, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!locationRegistered){
            locationRegistered = myLocationUtil.registerCallback(this, this);
        }
    }

    //this is not used, but keep it for reference
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
                myJsonUtil ut = new myJsonUtil(MainActivity.this, getApplicationContext());

                //assign below list<Crime> to a global variable
                ut.getAllCrimeObj(jsonStr);
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
