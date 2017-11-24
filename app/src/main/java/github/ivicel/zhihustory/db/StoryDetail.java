package github.ivicel.zhihustory.db;


import org.litepal.crud.DataSupport;

/**
 * Created by sedny on 19/06/2017.
 */

public class StoryDetail extends DataSupport {
    private String mStoryImageSource;
    private String mStoryShareUrl;
    private String mStoryContentUri;
    private String mStoryId;
    
    public String getStoryImageSource() {
        return mStoryImageSource;
    }
    
    public void setStoryImageSource(String storyImageSource) {
        mStoryImageSource = storyImageSource;
    }
    
    public String getStoryShareUrl() {
        return mStoryShareUrl;
    }
    
    public void setStoryShareUrl(String storyShareUrl) {
        mStoryShareUrl = storyShareUrl;
    }
    
    public String getStoryContentUri() {
        return mStoryContentUri;
    }
    
    public void setStoryContentUri(String storyContentUri) {
        mStoryContentUri = storyContentUri;
    }
    
    public String getStoryId() {
        return mStoryId;
    }
    
    public void setStoryId(String storyId) {
        mStoryId = storyId;
    }
}
