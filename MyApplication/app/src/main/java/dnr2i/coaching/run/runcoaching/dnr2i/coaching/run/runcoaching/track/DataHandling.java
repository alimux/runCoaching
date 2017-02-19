package dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track;

import android.content.Context;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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

    //type activity
    private int activityStatus;
    //content handler
    private ContentHandler contentHandler;

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
        this.activityStatus = 0;

    }

    public interface onGPSServiceUpdate {
        void update();
    }

    public  DataHandling(onGPSServiceUpdate onGpsServiceUpdate, int activityStatus){
        this();
        setOnGPSServiceUpdate(onGpsServiceUpdate);
        this.activityStatus =activityStatus;
        if(activityStatus==1){
            contentHandler = new ContentHandler();
        }
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

    /**
     * setter current longitude
     * @param currentLongitude
     */
    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    /**
     * getter elevation
     * @return
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * setter elevation
     * @param elevation
     */
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    /**
     * setter hdop
     * @param hdop
     */
    public void setHdop(double hdop) {
        this.hdop = hdop;
    }

    /**
     * getter satellite
     * @return
     */
    public int getSat() {
        return sat;
    }

    /**
     * setter satellite
     * @param sat
     */
    public void setSat(int sat) {
        this.sat = sat;
    }

    /**
     * Methods which reccord temporary track data before register in a gpx file
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
           // Log.i("AD","arrayList : lat:"+trk.getLatitude()+" lon:"+trk.getLongitude());
        }
        //Log.i("AD","nombre enregistrement tracklist dans le recording :"+trackPointsList.size());
        getTrackPointsList();
    }

    public void setTrackPointsList(ArrayList<TrackPoint> trackPointsList) {
        this.trackPointsList = trackPointsList;
    }

    /**
     * methods which returns trackPointsList
     * @return
     */
    public ArrayList<TrackPoint> getTrackPointsList() {
       // Log.i("AD","nombre enregistrement tracklist getter :"+trackPointsList.size());
        return this.trackPointsList;
    }

    /**
     * methods which parses a course selected by user
     * @param filename
     * @param context
     */
    public  void createVirtualCourse(String filename, Context context){

        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            Log.i("AD","nom du fichier à récupérer : "+filename+"\n contentHandler status : "+contentHandler);
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);
            if(file.exists()) {
                String fileToParse = String.valueOf(file);
                Log.i("AD","Lancement du parsing du fichier : "+fileToParse);
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(file, contentHandler);
                //launch computing
                for(int i=0;i<contentHandler.getTracks().size();i++){
                    computeVirtualCourseDatas(i);
                }
                CharSequence message = "Parcours virtuel bien créer !";
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                CharSequence message = "Fichier non trouvé !";
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (ParserConfigurationException |SAXException | IOException ex){
            ex.printStackTrace();
        }

    }

    /**
     * methods which returns content handler & access to virtual track
     * @return
     */
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /**
     * methods which pre-computes datas if an existing course is selected
     * @param i
     */
    public void computeVirtualCourseDatas(int i){
        contentHandler.getTracks().get(i).setTotalDistance();
        contentHandler.getTracks().get(i).setTotalTime();
        contentHandler.getTracks().get(i).setIntermediatesTime();
    }

    /**
     * methods which returns a goal time, settings by a user difficulty's choices
     * @param i
     * @param difficulty
     * @return
     */
    public long goalTime(int i, int difficulty){
        long totalTime = contentHandler.getTracks().get(i).getTotalTime();
        Log.i("AD","Total Time =>"+totalTime);
        long goalTime, tmp;
        if(difficulty==0){
            goalTime = totalTime;
            Log.i("AD","GoalTime Sans difficulté =>"+goalTime+" Total Time =>"+totalTime);
        }else {
            tmp = totalTime * difficulty/100;
            goalTime = totalTime - tmp;
            Log.i("AD","GoalTime avec difficulté=>"+goalTime+" Total Time =>"+totalTime);
        }

        return goalTime;
    }

    /**
     * methods which returns if user is early or late
     * @param i
     * @param currentTime
     * @param segment
     * @return
     */
    public boolean timeFollowUp(int i, long currentTime, int segment){
        if(currentTime<contentHandler.getTracks().get(i).getIntermediatesTime().get(segment).getTime()){
            return true;
        }
        return false;
    }

    /**
     * returns the distance in meter
     * @return
     */
    public double getDistanceM() {
        return distanceM;
    }
}
