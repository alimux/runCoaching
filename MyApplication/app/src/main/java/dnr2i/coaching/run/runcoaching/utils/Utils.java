package dnr2i.coaching.run.runcoaching.utils;

import android.os.Environment;
import android.util.Log;

/**
 * @author Alexandre DUCREUX on 14/02/2017.
 * class util
 */

public class Utils {

    /**
     * methods which checks if external storage is available for read and write
     * @return boolean
     */
    public static boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Log.i("AD","Carte sd montée");
            return true;
        }
        return false;
    }

    /**
     * methodes which checks if external storage is available to at least read
     * @return boolean
     */
    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }
}
