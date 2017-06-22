package github.ivicel.zhihustory.responsejson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 16/06/2017.
 */

public class StoryContentJson {
    @SerializedName("body")
    public String body;
    
    @SerializedName("css")
    public List<String> css;
    
    @SerializedName("ga_prefix")
    public String gaPrefix;
    
    @SerializedName("id")
    public String storyId;
    
    @SerializedName("image")
    public String imageUrl;
    
    @SerializedName("image_source")
    public String imageSource;
    
    @SerializedName("images")
    public List<String> imageUrls;
    
    @SerializedName("js")
    public List<String> js;
    
    @SerializedName("recommenders")
    public List<Recommender> recommenders;

    public class Recommender {
        @SerializedName("avatar")
        public String avatar;
    }
    
    @SerializedName("share_url")
    public String shareUrl;
    
    @SerializedName("title")
    public String storyTitle;
    
    @SerializedName("type")
    public int storyType;
    
    private String bodyContent;

    public String getBodyContent() {
        return bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }
}
