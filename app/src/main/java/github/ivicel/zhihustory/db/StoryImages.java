package github.ivicel.zhihustory.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import github.ivicel.zhihustory.http.HttpRequest;

/**
 * Created by sedny on 19/06/2017.
 */

public class StoryImages extends DataSupport {
    private String mStoryId;
    private String mStoryDate;
    
    private byte[] mTopStoryImage;
    private byte[] mStoryImage;
    
    
    public String getStoryId() {
        return mStoryId;
    }
    
    public void setStoryId(String storyId) {
        mStoryId = storyId;
    }
    
    public String getStoryDate() {
        return mStoryDate;
    }
    
    public void setStoryDate(String storyDate) {
        mStoryDate = storyDate;
    }
    
    public byte[] getTopStoryImage() {
        /*if (mTopStoryImage == null) {
            String url = where("mStoryId = ?", mStoryId).findFirst(TopStory.class).getImageUrl();
            mTopStoryImage = HttpRequest.fetchImage(url);
            save();
        }*/
        return mTopStoryImage;
    }
    
    public void setTopStoryImage(byte[] topStoryImage) {
        mTopStoryImage = topStoryImage;
    }
    
    public byte[] getStoryImage() {
        /*if (mStoryImage == null) {
            String url = where("mStoryId = ?", mStoryId).findFirst(Story.class).getImageUrl();
            mStoryImage = HttpRequest.fetchImage(url);
            save();
        }*/
        return mStoryImage;
    }
    
    public void setStoryImage(byte[] storyImage) {
        mStoryImage = storyImage;
    }
}
