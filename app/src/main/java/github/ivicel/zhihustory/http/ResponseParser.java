package github.ivicel.zhihustory.http;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.litepal.crud.DataSupport;

import java.util.List;

import github.ivicel.zhihustory.db.DatabaseUtil;
import github.ivicel.zhihustory.db.Image;
import github.ivicel.zhihustory.db.StoryDetails;
import github.ivicel.zhihustory.db.Thumbnail;
import github.ivicel.zhihustory.responsejson.DayStoryJson;
import github.ivicel.zhihustory.responsejson.LastestStoryJson;
import github.ivicel.zhihustory.responsejson.StoryContentJson;

/**
 * Created by sedny on 16/06/2017.
 */

public class ResponseParser {
    private final static String TAG = "ResponseParser";
    
    
    public static LastestStoryJson parseLatestStory(String response) {
        try {
            LastestStoryJson lastestStoryJson;
            Gson gson = new Gson();
            lastestStoryJson = gson.fromJson(response, LastestStoryJson.class);
            return lastestStoryJson;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static DayStoryJson parseDayStoryJson(String response) {
        Gson gson = new Gson();
        try {
            Log.d(TAG, "parseDayStoryJson: " + response);
            return gson.fromJson(response, DayStoryJson.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static StoryDetails getStoryContent(String response, String date) {
        Gson gson = new Gson();
        try {
            StoryContentJson storyContentJson = gson.fromJson(response, StoryContentJson.class);
            return DatabaseUtil.saveStoryContent(storyContentJson, date);
       } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getWebContent(String body, List<String> css) {
        StringBuilder builder = new StringBuilder("<html><head>");
        if (body != null && css != null) {
            for (String cssLink : css) {
                cssLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssLink + ">";
                builder.append(cssLink);
            }
            String bodyContent = "</head><body>" + body + "</body></html>";
            builder.append(bodyContent);
        }
        return builder.toString();
    }
}
