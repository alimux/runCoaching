package dnr2i.coaching.run.runcoaching;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import dnr2i.coaching.run.runcoaching.utils.Utils;

public class RunCoachingHomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.mipmap.run_laucher);

    }

    /**
     * new course was dedicated to manage google map activity
     * not totally implement in this version
     * @param v
     */
    public void newCourseGM(View v){
        Intent intent = new Intent(getApplicationContext(), RunCoachingNewCourseGM.class);
        startActivity(intent);
    }

    /**
     * This method launch after to enter a tack name a new course activity
     * @param v
     */
    public void newCourse(View v) {

        //initialize alert dialog layout
        final EditText trackName = new EditText(RunCoachingHomeActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final Context context = this;
        trackName.setLayoutParams(lp);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Settings
        alertDialog.setTitle("Votre nouveau parcours :");
        alertDialog.setMessage("Veuillez entrer le nom de votre Parcours");
        alertDialog.setView(trackName);
        alertDialog.setIcon(R.drawable.mr_ic_play_dark);
        //if yes
        alertDialog.setPositiveButton("Nouveau Parcours", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(RunCoachingHomeActivity.this, RunCoachingCourseActivity.class);
                intent.putExtra("trackName",trackName.getText().toString());
                intent.putExtra("courseStatus","0");
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
    public void existingCourse(View v){

        //initialize alert dialog layout
        final Context context = this;


        //retrieve existing course
        final String[] tracknameList = Utils.getTracknameList(context);

        if(tracknameList==null){
            CharSequence message = "Aucun parcours enregistré...";
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        else {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Settings
        alertDialog.setTitle("Choisissez un parcours d'entraînement :");
        alertDialog.setIcon(R.drawable.mr_ic_play_dark);
        alertDialog.setItems(tracknameList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(RunCoachingHomeActivity.this, RunCoachingCourseActivity.class);
                intent.putExtra("trackName",tracknameList[which]);
                intent.putExtra("courseStatus","1");
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