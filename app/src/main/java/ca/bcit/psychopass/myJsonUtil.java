package ca.bcit.psychopass;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class myJsonUtil {
    private static final String TAG = "myJsonUtil";

    Context mContext;
    Activity v;


    public myJsonUtil(Activity v, Context context) {
        this.mContext=context;
        this.v = v;
    }

    public List<Crime> getAllCrimeObj(String jsonStr) {
        List<Crime> crimeList = new ArrayList<Crime>();

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
                String ReportedDateText = properties.getString("ReportedDateText");

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
                crime.setReportedDateText(ReportedDateText);

                // adding contact to contact list
                crimeList.add(crime);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
            v.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,
                            "Json parsing error: " + e.getMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

        } finally {
            return crimeList;
        }
    }

}
