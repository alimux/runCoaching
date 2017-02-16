package dnr2i.coaching.run.runcoaching;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.TrackPoint;
import dnr2i.coaching.run.runcoaching.gps.GPSTracker;
import dnr2i.coaching.run.runcoaching.gps.GPSUpdateListener;


public class RunCoachingNewCourseActivity extends AppCompatActivity implements GPSUpdateListener{

    //settings view assets
    private Button btnStart;
    private Button btnStop;
    private TextView coordinatesTextView;
    private TextView trackName;
    private TextView speedTextView;
    private TextView distanceTextView;
    private TextView durationTextView;

    //gps part
    private GPSTracker gpsTracker;

    //toast
    private CharSequence message;
    private Toast toast;

    //context
    private Context context;
    private GPSUpdateListener listener;


    //private LocationListener locationListener;
    private double distance;
    private double latA=0;
    private double latB=0;
    private double lonA=0;
    private double lonB=0;


    //coordinates
    private ArrayList<TrackPoint> trackpointsList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_new_course);

        //init view assets
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        coordinatesTextView = (TextView) findViewById(R.id.coordinates);
        trackName = (TextView) findViewById(R.id.trackName);
        speedTextView = (TextView) findViewById(R.id.speed);
        distanceTextView = (TextView) findViewById(R.id.distance);
        durationTextView = (TextView) findViewById(R.id.duration);
        context = this;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b!=null){
            String trackNameExtra = (String) b.get("trackName");
            trackName.setText(trackNameExtra);
        }

        //start new course
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getGPSTracker();
                //check if gps is turn-on
                if(!gpsTracker.CanGetLocation()){
                    gpsTracker.showSettingsAlert();
                }
                else{
                    message ="GPS / Réseau activé, vous pouvez débuté votre séance !";
                    toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);

                }
            }

        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AD","Etat gps tracker :"+gpsTracker);
                if(gpsTracker==null){
                    message = "Aucune activité lancée !";
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    gpsTracker.stopUsingGPS();
                }
            }
        });



    }

    private void getGPSTracker(){
        this.listener = this;
        gpsTracker = new GPSTracker(context, this);
    }

    private void displayInformation(){
        //retrieve coordinates
        double lat = (double) Math.round(gpsTracker.getLocation().getLatitude()*100)/100;
        double lon = (double) Math.round(gpsTracker.getLocation().getLongitude()*100)/100;
        //display speed in km/h
        double speed = (double) Math.round((gpsTracker.getLocation().getSpeed()*3.6)*100)/100;
        speedTextView.setText(speed+"Km/h");
        coordinatesTextView.setText("lat : " + lat + " lon : " + lon );
    }


    @Override
    public void onUpdate(Location location) {
        displayInformation();
    }
}
