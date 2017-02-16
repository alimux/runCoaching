package dnr2i.coaching.run.runcoaching;

import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RunCoachingNewCourseGM extends FragmentActivity implements OnMapReadyCallback {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_coaching_new_course_gm);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {

        // Add a marker in Sydney, Australia, and move the camera.
       // LatLng sydney = new LatLng(-34, 151);
        //map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        //GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
        /*LatLng mypos = new LatLng(lat,lon);
        Log.i("AD","Ma position : "+mypos);
        map.addMarker(new MarkerOptions().position(mypos).title("I'm Here"));
        map.moveCamera(CameraUpdateFactory.newLatLng(mypos));*/
    }



}
