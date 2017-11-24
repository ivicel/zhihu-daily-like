package github.ivicel.zhihustory.http;

import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static github.ivicel.zhihustory.BuildConfig.DEBUG;

/**
 * get the latest stories: https://news-at.zhihu.com/api/4/stories/latest
 * get the stories of specify day: https://news-at.zhihu.com/api/4/stories/before/20170613
 * get story details: https://news-at.zhihu.com/api/4/story/9472025
 */

public class HttpRequest {
    private static final String TAG = "HttpRequest";
    
    private final static String BASE_URL = "https://news-at.zhihu.com/api/4";
    private final static String LATEST_URL = BASE_URL + "/stories/latest";
      
    
    private static void call(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(callback);
    }
    
    public static void fetchLatest(Callback callback) {
        call(LATEST_URL, callback);
    }
    
    public static void fetchSpecifyDay(String date, Callback callback) {
        String url = BASE_URL + "/stories/before/" + date;
        call(url, callback);
    }
    
    public static String fetchSpecifyStory(String storyId) throws IOException {
        String url = BASE_URL + "/story/" + storyId;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        ResponseBody body = client.newCall(request).execute().body();
        String result = null;
        if (body != null) {
            result = body.string();
            body.close();
        }
        return result;
    }
    
    public static void fetchExtra(String storyId, Callback callback) {
        String url = BASE_URL + "/story-extra/" + storyId;
        call(url, callback);
    }
    
    public static byte[] fetchImage(String url) {
        ResponseBody body = null;
        byte[] byteContent = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            body = client.newCall(request).execute().body();
            if (body != null) {
                byteContent = body.bytes();
            }
        } catch (IOException e) {
            if (DEBUG) {
                Log.e(TAG, "Can't find image: ", e);
            }
            byteContent = null;
        } finally {
            if (body != null) {
                body.close();
            }
        }
        return byteContent;
    }
    
    
}
