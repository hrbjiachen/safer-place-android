package ca.bcit.psychopass;

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

        List<Crime> originalList = parseLocalJSON();
        Double Longitude = (Double)getIntent().getExtras().get("Longitude");
        Double Latitude = (Double)getIntent().getExtras().get("Latitude");

        DataAnalysis d = new DataAnalysis(Longitude,Latitude,originalList);
        crimeModels = d.getNearbyCrime();
        CrimeListAdaptor adapter = new CrimeListAdaptor(crimeModels, getApplicationContext());
        listView.setAdapter(adapter);

    }

    public List<Crime> parseLocalJSON(){

        String jsonStr;
        List<Crime> crimeList = new ArrayList<Crime>();

        try {
            InputStream is = getAssets().open("Property_Crimes.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonStr = new String(buffer, "UTF-8");

            myJsonUtil ut = new myJsonUtil(CrimeListActivity.this, getApplicationContext());
            crimeList = ut.getAllCrimeObj(jsonStr);

        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file:" + e.getMessage());
        } finally {
            return crimeList;
        }
    }
}
