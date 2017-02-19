package dnr2i.coaching.run.runcoaching.gps;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import dnr2i.coaching.run.runcoaching.RunCoachingCourseActivity;
import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.DataHandling;

import static android.location.LocationManager.GPS_PROVIDER;

/**
 * @author Alexandre DUCREUX on 15/02/2017.
 *         Class which manage GPS activity
 */

public class GPSTracker extends Service implements LocationListener {


    private LocationManager locationManager;

    private Context context;
    private DataHandling datas;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private double distance=0;
    private Location lastlocation = new Location("last");

    //constants Time & Distance udpate
    private final static double MIN_DISTANCE_UPDATE = 0;
    private final static long MIN_TIME_UPDATES = 100;

    //for toast
    private CharSequence message = "";
    private Toast toast;


    @Override
    public void onCreate() {

        this.context = getBaseContext();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_UPDATES, (float) MIN_DISTANCE_UPDATE, this);
        if(locationManager!=null) {
            Log.i("AD", "Lancement du service GPS : " + locationManager);
        }
        super.onCreate();

    }

    @Override
    public void onDestroy() {
            locationManager.removeUpdates(this);
            message = "Activité Terminée !";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        datas = RunCoachingCourseActivity.getDatas();
        if(locationManager!=null) {
            if (datas.isRunning()) {
                double hdop = (location.getAccuracy()/5);
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();


                if (datas.isFirstTime()) {
                    distance = 0;
                    lastLatitude = currentLatitude;
                    lastLongitude = currentLongitude;
                    datas.setFirstTime(false);
                }
                if( lastLatitude!=0 && lastLongitude !=0 && currentLatitude!=0 && currentLongitude!=0){
                    lastlocation.setLatitude(lastLatitude);
                    lastlocation.setLongitude(lastLongitude);
                    distance = lastlocation.distanceTo(location);
                }

                if (location.getAccuracy() < distance ) {
                    datas.addDistance(distance);

                    lastLatitude = currentLatitude;
                    lastLongitude = currentLongitude;
                    datas.setCurrentLatitude(lastLatitude);
                    datas.setCurrentLongitude(lastLongitude);
                    datas.setHdop(hdop);
                    datas.setElevation(location.getAltitude());
                    datas.setSat(location.getExtras().getInt("satellites"));

                    datas.setTime(location.getTime());
                }

                if (location.hasSpeed()) {
                    datas.setCurrentSpeed(location.getSpeed() * 3.6);
                    if (location.getSpeed() == 0) {
                        new IsStillStopped().execute();
                    }
                }
                if (datas != null) {
                    datas.recordTrackPoint(lastLatitude,lastLongitude,datas.getTime(),datas.getElevation(), datas.getCurrentSpeed(), datas.getCurrentLatitude(), datas.getSat());
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

    @Override
    public void onProviderDisabled(String provider) {

    }

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




        /**
         * method which is called if gps is turned-off
         */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            //Settings
            alertDialog.setTitle("Paramétrages GPS");
            alertDialog.setMessage("Le GPS n'est pas activé, Souhaitez-vous l'activer?");
            //if yes
            alertDialog.setPositiveButton("Paramétrages", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

            //if cancel
            alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }


    }
}




