package github.ivicel.zhihustory.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import github.ivicel.zhihustory.gson.SpecifyDayStoryGson;
import github.ivicel.zhihustory.gson.LatestStoriesGson;
import github.ivicel.zhihustory.gson.StoryDetailGson;

import static github.ivicel.zhihustory.BuildConfig.DEBUG;

/**
 * Created by sedny on 16/06/2017.
 */

public class ResponseParser {
    private final static String TAG = "ResponseParser";
    
    
    public static LatestStoriesGson parseLatestStories(String response) {
        LatestStoriesGson latestStoriesGson = null;
        try {
            Gson gson = new Gson();
            latestStoriesGson = gson.fromJson(response, LatestStoriesGson.class);
        } catch (JsonSyntaxException jse) {
            if (DEBUG) {
                Log.e(TAG, "Can't parse latest stoies string to json", jse);
            }
        }
        return latestStoriesGson;
    }
    
    public static SpecifyDayStoryGson parseSpecifyDayStory(String response) {
        SpecifyDayStoryGson result = null;
        Gson gson = new Gson();
        try {
            result = gson.fromJson(response, SpecifyDayStoryGson.class);
        } catch (JsonSyntaxException jse) {
            if (DEBUG) {
                Log.e(TAG, "Can't parse specify day string to json", jse);
            }
        }
        return result;
    }
    
    public static StoryDetailGson parseSpecifyStoryDetail(String response) {
        StoryDetailGson storyDetailGson = null;
        Gson gson = new Gson();
        try {
            storyDetailGson = gson.fromJson(response, StoryDetailGson.class);
        } catch (JsonSyntaxException jse) {
            if (DEBUG) {
                Log.e(TAG, "Can't parse story details to json", jse);
            }
        }
        return storyDetailGson;
    }
}
