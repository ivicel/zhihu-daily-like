package github.ivicel.zhihustory.responsejson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 16/06/2017.
 */

public class LastestStoryJson {
    @SerializedName("stories")
    public List<StoryJson> stories;
    
    @SerializedName("top_stories")
    public List<StoryJson> topStories;
    
    @SerializedName("date")
    public String date;
}
