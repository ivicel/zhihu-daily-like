package github.ivicel.zhihustory.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sedny on 16/06/2017.
 */

public class StoryDetailGson {
    @SerializedName("ga_prefix")
    public String storyPrefix;
    
    @SerializedName("id")
    public String storyId;
    
    @SerializedName("image")
    public String topStoryImageUrl;
    
    @SerializedName("images")
    public List<String> storyImageUrls;
    
    @SerializedName("recommenders")
    public List<Recommender> recommenders;

    public class Recommender {
        @SerializedName("avatar")
        public String authorAvatar;
    }
    
    @SerializedName("image_source")
    public String storyImageSource;
    
    @SerializedName("js")
    public List<String> js;
    
    @SerializedName("body")
    public String storyBody;
    
    @SerializedName("css")
    public List<String> storyCssStyle;
    
    @SerializedName("share_url")
    public String shareUrl;
    
    @SerializedName("title")
    public String storyTitle;
    
    @SerializedName("type")
    public int storyType;
    
    
    public String buildStoryContent() {
        StringBuilder builder = new StringBuilder("<html><head>");
        for (String css : storyCssStyle) {
            css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + css + ">";
            builder.append(css);
        }
        builder.append("</head><body>");
        builder.append(storyBody);
        builder.append("</body></html>");
        
        return builder.toString();
    }
}
