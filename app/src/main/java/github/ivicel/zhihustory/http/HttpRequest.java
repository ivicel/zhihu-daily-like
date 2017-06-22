package github.ivicel.zhihustory.http;

import android.util.Log;

import java.io.IOException;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by sedny on 18/06/2017.
 */

public class HttpRequest {
    private static OkHttpClient client;
    private final static String BASE_URL = "https://news-at.zhihu.com/api/4";
    private final static String LATEST_URL = BASE_URL + "/stories/latest";
    // https://news-at.zhihu.com/api/4/stories/latest
    // https://news-at.zhihu.com/api/4/stories/before/20170613
    // https://news-at.zhihu.com/api/4/story/9472025
    
    private static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }
    
    
    public static void call(String url, Callback callback) {
        Request request = new Request.Builder().url(url).get().build();
        getClient().newCall(request).enqueue(callback);
    }
    
    public static void getLatestStories(Callback callback) {
        call(LATEST_URL, callback);
    }
    
    public static void getStoriesBeforeDate(String date, Callback callback) {
        if (date != null) {
            String url = BASE_URL + "/stories/before/" + date;
            call(url, callback);
        }
    }
    
    public static void getSpefifyStoryById(String storyId, Callback callback) {
        if (storyId != null) {
            String url = BASE_URL + "/story/" + storyId;
            call(url, callback);
        }
    }
    
    public static void getStoryExtraInfo(String storyId, Callback callback) {
        if (storyId != null) {
            String url = BASE_URL + "/story-extra/" + storyId;
            call(url, callback);
        }
    }
    
    public static byte[] getImage(String url) {
        ResponseBody body;
        byte[] byteContent = null;
        try {
            Request request = new Request.Builder().url(url).build();
            body = getClient().newCall(request).execute().body();
            if (body != null) {
                byteContent = body.bytes();
                body.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            byteContent = null;
        }
        return byteContent;
    }
}
