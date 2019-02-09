package ca.bcit.psychopass;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CrimeListActivity extends AppCompatActivity {

    private static final String TAG = "CrimeListActivity";

    ListView listView;
    ArrayList<Crime> crimeModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_list);

        listView = findViewById(R.id.crimList);

        Double Longitude = (Double)getIntent().getExtras().get("Longitude");
        Double Latitude = (Double)getIntent().getExtras().get("Latitude");

        DataAnalysis d = new DataAnalysis(Longitude,Latitude, MyJsonUtil.crimeList);
        crimeModels = d.getNearbyCrime();
        CrimeListAdaptor adapter = new CrimeListAdaptor(crimeModels, getApplicationContext());

        Location curlocation = new Location("service Provider");
        curlocation.setLatitude(Latitude);
        curlocation.setLongitude(Longitude);

        adapter.setCurLocation(curlocation);
        listView.setAdapter(adapter);
    }


}
