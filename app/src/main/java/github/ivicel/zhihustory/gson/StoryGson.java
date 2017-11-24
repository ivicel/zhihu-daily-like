package github.ivicel.zhihustory.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 16/06/2017.
 */

public class StoryGson {
    @SerializedName("ga_prefix")
    public String storyPrefix;
    
    @SerializedName("id")
    public String storyId;
    
    @SerializedName("images")
    public List<String> storyImageUrls;
    
    
    /* top story banner image */
    @SerializedName("image")
    public String topStoryImage;
    
    @SerializedName("title")
    public String storyTitle;
    
    @SerializedName("type")
    public int storyType;

}
