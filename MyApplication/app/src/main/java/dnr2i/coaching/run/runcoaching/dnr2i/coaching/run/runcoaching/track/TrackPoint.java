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
    private double speed;
    private double hdop;
    private int sat;

    /**
     * constructor
     * @param latitude
     * @param longitude
     * @param elevation
     * @param time
     */
    public TrackPoint(double latitude, double longitude, double elevation, long time, double speed, double hdop,int sat) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.time = time;
        this.speed = speed;
        this.hdop = hdop;
        this.sat = sat;
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

    /**
     * getter speed
     * @return speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * getter hdop
     * @return hdop
     */

    public double getHdop() {
        return hdop;
    }

    /**
     * getter number sattelites
     * @return sat
     */
    public int getSat() {
        return sat;
    }
}
