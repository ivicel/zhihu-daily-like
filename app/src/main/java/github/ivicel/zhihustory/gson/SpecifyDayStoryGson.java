package github.ivicel.zhihustory.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 30/08/2017.
 */

public class SpecifyDayStoryGson {
    @SerializedName("date")
    public String date;
    
    @SerializedName("stories")
    public List<StoryGson> storyList;
}
