package github.ivicel.zhihustory.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sedny on 19/06/2017.
 */

public class MainStories extends DataSupport {
    @Column(unique = true)
    private String story_id;
    private String ga_prefix;
    private String title;
    private int type;
    private String date;
    private StoryDetails storyDetails;
    private List<Thumbnail> thumbnails;
    
    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }
    
    public void setThumbnails(List<Thumbnail> thumbnails) {
        this.thumbnails = thumbnails;
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
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public StoryDetails getStoryDetails() {
        return storyDetails;
    }
    
    public void setStoryDetails(StoryDetails storyDetails) {
        this.storyDetails = storyDetails;
    }
}
