package dnr2i.coaching.run.runcoaching.dnr2i.coaching.run.runcoaching.track;

import android.sax.Element;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * @author Alexandre DUCREUX on 16/02/2017.
 * class which read and parse gpx datas
 */

public class ContentHandler extends DefaultHandler {

    private double latitude, longitude;
    private long time;
    private ArrayList<Track> tracks;
    private String characters;



    public ContentHandler(){
        Log.i("AD","ContentHandler : initialisation...");
        tracks = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if(qName.equalsIgnoreCase("trk")){
            tracks.add(new Track());
        }
        if(qName.equalsIgnoreCase("trkpt")){
            latitude = Double.parseDouble(attributes.getValue("lat"));
            longitude = Double.parseDouble(attributes.getValue("lon"));
        }
    }

    @Override
    public void characters(char[] chars, int i, int j) throws SAXException
    {
        characters+=new String(chars,i,j);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException{
        if(qName.equalsIgnoreCase("time")){
                time=Long.valueOf(time);
        }
        if(qName.equalsIgnoreCase("trkpt")){
            tracks.get(tracks.size()-1).addTrackPoint(new TrackPoint(latitude, longitude, time ));
            //Log.i("AD","new point :"+latitude+" longitude="+longitude+" date="+(time));
        }
        characters="";
    }


    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
