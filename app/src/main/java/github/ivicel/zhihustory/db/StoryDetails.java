package github.ivicel.zhihustory.db;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sedny on 19/06/2017.
 */

public class StoryDetails extends DataSupport {
    private String image_source;
    private String shared_url;
    private String content;
    private Image image;
    private String story_id;
    private String date;
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getStory_id() {
        return story_id;
    }
    
    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }
    
    public String getImage_source() {
        return image_source;
    }
    
    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }
    
    public String getShared_url() {
        return shared_url;
    }
    
    public void setShared_url(String shared_url) {
        this.shared_url = shared_url;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image) {
        this.image = image;
    }
}
