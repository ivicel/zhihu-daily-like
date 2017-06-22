package github.ivicel.zhihustory.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sedny on 19/06/2017.
 */

public class TopStories extends DataSupport {
    @Column(unique = true)
    private String story_id;
    private String ga_prefix;
    private String title;
    private int type;
    private String date;
    private Image image;
    
    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image) {
        this.image = image;
    }
    
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
    
    public String getGa_prefix() {
        return ga_prefix;
    }
    
    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
}
