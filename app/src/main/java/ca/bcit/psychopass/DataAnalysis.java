package ca.bcit.psychopass;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class DataAnalysis {
    private double Longitude;
    private double Latitude;
    private List<Crime> crimeList;

    private static final double RADIUS_LONGTITUDE = 0.005;
    private static final double RADIUS_LATITUDE = 0.0025;


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

    public List<Crime> getNearbyCrime() {
        List<Crime> tempList = new ArrayList<Crime>();
        for(int i=0; i<crimeList.size(); ++i){
            Crime c = crimeList.get(i);
            if(abs(c.getLongitude() - this.Longitude) < RADIUS_LONGTITUDE && abs(c.getLatitude() - this.Latitude) < RADIUS_LATITUDE){
                tempList.add(c);
            }
        }

        return tempList;
    }

}
