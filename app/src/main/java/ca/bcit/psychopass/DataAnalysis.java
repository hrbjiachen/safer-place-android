package ca.bcit.psychopass;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static java.lang.Math.abs;

public class DataAnalysis {
    private double Longitude;
    private double Latitude;
    private List<Crime> crimeList;

    private static final double RADIUS_LONGTITUDE = 0.0005;
    private static final double RADIUS_LATITUDE = 0.00025;


    DataAnalysis(){
        Longitude = -122.6039533;
        Latitude = 49.2178709;
        Crime sample = new Crime();
        crimeList = new ArrayList<Crime>();
        crimeList.add(sample);
    }

    DataAnalysis(double Longitude, double Latitude, List<Crime> crimeList){
        this.Longitude = Longitude;
        this.Latitude = Latitude;
        this.crimeList = crimeList;
    }

    public ArrayList<Crime> getNearbyCrime() {
        ArrayList<Crime> tempList = new ArrayList<Crime>();
        for(int i=0; i<crimeList.size(); ++i){
            Crime c = crimeList.get(i);
            if(abs(c.getLongitude() - this.Longitude) < RADIUS_LONGTITUDE && abs(c.getLatitude() - this.Latitude) < RADIUS_LATITUDE){
                tempList.add(c);
            }
        }
        sortCimeList(tempList);

        return tempList;
    }

    public void sortCimeList(ArrayList<Crime> crimeList){

        Collections.sort(crimeList,new Comparator<Crime>() {
            @Override
            public int compare(Crime c1, Crime c2) {
                String date1 = c1.getReportedTime().substring(0,23);
                String date2 = c2.getReportedTime().substring(0,23);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                try {
                    Date d1 = df.parse(date1);
                    Date d2 = df.parse(date2);
                    return d2.compareTo(d1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        });

    }


    public boolean isDangerZone() {
        return true;
    }

}
