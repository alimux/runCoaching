package dnr2i.coaching.run.runcoaching;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.DataHandling;
import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.TrackPoint;
import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.TrackRecorder;
import dnr2i.coaching.run.runcoaching.gps.GPSTracker;


public class RunCoachingNewCourseActivity extends AppCompatActivity {

    //settings view assets
    private Button btnStart;
    private Button btnStop;
    private TextView coordinatesTextView;
    private TextView trackName;
    private TextView speedTextView;
    private TextView distanceTextView;
    private Chronometer durationChronometer;

    //toast
    private CharSequence message;
    private Toast toast;

    //context
    private Context context;
    private DataHandling.onGPSServiceUpdate onGpsServiceUpdate;

    //datas part
    private static DataHandling datas;
    private ArrayList<TrackPoint> trackPoints;

    //constants
    private final static int ACTIVITY = 1;
    //coordinates
    private ArrayList<TrackPoint> trackpointsList = new ArrayList<>();

    public RunCoachingNewCourseActivity() {
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_new_course);

        onGpsServiceUpdate = new DataHandling.onGPSServiceUpdate() {
            @Override
            public void update() {
                Log.i("AD", "update view\n infos lat:" + datas.getCurrentLatitude());
                displayInformation();
            }
        };
        datas = new DataHandling(onGpsServiceUpdate);

        //init view assets
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        coordinatesTextView = (TextView) findViewById(R.id.coordinates);
        trackName = (TextView) findViewById(R.id.trackName);
        speedTextView = (TextView) findViewById(R.id.speed);
        distanceTextView = (TextView) findViewById(R.id.distance);
        durationChronometer = (Chronometer) findViewById(R.id.duration);
        context = this;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            String trackNameExtra = (String) b.get("trackName");
            trackName.setText(trackNameExtra);
        }

        //start new course
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!datas.isRunning()) {
                    Log.i("AD", "Lancement du service GPS");
                    datas.setRunning(true);
                    datas.setFirstTime(true);
                    durationChronometer.setBase(SystemClock.elapsedRealtime() - datas.getTime());
                    durationChronometer.start();
                    Intent intent = new Intent(RunCoachingNewCourseActivity.this, GPSTracker.class);
                    intent.putExtra("currentActivity", ACTIVITY);
                    startService(intent);
                    message = "GPS / Réseau activé, vous pouvez débuté votre séance !";
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

                } else {
                    Log.i("AD", "Pause du service GPS");
                    durationChronometer.stop();
                    datas.setRunning(false);
                    stopService(new Intent(getBaseContext(), GPSTracker.class));
                }

            }

        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.setRunning(false);
                durationChronometer.stop();
                datas = new DataHandling(onGpsServiceUpdate);
                stopService(new Intent(getBaseContext(), GPSTracker.class));
                resetView();
                message = "Activité terminée !";
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
                if (recordTrack()) {
                    message = "Fichier bien enregistré !";

                } else {
                    message = "Un problème est survenu pendant l'enregistrement";
                }
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();


            }
        });


        durationChronometer.setText("00:00:00");
        durationChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time;
                if (datas.isRunning()) {
                    time = SystemClock.elapsedRealtime() - chronometer.getBase();
                    datas.setTime(time);
                } else {
                    time = datas.getTime();
                }

                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                chronometer.setText(hh + ":" + mm + ":" + ss);

                if (datas.isRunning()) {
                    chronometer.setText(hh + ":" + mm + ":" + ss);
                } else {
                    if (isPair) {
                        isPair = false;
                        chronometer.setText(hh + ":" + mm + ":" + ss);
                    } else {
                        isPair = true;
                        chronometer.setText("");
                    }
                }
            }
        });

    }


    private void displayInformation() {

        distanceTextView.setText(datas.getDistance());
        double speed = (double) Math.round((datas.getCurrentSpeed() * 3.6) * 100) / 100;
        speedTextView.setText(String.valueOf(speed));
        coordinatesTextView.setText("lat:" + datas.getCurrentLatitude() + " lon:" + datas.getCurrentLongitude());
        trackpointsList = datas.getTrackPointsList();


    }

    private void resetView() {
        speedTextView.setText("0.0");
        durationChronometer.setText("00:00:00");
        coordinatesTextView.setText("Coordinates");
        distanceTextView.setText("0.0km");
    }


    public static DataHandling getDatas() {
        return datas;
    }

    public boolean recordTrack() {
        String tracknameRecord = trackName.getText().toString();
        if (tracknameRecord == "") {
            tracknameRecord = "Default";
        }

        Log.i("AD","nombre enregistrement tracklist :"+trackpointsList.size());
        for(TrackPoint trackPoint : trackpointsList){
            message = "lat :"+trackPoint.getLatitude();
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        TrackRecorder trackRecorder = new TrackRecorder(trackpointsList, context, tracknameRecord);
        return trackRecorder.isSuccess();

    }

}
