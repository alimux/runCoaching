package dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Alexandre DUCREUX on 15/02/2017.
 */

public class DataHandling{

    private boolean isRunning;
    private boolean isFirstTime;

    private long time;
    private long timeStopped;

    private double distanceKm;
    private double distanceM;
    private  double currentSpeed;
    private double maxSpeed;

    private double elevation;
    private double hdop;
    private int sat;
    //coordinates
    private double currentLatitude;
    private double currentLongitude;

    //Trackpoints
    private ArrayList<TrackPoint> trackPointsList;

    //listener
    private onGPSServiceUpdate onGpsServiceUpdate;



    public DataHandling(){

        this.isRunning = false;
        this.distanceKm = 0;
        this.distanceM = 0;
        this.currentSpeed = 0;
        this.maxSpeed = 0;
        this.timeStopped = 0;
        this.trackPointsList = new ArrayList<>();
    }

    public interface onGPSServiceUpdate {
        void update();
    }

    public  DataHandling(onGPSServiceUpdate onGpsServiceUpdate){
        this();
        setOnGPSServiceUpdate(onGpsServiceUpdate);
    }

    public void update() {
        onGpsServiceUpdate.update();
    }

    public void setOnGPSServiceUpdate(onGPSServiceUpdate onGpsServiceUpdate){
        Log.i("AD","Assignation du Listener"+onGpsServiceUpdate);
        this.onGpsServiceUpdate = onGpsServiceUpdate;
    }
    public void addDistance(double distance){
        distanceM = distanceM + distance;
        distanceKm = distanceM / 1000f;
    }

    public SpannableString getDistance(){
        SpannableString s;
        if(distanceKm<1){
            s = new SpannableString(String.format(Locale.ENGLISH,"%.0f", distanceM)+ "m");
            s.setSpan(new RelativeSizeSpan(0.5f), s.length() -1, s.length(), 0);
        }
        else {
            s = new SpannableString(String.format(Locale.ENGLISH,"%.3f", distanceKm)+ "Km");
            s.setSpan(new RelativeSizeSpan(0.5f), s.length() -2, s.length(), 0);
        }
        return s;
    }

    public SpannableString getMaxSpeed(){
        SpannableString s = new SpannableString(String.format("%.0f", maxSpeed) + "Km/h");
        s.setSpan(new RelativeSizeSpan(0.5f), s.length() -4, s.length(), 0);
        return s;
    }

    public void setCurrentSpeed(double currentSpeed){
        this.currentSpeed = currentSpeed;
        if(currentSpeed > maxSpeed){
            maxSpeed = currentSpeed;
        }
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }

    public void setTimeStopped(long timeStopped) {
        this.timeStopped += timeStopped;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getHdop() {
        return hdop;
    }

    public void setHdop(double hdop) {
        this.hdop = hdop;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    /**
     * Methods which reccord temporary track data
     * @param lat
     * @param lon
     * @param time
     * @param elevation
     * @param speed
     * @param hdop
     * @param sat
     */
    public void recordTrackPoint(double lat, double lon, long time, double elevation, double speed, double hdop, int sat) {

        TrackPoint trkpt = new TrackPoint(lat, lon, elevation, time, speed, hdop, sat);
        this.trackPointsList.add(trkpt);
        for(TrackPoint trk : trackPointsList){
            Log.i("AD","arrayList : lat:"+trk.getLatitude()+" lon:"+trk.getLongitude());
        }
        Log.i("AD","nombre enregistrement tracklist dans le recording :"+trackPointsList.size());
        getTrackPointsList();
    }

    public void setTrackPointsList(ArrayList<TrackPoint> trackPointsList) {
        this.trackPointsList = trackPointsList;
    }

    public ArrayList<TrackPoint> getTrackPointsList() {
        Log.i("AD","nombre enregistrement tracklist getter :"+trackPointsList.size());
        return this.trackPointsList;
    }
}
