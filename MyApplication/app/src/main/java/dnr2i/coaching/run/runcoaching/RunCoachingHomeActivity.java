package dnr2i.coaching.run.runcoaching;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RunCoachingHomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.mipmap.run_laucher);


    }


   /* public void newCourse(View v){
        Intent intent = new Intent(getApplicationContext(), RunCoachingNewCourseActivity.class);
        startActivity(intent);

    }*/

    public void newCourseGM(View v){
        Intent intent = new Intent(getApplicationContext(), RunCoachingNewCourseGM.class);
        startActivity(intent);
    }

    public void newCourse(View v) {
        final EditText trackName = new EditText(RunCoachingHomeActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final Context context = this;
        trackName.setLayoutParams(lp);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Settings
        alertDialog.setTitle("Nouveau Parcours");
        alertDialog.setMessage("Veuillez entrer le nom de votre Parcours");
        alertDialog.setView(trackName);
        alertDialog.setIcon(R.drawable.run_coaching_mini);
        //if yes
        alertDialog.setPositiveButton("Nouveau Parcours", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), RunCoachingNewCourseActivity.class);
                intent.putExtra("trackName",trackName.getText().toString());
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