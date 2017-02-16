package dnr2i.coaching.run.runcoaching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.*;
/**
 * @author Alexandre DUCREUX 13/02/2017
 * Splash screen Activity
 */
public class RunCoachingSplashScreenActivity extends AppCompatActivity {

    private final static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RunCoachingSplashScreenActivity.this, RunCoachingHomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}
