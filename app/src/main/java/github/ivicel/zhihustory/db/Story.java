package github.ivicel.zhihustory.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by sedny on 19/06/2017.
 */

public class Story extends DataSupport {
    @Column(unique = true)
    private String mStoryId;
    private String mStoryPrefix;
    private String mStoryTitle;
    private int mStoryType;
    private String mStoryDate;
    private StoryDetail mStoryDetail;
    private StoryImages mStoryImages;
    private boolean mIsRead;
    private String mImageUrl;
    
    public String getImageUrl() {
        return mImageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
    
    public void setStoryImages(StoryImages storyImages) {
    
        mStoryImages = storyImages;
    }
    
    public Story() {
        mIsRead = false;
    }
    
    public boolean isRead() {
        return mIsRead;
    }
    
    public void setRead(boolean read) {
        mIsRead = read;
    }
    
    public String getStoryId() {
        return mStoryId;
    }
    
    public void setStoryId(String storyId) {
        mStoryId = storyId;
    }
    
    public String getStoryPrefix() {
        return mStoryPrefix;
    }
    
    public void setStoryPrefix(String storyPrefix) {
        mStoryPrefix = storyPrefix;
    }
    
    public String getStoryTitle() {
        return mStoryTitle;
    }
    
    public void setStoryTitle(String storyTitle) {
        mStoryTitle = storyTitle;
    }
    
    public int getStoryType() {
        return mStoryType;
    }
    
    public void setStoryType(int storyType) {
        mStoryType = storyType;
    }
    
    public String getStoryDate() {
        return mStoryDate;
    }
    
    public void setStoryDate(String storyDate) {
        mStoryDate = storyDate;
    }
    
    public StoryDetail getStoryDetail() {
        return mStoryDetail;
    }
    
    public void setStoryDetail(StoryDetail storyDetail) {
        mStoryDetail = storyDetail;
    }
}
