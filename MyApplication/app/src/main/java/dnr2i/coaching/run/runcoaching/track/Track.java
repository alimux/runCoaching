package dnr2i.coaching.run.runcoaching.track;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by alexandre DUCREUX on 01/02/2017.
 * Class defining a Track
 */

public class Track {

    private ArrayList<TrackPoint> trackPoints;
    private TrackPoint currentPosition;
    private int segmentNumber;
    private double totalDistance;
    private long totalTime;
    private int segmentType;
    private ArrayList<TrackPoint> intermediatesTime;


    /**
     * Constructor
     * initialize an ArrayList of track points
     */
    public Track() {
        this.trackPoints = new ArrayList<TrackPoint>();
        this.segmentNumber = 0;
        this.totalDistance=0;
        this.totalTime=0;
        this.segmentType=0;
        this.intermediatesTime = new ArrayList<TrackPoint>();
    }

    public void addTrackPoint(TrackPoint point){
        trackPoints.add(point);
    }

    /**
     *  Getter, method which returns an array of track points
     * @return ArrayList<TrackPoint>
     */
    public ArrayList<TrackPoint> getTrackPoints() {
        return trackPoints;
    }

    /**
     * getter, method which returns the current position of the runner
     * @return
     */
    public TrackPoint getCurrentPosition(){
        return currentPosition;
    }

    /**
     * methods which computes the total distance of the current track
     * @return totalDistance
     */
    public void setTotalDistance(){

        double tmpTotalDistance = 0;

        for (int i=0; i<trackPoints.size()-1;i++){
            TrackPoint trackpointA = trackPoints.get(i);
            TrackPoint trackPointB = trackPoints.get(i+1);
            float[] results = new float[3];
            Location.distanceBetween(trackpointA.getLatitude(), trackpointA.getLongitude(), trackPointB.getLatitude(), trackPointB.getLongitude(), results);
            tmpTotalDistance+=results[0];

        }
        totalDistance = tmpTotalDistance;

    }

    /**
     * methods wich returns globa time of a track
     */

    public void setTotalTime(){
        totalTime =  trackPoints.get(trackPoints.size()-1).getTime() - trackPoints.get(0).getTime();
    }

    /**
     * methods which set a list of intermediates time of the current track
     */
    public void setIntermediatesTime(){

        setSegment(totalDistance);
        Log.i("AD", "taile de trackpoints : "+trackPoints.size());
        double tmpTotalDistance = 0;
        int j=1;
        for (int i=0; i<trackPoints.size()-1;i++){
            TrackPoint trackpointA = trackPoints.get(i);
            TrackPoint trackPointB = trackPoints.get(i+1);
            float[] results = new float[3];
            Location.distanceBetween(trackpointA.getLatitude(), trackpointA.getLongitude(), trackPointB.getLatitude(), trackPointB.getLongitude(), results);
            tmpTotalDistance+=results[0];
            Log.i("AD", "Distance / tour "+tmpTotalDistance);
            if(segmentType==0) {
                //Log.i("AD", "Segment type 0");
                if (tmpTotalDistance == Integer.valueOf(String.valueOf(j) + String.valueOf("00")) || tmpTotalDistance > Integer.valueOf(String.valueOf(j) + String.valueOf("00"))-1 && tmpTotalDistance < Integer.valueOf(String.valueOf(j) + String.valueOf("01"))) {
                    //Log.i("AD", "MATCH !!!!!!!!!!");
                    this.intermediatesTime.add(trackPoints.get(i));
                    Log.i("AD", "Ajout d'une référence temporelle : "+trackPoints.get(i));
                    j++;
                }
            }
            else {
                //Log.i("AD", "Segment type 1");
                if (tmpTotalDistance == Integer.valueOf(String.valueOf(j) + String.valueOf("000")) || tmpTotalDistance > Integer.valueOf(String.valueOf(j) + String.valueOf("000"))-1 && tmpTotalDistance < Integer.valueOf(String.valueOf(j) + String.valueOf("001"))) {
                    //Log.i("AD", "MATCH !!!!!!!!!!");
                    this.intermediatesTime.add(trackPoints.get(i));
                    Log.i("AD", "Ajout d'une référence temporelle : "+trackPoints.get(i));
                    j++;
                }
            }

        }
        //Log.i("AD", "valeur de j "+j);
        if (segmentNumber < j+1) {
            this.intermediatesTime.add(trackPoints.get(trackPoints.size() - 1));
        }

        Log.i("AD", "nombre d'enregistrement dans le tableau des références temporelles : "+this.intermediatesTime.size());
        for(int k=0;k<this.intermediatesTime.size();k++){
            //Log.i("AD", "Trackpoint enregistré latitude : "+this.intermediatesTime.get(k).getLatitude());
        }

    }

    /**
     * Methods defines the numbers of segments of the existing course
     * @param totalDistance
     */
    public void setSegment(double totalDistance){

        if(totalDistance<1000){
            segmentNumber =(int) Math.ceil(totalDistance /100);
            segmentType = 0;
        }
        else {
            segmentNumber = (int) Math.ceil(totalDistance / 1000);
            segmentType = 1;
        }
        //Log.i("AD", "nombre de segment : "+segmentNumber);
    }

    /**
     * getter total distance of the selected course
     * @return totalDistance
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * getter totalTime of selected course
     * @return totalTime
     */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * getter segment number
     * @return segmentNumber
     */
    public int getSegmentNumber() {
        return segmentNumber;
    }

    /**
     *  getter ArrayList of the intermediates time
     * @return intermediatesTime
     */
    public ArrayList<TrackPoint> getIntermediatesTime() {
        return intermediatesTime;
    }

    /**
     * Getter segment type (0 if distance <1000 & 1 if > 1000
     * @return segmentType
     */
    public int getSegmentType() {
        return segmentType;
    }
}


