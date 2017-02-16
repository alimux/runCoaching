package dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track.TrackPoint;
import dnr2i.coaching.run.runcoaching.utils.Utils;

/**
 * @author Alexandre DUCREUX on 13/02/2017.
 * Class which create a gpx file
 */

public class TrackRecorder {

    private ArrayList<TrackPoint> trackPoints;
    private String input = null;
    private  Context context;
    private String trackName;
    private CharSequence text;
    private boolean isSuccess = false;



    public TrackRecorder(ArrayList<TrackPoint> trkpt, Context context, String trackName){
        Log.i("AD","Initialisation de l'enregistrement");
        this.trackName = trackName;
        this.trackPoints = trkpt;
        this.context = context;
        this.input = writeGPXFile(this.trackPoints);
        createFile(this.input, this.trackName);
    }

    private String writeGPXFile(ArrayList<TrackPoint> trackPoints){

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("","gpx");
                serializer.attribute("", "version", "1.0");
                serializer.attribute("", "creator", "Alexandre DUCREUX");

            serializer.startTag("","trk");
                serializer.startTag("", "name");
                 serializer.text(this.trackName);
                serializer.endTag("", "name");

            for(TrackPoint trkpt : trackPoints){
                serializer.startTag("","trkpt");

                    serializer.attribute("", "lat", String.valueOf(trkpt.getLatitude()));
                    serializer.attribute("", "lon", String.valueOf(trkpt.getLongitude()));
                        serializer.startTag("", "ele");
                            serializer.text(String.valueOf(trkpt.getElevation()));
                        serializer.endTag("","ele");
                        serializer.startTag("","time");
                            serializer.text(String.valueOf(trkpt.getTime()));
                        serializer.endTag("","time");
                        serializer.startTag("", "speed");
                            serializer.text(String.valueOf(trkpt.getSpeed()));
                        serializer.endTag("", "speed");
                        serializer.startTag("", "hdop");
                            serializer.text(String.valueOf(trkpt.getHdop()));
                        serializer.endTag("", "hdop");
                        serializer.startTag("", "sat");
                            serializer.text(String.valueOf(trkpt.getSat()));
                        serializer.endTag("", "sat");

                serializer.endTag("","trkpt");

            }


            serializer.endTag("","trk");
            serializer.endTag("","gpx");
            serializer.endDocument();
            return writer.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void createFile(String input, String trackName){


            if(Utils.isExternalStorageWritable()) {

                text = "Carte SD détectée, Tentative d'enregistrement..";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.show();
                Log.i("AD", "Enregistrement du fichier, stockage SD exterbe présent");
                String fileName = trackName + ".gpx";
                File path = getNameStorage();
                File storage = new File(path, fileName);
                Log.i("AD", "External storage : " + storage);

                try {
                    FileOutputStream fos = new FileOutputStream(storage);
                    fos.write(input.getBytes());
                    if(fos!=null) {
                        isSuccess = true;
                        fos.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }

            }

    }

    private File getNameStorage(){
        //private final static File PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), trackName);

        if(!file.mkdir()){
            text = "Impossible de créer le répertoire !";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return file;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}


