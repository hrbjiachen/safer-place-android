package ca.bcit.psychopass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Crime {
    private double Longitude;

    private double Latitude;

    private int OBJECTID;

    private String FileNumber;

    private String OccuranceYear;

    private String ReportedDate;

    private Date ReportedTime;

    private String ReportedWeekday;

    private String Offense;

    private String OffenseCategory;

    private String HouseNumber;

    private String StreetName;

    private String City;

    private String ReportedDateText;

    private String ReportedTimeText;

    public void setLongitude(double Longitude){
        this.Longitude = Longitude;
    }
    public double getLongitude(){
        return this.Longitude;
    }

    public void setLatitude(double Latitude){
        this.Latitude = Latitude;
    }
    public double getLatitude(){
        return this.Latitude;
    }

    public void setOBJECTID(int OBJECTID){
        this.OBJECTID = OBJECTID;
    }
    public int getOBJECTID(){
        return this.OBJECTID;
    }
    public void setFileNumber(String FileNumber){
        this.FileNumber = FileNumber;
    }
    public String getFileNumber(){
        return this.FileNumber;
    }
    public void setOccuranceYear(String OccuranceYear){
        this.OccuranceYear = OccuranceYear;
    }
    public String getOccuranceYear(){
        return this.OccuranceYear;
    }
    public void setReportedDate(String ReportedDate){
        this.ReportedDate = ReportedDate;
    }
    public String getReportedDate(){
        return this.ReportedDate;
    }
    public void setReportedTime(Date ReportedTime){
        this.ReportedTime = ReportedTime;
    }
    public Date getReportedTime(){
        return this.ReportedTime;
    }
    public void setReportedWeekday(String ReportedWeekday){
        this.ReportedWeekday = ReportedWeekday;
    }
    public String getReportedWeekday(){
        return this.ReportedWeekday;
    }
    public void setOffense(String Offense){
        this.Offense = Offense;
    }
    public String getOffense(){
        return this.Offense;
    }
    public void setOffenseCategory(String OffenseCategory){
        this.OffenseCategory = OffenseCategory;
    }
    public String getOffenseCategory(){
        return this.OffenseCategory;
    }
    public void setHouseNumber(String HouseNumber){
        this.HouseNumber = HouseNumber;
    }
    public String getHouseNumber(){
        return this.HouseNumber;
    }
    public void setStreetName(String StreetName){
        this.StreetName = StreetName;
    }
    public String getStreetName(){
        return this.StreetName;
    }
    public void setCity(String City){
        this.City = City;
    }
    public String getCity(){
        return this.City;
    }
    public void setReportedDateText(String ReportedDateText){
        this.ReportedDateText = ReportedDateText;
    }
    public String getReportedDateText(){
        return this.ReportedDateText;
    }
    public void setReportedTimeText(String ReportedTimeText){
        this.ReportedTimeText = ReportedTimeText;
    }
    public String getReportedTimeText(){
        return this.ReportedTimeText;
    }
}

