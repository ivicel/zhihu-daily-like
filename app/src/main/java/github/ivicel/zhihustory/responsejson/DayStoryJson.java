package github.ivicel.zhihustory.responsejson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 30/08/2017.
 */

public class DayStoryJson {
    @SerializedName("date")
    public String date;
    
    @SerializedName("stories")
    public List<StoryJson> storyList;
}
