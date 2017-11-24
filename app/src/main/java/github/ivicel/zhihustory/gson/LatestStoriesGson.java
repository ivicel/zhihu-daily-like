package github.ivicel.zhihustory.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 16/06/2017.
 */

public class LatestStoriesGson {
    @SerializedName("stories")
    public List<StoryGson> storiesGson;
    
    @SerializedName("top_stories")
    public List<StoryGson> topStoriesGson;
    
    @SerializedName("date")
    public String date;
}
