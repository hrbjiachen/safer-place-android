package ca.bcit.psychopass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    private MyLocationService locationService;
    private boolean isBoundLocation = false;
    private Timer timer = new Timer();
    private Location curLocation;

    public static final String GOOGLE_MAP_URL = "https://www.google.com/maps/@";
    public static final String SERVICE_URL = "https://opendata.arcgis.com/datasets/28c37c4693fc4db68665025c2874e76b_7.geojson";
    public static final String INITIAL_LOCATION =
        "https://www.google.com/maps/place/Maple+Ridge,+BC/@49.2599033,-122.6800957,11z/data=!3m1!4b1!4m5!3m4!1s0x5485d3614f013ecb:0x47a5c3ea30cde8ea!8m2!3d49.2193226!4d-122.5983981";

    public boolean permissionRequested = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationServiceCheck();
        setInitialWebView();
        initializeSettingIcon();
        initializeLocationIcon();

        MyJsonUtil jsonUtil = new MyJsonUtil(MainActivity.this,getApplicationContext());
        jsonUtil.parseLocalJSON();

    }

    public void setInitialWebView() {
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.mapWebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    webView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    webView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.loadUrl(INITIAL_LOCATION);
    }

    public void initializeLocationIcon() {
        ImageView location = findViewById(R.id.locationImg);
        location.setClickable(true);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curLocation != null){
                    DecimalFormat df = new DecimalFormat("#.#######");
                    double Lat = Double.valueOf(df.format(curLocation.getLatitude()));
                    double Long = Double.valueOf(df.format(curLocation.getLongitude()));
                    Log.e(TAG, Lat + ", " + Long);

                    webView = findViewById(R.id.mapWebView);
                    webView.loadUrl(GOOGLE_MAP_URL + Lat + "," + Long + ",16z");
                }
            }
        });
    }

    public void initializeSettingIcon() {
        ImageView setting = findViewById(R.id.settingImg);
        setting.setClickable(true);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClickBtn(View v) {
        //testing coordinates
        //double testLongitude = -122.6039533;
        //double testLatitude = 49.2178709;

        Intent intent = new Intent(MainActivity.this,CrimeListActivity.class);
        webView = findViewById(R.id.mapWebView);
        String webUrl = webView.getUrl();

        String pattern = "\\@(-?[\\d\\.]*)\\,(-?[\\d\\.]*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(webUrl);

        if(m.find()){
            String[] coordinates = m.group(0).replace("@","").split(",");
            prepareInfoDisplay(coordinates);
        } else {
            Toast.makeText(MainActivity.this, R.string.errorLocation, Toast.LENGTH_LONG).show();
        }

    }

    public void prepareInfoDisplay(String[] coordinates) {
        final double curLong = Double.parseDouble(coordinates[1]);
        final double curLati = Double.parseDouble(coordinates[0]);

        LinearLayout layout = findViewById(R.id.infoLayout);
        DataAnalysis d = new DataAnalysis(curLong,curLati, MyJsonUtil.crimeList);

        int size = d.getNearbyCrime().size();
        if(layout.getChildCount() > 1) {
            layout.removeViewAt(1);
        }

        if(size == 0) {
            TextView tvInfo = findViewById(R.id.infoContainer);
            tvInfo.setText(R.string.sampleText);
            Toast.makeText(MainActivity.this, R.string.noCrimeData, Toast.LENGTH_LONG).show();
        } else {
            TextView tvInfo = findViewById(R.id.infoContainer);
            tvInfo.setText("Found " + size + " crimes nearby.");

            TextView textView = new TextView(this);
            textView.setText(R.string.viewDetail);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLUE);

            layout.addView(textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CrimeListActivity.class);
                    intent.putExtra("Longitude", curLong);
                    intent.putExtra("Latitude", curLati);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationServiceCheck();
        Log.e("checkingpoint", "resuming--------");
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionRequested = false;
        if(!isBoundLocation){
            Intent intent = new Intent(this, MyLocationService.class);
            isBoundLocation = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        Log.e("checkingpoint", "starting--------");
    }

    public void registerCallback(){
        MyLocationService.LocationCallback cb = new MyLocationService.LocationCallback() {
            @Override
            public void onCallback(Location location) {
                Log.e(TAG,location.getLatitude() + ":" + location.getLongitude());
                curLocation = location;
            }
        };
        locationService.registerCallback(MainActivity.class, cb);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBoundLocation) {
            locationService.removeCallback(MainActivity.class);
            unbindService(mConnection);
            isBoundLocation = false;
        }
    }

    private void locationServiceCheck(){
        Intent intent = new Intent(this, MyLocationService.class);
        if (hasAllPermission()) {

            timer.cancel();

            if(MyLocationService.isRunning)
                return;

            startService(intent);

        } else {
            if(MyLocationService.isRunning)
                stopService(intent);

            if(permissionRequested){

                timer.scheduleAtFixedRate(new TimerTask() {

                    synchronized public void run() {

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, R.string.requirePermission , Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(30));
            } else {
                requestForAllPermission();
                permissionRequested = true;
            }
        }
    }

    public boolean hasAllPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
    }
    public void requestForAllPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.VIBRATE },
                1);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyLocationService.LocationBinder binder = (MyLocationService.LocationBinder) service;
            locationService = binder.getService();
            isBoundLocation = true;
            registerCallback();
            curLocation = locationService.getLastLocation();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBoundLocation = false;
        }
    };

}
