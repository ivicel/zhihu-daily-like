package github.ivicel.zhihustory.db;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;

import java.util.ArrayList;
import java.util.List;

import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.http.ResponseParser;
import github.ivicel.zhihustory.responsejson.StoryContentJson;
import github.ivicel.zhihustory.responsejson.StoryJson;

/**
 * Created by sedny on 21/06/2017.
 */

public class DatabaseUtil {
    private final static String TAG = "DatabaseUtil";
    public static void saveMainStories(List<StoryJson> mainStories, String date) {
        for (StoryJson storyJson : mainStories) {
            if (DataSupport.where("story_id = ?", storyJson.storyId)
                    .findFirst(MainStories.class) == null) {
                try {
                    MainStories story = new MainStories();
                    List<Thumbnail> thumbnails = new ArrayList<>();
                    story.setDate(date);
                    story.setGa_prefix(storyJson.gaPrefix);
                    story.setStory_id(storyJson.storyId);
                    story.setTitle(storyJson.storyTitle);
                    story.setType(storyJson.storyType);
                    for (String image : storyJson.imageUrls) {
                        byte[] byteContent = HttpRequest.getImage(image);
                        if (byteContent != null) {
                            Thumbnail thumbnail = new Thumbnail();
                            thumbnail.setImages(byteContent);
                            thumbnail.setStory_id(story.getStory_id());
                            thumbnail.setDate(date);
                            thumbnail.save();
                            thumbnails.add(thumbnail);
                        }
                    }
                    story.setThumbnails(thumbnails);
                    story.save();
                } catch (SQLiteConstraintException | DataSupportException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void saveTopStories(List<StoryJson> topStoriesJson, String date) {
        DataSupport.deleteAll(TopStories.class);
        for (StoryJson storyJson : topStoriesJson) {
            try {
                TopStories story = new TopStories();
                story.setGa_prefix(storyJson.gaPrefix);
                story.setStory_id(storyJson.storyId);
                story.setTitle(storyJson.storyTitle);
                story.setType(storyJson.storyType);
                story.setDate(date);
                String imageUrl = storyJson.imageUrl;
                byte[] byteContent = HttpRequest.getImage(imageUrl);
                if (byteContent != null) {
                    Image image = new Image();
                    image.setImage(byteContent);
                    image.setStory_id(story.getStory_id());
                    image.setDate(date);
                    image.save();
                    story.setImage(image);
                }
                story.save();
            } catch (SQLiteConstraintException | DataSupportException e) {
                Log.d("top stories", Log.getStackTraceString(e));
            }
        }
    }
    
    public static StoryDetails saveStoryContent(StoryContentJson storyContentJson, String date) {
        try {
            String bodyContent =
                    ResponseParser.getWebContent(storyContentJson.body, storyContentJson.css);
            StoryDetails storyDetails = new StoryDetails();
            storyDetails.setContent(bodyContent);
            storyDetails.setStory_id(storyContentJson.storyId);
            storyDetails.setImage_source(storyContentJson.imageSource);
            storyDetails.setShared_url(storyContentJson.shareUrl);
            storyDetails.setDate(date);
            Image image = DataSupport.where("story_id = ?", storyContentJson.storyId)
                    .findFirst(Image.class);
            if (image == null) {
                Log.d(TAG, "saveStoryContent: image is null");
                byte[] imageContent = HttpRequest.getImage(storyContentJson.imageUrl);
                image = new Image();
                image.setImage(imageContent);
                image.setStory_id(storyContentJson.storyId);
                image.setDate(date);
                image.save();
            }
            storyDetails.setImage(image);
            storyDetails.save();
            Log.d(TAG, "saveStoryContent: " + image.toString());
            
            return storyDetails;
        } catch (SQLiteConstraintException | DataSupportException e) {
            e.printStackTrace();
            return null;
        }
    }
}
