import twitter4j.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hakoneko on 2016/12/05.
 */

public class Main {


    static class MyStatusListener implements StatusListener {

        private String getExtension(String path) throws StringIndexOutOfBoundsException{
            int idx = path.lastIndexOf(".");
            if(idx == -1){
                throw new StringIndexOutOfBoundsException(idx);
            }
            return path.substring(idx + 1);
        }

        public void onStatus(Status status) {
            String[] medias = null;

            MediaEntity[] mentitys = status.getMediaEntities();
            if( mentitys != null && mentitys.length > 0 ){
                List list = new ArrayList();
                for( int i = 0; i < mentitys.length; i ++ ){
                    MediaEntity mediaEntity = mentitys[i];
                    String mediaURL = mediaEntity.getMediaURL();
                    System.out.println(mediaURL);
                    list.add(mediaURL);


                    URL website = null;
                    try {
                        website = new URL(mediaURL);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    ReadableByteChannel rbc = null;
                    try {
                        rbc = Channels.newChannel(website.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                    FileOutputStream fos =
                            null;
                    try {
                        fos = new FileOutputStream(df.format(status.getCreatedAt()) + '.'+ getExtension(mediaURL));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                medias = ( String[] )list.toArray( new String[0] );

            }

            //System.out.println( "username = " + username + "\n \t text = " + text);
        }

        public void onDeletionNotice(StatusDeletionNotice sdn) {
            //System.out.println("onDeletionNotice.");
        }

        public void onTrackLimitationNotice(int i) {
            //System.out.println("onTrackLimitationNotice.(" + i + ")");
        }

        public void onScrubGeo(long lat, long lng) {
            //System.out.println("onScrubGeo.(" + lat + ", " + lng + ")");
        }

        public void onException(Exception excptn) {
            //System.out.println("onException.");
        }

        public void onStallWarning(StallWarning arg0) {
        }
    }

    
    public static void main(String[] args){
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory();
        TwitterStream twitterStream= twitterStreamFactory.getInstance();
        twitterStream.addListener(new MyStatusListener());
        twitterStream.user();
    }
}
