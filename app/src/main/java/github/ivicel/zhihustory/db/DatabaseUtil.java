package github.ivicel.zhihustory.db;

import org.litepal.crud.DataSupport;

import java.util.List;

import github.ivicel.zhihustory.ArticleDetail;
import github.ivicel.zhihustory.gson.LatestStoriesGson;
import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.gson.StoryDetailGson;
import github.ivicel.zhihustory.gson.StoryGson;

/**
 * Created by sedny on 21/06/2017.
 */

public class DatabaseUtil {
    private final static String TAG = "DatabaseUtil";
    
    public static void saveLatestStories(LatestStoriesGson storiesGson) {
        if (storiesGson == null) {
            return;
        }
        String date = storiesGson.date;
        saveStoryList(storiesGson.storiesGson, date);
        saveTopStories(storiesGson.topStoriesGson, date);
    }
    
    public static void saveStoryList(List<StoryGson> storiesGson, String date) {
        for (StoryGson storyGson : storiesGson) {
            Story story = DataSupport.where("mStoryId = ?", storyGson.storyId).findFirst(Story.class);
            if (story != null) {
                continue;
            }
            
            String imageUrl = storyGson.storyImageUrls.get(0);
            story = new Story();
            story.setStoryId(storyGson.storyId);
            story.setStoryPrefix(storyGson.storyPrefix);
            story.setStoryTitle(storyGson.storyTitle);
            story.setStoryType(storyGson.storyType);
            story.setStoryDate(date);
            story.setImageUrl(imageUrl);
    
            byte[] imageContent = HttpRequest.fetchImage(imageUrl);
            StoryImages storyImages = new StoryImages();
            storyImages.setStoryId(storyGson.storyId);
            storyImages.setStoryDate(date);
            storyImages.setStoryImage(imageContent);
            storyImages.save();
            
            story.setStoryImages(storyImages);
            story.save();
        }
    }
    
    private static void saveTopStories(List<StoryGson> topStoriesGson, String date) {
        DataSupport.deleteAll(TopStory.class);
        for (StoryGson storyGson : topStoriesGson) {
            TopStory story = new TopStory();
            story.setStoryId(storyGson.storyId);
            story.setStoryDate(date);
            story.setStoryTitle(storyGson.storyTitle);
            story.setStoryPrefix(storyGson.storyPrefix);
            story.setStoryType(storyGson.storyType);
            story.setImageUrl(storyGson.topStoryImage);
    
            StoryImages storyImages = DataSupport.where("mStoryId = ?", storyGson.storyId)
                    .findFirst(StoryImages.class);
            if (storyImages == null) {
                storyImages = new StoryImages();
                storyImages.setStoryDate(date);
                storyImages.setStoryId(storyGson.storyId);
            }
    
            byte[] imageContent = HttpRequest.fetchImage(storyGson.topStoryImage);
            storyImages.setTopStoryImage(imageContent);
            storyImages.save();
            
            story.setStoryImages(storyImages);
            story.save();
        }
    }
    
    public static StoryDetail saveStoryContent(StoryDetailGson storyDetailGson, String date) {
        // try {
        //     String bodyContent =
        //             ResponseParser.getWebContent(storyDetailGson.body, storyDetailGson.css);
        //     StoryDetail storyDetails = new StoryDetail();
        //     storyDetails.setContent(bodyContent);
        //     storyDetails.setStory_id(storyDetailGson.storyId);
        //     storyDetails.setImage_source(storyDetailGson.imageSource);
        //     storyDetails.setShared_url(storyDetailGson.shareUrl);
        //     storyDetails.setDate(date);
        //     StoryImages storyImages = DataSupport.where("story_id = ?", storyDetailGson.storyId)
        //             .findFirst(StoryImages.class);
        //     if (storyImages == null) {
        //         Log.d(TAG, "saveStoryContent: storyImages is null");
        //         byte[] imageContent = HttpRequest.getImage(storyDetailGson.imageUrl);
        //         storyImages = new StoryImages();
        //         storyImages.setImage(imageContent);
        //         storyImages.setStory_id(storyDetailGson.storyId);
        //         storyImages.setDate(date);
        //         storyImages.save();
        //     }
        //     storyDetails.setStoryImages(storyImages);
        //     storyDetails.save();
        //     Log.d(TAG, "saveStoryContent: " + storyImages.toString());
        //
        //     return storyDetails;
        // } catch (SQLiteConstraintException | DataSupportException e) {
        //     e.printStackTrace();
        //     return null;
        // }
        return null;
    }

    public static int querySpecifyDayStoryCount(String date) {
        return DataSupport.where("mStoryDate = ?", date).count(Story.class);
    }
    
    public static List<Story> queryStories(String date) {
        return DataSupport.where("mStoryDate = ?", date).order("mStoryPrefix desc").find(Story.class);
    }
    
    public static ArticleDetail queryStoryDetail(String storyId) {
        StoryDetail storyDetail = DataSupport.where("mStoryId = ?", storyId).findFirst(StoryDetail.class);
        ArticleDetail articleDetail = null;
        if (storyDetail != null) {
            articleDetail = new ArticleDetail();
            articleDetail.setArticleId(storyDetail.getStoryId());
            articleDetail.setImageSource(storyDetail.getStoryImageSource());
            articleDetail.setContentUri(storyDetail.getStoryContentUri());
            articleDetail.setShareUrl(storyDetail.getStoryShareUrl());
        }
        return articleDetail;
    }
    
    public static void saveStoryDetail(ArticleDetail articleDetail) {
        StoryDetail storyDetail = new StoryDetail();
        storyDetail.setStoryId(articleDetail.getArticleId());
        storyDetail.setStoryContentUri(articleDetail.getContentUri());
        storyDetail.setStoryImageSource(articleDetail.getImageSource());
        storyDetail.setStoryShareUrl(articleDetail.getShareUrl());
        storyDetail.saveAsync();
    }
    
    public static List<TopStory> queryTopStories() {
        return DataSupport.findAll(TopStory.class);
    }
    
    public static void setStoryRead(String id) {
        Story story = DataSupport.where("mStoryId = ?", id).findFirst(Story.class);
        story.setRead(true);
        story.save();
    }
}
