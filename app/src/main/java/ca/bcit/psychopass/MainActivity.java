package ca.bcit.psychopass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements myLocationUtil.myLocationCallback {

    private WebView webView;

    public static final String GOOGLE_MAP_URL = "https://www.google.com/maps/";

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
        webView.loadUrl("https://www.google.com/maps/place/Maple+Ridge,+BC/@49.2599033,-122.6800957,11z/data=!3m1!4b1!4m5!3m4!1s0x5485d3614f013ecb:0x47a5c3ea30cde8ea!8m2!3d49.2193226!4d-122.5983981");
    }

    @Override
    public void onLocationChange(Location location) {
        double la = location.getLatitude();
        double lon = location.getLongitude();
        Toast.makeText(this, la + ":" + lon, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!locationRegistered){
            locationRegistered = myLocationUtil.registerCallback(this, this);
        }
    }
}
