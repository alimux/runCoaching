package dnr2i.coaching.run.runcoaching.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author Alexandre DUCREUX on 14/02/2017.
 * class util
 */

public class Utils {

    /**
     * method which checks if external storage is available for read and write
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
     * method which checks if external storage is available to at least read
     * @return boolean
     */
    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }

    /**
     * method which send the list of existing course
     * @param context
     * @return tracknameList
     */
    public static String[] getTracknameList(Context context){
        if(isExternalStorageReadable()) {
            String path = getExternalStoragePath(context);
            File file = new File(path);
            File existingDirectoryList[] = file.listFiles();
            String[] tracknameList = new String[existingDirectoryList.length];
            for (int i = 0; i<existingDirectoryList.length;i++){
                tracknameList[i] = existingDirectoryList[i].getName();
            }
            return tracknameList;
        }
        return null;
    }

    public static String getExternalStoragePath(Context context){
        String path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
        return path;
    }
}
