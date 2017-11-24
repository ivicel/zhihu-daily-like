package github.ivicel.zhihustory;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import github.ivicel.zhihustory.db.DatabaseUtil;
import github.ivicel.zhihustory.db.Story;
import github.ivicel.zhihustory.db.TopStory;
import github.ivicel.zhihustory.gson.LatestStoriesGson;
import github.ivicel.zhihustory.gson.SpecifyDayStoryGson;
import github.ivicel.zhihustory.http.ResponseParser;

import static github.ivicel.zhihustory.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 11/10/2017.
 */

public class StoryController {
    private static final String TAG = "StoryController";
    
    private static StoryController mStoryController;
    /* mAppContext should be a application context, or there may be memory leaked. */
    private Context mAppContext;
    private List<Article> mStories;
    private List<Article> mTopStories;
    private String mCurrentDate;
    
    private StoryController(Context context) {
        mStories = new ArrayList<>();
        mTopStories = new ArrayList<>();
        mAppContext = context.getApplicationContext();
    }
    
    public static StoryController getInstance(Context context) {
        if (mStoryController == null) {
            mStoryController = new StoryController(context);
        }
        
        return mStoryController;
    }
    
    public List<Article> getStories() {
        return mStories;
    }
    
    public List<Article> getTopStories() {
        return mTopStories;
    }
    
    public Article getTopStory(int position) {
        return mTopStories.get(position);
    }
    
    private void addToStoryQueue(String date) {
        List<Story> stories = DatabaseUtil.queryStories(date);
        for (Story story : stories) {
            Article article = new Article();
            article.setId(story.getStoryId());
            article.setDate(story.getStoryDate());
            article.setRead(story.isRead());
            article.setTitle(story.getStoryTitle());
            mStories.add(article);
        }
    }
    
    private void addToTopStoryQueue() {
        List<TopStory> topStories = DatabaseUtil.queryTopStories();
        for (TopStory topStory : topStories) {
            Article article = new Article();
            article.setId(topStory.getStoryId());
            article.setDate(topStory.getStoryId());
            article.setTitle(topStory.getStoryTitle());
            mTopStories.add(article);
        }
    }
    
    public boolean renewLatestStory(String responseString) {
        LatestStoriesGson latestStories = ResponseParser.parseLatestStories(responseString);
        if (latestStories != null) {
            boolean newDay = QueryPreferences.isNewDay(mAppContext, latestStories.date);
            boolean newStory = latestStories.storiesGson.size() !=
                    DatabaseUtil.querySpecifyDayStoryCount(latestStories.date);
            
            if (newDay || newStory) {
                DatabaseUtil.saveLatestStories(latestStories);
                mStories.clear();
                mTopStories.clear();
                addToStoryQueue(latestStories.date);
                addToTopStoryQueue();
                mCurrentDate = latestStories.date;
                QueryPreferences.saveCurrentDay(mAppContext, latestStories.date);
            } else if (mStories.isEmpty()) {
                loadLocalLatestStories();
            }
            return true;
        }
        
        return false;
    }
    
    public void loadLocalLatestStories() {
        String date = QueryPreferences.queryCurrentDay(mAppContext);
        if (date != null) {
            addToStoryQueue(date);
            addToTopStoryQueue();
            mCurrentDate = date;
        }
    }
    
    private String getYestoday() {
        String yestoday = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        try {
            Date date = dateFormat.parse(mCurrentDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
            yestoday = dateFormat.format(c.getTime());
        } catch (ParseException pe) {
            if (DEBUG) {
                Log.e(TAG, "load yestoday error", pe);
            }
        } catch (NullPointerException npe) {
            if (DEBUG) {
                Log.i(TAG, "No story to load");
            }
        }
        return yestoday;
    }
    
    public int getLastItemPosition() {
        return mStories.size();
    }
    
    public int loadMoreStoryFromDB() {
        String date = getYestoday();
        return loadMoreStoryFromDB(date);
    }
    
    private int loadMoreStoryFromDB(String date) {
        if (date == null) {
            return 0;
        }
        List<Story> stories = DatabaseUtil.queryStories(date);
        if (stories.size() > 0) {
            mCurrentDate = date;

            for (Story story : stories) {
                Article article = new Article();
                article.setTitle(story.getStoryTitle());
                article.setId(story.getStoryId());
                article.setRead(story.isRead());
                mStories.add(article);
            }
        }
        
        return stories.size();
    }
    
    public int loadMoreStoryFromWeb(String responseString) {
        SpecifyDayStoryGson stories = ResponseParser.parseSpecifyDayStory(responseString);
        if (stories != null) {
            DatabaseUtil.saveStoryList(stories.storyList, stories.date);
            return loadMoreStoryFromDB(stories.date);
        }
        return 0;
    }
    
    public String getCurrentDate() {
        return mCurrentDate;
    }
    
    public void setArticleRead(Article article) {
        article.setRead(true);
        DatabaseUtil.setStoryRead(article.getId());
    }
    
    public int indexOfStoryList(String articleId) {
        for (int i = 0; i < mStories.size(); i++) {
            Article article = mStories.get(i);
            if (article.getId().equalsIgnoreCase(articleId)) {
                article.setRead(true);
                return i;
            }
        }
        return -1;
    }
}
