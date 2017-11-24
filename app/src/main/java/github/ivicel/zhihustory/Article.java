package github.ivicel.zhihustory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.litepal.crud.DataSupport;

import github.ivicel.zhihustory.db.StoryImages;

/**
 * Created by sedny on 15/06/2017.
 */

public class Article {
    private String mTitle;
    private String mId;
    private String mDate;
    private boolean mIsRead;
    
    public String getTitle() {
        return mTitle;
    }
    
    public void setTitle(String title) {
        mTitle = title;
    }
    
    public String getId() {
        return mId;
    }
    
    public void setId(String id) {
        mId = id;
    }
    
    public String getDate() {
        return mDate;
    }
    
    public void setDate(String date) {
        mDate = date;
    }
    
    public boolean isRead() {
        return mIsRead;
    }
    
    public void setRead(boolean read) {
        mIsRead = read;
    }
    
    public Bitmap getStoryImage() {
        StoryImages storyImage = DataSupport.where("mStoryId = ?", mId)
                .findFirst(StoryImages.class);
        byte[] bytesImage = storyImage.getStoryImage();
        if (bytesImage != null) {
            return BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
        } else {
            return null;
        }
    }
    
    public Bitmap getTopStoryImage() {
        StoryImages storyImage = DataSupport.where("mStoryId = ?", mId)
                .findFirst(StoryImages.class);
        byte[] bytesImage = storyImage.getTopStoryImage();
        if (bytesImage != null) {
            return BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
        } else {
            return null;
        }
    }
}
