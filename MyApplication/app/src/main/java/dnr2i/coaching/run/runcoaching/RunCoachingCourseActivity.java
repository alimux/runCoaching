package dnr2i.coaching.run.runcoaching;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.Track;
import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.TrackPoint;
import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.TrackRecorder;
import dnr2i.coaching.run.runcoaching.gps.GPSTracker;
import dnr2i.coaching.run.runcoaching.utils.Utils;


public class RunCoachingCourseActivity extends AppCompatActivity {

    //settings view assets
    private Button btnStart;
    private Button btnStop;
    private TextView coordinatesTextView;
    private TextView trackName;
    private TextView speedTextView;
    private TextView distanceTextView;
    private Chronometer durationChronometer;
    private TextView goal;
    private TextView timeStatus;
    private TextView goallbl;

    //toast
    private CharSequence message;
    private Toast toast;

    //context
    private Context context;
    private DataHandling.onGPSServiceUpdate onGpsServiceUpdate;

    //datas part
    private static DataHandling datas;
    private ArrayList<TrackPoint> trackPoints;

    //Activity type
    private int activityStatus;
    //coordinates
    private ArrayList<TrackPoint> trackpointsList = new ArrayList<>();
    //virtual course
    private ArrayList<Track> virtualCourse = new ArrayList<>();

    //permissions
    private static final int PERMISSIONS_REQUEST_READEABLE_STORAGE = 1;

    public RunCoachingCourseActivity() {
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_course);

        //settings permission requestion to read file
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                //permission was refused by user
            }
            else {
                //requestion permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READEABLE_STORAGE);
            }
        }

        //init view assets
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        coordinatesTextView = (TextView) findViewById(R.id.coordinates);
        trackName = (TextView) findViewById(R.id.trackName);
        speedTextView = (TextView) findViewById(R.id.speed);
        distanceTextView = (TextView) findViewById(R.id.distance);
        durationChronometer = (Chronometer) findViewById(R.id.duration);
        goal =(TextView)findViewById(R.id.goal);
        timeStatus = (TextView)findViewById(R.id.timeStatus);
        goallbl = (TextView)findViewById(R.id.goallbl);
        context = this;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            String trackNameExtra = (String) b.get("trackName");
            String tmpStatus = (String) b.get("courseStatus");
            activityStatus = Integer.valueOf(tmpStatus);
            Log.i("AD", "ActivityStatus :"+activityStatus);
            trackName.setText(trackNameExtra);
        }
        onGpsServiceUpdate = new DataHandling.onGPSServiceUpdate() {
            @Override
            public void update() {
                // Log.i("AD", "update view\n infos lat:" + datas.getCurrentLatitude());
                displayInformation();
            }
        };
        datas = new DataHandling(onGpsServiceUpdate, activityStatus);

        if(activityStatus==1){
            goal.setVisibility(View.VISIBLE);
            timeStatus.setVisibility(View.VISIBLE);
            goallbl.setVisibility(View.VISIBLE);
        }
        else {
            goal.setVisibility(View.INVISIBLE);
            timeStatus.setVisibility(View.INVISIBLE);
            goallbl.setVisibility(View.INVISIBLE);
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
                    Intent intent = new Intent(RunCoachingCourseActivity.this, GPSTracker.class);
                    startService(intent);
                    message = "GPS / Réseau activé, vous pouvez débuté votre séance !";
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

                } else {
                    Log.i("AD", "Pause du service GPS");
                    durationChronometer.stop();
                    datas.setRunning(false);
                    stopService(new Intent(getBaseContext(), GPSTracker.class));
                }

                if(activityStatus==1){

                    message = "Status "+activityStatus;
                    toast = Toast.makeText(context, message,Toast.LENGTH_LONG);
                    toast.show();
                    if(Utils.isExternalStorageWritable()){
                        message ="Mémoire en Lecture disponible \n Création du parcours virtuel en cours !";
                        toast = Toast.makeText(context, message,Toast.LENGTH_SHORT);
                        toast.show();
                        datas.createVirtualCourse(trackName.getText().toString()+"/"+trackName.getText().toString()+".gpx", context);
                        message = "Durée total du parcours pré enregistré : "+datas.getContentHandler().getTracks().get(0).getTotalDistance()+
                        "\n nombre de segments : "+datas.getContentHandler().getTracks().get(0).getSegmentNumber();
                        toast = Toast.makeText(context, message,Toast.LENGTH_LONG);
                        toast.show();
                    }


                }

            }

        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.setRunning(false);
                durationChronometer.stop();
                datas = new DataHandling(onGpsServiceUpdate, activityStatus);
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

    private boolean recordTrack() {
        String tracknameRecord = trackName.getText().toString();
        if (tracknameRecord == "") {
            tracknameRecord = "Default";
        }

        TrackRecorder trackRecorder = new TrackRecorder(trackpointsList, context, tracknameRecord);
        return trackRecorder.isSuccess();

    }

    private void trainingMessage(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_READEABLE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                } else {
                    //permission refused
                }
                return;
            }
        }
    }
}
