package dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track;

/**
 * Created by alexandre DUCREUX on 01/02/2017.
 * Class defining trackpoints
 */

public class TrackPoint {

    private double latitude;
    private double longitude;
    private double elevation;
    private long time;

    /**
     * constructor
     * @param latitude
     * @param longitude
     * @param elevation
     * @param time
     */
    public TrackPoint(double latitude, double longitude, double elevation, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.time = time;
    }

    /**
     * getter lattitude
     * @return lattitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * getter longitude
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * getter elevation
     * @return elevation
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * getter time
     * @return time
     */
    public long getTime() {
        return time;
    }
    
}
