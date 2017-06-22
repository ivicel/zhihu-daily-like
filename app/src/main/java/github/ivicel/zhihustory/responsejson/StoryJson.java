package github.ivicel.zhihustory.responsejson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 16/06/2017.
 */

public class StoryJson {
    @SerializedName("ga_prefix")
    public String gaPrefix;
    
    @SerializedName("id")
    public String storyId;
    
    @SerializedName("images")
    public List<String> imageUrls;
    
    @SerializedName("image")
    public String imageUrl;
    
    @SerializedName("title")
    public String storyTitle;
    
    @SerializedName("type")
    public int storyType;

}
