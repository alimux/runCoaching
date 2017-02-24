package dnr2i.coaching.run.runcoaching.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import dnr2i.coaching.run.runcoaching.RunCoachingCourseActivity;
import dnr2i.coaching.run.runcoaching.track.DataHandling;

import static android.location.LocationManager.GPS_PROVIDER;

/**
 * @author Alexandre DUCREUX on 15/02/2017.
 * Class which manage GPS  - Android Service
 */

public class GPSTracker extends Service implements LocationListener {

    private LocationManager locationManager;
    private Context context;
    private DataHandling datas;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private double distance = 0;
    private float speed = 0;
    private Location lastlocation = new Location("last");

    //constants Time & Distance udpate
    private final static float MIN_DISTANCE_UPDATE = 0f;
    private final static long MIN_TIME_UPDATES = 100L;
    private final static  int DELTA = 3;

    /**
     * Method which is called when service is started
     * Launch Locoation manager to retrieve gps data if Localization is turn-on
     */
    @Override
    public void onCreate() {
        //initialize location manager
        this.context = getBaseContext();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //settings of the updates
        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATE, this);
        if (locationManager != null) {
            Log.i("AD", "Lancement du service GPS : " + locationManager);
        }
        super.onCreate();

    }

    /**
     * method which is called when the service is stopped
     */
    @Override
    public void onDestroy() {
        //remove location manager so stop update
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    /**
     * Location Listener part, these methods manage update, provider status...
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Methods which retrieve all datas, set information contents in DataHandling
     * Retrieve coordinates, time, elevation, hdop, sat, accuracy, speed.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {

        datas = RunCoachingCourseActivity.getDatas();
        //if location manager & location not null update datas
        if (locationManager != null && location != null) {
            datas.setLocation(location);
            double accuracy =  location.getAccuracy()+DELTA;
            long currentTime = System.currentTimeMillis();
            if (datas.isRunning()) {
                double hdop = (location.getAccuracy() / 5);

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                if (datas.isFirstTime()) {
                    distance = 0;
                    lastLatitude = currentLatitude;
                    lastLongitude = currentLongitude;
                    datas.setFirstTime(false);
                }

                lastlocation.setLatitude(lastLatitude);
                lastlocation.setLongitude(lastLongitude);
                distance = lastlocation.distanceTo(location);
                //distance = distance+0.5;
                //datas.addDistance(distance);
                if (location.hasSpeed()) {
                    //Log.i("AD", "Hasspeed : "+location.hasSpeed()+"Location accuracy : " + (location.getAccuracy()+5) + " distance : " + distance);
                    speed = location.getSpeed();
                    datas.setCurrentSpeed(speed);
                    if (location.getSpeed() == 0) {
                        new IsStillStopped().execute();
                    }else {

                        if (accuracy < distance) {
                            Log.i("AD", "------------------------------------>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            datas.addDistance(distance);
                            lastLatitude = currentLatitude;
                            lastLongitude = currentLongitude;
                            datas.setCurrentLatitude(lastLatitude);
                            datas.setCurrentLongitude(lastLongitude);
                            datas.setHdop(hdop);
                            datas.setElevation(location.getAltitude());
                            datas.setSat(location.getExtras().getInt("satellites"));
                            datas.setTime(currentTime);
                        }
                    }
                }
               // Log.i("AD", "Etat de datas=" + datas + "\n test time system " + System.currentTimeMillis() + "\n Update time" + datas.getTime() + "infos : speed:" + datas.getCurrentSpeed() + "\n lat:" + datas.getCurrentLatitude() + "\n lon:" + datas.getCurrentLongitude());
                if (datas != null) {
                    datas.recordTrackPoint(lastLatitude, lastLongitude, datas.getTime(), datas.getElevation(), datas.getCurrentSpeed(), datas.getCurrentLatitude(), datas.getSat());
                    datas.update();
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * if provider is disable display the window of gps activation
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    /**
     * Async Task when timer is in pause
     */
    class IsStillStopped extends AsyncTask<Void, Integer, String> {
        int timer = 0;

        @Override
        protected String doInBackground(Void... unused) {
            try {
                while (datas.getCurrentSpeed() == 0) {
                    Thread.sleep(1000);
                    timer++;
                }
            } catch (InterruptedException t) {
                return ("The sleep operation failed");
            }
            return ("return object when task is finished");
        }

        @Override
        protected void onPostExecute(String message) {
            datas.setTimeStopped(timer);
        }


    }
}




