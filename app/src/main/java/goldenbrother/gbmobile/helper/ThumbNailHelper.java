package goldenbrother.gbmobile.helper;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by asus on 2016/11/23.
 */

public class ThumbNailHelper {
    private static final String str_yt_web = "www.youtube.com";
    private static final String str_yt_short = "youtu.be";
    private static final String str_fb_web = "www.facebook.com";

    public static String getThumbNail(String url) {
        if (url.contains(str_yt_web)) {
            return getYoutubeID_web(url);
        } else if (url.contains(str_yt_short)) {
            return getYoutubeID_short(url);
        } else if (url.contains(str_fb_web)) {
            return getFacebookID_web(url);
        }
        return null;
    }

    private static String getYoutubeThumbNail(String id) {
        return "http://img.youtube.com/vi/" + id + "/0.jpg";
    }

    // https://www.youtube.com/watch?v=QJZNDVubrRo
    private static String getYoutubeID_web(String url) {
        try {
            String query = new URL(url).getQuery();
            String[] param = query.split("&");
            String id = null;
            for (String row : param) {
                String[] param1 = row.split("=");
                if (param1[0].equals("v")) {
                    id = param1[1];
                }
            }
            return getYoutubeThumbNail(id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // https://youtu.be/QJZNDVubrRo
    private static String getYoutubeID_short(String url) {
        return getYoutubeThumbNail(url.substring(url.lastIndexOf("/") + 1));
    }

    private static String getFacebookThumbNail(String id) {
        return "https://graph.facebook.com/" + id + "/picture";
    }

    // https://www.facebook.com/nba/videos/10154572407433463/
    private static String getFacebookID_web(String url) {
        String v = "videos/";
        int i = url.indexOf(v);
        if (i != -1) {
            return getFacebookThumbNail(url.substring(url.indexOf(v)).split("/")[1]);
        } else {
            return null;
        }
    }
}
