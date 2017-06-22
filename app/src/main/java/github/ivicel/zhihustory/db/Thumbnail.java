package github.ivicel.zhihustory.db;

import org.litepal.crud.DataSupport;

/**
 * Created by sedny on 19/06/2017.
 */

public class Thumbnail extends DataSupport {
    private byte[] images;
    private String story_id;
    private String date;
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public byte[] getImages() {
        return images;
    }
    
    public void setImages(byte[] images) {
        this.images = images;
    }
    
    public String getStory_id() {
        return story_id;
    }
    
    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }
}
