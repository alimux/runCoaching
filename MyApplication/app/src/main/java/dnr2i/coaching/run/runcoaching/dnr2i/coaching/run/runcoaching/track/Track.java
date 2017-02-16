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
     * Method which consists to return the distance between two trackpoints
     * @param lat
     * @param lon
     * @param lat1
     * @param lon1
     * @return length
     */
    public double getLength(double lat, double lon, double lat1, double lon1){
        converter = new DegreesToUTM();
        converter.convertDegreesToUTM(lat,lon);
        double posXPointA = converter.getEasting();
        double posYPointA = converter.getNorthing();

        converter.convertDegreesToUTM(lat1, lon1);
        double posXPointB = converter.getEasting();
        double posYPointB = converter.getNorthing();

        double length = Math.sqrt(Math.pow((posXPointB-posXPointA),2)+Math.pow((posYPointB-posYPointA),2));
        Log.i("INFO LONGUEUR","length : "+length);

        return length;
    }

    public long getDuration(){
        long time=0;
        return time;
    }

}

