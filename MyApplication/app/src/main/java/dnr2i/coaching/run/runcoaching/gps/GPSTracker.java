package dnr2i.coaching.run.runcoaching.gps;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * @author Alexandre DUCREUX on 15/02/2017.
 * Class which manage GPS activity
 */

public class GPSTracker extends Service implements LocationListener {


    private LocationManager locationManager;
    private Context context;
    //gps status
    private boolean isGPSEnabled = false;
    private boolean canGetLocation = false;
    //network status
    private boolean isNetworkEnabled = false;

    private Location location;
    private double latitude;
    private double longitude;
    private GPSUpdateListener listener;

    //constants Time & Distance udpate
    private final static double MIN_DISTANCE_UPDATE = 0;
    private final static long MIN_TIME_UPDATES = 100;
    //permissions
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    //for toast
    private CharSequence message = "";
    private Toast toast;


    public GPSTracker(Context context,GPSUpdateListener listener ) {
        this.context = context;
        this.listener = listener;
        getServiceStatus();
        Log.i("AD","Initialisation du Service GPS");
    }

    public void getServiceStatus(){

        try {
            Log.i("AD","Tentative d'accès au Service GPS");
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            //retrieve gps status
            Log.i("AD","TOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOTTTTTTTTTTTTTTTTTOOOOOOOOOOO");
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i("AD","Statut GPS -> "+isGPSEnabled);
            //retrieve network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.i("AD","Statut du Réseau -> "+isGPSEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                message = "Aucun fournisseur GPS ou réseau disponible";
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //provider enable
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    message = "Utilisation de la géolocalisation par le réseau !";
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            //if GPS services is enable
            if (isGPSEnabled) {
                    message = "Utilisation de la géolocalisation par le GPS !";
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isGPSEnabled()){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES, (float) MIN_DISTANCE_UPDATE, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.i("AD","GPS update");
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATES, (float) MIN_DISTANCE_UPDATE, this);
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.i("AD","Network update");
        }

    }
    /**
     * Method which return network / GPS coordinates if provider is available
     * @return location
     */
    private Location getLocation(Location location) {

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
          Log.i("AD","-------------------------->Reseau : Test d'envoi data lat :"+latitude);


        return location;
    }
    public Location getLocation(){
        return location;
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

    // getters

    /**
     * method which stop GPS listener
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
             locationManager.removeUpdates(GPSTracker.this);
            message = "Activité Terminée !";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * method which checks if GPS or network is available
     * @return
     */
    public boolean CanGetLocation(){
        Log.i("AD", "GPS activé !!!");
        return this.canGetLocation;
    }

    /**
     * to getLatitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * to get longitude
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * get GPS status
     * @return isGPSEnabled
     */
    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //we don't provide binding, so return null
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        //retrieve gps information)
        getLocation(location);
        listener.onUpdate(location);
       // Log.i("AD","Mise à jour coordonnées : lat:"+latitude+" lon:"+longitude);
        long time = location.getTime();
        double speed = location.getSpeed();
        double hdop = (location.getAccuracy()/5);
        double elevation = location.getAltitude();
        int sat = location.getExtras().getInt("satellites");


        //double travelDistance = getTravelDistance(lat,lon);


        //recordTrackPoint(lat, lon, time, elevation, speed, hdop, sat);
        //coordinatesTextView.setText("\n lat : " + lat + " lon : " + lon );
        //speedTextView.setText(String.valueOf(speedKMperHour));
        //distanceTextView.setText(String.valueOf(travelDistance));

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



    /*
    private void recordTrackPoint(double lat, double lon, long time, double elevation, double speed, double hdop, int sat) {

        TrackPoint trkpt = new TrackPoint(lat, lon, elevation, time, speed, hdop, sat);
        trackpointsList.add(trkpt);

    }
    /*


    /*
    private double getTravelDistance(double lat, double lon){

        Location locationA = new Location("Point A");
        Location locationB = new Location("Point B");


        if(latA==0 && lonA==0){
            latA=lat;
            lonA=lon;
            latB=lat;
            lonB=lon;
        }
        else {
            latA = latB;
            lonA = lonB;
            latB = latA;
            lonB = lonA;
        }

        locationA.setLatitude(latA);
        locationA.setLongitude(lonA);

        locationB.setLatitude(latB);
        locationB.setLongitude(lonB);

        distance += locationA.distanceTo(locationB)/1000;

        return distance;

    }
    */
}
