package dnr2i.coaching.run.runcoaching.gps;

import android.location.Location;

/**
 * @author Alexandre DUCREUX on 16/02/2017.
 * Listener Interface which able to listen gps update
 */

public interface GPSUpdateListener {
    void onUpdate(Location location);
}
