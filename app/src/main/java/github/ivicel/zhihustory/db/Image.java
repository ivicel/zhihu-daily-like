package github.ivicel.zhihustory.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by sedny on 19/06/2017.
 */

public class Image extends DataSupport {
    private byte[] image;
    @Column(unique = true)
    private String story_id;
    private String date;
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getStoryId() {
        return story_id;
    }
    
    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public void setImage(byte[] image) {
        this.image = image;
    }
}
