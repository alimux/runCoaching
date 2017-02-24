package dnr2i.coaching.run.runcoaching;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import dnr2i.coaching.run.runcoaching.track.DataHandling;
import dnr2i.coaching.run.runcoaching.track.Track;
import dnr2i.coaching.run.runcoaching.track.TrackPoint;
import dnr2i.coaching.run.runcoaching.track.TrackRecorder;
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
    private String endMessage;
    private Toast toast;

    //context
    private Context context;
    private DataHandling.onGPSServiceUpdate onGpsServiceUpdate;

    //datas part
    private static DataHandling datas;
    private ArrayList<TrackPoint> trackPoints;
    private int difficulty;
    private long goalTime;
    private int currentSegment = 0;
    private int distanceSegmentExpected=0;

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

    /**
     * Initialize view, This activity content new course and exisiting course
     * An extra was putted in home activity to make a difference between these two activities
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_course);

        //settings permission requestion to read file
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //permission was refused by user
            } else {
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
        goal = (TextView) findViewById(R.id.goal);
        timeStatus = (TextView) findViewById(R.id.timeStatus);
        goallbl = (TextView) findViewById(R.id.goallbl);
        context = this;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        //if extra is not null retrieve trackname & activity status
        if (b != null) {
            String trackNameExtra = (String) b.get("trackName");
            String tmpStatus = (String) b.get("courseStatus");
            activityStatus = Integer.valueOf(tmpStatus);
            Log.i("AD", "ActivityStatus :" + activityStatus);
            trackName.setText(trackNameExtra);
        }
        //initialize listener
        onGpsServiceUpdate = new DataHandling.onGPSServiceUpdate() {
            @Override
            public void update() {
                // Log.i("AD", "update view\n infos lat:" + datas.getCurrentLatitude());
                displayInformation();
            }
        };
        //instantiating datas handling
        datas = new DataHandling(onGpsServiceUpdate, activityStatus);
        //set visible only if existing course
        if (activityStatus == 1) {
            chooseDifficulty();
            goal.setVisibility(View.VISIBLE);
            timeStatus.setVisibility(View.VISIBLE);
            goallbl.setVisibility(View.VISIBLE);
        } else {
            goal.setVisibility(View.INVISIBLE);
            timeStatus.setVisibility(View.INVISIBLE);
            goallbl.setVisibility(View.INVISIBLE);
        }

        //start course
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
                    message = "GPS / Réseau activé, vous pouvez débuté votre séance quand le chrono débutera !";
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    Log.i("AD", "Pause du service GPS");
                    durationChronometer.stop();
                    datas.setRunning(false);
                    stopService(new Intent(getBaseContext(), GPSTracker.class));
                }

            }

        });
        //stop course
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
                if(activityStatus==1){
                    EndExistingCourse((String) endMessage);
                }


            }
        });

        //initialize chronometer
        durationChronometer.setText("00:00:00");
        durationChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time;
                if (datas.isRunning() && datas.getLocation()!=null) {
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

    /**
     * method which display information about distance, speed, coordinates, and if it's an existing course, launching a follow-up of the course
     */
    private void displayInformation() {

        distanceTextView.setText(datas.getDistance());
        double speed = (double) Math.round((datas.getCurrentSpeed() * 3.6) * 100) / 100;
        speedTextView.setText(String.valueOf(speed));
        coordinatesTextView.setText("lat:" + datas.getCurrentLatitude() + " lon:" + datas.getCurrentLongitude());
        trackpointsList = datas.getTrackPointsList();
        if (activityStatus == 1) {
            displayFollowUp();
        }

    }

    /**
     * method which reset all views
     */
    private void resetView() {
        speedTextView.setText("0.0");
        durationChronometer.setText("00:00:00");
        coordinatesTextView.setText("Coordinates");
        distanceTextView.setText("0.0km");
        if (activityStatus == 1) {
            goal.setText("");
            timeStatus.setText("");
        }
    }

    /**
     * method which retrieve datas
     * @return DataHandling
     */
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

    /**
     * Method which display goal time if is an existing course
     */
    private void displayGoalTime() {
        Log.i("AD","Difficulté choisie : "+difficulty);
        goalTime = datas.goalTime(0, difficulty);
        int h = (int) (goalTime / 3600000);
        int m = (int) (goalTime - h * 3600000) / 60000;
        int s = (int) (goalTime - h * 3600000 - m * 60000) / 1000;
        String hh = h < 10 ? "0" + h : h + "";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";

        goal.setText(hh + ":" + mm + ":" + ss);
    }

    /**
     * Methods which retrieve the follow-up of the course
     */
    private void displayFollowUp() {

        int segment = datas.getContentHandler().getTracks().get(0).getSegmentNumber();

        int currentDistance = (int) datas.getDistanceM();
        //int currentDistance = 0;

        //currentDistance = 95;


        long currentTime = SystemClock.elapsedRealtime() - durationChronometer.getBase();
        //currentTime = 87330;
        int coef;

        int distanceSegmentExpectedLower;
        int distanceSegementExpectedhigher;


        Log.i("AD", "appel follow up :GoalTime:" + goalTime + "segment"+segment+ " CurrentTime:" + currentTime+ "currentSegment"+currentSegment+"current Distance"+currentDistance);

        if (datas.getContentHandler().getTracks().get(0).getSegmentType() == 0) {
            coef = 100;
        } else {
            coef = 1000;
        }


        if (currentSegment == segment) {
            Log.i("AD", "segment = segment");
            if (datas.timeFollowUp(0, currentTime, currentSegment-1)) {
                endMessage = "Bravo vous avez réussi votre entraînement avec Brio !";
                btnStop.performClick();
            } else {
                endMessage = "Oups, vous ferez mieux la prochaine fois !";
                btnStop.performClick();
            }
            //btnStop.performClick();
        }else if(currentSegment<segment) {
            Log.i("AD", "dedans");
            distanceSegmentExpected = (1 + currentSegment) * coef;
        }

        if (currentTime >= goalTime) {
            timeStatus.setText("rapé...");
            Log.i("AD", "rapé!");
        }


        distanceSegmentExpectedLower = distanceSegmentExpected - 10;
        distanceSegementExpectedhigher = distanceSegmentExpected +15;

        Log.i("AD", "Distance du segment courant attendu :" + distanceSegmentExpected+" Distance mini :"+distanceSegmentExpectedLower+" distance maxi :"+distanceSegementExpectedhigher);
        if (currentDistance >= distanceSegmentExpectedLower && currentDistance <= distanceSegementExpectedhigher) {
            if (datas.timeFollowUp(0, currentTime, currentSegment)) {
                timeStatus.setText("S:"+currentSegment+" Avance");

            } else {
                timeStatus.setText("S:"+currentSegment+" Retard");
            }
            currentSegment++;
            Log.i("AD", "Segment courant :" + currentSegment);
            updateCurrentSegmentExpected(currentSegment, coef);
        }

    }
    public int updateCurrentSegmentExpected(int currentSegment, int coef){
        int distanceSegmentExpected = (1+currentSegment)*coef;
        Log.i("AD", "Nouvelle distance Expected :" + distanceSegmentExpected);
        return distanceSegmentExpected ;
    }

    /**
     * method which return an alert dialog when the course is over
     * @param endMessage
     */
    public void EndExistingCourse(String endMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Settings
        alertDialog.setTitle("Course Terminée\n"+endMessage);
        alertDialog.setIcon(R.drawable.mr_ic_play_dark);
        alertDialog.setMessage(endMessage + "\n voulez-vous enregistrer votre performance?");
        alertDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recordTrack();
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

    /**
     * method which allows to select a difficulty index
     */
    public void chooseDifficulty() {
        if (Utils.isExternalStorageWritable()) {
            message = "Mémoire en Lecture disponible \n Création du parcours virtuel en cours !";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            datas.createVirtualCourse(trackName.getText().toString() + "/" + trackName.getText().toString() + ".gpx", context);


            final String[] choices = {"Sans difficulté", "Très facile", "Facile", "Moyen", "Difficile", "Champion", "Surhomme"};
            long referenceTime = datas.getContentHandler().getTracks().get(0).getTotalTime();

            int h = (int) (referenceTime / 3600000);
            int m = (int) (referenceTime - h * 3600000) / 60000;
            int s = (int) (referenceTime - h * 3600000 - m * 60000) / 1000;
            String hh = h < 10 ? "0" + h : h + "";
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";

            String displayTotalTime = hh + ":" + mm + ":" + ss;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            Log.i("AD","Temps de référence : "+displayTotalTime);
            //Settings
            alertDialog.setTitle("Sélectionner votre difficulté :\nRéférence : "+displayTotalTime);
            alertDialog.setIcon(R.drawable.mr_ic_play_dark);
            alertDialog.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            difficulty = 0;
                            displayGoalTime();
                            break;
                        case 1:
                            difficulty = 2;
                            displayGoalTime();
                            break;
                        case 2:
                            difficulty = 4;
                            displayGoalTime();
                            break;
                        case 3:
                            difficulty = 10;
                            displayGoalTime();
                            break;
                        case 4:
                            difficulty = 25;
                            displayGoalTime();
                            break;
                        case 5 :
                            difficulty = 35;
                            displayGoalTime();
                            break;
                        case 6 :
                            difficulty = 50;
                            displayGoalTime();
                            break;
                        default:
                            difficulty = 0;
                            displayGoalTime();
                            break;
                    }
                }
            });

            alertDialog.show();

        }
    }

    /**
     * Android overide method when activity is stopped
     */
    @Override
    protected void onStop() {
        super.onStop();
        datas.setRunning(false);
        durationChronometer.stop();
        datas = new DataHandling(onGpsServiceUpdate, activityStatus);
        stopService(new Intent(getBaseContext(), GPSTracker.class));
    }

    /**
     * methods API 23 Marshmallow of security storage
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
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
