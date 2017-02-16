package dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by alexandre DUCREUX on 01/02/2017.
 * Class defining a Track
 */

public class Track {

    private ArrayList<TrackPoint> trackPoints;
    private TrackPoint currentPosition;
    private DegreesToUTM converter;


    /**
     * Constructor
     * initialize an ArrayList of track points
     */
    public Track() {
        this.trackPoints = new ArrayList<TrackPoint>();

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



    public long getDuration(){
        long time=0;
        return time;
    }

}

